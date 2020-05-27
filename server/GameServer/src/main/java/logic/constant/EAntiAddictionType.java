package logic.constant;

/***
 * 
 * @Description 防沉迷状态类型枚举
 * @author LiuJiang
 * @date 2018年8月16日 下午9:04:39
 *
 */
public enum EAntiAddictionType {
    /**
     * 未认证
     */
    UNCERTIFIED(0),
    /**
     * 认证未成年
     */
    MINOR(1),
    /**
     * 认证已成年
     */
    ADULT(2);

    private final int value;

    EAntiAddictionType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

}
