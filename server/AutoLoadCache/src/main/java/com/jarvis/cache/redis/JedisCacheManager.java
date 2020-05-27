package com.jarvis.cache.redis;

import java.io.IOException;
import java.util.Map;

import com.jarvis.cache.serializer.ISerializer;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Pool;

/**
 * Redis缓存管理
 * 
 * @author jiayu.qiu
 */
public class JedisCacheManager extends AbstractRedisCacheManager {

    private Pool[] jedisPools;


    public JedisCacheManager(Pool jedisPool, ISerializer<Object> serializer) {
        super(serializer);
        this.jedisPools = new Pool[] {jedisPool};
    }

    public JedisCacheManager(Pool jedisPool,
            Pool jedisPool2,
            ISerializer<Object> serializer) {
        super(serializer);
        this.jedisPools = new Pool[] {jedisPool, jedisPool2};
    }

    @Override
    protected IRedis getRedis(int jedisIndex, String cacheKey) {
        Pool _pool = jedisPools[jedisIndex - 1];
        if (_pool instanceof JedisPool) {
            Jedis jedis = ((JedisPool) _pool).getResource();
            return new JedisClient(jedis, cacheKey);
        } else {
            ShardedJedis shardedJedis = ((ShardedJedisPool) _pool).getResource();
            return new ShardedJedisClient(shardedJedis, cacheKey);
        }
    }

    
    public static class JedisClient implements IRedis {
        private final Jedis jedis;

        public JedisClient(Jedis jedis, String cacheKey) {
            this.jedis = jedis;
        }

        @Override
        public void close() throws IOException {
            jedis.close();
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
