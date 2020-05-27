package kafka.service;

import org.apache.log4j.Logger;
import kafka.lib.KafkaConsumerUtil;
import kafka.team.action.TeamActionManager;
import server.ServerConfig;

// 功能 | topic | groupId | 备注 |
// 全体游戏服 | "g" + serverGroudId + "room" | topic + "s" + serverid | 世界聊天 |
public class AllConsumerService extends KafkaConsumerUtil {

    private static final Logger LOGGER = Logger.getLogger(AllConsumerService.class);

    @Override
    protected String getPropPath() {
        return "./config/properties/kafka-consumer-room.properties";
    }

    @Override
    protected String getTopic(int serverId) {
        return "g" + getServerGroup() + "room";
    }

    @Override
    protected String getGroupId() {
        int serverId = ServerConfig.getInstance().getServerId();
        return getTopic(serverId) + "s" + serverId;
    }

    @Override
    protected int pollCount() {
        return 5000;
    }

    @Override
    protected int timeOut() {
        return 200;
    }

    @Override
    protected void process(String msg) {
        // LOGGER.info("msg=" + msg);
        TeamActionManager.getDefault().process(msg);
    }

    public static AllConsumerService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        AllConsumerService instance;

        private Singleton() {
            instance = new AllConsumerService();
        }

        AllConsumerService getInstance() {
            return instance;
        }
    }

}
