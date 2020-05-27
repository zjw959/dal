package utils;

import java.util.UUID;

/**
 * 此类提供了将UUID中组织各部分的横线(-)去掉的方法，以及使用去掉横线(-)后的字符串创建UUID的方法.
 */
public class UUIDUtils {

    /**
     * 返回表示指定UUID的字符串. 当需要创建不包含横线(-)分隔的UUID字符串时，可以用于替代java.util.UUID.toString()
     * 
     * @param uuid 指定的UUID对象
     * @return
     */
    public static String toCompactString(UUID uuid) {
        return (digits(uuid.getMostSignificantBits() >> 32, 8)
                + digits(uuid.getMostSignificantBits() >> 16, 4)
                + digits(uuid.getMostSignificantBits(), 4)
                + digits(uuid.getLeastSignificantBits() >> 48, 4)
                + digits(uuid.getLeastSignificantBits(), 12));
    }

    /**
     * 根据toString()方法返回的字符串创建UUID.
     * 当需要根据不包含横线(-)分隔的UUID字符串来创建UUID时，可以用于替代java.util.UUID.fromString(String name)
     * 
     * @param name 指定的UUID字符串
     * @return
     */
    public static UUID fromCompactString(String name) {
        if (name == null || name.length() != 32)
            throw new IllegalArgumentException("Invalid UUID string: " + name);

        long mostSigBits = Long.decode("0x" + name.substring(0, 8));
        mostSigBits <<= 16;
        mostSigBits |= Long.decode("0x" + name.substring(8, 12));
        mostSigBits <<= 16;
        mostSigBits |= Long.decode("0x" + name.substring(12, 16));

        long leastSigBits = Long.decode("0x" + name.substring(16, 20));
        leastSigBits <<= 48;
        leastSigBits |= Long.decode("0x" + name.substring(20));

        return new UUID(mostSigBits, leastSigBits);
    }

    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }

    public static void main(String[] args) {
        UUID uuid1 = UUID.randomUUID();
        System.out.println("UUID toString：" + uuid1.toString());
        System.out.println("UUIDUtils toString：" + UUIDUtils.toCompactString(uuid1));

        System.out.println("UUIDUtils fromString...");

        UUID uuid2 = UUIDUtils.fromCompactString(UUIDUtils.toCompactString(uuid1));
        System.out.println("UUID toString：" + uuid2.toString());
        System.out.println("UUIDUtils toString：" + UUIDUtils.toCompactString(uuid2));
    }

}
