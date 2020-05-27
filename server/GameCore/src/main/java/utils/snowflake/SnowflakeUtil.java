package utils.snowflake;

import java.util.HashSet;
import java.util.Set;

import utils.DateEx;

/**
 *
 * Twitter_Snowflake<br>
 * SnowFlake算法的结构（用-隔开）如下:<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)
 *
 *
 * 第一部分为符号位 1位 第二部分为时间戳 41位 第三部分为数据中心标号 5位 第四部分为工作机器标号 5位 第五部分为序列号，同一毫秒内的分配 的自增序列号，如果该毫秒内的序列号超过12位，
 * 那么等待到下一毫秒再计算序列号
 * 
 * @author liu.jiang
 * 
 */
public class SnowflakeUtil implements Sequence {

    /** 起始时间 （2018-08-01 00-00-00） */
    private static final long twepoch = 1533052800000l;
    /** 工作机器位数 */
    private static final long workerIdBits = 7l;
    /** 序列号位数 */
    private static final long sequenceBits = 2l;
    /** 时间戳位数 */
    private static final long timestampBits = 38l;// 从起始时间算起，可支持8年时间内的id生成，超过之后可能会出现重复id
    /** 时间戳位偏移量 */
    private static final long timestampShift = sequenceBits;
    /** 工作机器位偏移量 */
    private static final long workerShift = timestampShift + timestampBits;
    /** 最高位占位符位偏移量 */
    private static final long highestShift = workerShift + workerIdBits;
    /** 时间戳掩码(最大值) */
    private static final long timestampMask = -1L ^ (-1L << timestampBits);
    /** 工作机器掩码(最大值) */
    private static final long workerIdMask = -1L ^ (-1L << workerIdBits);
    /** 序列号掩码(最大值) */
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;
    /** 毫秒内序列 */
    private long sequence = 0L;

    private final Long workerId;

    public SnowflakeUtil(long workerId) {
        if (workerId > workerIdMask || workerId < 0) {
            throw new IllegalArgumentException(String.format(
                    "worker Id can't be greater than %d or less than 0", workerIdMask));
        }
        this.workerId = workerId;
    }

    public synchronized long next() {
        long timestamp = this.currentTimestamp();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        // 系统处理闰秒， 正闰秒 和 负闰秒之分， 目前出现的都是正闰秒
        // if (timestamp < lastTimestamp) {
        // throw new RuntimeException();
        // }

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
        long disTime = timestamp - twepoch;
        // 只预留指定时间内
        final long currentSequenceId =
                (1L << highestShift) | ((workerId & workerIdMask) << workerShift)
                        | ((disTime & timestampMask) << timestampShift) | sequence & sequenceMask;
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
        int serverId = 125;// 设置serverId
        long now = System.currentTimeMillis();
        Set ids = new HashSet();
        Sequence sequence = new SnowflakeUtil(serverId);
        long s = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            long id = sequence.next();
            if (ids.contains(id)) {
                System.err.println("重复id：" + id + "  i=" + i);
                break;
            } else {
                ids.add(id);
                System.out.println(id);
            }
        }
        long v = 8 * DateEx.TIME_YEAR;
        System.out.println("---v=" + v);
        System.out.println("耗时=" + (System.currentTimeMillis() - now));
        // System.err.println(Math.pow(2, 7));
        System.out.println("maxTimestamp=" + timestampMask);
    }
}
