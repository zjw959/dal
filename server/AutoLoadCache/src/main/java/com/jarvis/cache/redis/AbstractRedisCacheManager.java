package com.jarvis.cache.redis;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jarvis.cache.ICacheManager;
import com.jarvis.cache.exception.CacheCenterConnectionException;
import com.jarvis.cache.serializer.ISerializer;
import com.jarvis.cache.serializer.StringSerializer;
import com.jarvis.cache.to.CacheKeyTO;
import com.jarvis.cache.to.CacheWrapper;

/**
 * @author: jiayu.qiu
 */
public abstract class AbstractRedisCacheManager implements ICacheManager {
    private static Logger log = LoggerFactory.getLogger(AbstractRedisCacheManager.class);


    private static final StringSerializer KEY_SERIALIZER = new StringSerializer();


    /**
     * Hash的缓存时长：等于0时永久缓存；大于0时，主要是为了防止一些已经不用的缓存占用内存;hashExpire小于0时，则使用@Cache中设置的expire值（默认值为-1）。
     */
    private int hashExpire = -1;

    private final ISerializer<Object> serializer;

    public AbstractRedisCacheManager(ISerializer<Object> serializer) {
        this.serializer = serializer;
    }

    protected abstract IRedis getRedis(int jedisIndex, String cacheKey);

    @Override
    public void setCache(final CacheKeyTO cacheKeyTO, final CacheWrapper<Object> result, final Method method,
            final Object target, final Object args[]) throws CacheCenterConnectionException {
        if (null == cacheKeyTO) {
            return;
        }
        String cacheKey = cacheKeyTO.getCacheKey();
        if (null == cacheKey || cacheKey.length() == 0) {
            return;
        }
        int jedisIndex = cacheKeyTO.getJedisIndex();
        try (IRedis redis = getRedis(jedisIndex, cacheKey)) {
            int expire = result.getExpire();
            boolean hfield = cacheKeyTO.getHfield();
            if (!hfield) {
                if (expire == 0) {
                    redis.set(KEY_SERIALIZER.serialize(cacheKey), serializer.serialize(result));
                } else if (expire > 0) {
                    // 过期时间设置失败，重新序列化后保存到redis
                    redis.setex(KEY_SERIALIZER.serialize(cacheKey), expire,
                            serializer.serialize(result));
                }
            } else {
                hashSet(redis, cacheKey, result);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }
    
    @Override
    public void expireCache(final CacheKeyTO cacheKeyTO, final CacheWrapper<Object> result, final Method method,
            final Object target, final Object args[]) throws CacheCenterConnectionException {
        if (null == cacheKeyTO) {
            return;
        }
        String cacheKey = cacheKeyTO.getCacheKey();
        if (null == cacheKey || cacheKey.length() == 0) {
            return;
        }
        int expire = result.getExpire();
        if (expire <= 0) {
            return;
        }
        int jedisIndex = cacheKeyTO.getJedisIndex();
        try (IRedis redis = getRedis(jedisIndex, cacheKey)) {
            // 设置过期时间
            redis.expire(KEY_SERIALIZER.serialize(cacheKey), expire);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private void hashSet(IRedis redis, String cacheKey, CacheWrapper<Object> result)
            throws Exception {
        // byte[] key = KEY_SERIALIZER.serialize(cacheKey);
        // byte[] val = serializer.serialize(result);
        
        Map<String, String> mapCache = result.toMap();
        
        int hExpire;
        if (hashExpire < 0) {
            hExpire = result.getExpire();
        } else {
            hExpire = hashExpire;
        }
        if (hExpire == 0) {
            redis.hmset(cacheKey, mapCache);
        } else if (hExpire > 0) {
            throw new UnsupportedOperationException();
            // redis.hset(key, field, val, hExpire);
        }
    }

    @Override
    public CacheWrapper<Object> get(final CacheKeyTO cacheKeyTO, final Method method,
            final Object target, final Object args[])
            throws CacheCenterConnectionException {
        if (null == cacheKeyTO) {
            return null;
        }
        String cacheKey = cacheKeyTO.getCacheKey();
        if (null == cacheKey || cacheKey.length() == 0) {
            return null;
        }
        CacheWrapper<Object> res = null;
        int jedisIndex = cacheKeyTO.getJedisIndex();
        try (IRedis redis = getRedis(jedisIndex, cacheKey)) {
            byte bytes[] = null;
            boolean hfield = cacheKeyTO.getHfield();
            if (!hfield) {
                bytes = redis.get(KEY_SERIALIZER.serialize(cacheKey));

                Type returnType = null;
                if (null != method) {
                    returnType = method.getGenericReturnType();
                }
                res = (CacheWrapper<Object>) serializer.deserialize(bytes, returnType);
            } else {
                // bytes = redis.hget(KEY_SERIALIZER.serialize(cacheKey),
                // KEY_SERIALIZER.serialize(hfield));
                Map<String, String> map = redis.hgetAll(cacheKey);
                // res = new CacheWrapper<>();
                //
                // res.setExpire(Integer.valueOf(map.get("expire")));
                // res.setExpireAt(Long.valueOf(map.get("expireAt")));
                // res.setLastLoadTime(Long.valueOf(map.get("lastLoadTime")));
                //
                // map.remove("expire");
                // map.remove("expireAt");
                // map.remove("lastLoadTime");
                // res.setCacheObject(map);

                Type returnType = null;
                if (null != method) {
                    returnType = method.getGenericReturnType();
                }

                res = new CacheWrapper<>(map, returnType);
                // res = (CacheWrapper<Object>) serializer.hashDeserialize(map, returnType);
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return res;
    }

    /**
     * 根据缓存Key删除缓存
     * 
     * @param cacheKeyTO 缓存Key
     */
    @Override
    public void delete(CacheKeyTO cacheKeyTO) throws CacheCenterConnectionException {
        if (null == cacheKeyTO) {
            return;
        }
        String cacheKey = cacheKeyTO.getCacheKey();
        if (null == cacheKey || cacheKey.length() == 0) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug("delete cache {}", cacheKey);
        }
        int jedisIndex = cacheKeyTO.getJedisIndex();
        try (IRedis redis = getRedis(jedisIndex, cacheKey)) {
            boolean hfield = cacheKeyTO.getHfield();
            if (!hfield) {
                redis.del(KEY_SERIALIZER.serialize(cacheKey));
            } else {
                // redis.hdel(KEY_SERIALIZER.serialize(cacheKey), KEY_SERIALIZER.serialize(hfield));
                throw new UnsupportedOperationException("ca not del hashtype key");
            }

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public int getHashExpire() {
        return hashExpire;
    }

    public void setHashExpire(int hashExpire) {
        if (hashExpire < 0) {
            return;
        }
        this.hashExpire = hashExpire;
    }
}
