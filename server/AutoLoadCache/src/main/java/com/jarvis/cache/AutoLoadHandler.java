package com.jarvis.cache;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.aop.CacheAopProxyChain;
import com.jarvis.cache.to.AutoLoadConfig;
import com.jarvis.cache.to.AutoLoadTO;
import com.jarvis.cache.to.CacheKeyTO;
import com.jarvis.cache.to.CacheWrapper;


/**
 * 用于处理自动加载缓存，sortThread 从autoLoadMap中取出数据，然后通知threads进行处理。
 * 
 * @author jiayu.qiu
 */
public class AutoLoadHandler {
    private static Logger log = LoggerFactory.getLogger(AutoLoadHandler.class);


    /**
     * 自动加载最小过期时间
     */
    public static final int AUTO_LOAD_MIN_EXPIRE = 120;

    private static final int ONE_THOUSAND_MS = 1000;

    public static final String THREAD_NAME_PREFIX = "autoLoadThread-";

    /**
     * 自动加载队列
     */
    private final ConcurrentHashMap<CacheKeyTO, AutoLoadTO> autoLoadMap;

    private final CacheHandler cacheHandler;

    /**
     * 缓存池
     */
    private final Thread[] threads;

    /**
     * 排序进行，对自动加载队列进行排序
     */
    private final Thread sortThread;

    /**
     * 自动加载队列
     */
    private final LinkedBlockingQueue<AutoLoadTO> autoLoadQueue;

    private volatile boolean running = false;

    /**
     * 自动加载配置
     */
    private final AutoLoadConfig config;
    
    private final DBLoadHanler dbLoadHandler;
    
    /** cache低访问频率 */
    private int cachePeerHitRate = 10;
    
    /**
     * @param cacheHandler 缓存的set,get方法实现类
     * @param config 配置
     */
    public AutoLoadHandler(CacheHandler cacheHandler, AutoLoadConfig config, DBLoadHanler dbLoadHandler) {
        this.cacheHandler = cacheHandler;
        this.config = config;
        this.dbLoadHandler = dbLoadHandler;
        if (this.config.getThreadCnt() > 0) {
            this.running = true;
            this.threads = new Thread[this.config.getThreadCnt()];
            this.autoLoadMap = new ConcurrentHashMap<CacheKeyTO, AutoLoadTO>(this.config.getMaxElement());
            this.autoLoadQueue = new LinkedBlockingQueue<AutoLoadTO>(this.config.getMaxElement());
            this.sortThread = new Thread(new SortRunnable());
            this.sortThread.setDaemon(true);
            this.sortThread.start();
            for (int i = 0; i < this.config.getThreadCnt(); i++) {
                this.threads[i] = new Thread(new AutoLoadRunnable());
                this.threads[i].setName(THREAD_NAME_PREFIX + i);
                this.threads[i].setDaemon(true);
                this.threads[i].start();
            }
        } else {
            this.threads = null;
            this.autoLoadMap = null;
            this.autoLoadQueue = null;
            this.sortThread = null;
        }
    }


    public int getCachePeerHitRate() {
        return cachePeerHitRate;
    }

    public void setCachePeerHitRate(int cachePeerHitRate) {
        this.cachePeerHitRate = cachePeerHitRate;
    }

    public int getSize() {
        if (null != autoLoadMap) {
            return autoLoadMap.size();
        }
        return -1;
    }

    public AutoLoadTO getAutoLoadTO(CacheKeyTO cacheKey) {
        if (null == autoLoadMap) {
            return null;
        }
        return autoLoadMap.get(cacheKey);
    }

    public void removeAutoLoadTO(CacheKeyTO cacheKey) {
        if (null == autoLoadMap) {
            return;
        }
        autoLoadMap.remove(cacheKey);
    }

    /**
     * 重置自动加载时间
     * 
     * @param cacheKey 缓存Key
     */
    public void resetAutoLoadLastLoadTime(CacheKeyTO cacheKey) {
        if (null == autoLoadMap) {
            return;
        }
        AutoLoadTO autoLoadTO = autoLoadMap.get(cacheKey);
        if (null != autoLoadTO && !autoLoadTO.isLoading()) {
            autoLoadTO.setLastLoadTime(1L);
        }
    }

