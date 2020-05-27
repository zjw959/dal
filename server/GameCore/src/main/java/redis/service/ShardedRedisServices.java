package redis.service;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.base.RedisOper;
import redis.base.ShardedRedisOper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import utils.SpringContextUtils;

public class ShardedRedisServices extends RedisServices {

    private static final Logger LOGGER = Logger.getLogger(ShardedRedisServices.class);


    private ShardedJedisPool shardedJedisPool;



    private void _init(int redisType, String poolName, int springType, String springPath)
            throws Exception {
        if (redisServices.containsKey(redisType)) {
            throw new Exception("springIndex has bean exist. path:" + springPath);
        }

        if (SpringContextUtils.getApplicationContext(springType) == null) {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(springPath);
            context.setDisplayName(String.valueOf(springType));
            SpringContextUtils.getInstance().setApplicationContext(context);
        }

        this.redisType = redisType;
        this.shardedJedisPool =
                (ShardedJedisPool) SpringContextUtils.getBeanByName(springType, poolName);

        try (ShardedJedis shardedJedis = shardedJedisPool.getResource()) {
            Collection<Jedis> jedis = shardedJedis.getAllShards();
            for (Jedis _jedis : jedis) {
                LOGGER.info("redis info. host:" + _jedis.getClient().getHost() + ",port:"
                        + _jedis.getClient().getPort());
            }
        } catch (Exception e) {
            throw e;
        }

        this.redisOper = new ShardedRedisOper(this.shardedJedisPool);

        redisServices.put(this.redisType, this);
    }

    public ShardedRedisServices(int redisType, String poolName, int springIndex, String springPath)
            throws Exception {
        super();
        _init(redisType, poolName, springIndex, springPath);
    }

    public ShardedJedis getShardedJedis() {
        return shardedJedisPool.getResource();
    }

    public RedisOper getRedisOper() {
        return this.redisOper;
    }


    public void returnJedis() {
        shardedJedisPool.close();
    }

    public static RedisServices getRedisService(int redisIndex) {
        return redisServices.get(redisIndex);
    }
}
