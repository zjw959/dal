package redis;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import server.GameServer;
import thread.player.PlayerProcessorManager;

public class RedisPropertyConfigurer extends AbsRedisPropertyConfigurer {
    private static final Logger LOGGER = Logger.getLogger(AbsRedisPropertyConfigurer.class);

    @Override
    protected void _init(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) {
        if (GameServer.getInstance().isIDEMode()) {
            // IDE 模式下默认使用同一个redis.
            String port = props.getProperty("redis_view.port");
            props.setProperty("redis_friend.port", port);
            props.setProperty("redis_snap.port", port);
            props.setProperty("redis_chatroom.port", port);
            props.setProperty("redis_fight.port", port);
            props.setProperty("redis_level.port", port);
            props.setProperty("redis_activity.port", port);
            props.setProperty("redis_comment.port", port);
            props.setProperty("redis_activity.port", port);

            // 并且 view 数据需要与其他的独立开(ide通过指定数据库的方式隔离)
            props.setProperty("redis_view.db", "1");

            String[] args = GameServer.getInstance().getArgs();
            if (args != null && args.length >= 1) {
                String ip = args[0];
                props.setProperty("redis_view.host", ip);
                props.setProperty("redis_friend.host", ip);
                props.setProperty("redis_snap.host", ip);
                props.setProperty("redis_chatroom.host", ip);
                props.setProperty("redis_fight.host", ip);
                props.setProperty("redis_level.host", ip);
                props.setProperty("redis_comment.host", ip);
                props.setProperty("redis_activity.host", ip);
            }
        }

        int playerProcess = PlayerProcessorManager.getInstance().processorSize();

        props.setProperty("jedis_pool.maxTotal", String.valueOf(playerProcess * 2));
        props.setProperty("jedis_pool.maxIdle", String.valueOf(playerProcess));
        props.setProperty("jedis_pool.minIdle", String.valueOf(playerProcess));

        // 拼接URL
        props.setProperty("redis_view.url",
                _url(props.getProperty("redis_view.pass"), props.getProperty("redis_view.host"),
                        props.getProperty("redis_view.port"), props.getProperty("redis_view.db")));
        props.setProperty("redis_friend.url", _url(props.getProperty("redis_friend.pass"),
                props.getProperty("redis_friend.host"), props.getProperty("redis_friend.port"),
                props.getProperty("redis_friend.db")));
        props.setProperty("redis_snap.url",
                _url(props.getProperty("redis_snap.pass"), props.getProperty("redis_snap.host"),
                        props.getProperty("redis_snap.port"),
                        props.getProperty("redis_snap.db")));
        props.setProperty("redis_activity.url", _url(props.getProperty("redis_activity.pass"),
                props.getProperty("redis_activity.host"), props.getProperty("redis_activity.port"),
                props.getProperty("redis_activity.db")));
        props.setProperty("redis_chatroom.url", _url(props.getProperty("redis_chatroom.pass"),
                props.getProperty("redis_chatroom.host"), props.getProperty("redis_chatroom.port"),
                props.getProperty("redis_chatroom.db")));
        props.setProperty("redis_fight.url",
                _url(props.getProperty("redis_fight.pass"), props.getProperty("redis_fight.host"),
                        props.getProperty("redis_fight.port"),
                        props.getProperty("redis_fight.db")));
        props.setProperty("redis_level.url",
                _url(props.getProperty("redis_level.pass"), props.getProperty("redis_level.host"),
                        props.getProperty("redis_level.port"),
                        props.getProperty("redis_level.db")));
        props.setProperty("redis_comment.url",
                _url(props.getProperty("redis_comment.pass"), props.getProperty("redis_comment.host"),
                        props.getProperty("redis_comment.port"),
                        props.getProperty("redis_comment.db")));
        props.setProperty("redis_activity.url",
                _url(props.getProperty("redis_activity.pass"), props.getProperty("redis_activity.host"),
                        props.getProperty("redis_activity.port"),
                        props.getProperty("redis_activity.db")));
    }
}
