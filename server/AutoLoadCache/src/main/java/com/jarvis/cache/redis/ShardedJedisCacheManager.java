package com.jarvis.cache.redis;

import java.io.IOException;
import java.util.Map;

import com.jarvis.cache.serializer.ISerializer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Redis缓存管理
 * 
 * @author jiayu.qiu
 */
public class ShardedJedisCacheManager extends AbstractRedisCacheManager {

    private ShardedJedisPool[] shardedJedisPools;

    public ShardedJedisCacheManager(ShardedJedisPool shardedJedisPool, ISerializer<Object> serializer) {
        super(serializer);
        this.shardedJedisPools = new ShardedJedisPool[] {shardedJedisPool};
    }

    public ShardedJedisCacheManager(ShardedJedisPool shardedJedisPool,
            ShardedJedisPool shardedJedisPool2,
            ISerializer<Object> serializer) {
        super(serializer);
        this.shardedJedisPools = new ShardedJedisPool[] {shardedJedisPool, shardedJedisPool2};
    }

    @Override
    protected IRedis getRedis(int jedisIndex, String cacheKey) {
        ShardedJedis shardedJedis = shardedJedisPools[jedisIndex - 1].getResource();
        return new ShardedJedisClient(shardedJedis, cacheKey);
    }

    public static class ShardedJedisClient implements IRedis {
        private final ShardedJedis shardedJedis;
        private final Jedis jedis;

        public ShardedJedisClient(ShardedJedis shardedJedis, String cacheKey) {
            this.shardedJedis = shardedJedis;
            jedis = shardedJedis.getShard(cacheKey);
        }

        @Override
        public void close() throws IOException {
            shardedJedis.close();
        }

        @Override
        public void set(byte[] key, byte[] value) {
            jedis.set(key, value);
        }

        @Override
        public void setex(byte[] key, int seconds, byte[] value) {
            jedis.setex(key, seconds, value);
        }

        @Override
        public void hset(byte[] key, byte[] field, byte[] value) {
            jedis.hset(key, field, value);
        }

        @Override
        public void hmset(byte[] key, Map<byte[], byte[]> hash) {
            jedis.hmset(key, hash);
        }

        @Override
        public void hmset(String key, Map<String, String> hash) {
            jedis.hmset(key, hash);
        }

        @Override
        public void hset(byte[] key, byte[] field, byte[] value, int seconds) {
            Pipeline p = jedis.pipelined();
            p.hset(key, field, value);
            p.expire(key, seconds);
            p.sync();
        }

        @Override
        public byte[] get(byte[] key) {
            return jedis.get(key);
        }

        @Override
        public byte[] hget(byte[] key, byte[] field) {
            return jedis.hget(key, field);
        }

        @Override
        public Map<String, String> hgetAll(String key) {
            return jedis.hgetAll(key);
        }

        @Override
        public void del(byte[] key) {
            jedis.del(key);
        }

        @Override
        public void hdel(byte[] key, byte[]... fields) {
            jedis.hdel(key, fields);
        }

        @Override
        public Long ttl(byte[] key) {
            return jedis.ttl(key);
        }

        @Override
        public Long expire(byte[] key, int seconds) {
            return jedis.expire(key, seconds);
        }
    }

}
