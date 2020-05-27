package redis;

import java.util.Properties;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import server.FightServer;

public class RedisPropertyConfigurer extends AbsRedisPropertyConfigurer {
    @Override
    protected void _init(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) {
        // IDE 模式下默认使用同一个redis.
        if (FightServer.getInstance().isIDEMode()) {
            // IDE 模式下默认使用同一个redis.
            props.setProperty("redis_fight.port", "6379");

            String[] args = FightServer.getInstance().getArgs();
            if (args != null && args.length >= 1) {
                String ip = args[0];
                // props.setProperty("redis_view_1.host", ip);
            }
        }
        
        int pNum = Runtime.getRuntime().availableProcessors() + 2;
        props.setProperty("jedis_pool.maxTotal", String.valueOf(pNum * 2));
        props.setProperty("jedis_pool.maxIdle", String.valueOf(pNum));
        props.setProperty("jedis_pool.minIdle", String.valueOf(pNum));
        
        // 拼接URL
        props.setProperty("redis_fight.url",
                _url(props.getProperty("redis_fight.pass"), props.getProperty("redis_fight.host"),
                        props.getProperty("redis_fight.port"),
                        props.getProperty("redis_fight.db")));
    }
}
