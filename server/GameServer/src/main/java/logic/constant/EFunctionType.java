package logic.constant;

/***
 * 
 * @Description 服务器功能类型枚举（功能开关用到）
 * @author LiuJiang
 * @date 2018年8月8日 下午12:25:01
 *
 */
public enum EFunctionType {
    /**
     * 充值
     */
    RECHARGE(1),
    /**
     * 组队副本
     */
    TEAM_FIGHT(2),
    /**
     * 防沉迷
     */
    ANTI_ADDICTION(3),
    /**
     * 看板娘触摸
     */
    ROLE_TOUCH(4),
    /**
     * 新手引导
     */
    NEW_GUIDE(5),
    /**
     * 后台活动
     */
    BACK_ACTIVITY(6),
    /**
     * 无尽副本
     */
    ENDLESS(7),
    /**
     * 卡巴拉副本
     */
    QLIPHOTH(8),
    /**
     * 精灵升级
     */
    HERO_LEVEL_UP(9),
    /**
     * 精灵突破
     */
    HERO_UP_QUALITY(10),
    /**
     * 天使
     */
    ANGEL(11),
    /**
     * 质点强化
     */
    EQUIP_LEVEL_UP(12),
    /**
     * 质点洗练
     */
    EQUIP_CHANGE_SPECIAL_ATTR(13),
    /**
     * 约会-主线
     */
    DATE_MAIN_LINE(14),
    /**
     * 约会-日常
     */
    DATE_DAILY(15),
    /**
     * 约会-外传
     */
    DATE_OUTSIDE(16),
    /**
     * 约会-事件
     */
    DATE_EVENT(17),
    /**
     * 精灵灵装
     */
    HERO_SKIN(18),
    /**
     * 商店
     */
    STORE(19),
    /**
     * 召唤
     */
    SUMMON(20),
    /**
     * 道具出售
     */
    ITEM_SELL(21),
    /**
     * 顶部资源购买
     */
    BUY_RESOURCE(22),
    /**
     * 任务
     */
    TASK(23),
    /**
     * 礼包码兑换
     */
    GIFT_CODE(24),
    /****
     * 7日活动奖励
     */
    SEVEN_GIFT(38),
    /**
     * 质点装备/卸下
     */
    EQUIP_TAKE(49),
    /**
     * 赠送礼物
     */
    ROLE_DONATE(50),
    /**
     * 普通副本
     */
    NORMAL_DUNGEON(51),
    /**
     * 日常副本
     */
    DAILY_DUNGEON(52),;


    private final int value;

    EFunctionType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

}
