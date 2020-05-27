package com.jarvis.cache.redis;

import java.io.Closeable;
import java.util.Map;

/**
 * Redis缓存操作
 * 
 * @author: jiayu.qiu
 */
public interface IRedis extends Closeable {

    void set(final byte[] key, final byte[] value);

    void setex(final byte[] key, final int seconds, final byte[] value);

    void hset(byte[] key, byte[] field, byte[] value);

    void hset(byte[] key, byte[] field, byte[] value, int seconds);

    byte[] get(byte[] key);

    byte[] hget(final byte[] key, final byte[] field);

    void del(final byte[] key);

    void hdel(final byte[] key, final byte[]... fields);

    Long ttl(byte[] key);
    
    Long expire(final byte[] key, final int seconds);

    default void hmset(byte[] key, Map<byte[], byte[]> hash) {
        throw new UnsupportedOperationException();
    }

    default void hmset(String key, Map<String, String> hash) {
        throw new UnsupportedOperationException();
    }

    default Map<String, String> hgetAll(String key) {
        throw new UnsupportedOperationException();
    }
}
