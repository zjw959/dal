package kafka.service;

import org.apache.log4j.Logger;
import kafka.team.action.TeamActionManager;

public class G2GInviteTeamProductService extends AbstractProductService {
    private static final Logger LOGGER = Logger.getLogger(G2GInviteTeamProductService.class);

    private static class DEFAULT {
        private static final G2GInviteTeamProductService provider =
                new G2GInviteTeamProductService();
    }

    public static G2GInviteTeamProductService getDefault() {
        return DEFAULT.provider;
    }

    @Override
    public String getPropPath() {
        return "./config/properties/kafka-producer-game.properties";
    }

    public <T> void invite(int serverId, T t)
            throws InstantiationException, IllegalAccessException {
        String data = TeamActionManager.getDefault().makeParam(t);
        getProducer().send(this.getTopic(serverId), "", data);
    }

    protected String getTopic(int serverId) {
        return "g" + getServerGroup() + "game" + serverId;
    }

}
