package kafka.lib;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.log4j.Logger;
import server.ServerConfig;
import utils.ExceptionEx;

public class KafkaConsumerTest extends KafkaConsumerUtil {
    private static final Logger LOGGER = Logger.getLogger(KafkaConsumerTest.class);

    @Override
    public int pollCount() {
        return 10000;
    }

    @Override
    public String getTopic(int serverId) {
        return "test33";
    }

    @Override
    public void process(String msg) {
        System.out.println("msg=" + msg + " successCount=" + successCount.get() + " failCount="
                + failCount.get());
        LOGGER.info("msg=" + msg + " successCount=" + successCount.get() + " failCount="
                + failCount.get());
    }

    protected String getGroupId() {
        int serverId = ServerConfig.getInstance().getServerId();
        // + s 避免服务器序号位数重复问题
        String groupId = getTopic(serverId) + "s" + ServerConfig.getInstance().getServerId();
        return groupId;
    }

    private long lastPollTime = 0L;

    private static long sessionInterval = 5 * 60 * 1000L;

    @Override
    protected void receive() {
        int i = 0;
        while (true) {
            /*
             * System.out.println("0000000"); ConsumerRecords<String, String> records =
             * this.consumer.poll(this.timeOut()); System.out.println("11111111 count=" +
             * records.count()); for (ConsumerRecord<String, String> record : records) { boolean
             * isSuccess = recordQueue.offer(record); if (isSuccess) {
             * successCount.incrementAndGet(); } else { failCount.incrementAndGet(); } } try { int
             * ran = RandomUtil.randomInt(2); System.out.println("sleep" + ran); if (ran > 0) {
             * Thread.sleep(40000); } } catch (InterruptedException e) {
             * LOGGER.error(ExceptionEx.e2s(e)); }
             */
            try {
                long now = System.currentTimeMillis();
                if ((lastPollTime != 0L) && (now - lastPollTime) >= sessionInterval) {
                    LOGGER.error(
                            getGroupId() + " receive interval >= 5 min! " + (now - lastPollTime));
                }
                lastPollTime = now;
                System.out.println("0000!");
                LOGGER.info("0000!");
                ConsumerRecords<String, String> records = this.consumer.poll(this.timeOut());
                System.out.println("1111!");
                LOGGER.info("1111!");
                for (ConsumerRecord<String, String> record : records) {
                    this.recordQueue.offer(record);
                    System.out.println("offer!");
                    LOGGER.info("offer!");
                }
            } catch (Exception e) {
                // 避免异常线程崩溃
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }
    }

    // 成功的次数
    AtomicInteger successCount = new AtomicInteger(0);
    // 失败的次数
    AtomicInteger failCount = new AtomicInteger(0);

    public static void main(String[] args) {
        int serverId = 1;
        ServerConfig.getInstance().setServerId(Integer.valueOf(serverId));
        KafkaConsumerTest test = KafkaConsumerTest.getInstance();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                test.queueProcess();
            }
        }, 10, 100);

    }

    @Override
    protected int timeOut() {
        return 2000;
    }

    @Override
    protected String getPropPath() {
        return "./config/properties/kafka-consumer-game.properties";
    }

    public static KafkaConsumerTest getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        KafkaConsumerTest instance;
        private Singleton() {
            instance = new KafkaConsumerTest();
        }

        KafkaConsumerTest getInstance() {
            return instance;
        }
    }
}
