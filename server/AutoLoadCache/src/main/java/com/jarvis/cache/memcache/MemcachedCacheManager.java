package com.jarvis.cache.memcache;

import java.lang.reflect.Method;

import com.jarvis.cache.ICacheManager;
import com.jarvis.cache.exception.CacheCenterConnectionException;
import com.jarvis.cache.to.CacheKeyTO;
import com.jarvis.cache.to.CacheWrapper;

import net.spy.memcached.MemcachedClient;

/**
 * memcache缓存管理
 * 
 * @author: jiayu.qiu
 */
public class MemcachedCacheManager implements ICacheManager {

    private MemcachedClient memcachedClient;

    public MemcachedCacheManager() {
    }

    @Override
    public void setCache(final CacheKeyTO cacheKeyTO, final CacheWrapper<Object> result,
            final Method method, final Object target,
            final Object args[]) throws CacheCenterConnectionException {
        if (null == cacheKeyTO) {
            return;
        }
        String cacheKey = cacheKeyTO.getCacheKey();
        if (null == cacheKey || cacheKey.length() == 0) {
            return;
        }
        boolean hfield = cacheKeyTO.getHfield();
        if (hfield) {
            throw new RuntimeException("memcached does not support hash cache.");
        }
        if (result.getExpire() >= 0) {
            memcachedClient.set(cacheKey, result.getExpire(), result);
        }
    }

    @Override
    public CacheWrapper<Object> get(final CacheKeyTO cacheKeyTO, Method method, final Object target,
            final Object args[])
            throws CacheCenterConnectionException {
        if (null == cacheKeyTO) {
            return null;
        }
        String cacheKey = cacheKeyTO.getCacheKey();
        if (null == cacheKey || cacheKey.length() == 0) {
            return null;
        }
        boolean hfield = cacheKeyTO.getHfield();
        if (hfield) {
            throw new RuntimeException("memcached does not support hash cache.");
        }
        return (CacheWrapper<Object>) memcachedClient.get(cacheKey);
    }

    /**
     * 通过组成Key直接删除
     * 
     * @param cacheKeyTO 缓存Key
     */
    @Override
    public void delete(CacheKeyTO cacheKeyTO) throws CacheCenterConnectionException {
        if (null == memcachedClient || null == cacheKeyTO) {
            return;
        }
        String cacheKey = cacheKeyTO.getCacheKey();
        if (null == cacheKey || cacheKey.length() == 0) {
            return;
        }
        boolean hfield = cacheKeyTO.getHfield();
        if (hfield) {
            throw new RuntimeException("memcached does not support hash cache.");
        }
        try {
            String allKeysPattern = "*";
            if (allKeysPattern.equals(cacheKey)) {
                memcachedClient.flush();
            } else {
                memcachedClient.delete(cacheKey);
            }
        } catch (Exception e) {
        }
    }

    public MemcachedClient getMemcachedClient() {
        return memcachedClient;
    }

    public void setMemcachedClient(MemcachedClient memcachedClient) {
        this.memcachedClient = memcachedClient;
    }

    @Override
    public void expireCache(CacheKeyTO cacheKey, CacheWrapper<Object> result, Method method,
            Object target, Object[] args) throws CacheCenterConnectionException {
        setCache(cacheKey, result, method, target, args);
    }

}
