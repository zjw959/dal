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
        producer = new KafkaProducer<String, String>(props);
    }


    public void send(String topic, String key, String value) {
        ProducerRecord<String, String> producerRecord =
                new ProducerRecord<String, String>(topic, key, value);
        producer.send(producerRecord);
    }

    public static void main(String[] args) throws InterruptedException {
        long start = 0;
        long count = 1;
        String propPath = "./config/properties/kafka-producer-game.properties";
        KafkaProducerUtil producer = new KafkaProducerUtil(propPath);
        while (true) {
            long startTime = System.currentTimeMillis();
            System.out.println("start= " + start + " count=" + count);
            for (long i = start; i < start + count; i++) {
                System.out.println("send! i =" + i);
                LOGGER.info("send! i =" + i);
                producer.send("test33", "",
                        "msgmsgmsgmsgmsgmsgmsg" + i);
            }
            long endTime = System.currentTimeMillis();
            long usedTime = endTime - startTime;
            if (usedTime <= 1000) {
                System.out.println("sleep " + (10000 - usedTime));
                LOGGER.info("sleep " + (10000 - usedTime));
                Thread.sleep(1000 - usedTime);
            }
        }
    }
}
