package com.jarvis.cache.to;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 对缓存数据进行封装
 * 
 * @author jiayu.qiu
 */
public class CacheWrapper<T> implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    /**
     * 缓存数据
     */
    private T cacheObject;

    /**
     * 最后加载时间
     */
    private long lastLoadTime;

    /**
     * 缓存时长(缓存中的单位时长,可续期)
     */
    private int expire;

    /**
     * 缓存到期时间
     */
    private long expireAt;

    public CacheWrapper() {}


    public CacheWrapper(T cacheObject, int expire) {
        this.cacheObject = cacheObject;
        this.lastLoadTime = System.currentTimeMillis();
        this.expire = expire;
        this.expireAt = this.lastLoadTime + expire * 1000;
    }

    public CacheWrapper(Map<String, String> map, Type returnType) throws Exception {
        try {
            this.lastLoadTime = Long.valueOf(map.get("lastLoadTime"));
            this.expire = Integer.valueOf(map.get("expire"));
            this.expireAt = Long.valueOf(map.get("expireAt"));

            map.remove("expire");
            map.remove("expireAt");
            map.remove("lastLoadTime");

            String className = returnType.getTypeName();
            if (className == null || className.isEmpty()) {
                throw new Exception("classname is empty");
            }

            Class<T> class1 = (Class<T>) Class.forName(className);
            Constructor<?> constructor = class1.getConstructor(map.getClass());
            this.cacheObject = (T) constructor.newInstance(map);

        } catch (Exception e) {
            throw e;
        }

        // this.cacheObject = (T) map;
    }

    /**
     * 判断缓存是否已经过期
     * 
     * @return boolean
     */
    public boolean hasExpired() {
        if (expire > 0) {
            // long _time = System.currentTimeMillis() - lastLoadTime;
            // return _time > expire * 1000;

            return System.currentTimeMillis() >= expireAt;
        }
        return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        CacheWrapper<T> tmp = (CacheWrapper<T>) super.clone();
        tmp.setCacheObject(this.cacheObject);
        return tmp;
    }

    public void setCacheObject(T cacheObject) {
        this.cacheObject = cacheObject;
    }

    public T getCacheObject() {
        return this.cacheObject;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public long getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(long expireAt) {
        this.expireAt = expireAt;
    }

    public long getLastLoadTime() {
        return lastLoadTime;
    }

    public void setLastLoadTime(long lastLoadTime) {
        this.lastLoadTime = lastLoadTime;
    }

    public Map<String, String> toMap() throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        map.put("expire", String.valueOf(this.expire));
        map.put("expireAt", String.valueOf(this.expireAt));
        map.put("lastLoadTime", String.valueOf(this.lastLoadTime));

        if (cacheObject instanceof HashRedisObject) {
            HashRedisObject hashObject = (HashRedisObject) cacheObject;
            map.putAll(hashObject.toHash());
        } else {
            throw new Exception("hash object is not hash obj");
        }

        return map;
    }
}
