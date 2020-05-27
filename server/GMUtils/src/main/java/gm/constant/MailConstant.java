package gm.constant;

/**
 * 
 * @Description MailConstant
 * @author LiuJiang
 * @date 2018年7月2日 下午5:27:59
 *
 */
public enum MailConstant {
    // -------------------------------状态-------------------------------
    /** 自动提取类邮件 */
    STATUS_AUTO(-1),
    /** 未读 */
    STATUS_DEFAULT(0),
    /** 已读 */
    STATUS_READ(1),
    /** 已领 */
    STATUS_RECEIVE(2),
    /** 已删 */
    STATUS_DELETE(3);

    int code;

    private MailConstant(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