    public void shutdown() {
        running = false;
        if (null != autoLoadMap) {
            autoLoadMap.clear();
        }
        log.info("----------------------AutoLoadHandler.shutdown--------------------");
    }

    public AutoLoadTO putIfAbsent(CacheKeyTO cacheKey, CacheAopProxyChain joinPoint, Cache cache,
            CacheWrapper<Object> cacheWrapper) {
        if (null == autoLoadMap) {
            return null;
        }
        AutoLoadTO autoLoadTO = autoLoadMap.get(cacheKey);
        if (null != autoLoadTO) {
            return autoLoadTO;
        }
        try {
            if (!cacheHandler.getScriptParser().isAutoload(cache, joinPoint.getTarget(), joinPoint.getArgs(),
                    cacheWrapper.getCacheObject())) {
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }

        if (!dbLoadHandler.isDBLoad(cacheWrapper.getCacheObject())) {
            // 该类数据不进行续期
            if (log.isDebugEnabled()) {
                log.debug("autoload not put. online player, id:" + cacheKey.getKey());
            }
            return null;
        }

        int expire = cacheWrapper.getExpire();
        if (expire >= AUTO_LOAD_MIN_EXPIRE && autoLoadMap.size() <= this.config.getMaxElement()) {
            Object[] arguments;
            if (cache.argumentsDeepcloneEnable()) {
                try {
                    // 进行深度复制
                    arguments = (Object[]) cacheHandler.getCloner().deepCloneMethodArgs(joinPoint.getMethod(),
                            joinPoint.getArgs());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    return null;
                }
            } else {
                arguments = joinPoint.getArgs();
            }
            autoLoadTO = new AutoLoadTO(cacheKey, joinPoint, arguments, cache, expire);
            AutoLoadTO tmp = autoLoadMap.putIfAbsent(cacheKey, autoLoadTO);
            if (null == tmp) {
                return autoLoadTO;
            } else {
                return tmp;
            }
        }
        return null;
    }

    /**
     * 获取自动加载队列，如果是web应用，建议把自动加载队列中的数据都输出到页面中，并增加一些管理功能。
     * 
     * @return autoload 队列
     */
    public AutoLoadTO[] getAutoLoadQueue() {
        if (null == autoLoadMap || autoLoadMap.isEmpty()) {
            return null;
        }
        AutoLoadTO[] tmpArr = new AutoLoadTO[autoLoadMap.size()];
        // 复制引用
        tmpArr = autoLoadMap.values().toArray(tmpArr);
        if (null != config.getSortType() && null != config.getSortType().getComparator()) {
            Arrays.sort(tmpArr, config.getSortType().getComparator());
        }
        return tmpArr;
    }

    class SortRunnable implements Runnable {

        @Override
        public void run() {
            while (running) {
                int sleep = 100;
                // 如果没有数据 或 还有线程在处理，则继续等待
                if (autoLoadMap.isEmpty() || autoLoadQueue.size() > 0) {
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                    continue;
                } else if (autoLoadMap.size() <= threads.length * 10) {
                    sleep = 1000;
                } else if (autoLoadMap.size() <= threads.length * 50) {
                    sleep = 300;
                }
                try {
                    Thread.sleep(sleep);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }

                AutoLoadTO[] tmpArr = getAutoLoadQueue();
                if (null == tmpArr || tmpArr.length == 0) {
                    continue;
                }
                for (int i = 0; i < tmpArr.length; i++) {
                    try {
                        AutoLoadTO to = tmpArr[i];
                        autoLoadQueue.put(to);
                        if (i > 0 && i % 1000 == 0) {
                            // Thread.sleep(0);// 触发操作系统立刻重新进行一次CPU竞争,
                            // 让其它线程获得CPU控制权的权力。
                            Thread.yield();
                        }
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    class AutoLoadRunnable implements Runnable {

        @Override
        public void run() {
            while (running) {
                try {
                    AutoLoadTO tmpTO = autoLoadQueue.take();
                    if (null != tmpTO) {
                        loadCache(tmpTO);
                        Thread.sleep(config.getAutoLoadPeriod());
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

//        private void loadCache(AutoLoadTO autoLoadTO) {
//            if (null == autoLoadTO) {
//                return;
//            }
//            long now = System.currentTimeMillis();
//            if (autoLoadTO.getLastRequestTime() <= 0 || autoLoadTO.getLastLoadTime() <= 0) {
//                return;
//            }
//            Cache cache = autoLoadTO.getCache();
//            long requestTimeout = cache.requestTimeout();
//            // 如果超过一定时间没有请求数据，则从队列中删除
//            if (requestTimeout > 0 && (now - autoLoadTO.getLastRequestTime()) >= requestTimeout * ONE_THOUSAND_MS) {
//                autoLoadMap.remove(autoLoadTO.getCacheKey());
//                return;
//            }
//            // 如果效率比较高的请求，就没必要使用自动加载了。
//            if (autoLoadTO.getLoadCnt() > 100 && autoLoadTO.getAverageUseTime() < 10) {
//                autoLoadMap.remove(autoLoadTO.getCacheKey());
//                return;
//            }
//            // 对于使用频率很低的数据，也可以考虑不用自动加载
//            long difFirstRequestTime = now - autoLoadTO.getFirstRequestTime();
//            long oneHourSecs = 3600000L;
//            // 使用率比较低的数据，没有必要使用自动加载。
//            if (difFirstRequestTime > oneHourSecs && autoLoadTO.getAverageUseTime() < ONE_THOUSAND_MS
//                    && (autoLoadTO.getRequestTimes() / (difFirstRequestTime / oneHourSecs)) < 60) {
//                autoLoadMap.remove(autoLoadTO.getCacheKey());
//                return;
//            }
//            if (autoLoadTO.isLoading()) {
//                return;
//            }
//            int expire = autoLoadTO.getExpire();
//            // 如果过期时间太小了，就不允许自动加载，避免加载过于频繁，影响系统稳定性
//            if (expire < AUTO_LOAD_MIN_EXPIRE) {
//                return;
//            }
//            // 计算超时时间
//            int alarmTime = autoLoadTO.getCache().alarmTime();
//            long timeout;
//            if (alarmTime > 0 && alarmTime < expire) {
//                timeout = expire - alarmTime;
//            } else {
//                if (expire >= 600) {
//                    timeout = expire - 120;
//                } else {
//                    timeout = expire - 60;
//                }
//            }
//            int rand = ThreadLocalRandom.current().nextInt(10);
//            timeout = (timeout + (rand % 2 == 0 ? rand : -rand)) * ONE_THOUSAND_MS;
//            if ((now - autoLoadTO.getLastLoadTime()) < timeout) {
//                return;
//            }
//            CacheWrapper<Object> result = null;
//            if (config.isCheckFromCacheBeforeLoad()) {
//                try {
//                    Method method = autoLoadTO.getJoinPoint().getMethod();
//                    // Type returnType=method.getGenericReturnType();
//                    result = cacheHandler.get(autoLoadTO.getCacheKey(), method, autoLoadTO.getArgs());
//                } catch (Exception ex) {
//                    log.error(ex.getMessage(), ex);
//                }
//                // 如果已经被别的服务器更新了，则不需要再次更新
//                if (null != result) {
//                    autoLoadTO.setExpire(result.getExpire());
//                    if (result.getLastLoadTime() > autoLoadTO.getLastLoadTime()
//                            && (now - result.getLastLoadTime()) < timeout) {
//                        autoLoadTO.setLastLoadTime(result.getLastLoadTime());
//                        return;
//                    }
//                }
//            }
//            CacheAopProxyChain pjp = autoLoadTO.getJoinPoint();
//            CacheKeyTO cacheKey = autoLoadTO.getCacheKey();
//            DataLoaderFactory factory = DataLoaderFactory.getInstance();
//            DataLoader dataLoader = factory.getDataLoader();
//            CacheWrapper<Object> newCacheWrapper = null;
//            boolean isFirst = false;
//            long loadDataUseTime = 0L;
//            try {
//                newCacheWrapper = dataLoader.init(pjp, autoLoadTO, cacheKey, cache, cacheHandler).loadData()
//                        .getCacheWrapper();
//                isFirst = dataLoader.isFirst();
//                loadDataUseTime = dataLoader.getLoadDataUseTime();
//            } catch (Throwable e) {
//                log.error(e.getMessage(), e);
//            } finally {
//                factory.returnObject(dataLoader);
//            }
//            if (isFirst) {
//                // 如果数据加载失败，则把旧数据进行续租
//                if (null == newCacheWrapper && null != result) {
//                    int newExpire = AUTO_LOAD_MIN_EXPIRE + 60;
//                    newCacheWrapper = new CacheWrapper<Object>(result.getCacheObject(), newExpire);
//                }
//                try {
//                    if (null != newCacheWrapper) {
//                        cacheHandler.writeCache(pjp, autoLoadTO.getArgs(), cache, cacheKey, newCacheWrapper);
//                        autoLoadTO.setLastLoadTime(newCacheWrapper.getLastLoadTime())
//                                .setExpire(newCacheWrapper.getExpire()).addUseTotalTime(loadDataUseTime);
//                    }
//                } catch (Exception e) {
//                    log.error(e.getMessage(), e);
//                }
//            }
//        }
        
        /**
         * 针对当前项目的情况
         * 
         * 由于离线玩家的快照如果被获取过,则向redis中续期
         * @param autoLoadTO
         */
        private void loadCache(AutoLoadTO autoLoadTO) {
            if (null == autoLoadTO) {
                return;
            }
            long now = System.currentTimeMillis();
            if (autoLoadTO.getLastRequestTime() <= 0 || autoLoadTO.getLastLoadTime() <= 0) {
                return;
            }
            Cache cache = autoLoadTO.getCache();
            long requestTimeout = cache.requestTimeout();

            // 如果过期时间太小了，就不允许自动加载，避免加载过于频繁，影响系统稳定性
            if (autoLoadTO.getExpire() < AUTO_LOAD_MIN_EXPIRE) {
                autoLoadMap.remove(autoLoadTO.getCacheKey());
                return;
            }

            // 如果超过一定时间没有请求数据，则从队列中删除
            // 最近XX时间内没有被使用过,则移除
            if (requestTimeout > 0 && (now - autoLoadTO.getLastRequestTime()) >= requestTimeout
                    * ONE_THOUSAND_MS) {
                log.debug("autoload remove. time:" + (now - autoLoadTO.getLastRequestTime()));
                autoLoadMap.remove(autoLoadTO.getCacheKey());
                return;
            }
            // // 如果效率比较高的请求，就没必要使用自动加载了。
            // if (autoLoadTO.getLoadCnt() > 100 && autoLoadTO.getAverageUseTime() < 10) {
            // autoLoadMap.remove(autoLoadTO.getCacheKey());
            // return;
            // }

            // 对于使用频率很低的数据，也可以考虑不用自动加载
            long difFirstRequestTime = now - autoLoadTO.getFirstRequestTime();
            long oneHourSecs = 3600000L;
            // 使用率比较低的数据，没有必要使用自动加载。
            // if (difFirstRequestTime > oneHourSecs && autoLoadTO.getAverageUseTime() <
            // ONE_THOUSAND_MS
            // && (autoLoadTO.getRequestTimes() / (difFirstRequestTime / oneHourSecs)) < 60) {
            // autoLoadMap.remove(autoLoadTO.getCacheKey());
            // return;
            // }

            // 存在时间超过10分钟,并且使用率过低,低于每小时调用10次.(假设服务器数量为50,即为该数据全服每小时取500次)
            double rate = (double) (autoLoadTO.getRequestTimes()
                    / (double) (difFirstRequestTime / oneHourSecs));
            if (difFirstRequestTime > 600 * ONE_THOUSAND_MS && rate < cachePeerHitRate) {
                log.debug("autoload remove. rate:" + rate);
                autoLoadMap.remove(autoLoadTO.getCacheKey());
                return;
            }

            if (autoLoadTO.isLoading()) {
                return;
            }

            // 最近10分钟内没有被使用过
            if ((now - autoLoadTO.getLastRequestTime()) >= 600 * ONE_THOUSAND_MS) {
                return;
            }

            // // 计算超时时间
            // int alarmTime = autoLoadTO.getCache().alarmTime();
            // long timeout;
            // if (alarmTime > 0 && alarmTime < expire) {
            // timeout = expire - alarmTime;
            // } else {
            // if (expire >= 600) {
            // timeout = expire - 120;
            // } else {
            // timeout = expire - 60;
            // }
            // }
            // //避免在同一时间,同一秒过期都去进行cache,data中竞争
            // int rand = ThreadLocalRandom.current().nextInt(60);
            // timeout = (timeout + (rand % 2 == 0 ? rand : -rand)) * ONE_THOUSAND_MS;
            // // 当前时间是否在接近过期的时间范围内
            // if ((now - autoLoadTO.getLastLoadTime()) < timeout) {
            // return;
            // }

            // 计算超时时间
            int alarmTime = autoLoadTO.getCache().alarmTime();
            // 避免在同一时间,同一秒过期都去进行cache,data中竞争
            int offset = 60;
            int rand = 0;
            if (alarmTime >= offset) {
                rand = ThreadLocalRandom.current().nextInt(offset) * ONE_THOUSAND_MS;
            }
            long exAt = autoLoadTO.getExpireAt();
            long leftTime = exAt - now;
            leftTime = (leftTime + (rand % 2 == 0 ? rand : -rand));

            if (leftTime >= 0) {
                if (alarmTime < 0) {
                    if (autoLoadTO.getExpire() >= 600) {
                        alarmTime = 120;
                    } else {
                        alarmTime = 60;
                    }
                }

                if (leftTime > alarmTime * ONE_THOUSAND_MS) {
                    return;
                }
            }

            CacheWrapper<Object> result = null;
            // 检查当前cache中的数据是否已经更新
            if (config.isCheckFromCacheBeforeLoad()) {
                try {
                    Method method = autoLoadTO.getJoinPoint().getMethod();
                    result = cacheHandler.get(autoLoadTO.getCacheKey(), method,
                            autoLoadTO.getTarget(),
                            autoLoadTO.getArgs());
                } catch (Exception ex) {
                    log.error(ex.getMessage(), ex);
                }

                // 缓存中已不存在当前数据,不应该过期的数据已经被清空不存在了,则跳过.
                // 如果缓存不到失效的时间就已经失效了,则说明内存紧张.
                if (result == null) {
                    log.warn("can not find obj in cache,cacheKey:"
                            + autoLoadTO.getCacheKey().getKey());
                    autoLoadMap.remove(autoLoadTO.getCacheKey());
                    return;
                }

                // 如果已经被别的服务器更新了，则不需要再次更新
                if (null != result) {
                    if (result.getCacheObject() == null) {
                        autoLoadMap.remove(autoLoadTO.getCacheKey());
                        return;
                    }

                    // 远程数据比本地数据老
                    if (result.getLastLoadTime() < autoLoadTO.getLastLoadTime()
                            || result.getExpireAt() < autoLoadTO.getExpireAt()) {
                        log.warn("cache obj is behind local,cacheKey:"
                                + autoLoadTO.getCacheKey().getKey());
                        autoLoadMap.remove(autoLoadTO.getCacheKey());
                        return;
                    }

                    // 远程数据>=本地数据
                    autoLoadTO.setExpire(result.getExpire());
                    autoLoadTO.setLastLoadTime(result.getLastLoadTime());
                    autoLoadTO.setExpireAt(result.getExpireAt());

                    // 远程数据比本地数据新
                    if (result.getExpireAt() > autoLoadTO.getExpireAt()) {
                        // 远程数据还未到过期时间
                        if (now - result.getExpireAt() > alarmTime) {
                            return;
                        }
                    }

                    if (dbLoadHandler != null) {
                        if (!dbLoadHandler.isDBLoad(result.getCacheObject())) {
                            // 该类数据不能进行续期
                            if (log.isDebugEnabled()) {
                                log.debug("autoload remove. online player, id:"
                                        + autoLoadTO.getCacheKey().getKey() + ",autoLoadTOId:"
                                        + autoLoadTO.hashCode());
                            }
                            autoLoadMap.remove(autoLoadTO.getCacheKey());
                            return;
                        }
                    }

                    CacheAopProxyChain pjp = autoLoadTO.getJoinPoint();
                    CacheKeyTO cacheKey = autoLoadTO.getCacheKey();
                    CacheWrapper<Object> newCacheWrapper = null;
                    try {
                        // 续时
                        int newExpire = autoLoadTO.getExpire();
                        newCacheWrapper =
                                new CacheWrapper<Object>(result.getCacheObject(), newExpire);
                        if (null != newCacheWrapper) {
                            cacheHandler.expireCache(pjp, autoLoadTO.getArgs(), cache, cacheKey,
                                    newCacheWrapper);
                        }
                    } catch (Throwable e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

}
