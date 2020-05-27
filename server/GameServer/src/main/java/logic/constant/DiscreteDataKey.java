package logic.constant;
/**
 * @author : DengYing
 * @CreateDate : 2017年8月30日 上午11:57:20
 * @Description ：Please describe this document
 */
public class DiscreteDataKey {
	
	public static final String CEILING = "ceiling";
	
	public static final String FLOOR = "floor";
	
	public static final String LIST = "list";
	
	public static final String MIN = "min";
	
	public static final String MAX = "max";
	/** 板娘心情值 */
	public static final String MOOD = "mood";
	/** 比例 */
	public static final String RATIO = "ratio";
	
	
	// ********************** 好友 **********************
	/** 好友上限 */
	public static final String MAX_FRIEND_COUNT = "max_friend_count";
	/** 推荐上限 */
	public static final String RECOMMEND_FRIEND_LIMIT = "recommend_friend_limit";
	/** 领取上限 */
	public static final String RECEIVE_MAX_NUM = "receive_max_num";
	/** 助战冷却时间 */
	public static final String HELP_FIGHTER_COLD_TIME = "help_fighter_cold_time";

	// ********************** 约会 **********************
	/**约会：日常约会次数*/
	public static String DATING_DAILY_DATING_COUNT = "dailyDatingCount";
	/**约会：时间段白天*/
	public static String DATING_DAY = "day";
	/**约会：邀请时间段*/
	public static String DATING_INVITATION_TIME_FRAME = "invitationTimeFrame";
	/**约会：约会时间段*/
	public static String DATING_DATING_TIME_FRAME = "datingTimeFrame";
	/**约会：该时间段最大约会次数（接受约会邀请次数）*/
	public static String DATING_MAX_DATING_COUNT = "maxDatingCount";
	/**约会：该时间段最大约会次数（接受约会邀请次数）*/
	public static String DATING_RESERVE_ROLE_DAY_DATING_COUNT = "roleDayDatingCount";
	/**约会：好感度加成规则*/
	public static String DATING_REWARD_FAVOR_RULE = "favorRule";
	/**约会：定时约会触发时间段，被所有城市约会共享*/
	public static String TRIGGER_FRAME = "triggerFrame";
	/**约会：定时约会触发时间点，每种城市约会独立 */
	public static String TRIGGER_TIME_POINT = "triggerTimePoint";
	/**约会：看板娘城市约会触发递减几率 */
	public static String DECREASE_RATE = "decreaseRate";
	/**约会：预定约会约会时间段 */
	public static String DATING_TIME_FRAME = "datingTimeFrame";
	/**约会：约会过期时间 */
	public static String DATING_OUT_TIME = "datingOutTime";
	/**约会：触发器顺序 */
	public static String TRIGGER_ORDER = "triggerOrder";
	/**约会：仅刷新不触发时间点*/
	public static String REFRESH_HOUR = "refreshHour";
	/**约会：登录游戏后需要删除记录的约会类型*/
	public static String REENTRY_TYPE = "reentryType";
	/**约会：登录游戏后需要删除记录的约会类型*/
	public static String CITY_DATING_TRIGGER_RATE = "triggerRate";


	/** 新手期：时间单位：天 */
	public static String NEW_PLAYER_TIME = "newPlayerTime";
	/** 召唤：玩家等级限制 */
	public static String SUMMON_PLAYER_LEVEL = "summonPlayerLevel";

	/** 触摸看板娘上限 */
	public static String TOUCH_ROLE_TIMES_LIMIT = "touchRoleTimesLimit";

	
	// ********************** 天使重置 **********************
	
	/** 免费重置等级限制 */
	public static String ANGEL_RESET_LIMIT_PLV = "resetPlv";
	/** 超过重置等级免费次数 */
	public static String ANGEL_RESET_FREE_COUNT = "freeResetCount";
	/** 付费重置消耗 */
	public static String ANGEL_RESET_COST = "resetCost";

	// ********************** 元素收集（图鉴） **********************

	/** 需收集的物品类型 */
	public static String COLLECT_ITEM_TYPES = "itemType";

	// ********************** 看板娘 **********************

	/** 看板娘初始房间 */
	public static String DEFAULT_ROOM = "defaultRoom";
	
	// ********************** 无尽回廊 **********************
    /** 跳过层级 */
    public static String SKIP_LEVELS = "skipLevels";
    /** 开放周期 */
    public static String ENDLESS_DURATION = "duration";
    /** 开始时间 */
    public static String OPEN_TIME = "opentime";
    /** 结算时间 */
    public static String BALANCE_TIME = "balancetime";
    /** 重置时间 */
    public static String RESETTING_TIME = "resettingtime";
    /** 最高层数限制*/
    public static String ENDLESS_MAX_LV = "maxLevel";

	// ********************** 助战 ********************** //
    /** 好友增加友情点 */
	public static String FRIEND_ADD_FRIEND_POINT = "friend";
	/** 路人增加好感度 */
	public static String STRANGER_ADD_FRIEND_POINT = "passer";
	/** 助战cd */
	public static String HELP_CD = "cd";

	// ********************** 质点 ********************** //
	/** 洗炼价格 */
	public static String EQUIPMENT_CHANGE_ATTR_PRICE = "price";
	/** 强化系数 */
	public static String EQUIPMENT_UPDATE_COST_RATIO = "ratio";
	
	// ********************** 多倍收益 ********************** //
	/** 单位体力 */
	public static String MULTIPLE_REWARD_UNIT_ENERGY = "unitEnergy";
	/** 连续离线基数 */
	public static String MULTIPLE_REWARD_SEQ_OFFLINE = "base";
	/** 最大连续离线 */
	public static String MULTIPLE_REWARD_MAX_OFFLINE = "day";
}
