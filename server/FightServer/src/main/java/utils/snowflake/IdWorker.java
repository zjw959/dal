package utils.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * tweeter的snowflake 移植到Java:long 64 位 支持34年 4096 台服务器 ，每秒支持4096个id生成 (a) id构成: 40位的时间前缀 + 12位的节点标识
 * + 12位的sequence避免并发的数字(12位不够用时强制得到新的时间前缀) 注意这里进行了小改动: snowkflake是5位的datacenter加5位的机器id;
 * 这里变成使用10位的机器id (b) 对系统时间的依赖性非常强，需关闭ntp的时间同步功能。当检测到ntp时间调整后，将会拒绝分配id
 * 
 * long max= 9223372036854775807 long min=-9223372036854775808
 * 
 * @author zhongmingyu@qq.com
 */

public class IdWorker {

    private final static Logger logger = LoggerFactory.getLogger(IdWorker.class);

    private final long workerId;
    private final long epoch = 1463712267558L; // 时间起始标记点，作为基准，一般取系统的最近时间
    private static final long workerIdBits = 12L; // 机器标识位数
    public static final long maxWorkerId = -1L ^ -1L << workerIdBits;// 机器ID: 0~4095
    private long sequence = 0L; // 0，并发控制
    private static final long sequenceBits = 12L; // 毫秒内自增位

    private static final long workerIdShift = sequenceBits; // 12
    private final long timestampLeftShift = sequenceBits + workerIdBits;// 22
    private final long sequenceMask = -1L ^ -1L << sequenceBits; // 4095,111111111111,12位
    private long lastTimestamp = -1L;

    // private long epochId = 0 << this.timestampLeftShift | this.workerId << this.workerIdShift;

    public IdWorker(long workerId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String
                    .format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.workerId = workerId;
    }

    public synchronized long nextId() throws Exception {
        long timestamp = this.timeGen();
        if (this.lastTimestamp == timestamp) { // 如果上一个timestamp与新产生的相等，则sequence加一(0-4095循环);
                                               // 对新的timestamp，sequence从0开始
            this.sequence = this.sequence + 1 & this.sequenceMask;
            if (this.sequence == 0) {
                timestamp = this.tilNextMillis(this.lastTimestamp);// 重新生成timestamp
            }
        } else {
            this.sequence = 0;
        }

        if (timestamp < this.lastTimestamp) {
            logger.error(String.format(
                    "clock moved backwards.Refusing to generate id for %d milliseconds",
                    (this.lastTimestamp - timestamp)));
            throw new Exception(String.format(
                    "clock moved backwards.Refusing to generate id for %d milliseconds",
                    (this.lastTimestamp - timestamp)));
        }

        this.lastTimestamp = timestamp;
        return timestamp - this.epoch << timestampLeftShift
                | this.workerId << workerIdShift | this.sequence;
    }

    // 服务器的selectid
    public long epochId() {
        return 0 << this.timestampLeftShift | this.workerId << workerIdShift;
    }

    /**
     * 等待下一个毫秒的到来, 保证返回的毫秒数在参数lastTimestamp之后
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    /**
     * 获得系统当前毫秒数
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    public static long getWorkerId(long id) {
        return (id & (maxWorkerId << workerIdShift)) >> workerIdShift;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public static void main(String[] args) throws Exception {
        // System.out.println(timeGen());
        // IdWorker idWorker = new IdWorker(4095);
        // IdWorker idWorker2 = new IdWorker(1023);
        // // System.out.println(Long.toBinaryString(idWorker.nextId()));
        // System.out.println(idWorker.nextId());
        // Thread.sleep(1000);
        // System.out.println(idWorker2.nextId());

        // long id = idWorker.nextId();
        // long server = idWorker.getWorkerId(id);
        // System.out.println("server"+server);
        // System.out.println("maxWorkerId"+maxWorkerId);

        // Assert.verify(4096<=IdWorker.maxWorkerId);
        long serverId = IdWorker.getWorkerId(168216489835401216L);
        System.out.println("" + serverId);
    }

}
