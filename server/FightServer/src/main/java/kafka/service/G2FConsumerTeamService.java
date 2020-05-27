package kafka.service;

import org.apache.log4j.Logger;
import kafka.lib.KafkaConsumerUtil;
import kafka.team.action.TeamActionManager;
import server.ServerConfig;

// 指定战斗服 | "g" + serverGroudId + "fight" + serverId | topic一样 | 组队（除创建队伍） |
public class G2FConsumerTeamService extends KafkaConsumerUtil {

    private static final Logger LOGGER = Logger.getLogger(G2FConsumerTeamService.class);

    private static class DEFAULT {
        private static final G2FConsumerTeamService provider = new G2FConsumerTeamService();
    }

    public static G2FConsumerTeamService getDefault() {
        return DEFAULT.provider;
    }

    @Override
    protected int pollCount() {
        return 1000;
    }

    @Override
    protected int timeOut() {
        return 200;
    }

    @Override
    protected String getPropPath() {
        return "./config/properties/kafka-consumer-fight.properties";
    }

    @Override
    protected String getTopic(int serverId) {
        return "g" + getServerGroup() + "fight" + serverId;
    }

    @Override
    protected String getGroupId() {
        int serverId = ServerConfig.getInstance().getServerId();
        return getTopic(serverId);
    }

    @Override
    protected void process(String msg) {
        LOGGER.info("msg=" + msg);
        TeamActionManager.getDefault().process(msg);
    }

}
