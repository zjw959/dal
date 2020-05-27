package kafka.lib;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;
import server.ServerConfig;
import utils.ExceptionEx;
import utils.FileEx;

public abstract class KafkaConsumerUtil {
    private static final Logger LOGGER = Logger.getLogger(KafkaConsumerUtil.class);

    KafkaConsumer<String, String> consumer;
    Thread receiverThread;
    /** 消息保存队列 */
    ArrayBlockingQueue<ConsumerRecord<String, String>> recordQueue =
            new ArrayBlockingQueue<ConsumerRecord<String, String>>(pollCount());

    protected abstract String getTopic(int serverId);

    protected abstract String getGroupId();

    /** 消息取出来后放到queue里，最多只保留的数量，处理不过来的，后面的丢弃 **/
    protected abstract int pollCount();

    /** kafka每次取消息，如果没有新的消息，阻塞等待的时间 **/
    protected abstract int timeOut();

    /** 具体逻辑处理方法 */
    protected abstract void process(String msg);

    /** 获取配置文件的路径 */
    protected abstract String getPropPath();

    public KafkaConsumerUtil() {
        Properties props = new Properties();
        try {
            InputStream in =
                    FileEx.openInputStream(getPropPath());
            props.load(in);
        } catch (IOException e) {
            LOGGER.error(ExceptionEx.e2s(e));
            System.exit(-1);
        }
        props.put("group.id", getGroupId());
        LOGGER.info("group.id=" + getGroupId());
        this.consumer = new KafkaConsumer<String, String>(props);
        this.consumer.subscribe(_toipcs());
        this.receiverThread = new Thread(() -> receive());
        this.receiverThread.setName(getGroupId());
        this.receiverThread.start();
    }

    protected int getServerGroup() {
        return ServerConfig.getInstance().getServerGroup();
    }

    /**
     * 已经取出的消息队列处理
     */
    public void queueProcess() {
        while (!this.recordQueue.isEmpty()) {
            ConsumerRecord<String, String> record = this.recordQueue.poll();
            if (record == null)
                continue;
            this.process(record.value());
        }
    }

    private List<String> _toipcs() {
        List<String> list = new ArrayList<String>();
        list.add(getTopic(ServerConfig.getInstance().getServerId()));
        return list;
    }

    /**
     * 定时任务执行
     */
    protected void receive() {
        while (true) {
            try {
                ConsumerRecords<String, String> records = this.consumer.poll(this.timeOut());
                for (ConsumerRecord<String, String> record : records) {
                    this.recordQueue.offer(record);
                }
            } catch (Exception e) {
                // 避免异常线程崩溃
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }
    }

}
