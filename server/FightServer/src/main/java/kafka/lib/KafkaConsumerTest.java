package kafka.lib;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.log4j.Logger;
import server.ServerConfig;

public class KafkaConsumerTest extends KafkaConsumerUtil {
    private static final Logger LOGGER = Logger.getLogger(KafkaConsumerTest.class);

    private static class DEFAULT {
        private static final KafkaConsumerTest provider = new KafkaConsumerTest();
    }

    public static KafkaConsumerTest getDefault() {
        return DEFAULT.provider;
    }

    @Override
    public int pollCount() {
        return 10000;
    }

    @Override
    public String getTopic(int serverId) {
        return /* getServerGroup(serverId) + */ "test21";
    }

    @Override
    protected String getGroupId() {
        int serverId = ServerConfig.getInstance().getServerId();
        // + s 避免服务器序号位数重复问题
        String groupId = getTopic(serverId) + "s" + ServerConfig.getInstance().getServerId();
        return groupId;
    }

    @Override
    public void process(String msg) {
        System.out.println("msg=" + msg + " successCount=" + successCount.get() + " failCount="
                + failCount.get());
    }

    @Override
    protected void receive() {
        while (true) {
            ConsumerRecords<String, String> records = this.consumer.poll(this.timeOut());
            for (ConsumerRecord<String, String> record : records) {
                boolean isSuccess = recordQueue.offer(record);
                if (isSuccess) {
                    successCount.incrementAndGet();
                } else {
                    failCount.incrementAndGet();
                }
            }
        }
    }

    // 成功的次数
    AtomicInteger successCount = new AtomicInteger(0);
    // 失败的次数
    AtomicInteger failCount = new AtomicInteger(0);

    public static void main(String[] args) {
        LOGGER.info("serverId:" + args[0]);
        // ServerConfig.getInstance().setServerId(Integer.valueOf(args[0]));
        KafkaConsumerTest test = KafkaConsumerTest.getDefault();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                test.queueProcess();
            }
        }, 10, 100);

    }

    @Override
    protected int timeOut() {
        return 10;
    }

    @Override
    protected String getPropPath() {
        return "./config/properties/kafka-consumer.properties";
    }
}
