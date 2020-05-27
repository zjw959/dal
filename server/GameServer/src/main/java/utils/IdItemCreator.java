package utils;

import java.util.UUID;

import utils.snowflake.SystemClock;

/**
 * 
 * @Description 道具id生成器
 *
 */
public class IdItemCreator {
    /** 起始时间 （2018-08-08 08-08-08） */
    private static final long twepoch = 1533686888000l;
    /** 服务器Id位数 */
    private static final long serverIdBits = 20l;// 0-999999
    /** 序列号位数 */
    private static final long sequenceBits = 6l;// 1毫秒内最多生成63个id
    /** 时间戳位数 */
    private static final long timestampBits = 38l;// 从起始时间算起，可支持8年时间内的id生成，超过之后可能会出现重复id
    /** 时间戳位偏移量 */
    private static final long timestampShift = sequenceBits;
    /** 服务器id位偏移量 */
    private static final long serverShift = timestampShift + timestampBits;
    /** 时间戳掩码(最大值) */
    private static final long timestampMask = -1L ^ (-1L << timestampBits);
    /** 服务器Id掩码(最大值) */
    private static final long serverMask = -1L ^ (-1L << serverIdBits);
    /** 序列号的掩码(最大值) */
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private static long seq = 1;
    private static long lastTimestamp = -1;


    public static synchronized long getItemId(int serverId, int itemType) {
        long timestamp = currentTimestamp();
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        // 系统处理闰秒， 正闰秒 和 负闰秒之分， 目前出现的都是正闰秒
        // if (timestamp < lastTimestamp) {
        // throw new RuntimeException();
        // }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (timestamp == lastTimestamp) {
            seq = (seq + 1) & sequenceMask;
            // 秒内序列溢出
            if (seq == 0) {
                // 阻塞到下一毫秒,获得新的时间戳
                timestamp = untilNextMills(lastTimestamp);
            }
        } else {// 时间戳改变，毫秒内序列重置
            seq = 0;
        }

        // 生成的时间戳已经成为过去式
        long dis = timestamp - twepoch;
        lastTimestamp = timestamp;
        long ret =
                (seq & sequenceMask) | ((dis & timestampMask) << timestampShift)
                        | (((long) serverId & serverMask) << serverShift);
        return ret;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    private static long untilNextMills(long lastTimestamp) {
        long timestamp = currentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimestamp();
        }
        return timestamp;
    }

    /** 当前时间戳（毫秒） */
    private static long currentTimestamp() {
        return SystemClock.currentTimestamp();
    }

    /**
     * 解析id包含的服务器id
     * 
     * @param uniqueId
     * @return
     */
    public static int parseServerId(long uniqueId) {
        return (int) ((uniqueId >> serverShift) & serverMask);
    }

    /**
     * 解析id包含的时间
     * 
     * @param uniqueId
     * @return
     */
    public static int parseTime(long uniqueId) {
        return (int) ((uniqueId >> timestampShift) & timestampMask);
    }

    /**
     * 解析id包含的序号
     * 
     * @param uniqueId
     * @return
     */
    public static int parseSeq(long uniqueId) {
        return (int) (uniqueId & sequenceMask);
    }


    /**
     * 根据uuid生成id <br>
     * 无规则可言
     * 
     * @return
     */
    public static long getLongByUUID() {
        String toString = getUUID();
        byte[] bytes = toString.getBytes();
        long num = 0;
        for (int ix = 0; ix < 7; ++ix) {
            num <<= 8;
            num |= (bytes[ix] & 0xff);
        }
        return num;
    }

    /**
     * 根据uuid生成id <br>
     * 无规则可言
     * 
     * @return
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static void main(String[] args) {
        long id = 198160588659751058l;
        int serverid = parseServerId(id);
        int time = parseTime(id);
        int seq = parseSeq(id);
        System.out.println("--serverid:" + serverid + " time:" + time
                + "  seq:" + seq);
    }
}
