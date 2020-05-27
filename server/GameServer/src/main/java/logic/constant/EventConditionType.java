package logic.constant;


public class EventConditionType {

    private static final int BASE_VAL = 1000;

    // -------------------------------------------充值事件----------------------------------------
    /**
     * 累计充值 参数:count
     */
    public static final int TOTAL_RECHARGE = EventType.RECHARGE * BASE_VAL + 1;
    /**
     * 单次充值 参数:count
     */
    public static final int ONCE_RECHARGE = EventType.RECHARGE * BASE_VAL + 2;

    // -------------------------------------------登陆事件----------------------------------------
    /**
     * 登陆事件-累计登陆天数 参数：
     */
    public static final int TOTAL_LOGIN_DAY = EventType.LOGIN * BASE_VAL + 1;

    /**
     * 登陆事件-连续登陆天数 参数：
     */
    public static final int CONT_LOGIN_DAY = EventType.LOGIN * BASE_VAL + 2;

    // -------------------------------------------获得事件----------------------------------------
    /**
     * 持有英雄条件 可选参数：heroId | quality
     */
    public static final int HOLD_HERO = EventType.ITEM_EVENT * BASE_VAL + 1;
    /**
     * 持有物品条件 参数：itemId | superType | subType | smallType | quality
     */
    public static final int HOLD_ITEM = EventType.ITEM_EVENT * BASE_VAL + 2;

    /**
     * 获得物品 参数：itemId | superType | subType | smallType | quality
     */
    public static final int ADD_ITEM = EventType.ITEM_EVENT * BASE_VAL + 3;

    /**
     * 消耗物品条件 可选参数：itemId
     */
    public static final int DEL_ITEM = EventType.ITEM_EVENT * BASE_VAL + 4;

    /**
     * 持有灵装套装X套 参数：coverId
     */
    public static final int EQUIP_COVER_COUNT = EventType.ITEM_EVENT * BASE_VAL + 5;


    // -------------------------------------------任务接取事件----------------------------------------
    /**
     * 常规任务接取条件 可选参数:plvl | pre_task_id(前置任务ID)
     */
    public static final int TASK_ACCEPT = EventType.TASK_ACCEPT * BASE_VAL + 1;

    // -------------------------------------------副本通关事件----------------------------------------
    // 可选参数：dunId | star | heroNum(战斗英雄数) | time(耗时) | difficulty(难度) | dunType(副本类型)
    /**
     * 关卡指定副本次数
     */
    public static final int FIGHT_EVENT = EventType.PASS_DUP * BASE_VAL + 1;

    /**
     * 关卡通关总星数 入参：addStar(增加星数)
     */
    public static final int FIGHT_TOTAL_STAR = EventType.PASS_DUP * BASE_VAL + 2;

    /**
     * 3星通关副本数 入参：first3Star(第一次获得3星)
     */
    public static final int PERFECT_DUN = EventType.PASS_DUP * BASE_VAL + 3;

    /**
     * 连击数 入参：batter(连击数)
     */
    public static final int FIGHT_BATTER = EventType.PASS_DUP * BASE_VAL + 4;

    /**
     * 拾取数
     */
    public static final int FIGHT_PICKUP_COUNT = EventType.PASS_DUP * BASE_VAL + 5;

    /**
     * 拾取类型数
     */
    public static final int FIGHT_PICKUP_TYPE_COUNT = EventType.PASS_DUP * BASE_VAL + 6;

    /**
     * 通关指定关卡
     */
    public static final int FIGHT_ASSIGN_DUP = EventType.PASS_DUP * BASE_VAL + 7;

    // -------------------------------------------玩家变化事件----------------------------------------
    /**
     * 玩家等级
     */
    public static final int PLAYER_LVL = EventType.PLAYER_CHANGE * BASE_VAL + 1;
    /**
     * 玩家改名
     */
    public static final int CHANGE_PLAYER_NAME = EventType.PLAYER_CHANGE * BASE_VAL + 2;

    // -------------------------------------------英雄变化事件----------------------------------------
    /**
     * 最大英雄等级 参数：heroId
     */
    public static final int HERO_MAX_LVL = EventType.HERO_CHANGE * BASE_VAL + 1;

    /**
     * 英雄突破等级 参数：heroId
     */
    public static final int ADVANCED_LVL = EventType.HERO_CHANGE * BASE_VAL + 2;

    /**
     * X个精灵达到N等级 参数：heroNum
     */
    public static final int ADVANCED_LVL_COUNT = EventType.HERO_CHANGE * BASE_VAL + 3;

