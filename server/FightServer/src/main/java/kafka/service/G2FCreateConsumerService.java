package kafka.service;

import org.apache.log4j.Logger;
import kafka.lib.KafkaConsumerUtil;
import kafka.team.action.TeamActionManager;
import server.ServerConfig;

// 任一战斗服 | "g" + serverGroudId + "team" | topic + "s" | 创建队伍 |
public class G2FCreateConsumerService extends KafkaConsumerUtil {

    private static final Logger LOGGER = Logger.getLogger(G2FCreateConsumerService.class);

    private static class DEFAULT {
        private static final G2FCreateConsumerService provider = new G2FCreateConsumerService();
    }

    public static G2FCreateConsumerService getDefault() {
        return DEFAULT.provider;
    }

    @Override
    protected String getGroupId() {
        int serverId = ServerConfig.getInstance().getServerId();
        return getTopic(serverId) + "s";
    }

    @Override
    protected String getTopic(int serverId) {
        return "g" + getServerGroup() + "team";
    }

    @Override
    protected int pollCount() {
        return 200;
    }

    @Override
    protected int timeOut() {
        return 200;
    }

    @Override
    protected void process(String msg) {
        LOGGER.info("msg=" + msg);
        TeamActionManager.getDefault().process(msg);
    }

    @Override
    protected String getPropPath() {
        return "./config/properties/kafka-consumer-team.properties";
    }

}
