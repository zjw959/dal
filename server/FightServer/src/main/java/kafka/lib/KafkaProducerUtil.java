package kafka.lib;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;
import utils.ExceptionEx;
import utils.FileEx;


public class KafkaProducerUtil {
    private static final Logger LOGGER = Logger.getLogger(KafkaConsumerUtil.class);

    Producer<String, String> producer;

    public KafkaProducerUtil(String propPath) {
        Properties props = new Properties();
        try {
            InputStream in =
                    FileEx.openInputStream(propPath);
            props.load(in);
        } catch (IOException e) {
            LOGGER.error(ExceptionEx.e2s(e));
            System.exit(-1);
        }
        producer = new KafkaProducer(props);
    }


    public void send(String topic, String key, String value) {
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<String, String>(topic, key, value);
        producer.send(producerRecord);
    }

    public static void main(String[] args) throws InterruptedException {
        int start = 0;
        int count = 3333;
        while (true) {
            long startTime = System.currentTimeMillis();
            for (int i = start; i < start + count; i++) {
                String propPath = "./config/properties/kafka-producer.properties";
                KafkaProducerUtil producer = new KafkaProducerUtil(propPath);
                producer.send("test21", "friend",
                        "msgmsgmsgmsgmsgmsgmsg" + i);
            }
            long endTime = System.currentTimeMillis();
            long usedTime = endTime - startTime;
            if (usedTime <= 1000) {
                Thread.sleep(1000 - usedTime);
            }
        }
    }
}