    /**
     * 英雄升级次数 参数：heroId
     */
    public static final int HERO_UP_LVL_COUNT = EventType.HERO_CHANGE * BASE_VAL + 4;

    // -------------------------------------------天使变化事件----------------------------------------
    /**
     * 天使星级 参数：heroId
     */
    public static final int ANGEL_STAR_LVL = EventType.ANGEL_CHANGE * BASE_VAL + 1;

    /**
     * x个天使星级达到X 参数：limitLvl
     */
    public static final int ANGEL_STAR_LVL_COUNT = EventType.ANGEL_CHANGE * BASE_VAL + 2;

    // -------------------------------------------装备事件----------------------------------------
    /**
     * 穿时装（8001）
     */
    public static final int DRESS = EventType.EQUIPMENT * BASE_VAL + 1;

    /**
     * 灵装最高等级 参数：equipId
     */
    public static final int EQUIP_MAX_LVL = EventType.EQUIPMENT * BASE_VAL + 2;
    /**
     * 强化装备
     */
    public static final int EQUIP_UPGRADE = EventType.EQUIPMENT * BASE_VAL + 3;
    /**
     * 装备洗练：参数：star
     */
    public static final int EQUIP_CHANGER = EventType.EQUIPMENT * BASE_VAL + 4;



    // -------------------------------------------精灵事件----------------------------------------
    /**
     * 精灵好感度达到X 参数：roleId
     */
    public static final int ROLE_FAVOR = EventType.ROLE_CHANGE * BASE_VAL + 1;

    /**
     * X个精灵好感度达到X 参数：roleId | limit
     */
    public static final int ROLE_FAVOR_COUNT = EventType.ROLE_CHANGE * BASE_VAL + 2;
    /**
     * 完成X次约会 参数：roleId | datingType(约会类型) | scriptId(剧本ID)
     */
    public static final int FINISH_DATING_COUNT = EventType.ROLE_CHANGE * BASE_VAL + 3;
    /**
     * 赠送礼物 参数：roleId | giftId | maxCount(最大赠送约会次数)
     */
    public static final int DONATE_GIFT = EventType.ROLE_CHANGE * BASE_VAL + 4;

    /**
     * 约会点击事件 参数：
     */
    public static final int DATING_CLICK = EventType.ROLE_CHANGE * BASE_VAL + 5;

    // -------------------------------------------建筑事件----------------------------------------
    /**
     * 打工阶段事件 参数：buildId | buildLvl
     */
    public static final int WORK_STAGE = EventType.BUILDING_EVENT * BASE_VAL + 1;

    /** X个建筑达到X级 */
    public static final int BUILDING_LVL_COUNT = EventType.BUILDING_EVENT * BASE_VAL + 2;

    /** 领取X次打工奖励 */
    public static final int RECEIVE_WORK_REWARD_COUNT = EventType.BUILDING_EVENT * BASE_VAL + 3;

    /** 打工时间 */
    public static final int WORK_TIME = EventType.BUILDING_EVENT * BASE_VAL + 4;

    // -------------------------------------------其他事件----------------------------------------
    /** 合成X次 */
    public static final int COMPOUND = EventType.OTHER_EVENT * BASE_VAL + 1;

    /** 召唤X次 */
    public static final int SUMMON = EventType.OTHER_EVENT * BASE_VAL + 2;

    /**
     * 购买资源X次 参数：resId
     */
    public static final int BUY_RESOURCE = EventType.OTHER_EVENT * BASE_VAL + 3;

    /**
     * 商城购买X次 参数：cid | storeId
     */
    public static final int STORE_BUY = EventType.OTHER_EVENT * BASE_VAL + 4;

    /**
     * 月卡领取条件
     */
    public static final int GAIN_MONTH_CARD = EventType.OTHER_EVENT * BASE_VAL + 5;

    /**
     * 玩小游戏X次
     */
    public static final int SMALL_GAME = EventType.OTHER_EVENT * BASE_VAL + 6;

    // -------------------------------------------好友事件----------------------------------------

    /**
     * 好友数量达到X
     */
    public static final int FRIEND_COUNT = EventType.FRIEND * BASE_VAL + 1;

    // -------------------------------------------月卡事件----------------------------------------

    /**
     * 领取月卡奖励
     */
    public static final int GAIN_MONTH_CARD_ITEM = EventType.MONTH_CARD * BASE_VAL + 1;
}
