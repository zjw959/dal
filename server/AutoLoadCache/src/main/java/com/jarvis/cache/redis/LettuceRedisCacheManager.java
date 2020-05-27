package com.jarvis.cache.redis;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jarvis.cache.serializer.ISerializer;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;

public class LettuceRedisCacheManager extends AbstractRedisCacheManager {
    private static Logger log = LoggerFactory.getLogger(LettuceRedisCacheManager.class);


    private final RedisClient[] redisClients;

    private final ByteArrayCodec byteArrayCodec = new ByteArrayCodec();

    public LettuceRedisCacheManager(RedisClient redisClient, ISerializer<Object> serializer) {
        super(serializer);
        this.redisClients = new RedisClient[] {redisClient};
    }

    @Override
    protected IRedis getRedis(int jedisIndex, String cacheKey) {
        StatefulRedisConnection<byte[], byte[]> connection =
                redisClients[jedisIndex - 1].connect(byteArrayCodec);
        return new LettuceRedisClient(connection);
    }

    public static class LettuceRedisClient implements IRedis {

        private final StatefulRedisConnection<byte[], byte[]> connection;

        public LettuceRedisClient(StatefulRedisConnection<byte[], byte[]> connection) {
            this.connection = connection;
        }

        @Override
        public void close() throws IOException {
            this.connection.close();
        }

        @Override
        public void set(byte[] key, byte[] value) {
            connection.async().set(key, value);
        }

        @Override
        public void setex(byte[] key, int seconds, byte[] value) {
            connection.async().setex(key, seconds, value);
        }

        @Override
        public void hset(byte[] key, byte[] field, byte[] value) {
            connection.async().hset(key, field, value);
        }

        @Override
        public void hset(byte[] key, byte[] field, byte[] value, int seconds) {
            connection.async().hset(key, field, value).whenComplete((res, throwable) -> {
                if (res) {
                    connection.async().expire(key, seconds);
                }
            });

        }

        @Override
        public byte[] get(byte[] key) {
            try {
                return connection.async().get(key).get();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }

        @Override
        public byte[] hget(byte[] key, byte[] field) {
            try {
                return connection.async().hget(key, field).get();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }

        @Override
        public void del(byte[] key) {
            connection.async().del(key);
        }

        @Override
        public void hdel(byte[] key, byte[]... fields) {
            connection.async().hdel(key, fields);
        }
        
        @Override
        public Long ttl(byte[] key) {
            try {
                return connection.async().ttl(key).get();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            } catch (ExecutionException e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }

        @Override
        public Long expire(byte[] key, int seconds) {
            try {
                return connection.async().expire(key, seconds).get() ? 1L : -1L;
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            } catch (ExecutionException e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }
    }

}
