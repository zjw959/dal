package com.jarvis.cache.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class JedisLock extends AbstractRedisLock {

    private static final Logger logger = LoggerFactory.getLogger(JedisLock.class);

    private JedisPool jedisPool;

    public JedisLock(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    private void returnResource(Jedis jedis) {
        jedis.close();
    }

    @Override
    protected Long setnx(String key, String val) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.setnx(key, val);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            returnResource(jedis);
        }
        return 0L;
    }

    @Override
    protected void expire(String key, int expire) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.expire(key, expire);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    protected String get(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    protected String getSet(String key, String newVal) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.getSet(key, newVal);
        } finally {
            returnResource(jedis);
        }
    }

    @Override
    protected void del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
        } finally {
            returnResource(jedis);
        }
    }

}
