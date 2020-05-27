package logic.constant;

public interface GiftConstant {
    /** 普通礼包 */
    public static final byte TYPE_ONCE = 1;
    /** 永久礼包 */
    public static final byte TYPE_FOREVER = 2;

    /**
     * OK
     */
    public static final String OK = "ok";
    /**
     * 未知错误
     */
    public static final String ERROR = "error";
    /**
     * 礼包码已经使用过
     */
    public static final String ALREADY_USE = "Already used.";
    /**
     * 已经使用过同一个礼包
     */
    public static final String ALREADY_USE_SAME_PACK = "Already used same package.";
    /**
     * 使用次数超过了上限
     */
    public static final String TIME_LESS_THAN = "Less than interval days.";
    /**
     * 找不到礼包码
     */
    public static final String CODE_NOT_FOUND = "Not Found";
    /**
     * 找不到礼包
     */
    public static final String PACK_NOT_FOUND = "Package Not Found";
    /**
     * 礼包仍未开放兑换
     */
    public static final String PACK_NOT_START = "Package Not start time";
    /**
     * 礼包过期
     */
    public static final String PACK_TIME_OUT = "Package out of end time";
    /**
     * 无效渠道号
     */
    public static final String INVALID_CHANNEL = "Channel is invalid";
}
