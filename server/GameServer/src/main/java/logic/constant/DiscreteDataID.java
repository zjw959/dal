package logic.constant;

/**
 * @author : DengYing
 * @CreateDate : 2017年8月16日 下午4:17:40
 * @Description ：离散ID
 */
public class DiscreteDataID {

    /** 精灵心情值规则 */
    public static final int ROLE_MOOD = 2003;

    /** 心情-对好感度影响 */
    public static final int ROLE_MOOD_FAVOR = 2005;

    /** 背包 */
    public static final int ROLE_BAG = 3002;

    /** 初始化玩家信息 */
    public static final int INIT_PLAYER = 3003;

    /** 世界聊天房间大小 */
    public static final int CHAT_ROOM_LIMIT = 3004;

    /**
     * 战斗力计算规则
     * {"rarityzl":[180,240,320,420,540],"herolvlzl":[1,2,3,4,5],"growupzl":[0,40,80,120,160,200,240,280,320,360,400],"equipzl":[30,60,90,120,150],"equiplvlzl":[1,1,2,2,3]}
     */
    public static final int FIGHT_POWER_RULE = 3005;

    /** 灵装洗炼 */
    public static final int EQUIP_ATTR_CHANGE = 4001;
    /** 灵装强化 */
    public static final int EQUIP_UPGRADE = 4002;

    /** 约会 */
    public static final int DATING = 5001;

    /** 召唤 */
    public static final int SUMMON = 5002;

    /** 看板娘 */
    public static final int ROLE = 5003;

    /** 助战 */
    public static final int HELP_FIGHTER = 6001;

    /** 优先消耗资源 */
    public static final int PRIORITY_COST_ITEM = 6002;

    /**
     * 好友配置
     * {"maxFriendsNum":100,"maxBlacklistNum":100,"receiveFriendshipTimes":60,"friendship":100,"friendRecommendation":10,"refreshCd":5000,"applyTimeout":7}
     */
    public static final int FRIEND_CONFIG = 7001;
    /** 灵装洗炼键 */
    public static String EQUIPMENT_CHANGE_ATTR_PRICE = "price";
    /** 灵装强化键 */
    public static String EQUIPMENT_UPDATE_COST_RATIO = "ratio";

    /**
     * 天使离散配置 {"resetPlv":30,"freeResetCount":3,"resetCost":{500002:100}}
     */
    public static final int ANGEL_CONFIG = 8001;

    /** 最大等级离散 */
    public static final int MAX_LVL_CONFIG = 9002;

    /** 要记录的元素收集物品类型 */
    public static final int COLLECT_ITEM_TYPES = 9003;

    /** 抓娃娃配置 */
    public static final int GASHAPON = 10001;

    /** 无尽回廊配置 */
    public static final int ENDLESS_CLOISTER = 11001;
    /** 无尽回廊最高层数限制 */
    public static final int ENDLESS_CLOISTER_MAX = 11002;

    /** 深渊副本离散 */
    public static final int CHASM_CONFIG = 12001;

    /** 外传约会配置 */
    public static final int OUTSIDE_DATING = 13001;

    /** 邮件配置 */
    public static final int MAIL = 16001;
    
    /** 多倍收益 */
    public static final int MULTIPLE_REWARD = 20002;

    /** 评价配置 */
    public static final int COMMENT = 20001;

    /** 防沉迷配置 */
    public static final int ANTI_ADDICTION = 20003;

    /** 主线约会进入消耗精力配置 */
    public static final int FAVOR_DATING_COST = 20004;

    /** 日常约会进入消耗精力配置 */
    public static final int DAILY_DATING_COST = 20005;
    /** 评价配置 */
    public static final int COMMENT_CONFIG = 20006;

}
