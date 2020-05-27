package gm.constant;

/**
 * 
 * @Description PayConstant
 * @author LiuJiang
 * @date 2018年7月14日 下午5:27:59
 *
 */
public enum PayConstant {
    // ~~~~~~~~~~~~~~~~~~~~~~~订单状态~~~~~~~~~~~~~~~~~~~~~~~
    /** 初始（刚创建订单） */
    STATUS_DEFAULT(0),
    /** 收到平台回调 */
    STATUS_CALL_BACK(1),
    /** 处理已完成 */
    STATUS_FINISH(2),

    // ~~~~~~~~~~~~~~~~~~~~~~~充值商品类型~~~~~~~~~~~~~~~~~~~~~~~
    /** 充值商品类型：月卡 */
    TYPE_MONTH_CARD(1),
    /** 充值商品类型：礼包 */
    TYPE_GIFT_BAG(2),

    // ~~~~~~~~~~~~~~~~~~~~~~~月卡类型~~~~~~~~~~~~~~~~~~~~~~~
    /** 月卡类型： */
    CARD_TYPE_MONTH_CARD(1),
    /** 季卡类型： */
    CARD_TYPE_QUARTER_CARD(2),
    /** 年卡类型： */
    CARD_TYPE_YEAR_CARD(3),

    // ~~~~~~~~~~~~~~~~~~~~~~~重置类型~~~~~~~~~~~~~~~~~~~~~~~
    /** 不重置 */
    RESET_TYPE_NO(0),
    /** 每日重置 */
    RESET_TYPE_DAILY(1),
    /** 每周重置 */
    RESET_TYPE_WEEK(2),
    /** 每月重置 */
    RESET_TYPE_MONTH(3);

    int code;

    private PayConstant(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
