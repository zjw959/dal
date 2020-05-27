package kafka.service;

import org.apache.log4j.Logger;
import kafka.lib.KafkaConsumerUtil;
import kafka.team.action.TeamActionManager;
import server.ServerConfig;

// 功能 | topic | groupId | 备注 |
// 指定游戏服 | "g" + serverGroudId + "game" + serverId | topic一样 | 好友、私聊、组队（除创建队伍） |
public class AssignConsumerService extends KafkaConsumerUtil {

    private static final Logger LOGGER = Logger.getLogger(AssignConsumerService.class);
    @Override
    protected String getPropPath() {
        return "./config/properties/kafka-consumer-game.properties";
    }

    @Override
    protected String getTopic(int serverId) {
        return "g" + getServerGroup() + "game" + serverId;
    }

    @Override
    protected String getGroupId() {
        int serverId = ServerConfig.getInstance().getServerId();
        return getTopic(serverId);
    }

    @Override
    protected int pollCount() {
        return 100;
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

    public static AssignConsumerService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        AssignConsumerService instance;

        private Singleton() {
            instance = new AssignConsumerService();
        }

        AssignConsumerService getInstance() {
            return instance;
        }
    }

}
