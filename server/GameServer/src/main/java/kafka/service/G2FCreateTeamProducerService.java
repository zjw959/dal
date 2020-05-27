package kafka.service;

import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.Logger;
import kafka.team.action.TeamActionManager;

public class G2FCreateTeamProducerService extends AbstractProductService {
    private static final Logger LOGGER = Logger.getLogger(G2FCreateTeamProducerService.class);

    AtomicLong inc = new AtomicLong();

    @Override
    public String getPropPath() {
        return "./config/properties/kafka-producer-team.properties";
    }

    private static class DEFAULT {
        private static final G2FCreateTeamProducerService provider = new G2FCreateTeamProducerService();
    }

    public static G2FCreateTeamProducerService getDefault() {
        return DEFAULT.provider;
    }

    public <T> void createTeam(T t) throws InstantiationException, IllegalAccessException {
        String data = TeamActionManager.getDefault().makeParam(t);
        getProducer().send(this.getTopic(), "" + inc.getAndIncrement(), data);
    }

    public String getTopic() {
        return "g" + getServerGroup() + "team";
    }

}
