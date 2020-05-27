package utils.snowflake;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.hutool.core.codec.Base64;
import security.MD5;
import utils.NumEx;

/**
 * Twitter的snowflake 移植到Java
 * 
 * 64位版,字符长度 20
 * 
 * id构成: 40位的时间前缀(34年) + 12位的节点标识(4096) + 12位(4096
 * 12位的计数顺序号支持每个节点每毫秒产生4096个ID序号)的sequence避免并发的数字(12位不够用时强制得到新的时间前缀)
 * 
 * 简短版:
 * 
 * 48位版,字符长度 16
 * 
 * id构成: 38位的时间前缀(5年) + 8位的节点标识(128) + 2位(4, 每秒4000)
 * 
 * 其中 一年时间至少为35位.字符长度 11位
 * 
 * 注意这里进行了小改动: snowkflake是5位的datacenter加5位的机器id; 这里变成使用10位的机器id (b)
 * 
 * 对系统时间的依赖性非常强，需关闭ntp的时间同步功能。当检测到ntp时间调整后，将会拒绝分配id
 * 
 * long max= 9223372036854775807 long min=-9223372036854775808
 * 
 * @author zhongmingyu@qq.com
 */

public class IdWorker {

    private final static Logger logger = LoggerFactory.getLogger(IdWorker.class);

    private final long workerId;
    // 时间起始标记点，作为基准 (2017-01-01 01-01-01)
    private final long epoch = 1483200000000l;
    private static final long workerIdBits = 12L; // 机器标识位数
    public static final long maxWorkerId = -1L ^ -1L << workerIdBits;// 机器ID: 0~4095
    private long sequence = 0L; // 0，并发控制
    private static final long sequenceBits = 12L; // 毫秒内自增位

    private static final long workerIdShift = sequenceBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits;
    private final long sequenceMask = -1L ^ -1L << sequenceBits;
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
        // 如果上一个timestamp与新产生的相等，则sequence加一(0-4095循环);
        if (this.lastTimestamp == timestamp) {
            this.sequence = this.sequence + 1 & this.sequenceMask;
            // 对新的timestamp，sequence从0开始
            if (this.sequence == 0) {
                // 重新生成timestamp
                timestamp = this.tilNextMillis(this.lastTimestamp);
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
        return (timestamp - this.epoch) << timestampLeftShift
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

        IdWorker idWorker = new IdWorker(5);
        long a = idWorker.nextId();
        System.out.println(a);

        String b64 = Base64.encode(String.valueOf(a));
        System.out.println(b64);

        b64 = MD5.MD5Encode(String.valueOf(a));
        System.out.println(b64);

        System.out.println("----------------");

        b64 = NumEx.toOtherBaseString(a, 32);
        System.out.println(b64);

        b64 = NumEx.toOtherBaseString(100000000000000L, 32);
        System.out.println(b64);

        b64 = NumEx.toOtherBaseString(999999999999999L, 32);
        System.out.println(b64);

        b64 = NumEx.toOtherBaseString(a, 32);
        System.out.println(b64);
    }
}
