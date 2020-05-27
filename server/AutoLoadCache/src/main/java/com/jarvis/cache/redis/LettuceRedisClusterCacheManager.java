package com.jarvis.cache.redis;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jarvis.cache.serializer.ISerializer;

import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.codec.ByteArrayCodec;

public class LettuceRedisClusterCacheManager extends AbstractRedisCacheManager {
    private static Logger log = LoggerFactory.getLogger(LettuceRedisClusterCacheManager.class);


    private final RedisClusterClient[] redisClusterClients;

    private final ByteArrayCodec byteArrayCodec = new ByteArrayCodec();

    public LettuceRedisClusterCacheManager(RedisClusterClient redisClusterClient, ISerializer<Object> serializer) {
        super(serializer);
        this.redisClusterClients = new RedisClusterClient[] {redisClusterClient};
    }

    @Override
    protected IRedis getRedis(int jedisIndex, String cacheKey) {
        StatefulRedisClusterConnection<byte[], byte[]> connection =
                redisClusterClients[jedisIndex - 1].connect(byteArrayCodec);
        return new LettuceRedisClusterClient(connection);
    }

    public static class LettuceRedisClusterClient implements IRedis {

        private final StatefulRedisClusterConnection<byte[], byte[]> connection;

        public LettuceRedisClusterClient(StatefulRedisClusterConnection<byte[], byte[]> connection) {
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
