package kafka.service;

import org.apache.log4j.Logger;
import kafka.team.action.TeamActionManager;
import kafka.team.param.g2g.PublicChatHandlerParam;
import server.ServerConfig;
import utils.ExceptionEx;

public class ChatRoomProducerService extends AbstractProductService {
    private static final Logger LOGGER = Logger.getLogger(ChatRoomProducerService.class);


    private static class DEFAULT {
        private static final ChatRoomProducerService provider = new ChatRoomProducerService();
    }

    public static ChatRoomProducerService getDefault() {
        return DEFAULT.provider;
    }

    @Override
    public String getPropPath() {
        return "./config/properties/kafka-producer-room.properties";
    }

    public <T> void sendChatRoom(T t) throws InstantiationException, IllegalAccessException {
        int serverId = ServerConfig.getInstance().getServerId();
        String data = TeamActionManager.getDefault().makeParam(t);
        getProducer().send(getTopic(serverId), "", data);
    }

    protected String getTopic(int serverId) {
        return "g" + getServerGroup() + "room";
    }

    public static void main(String[] str) throws InterruptedException {
        int start = 0;
        int count = 10;
        while (true) {
            long startTime = System.currentTimeMillis();
            for (int i = start; i < start + count; i++) {
                PublicChatHandlerParam chatObject =
                        new PublicChatHandlerParam(1111, "robot", 99, 0, "我是机器人", 5);
                try {
                    ChatRoomProducerService.getDefault().sendChatRoom(chatObject);
                } catch (InstantiationException | IllegalAccessException e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }
            long endTime = System.currentTimeMillis();
            long usedTime = endTime - startTime;
            if (usedTime <= 500) {
                Thread.sleep(500 - usedTime);
            }
        }
    }
}
