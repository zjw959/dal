package kafka.service;

import java.util.HashMap;
import java.util.Map;

import kafka.lib.KafkaProducerUtil;
import server.ServerConfig;

public abstract class AbstractProductService {

    static Map<String, KafkaProducerUtil> producerMap = new HashMap<>();

    public abstract String getPropPath();

    public AbstractProductService() {
        addProducer();
    }

    public void addProducer() {
        String propPath = getPropPath();
        KafkaProducerUtil producer = producerMap.get(propPath);
        if (producer == null) {
            producer = new KafkaProducerUtil(propPath);
            producerMap.put(propPath, producer);
        }
    }

    public KafkaProducerUtil getProducer() {
        return producerMap.get(getPropPath());
    }

    protected int getServerGroup() {
        return ServerConfig.getInstance().getSpecialId();
    }
}
