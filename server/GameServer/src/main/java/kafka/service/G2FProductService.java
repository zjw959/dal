package kafka.service;

import org.apache.log4j.Logger;
import kafka.team.action.TeamActionManager;

public class G2FProductService extends AbstractProductService {
    private static final Logger LOGGER = Logger.getLogger(G2FProductService.class);

    private static class DEFAULT {
        private static final G2FProductService provider = new G2FProductService();
    }

    public static G2FProductService getDefault() {
        return DEFAULT.provider;
    }

    @Override
    public String getPropPath() {
        return "./config/properties/kafka-producer-fight.properties";
    }

    public <T> void sendToFightServer(int serverId, T t)
            throws InstantiationException, IllegalAccessException {
        String data = TeamActionManager.getDefault().makeParam(t);
        getProducer().send(this.getSendTopic(serverId), "", data);
    }

    private String getSendTopic(int serverId) {

        return "g" + getServerGroup() + "fight" + serverId;
    }

}
