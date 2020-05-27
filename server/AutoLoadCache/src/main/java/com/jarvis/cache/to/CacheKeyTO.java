package com.jarvis.cache.to;

import java.io.Serializable;


/**
 * 缓存Key
 * 
 * @author jiayu.qiu
 */
public final class CacheKeyTO implements Serializable {

    private static final long serialVersionUID = 7229320497442357252L;

    private final String namespace;

    private final String key;// 缓存Key

    /**
     * 是否是hash结构
     */
    private final boolean hfield;

    /**
     * 使用指定的jedis
     */
    private int jedisIndex;

    public CacheKeyTO(String namespace, String key, boolean hfield, int jedisIndex) {
        this.namespace = namespace;
        this.key = key;
        this.hfield = hfield;
        this.jedisIndex = jedisIndex;
    }

    public String getCacheKey() {
        if (null != this.namespace && this.namespace.length() > 0) {
            return new StringBuilder(this.namespace).append(":").append(this.key).toString();
        }
        return this.key;
    }

    public String getLockKey() {
        StringBuilder key = new StringBuilder(getCacheKey());
        // if (null != hfield && hfield.length() > 0) {
        // key.append(":").append(hfield);
        // }
        key.append(":lock");
        return key.toString();
    }

    // public String getHfield() {
    // return hfield;
    // }

    public boolean getHfield() {
        return hfield;
    }

    public String getKey() {
        return key;
    }

    public int getJedisIndex() {
        return jedisIndex;
    }
}
