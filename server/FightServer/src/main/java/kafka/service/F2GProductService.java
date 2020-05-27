package kafka.service;

import java.util.Collection;
import kafka.lib.KafkaProducerUtil;
import kafka.team.action.TeamActionManager;
import server.ServerConfig;

// 指定游戏服 | "g" + serverGroudId + "game" + serverId | topic一样 | 好友、私聊、组队（除创建队伍） |
public class F2GProductService {

    KafkaProducerUtil producer;

    protected F2GProductService() {
        super();
        String propPath = "./config/properties/kafka-producer-game.properties";
        producer = new KafkaProducerUtil(propPath);
    }

    private static class DEFAULT {
        private static final F2GProductService provider = new F2GProductService();
    }

    public static F2GProductService getDefault() {
        return DEFAULT.provider;
    }

    public <T> void sendMsg(int serverId, T t)
            throws InstantiationException, IllegalAccessException {
        String data = TeamActionManager.getDefault().makeParam(t);
        producer.send(this.getSendTopic(serverId), "", data);
    }

    public <T> void sendMsg(Collection<Integer> serverIds, T t)
            throws InstantiationException, IllegalAccessException {
        String data = TeamActionManager.getDefault().makeParam(t);
        for (Integer serverId : serverIds) {
            producer.send(this.getSendTopic(serverId), "", data);
        }
    }

    private String getSendTopic(int serverId) {
        return "g" + getServerGroup() + "game" + serverId;
    }

    protected int getServerGroup() {
        return ServerConfig.getInstance().getServerGroup();
    }
}
