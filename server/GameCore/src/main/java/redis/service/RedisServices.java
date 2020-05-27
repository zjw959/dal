package redis.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.base.RedisOper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import utils.SpringContextUtils;

public class RedisServices {
    private static final Logger LOGGER = Logger.getLogger(RedisServices.class);

    private JedisPool jedisPool;

    protected RedisOper redisOper;

    protected int redisType;

    protected static Map<Integer, RedisServices> redisServices = new HashMap<>();

    public RedisServices() {

    }

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

        this.jedisPool = (JedisPool) SpringContextUtils.getBeanByName(springType, poolName);

        try (Jedis _jedis = jedisPool.getResource()) {
            LOGGER.info("redis info. host:" + _jedis.getClient().getHost() + ",port:"
                    + _jedis.getClient().getPort());
        } catch (Exception e) {
            throw e;
        }

        this.redisOper = new RedisOper(this.jedisPool);

        redisServices.put(this.redisType, this);
    }


    public RedisServices(int redisType, String poolName, int springIndex, String springPath)
            throws Exception {
        _init(redisType, poolName, springIndex, springPath);
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    public RedisOper getRedisOper() {
        return this.redisOper;
    }


    public void returnJedis() {
        jedisPool.close();
    }

    public static RedisServices getRedisService(int redisIndex) {
        return redisServices.get(redisIndex);
    }


}
