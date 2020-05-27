package utils.snowflake;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by raymond on 2017/7/22.(雪花算法参考示例)
 *
 * Twitter_Snowflake<br>
 * SnowFlake算法的结构（用-隔开）如下:<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)
 *
 *
 * 第一部分为符号位 1位 第二部分为时间戳 41位 第三部分为数据中心标号 5位 第四部分为工作机器标号 5位 第五部分为序列号，同一毫秒内的分配 的自增序列号，如果该毫秒内的序列号超过12位，
 * 那么等待到下一毫秒再计算序列号
 */
public class SnowflakeSequence implements Sequence {

    // 起始时间 （2017-01-01 01-01-01）
    private static final long twepoch = 1485882061743l;
    // 数据中心位数
    private static final long dataCenterIdBits = 5l;
    // 工作机器位数
    private static final long workerIdBits = 5l;
    // 序列号位数
    private static final long sequenceBits = 12l;
    // 工作机器位偏移量
    private static final long workerShift = sequenceBits; // 12
    // 数据中心位偏移量
    private static final long dataCenterShift = workerShift + dataCenterIdBits; // 12 + 5
    // 时间戳位偏移量
    private static final long timestampShift = dataCenterShift + dataCenterIdBits;// 17 + 5
    // 工作机器最大值
    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);
    // 数据中心最大值
    private static final long maxDatacenterId = -1L ^ (-1L << dataCenterIdBits);

    /* 生成序列的掩码，这里为4095 (0b111111111111=4095) */
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);


    /* 上次生成ID的时间截 */
    private long lastTimestamp = -1L;
    /* 毫秒内序列(0~4095) */
    private long sequence = 0L;


    private final Long dataCenterId;
    private final Long workerId;

    public SnowflakeSequence(Long dataCenterId, Long workerId) {

        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format(
                    "worker Id can't be greater than %d or less than 0", maxWorkerId));
        }

        if (dataCenterId > maxDatacenterId || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format(
                    "datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }

        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }


    public synchronized long next() {
        long timestamp = this.currentTimestamp();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        // 系统处理闰秒， 正闰秒 和 负闰秒之分， 目前出现的都是正闰秒
        if (timestamp < lastTimestamp) {
            try {
                wait(1000L);
                timestamp = this.currentTimestamp();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & sequenceMask;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = this.untilNextMillis(lastTimestamp);
            }
        } else {// 时间戳改变，毫秒内序列重置
            sequence = 0;
        }

        // 生成的时间戳已经成为过去式
        lastTimestamp = timestamp;
        // 只预留指定时间内
        final long currentSequenceId =
                ((timestamp - twepoch) << timestampShift) | (dataCenterId << dataCenterShift)
                        | (workerId << workerShift) | sequence;
        return currentSequenceId;
    }


    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long untilNextMillis(long lastTimestamp) {
        long timestamp = this.currentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = this.currentTimestamp();
        }
        return timestamp;
    }

    private long currentTimestamp() {

        return SystemClock.currentTimestamp();
    }

    public static void main(String[] args) {
        Set ids = new HashSet();
        Sequence sequence = new SnowflakeSequence(1L, 30L);
        long s = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            long id = sequence.next();
            if (ids.contains(id)) {
                System.err.println("重复id:" + id + "  i=" + i);
            } else {
                ids.add(id);
                // System.err.println(sequence.next());
            }
        }
        System.out.println("耗时：" + (System.currentTimeMillis() - s));
//        System.err.println(Math.pow(2, 7));
        long years = 5 * 365 * 24 * 60 * 60 * 1000L;
        System.out.println("years=" + years);

    }
}