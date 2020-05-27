package logic.constant;

/**
 * 游戏服异常码 Created by fxf on 2017/8/24.
 */
public class GameErrorCode {

    private static final int BASE_CODE = 200000;
    /** 功能暂未开启 */
    public static final int FUNCTION_NOT_OPEN = 2100109;
    // -----------------------------------------一般错误信息-------------------------------------------------
    private static final int GENERIC_CODE = BASE_CODE;
    /** 配置异常 */
    public static final int CFG_IS_ERR = GENERIC_CODE + 1;
    /** 参数错误 */
    public static final int CLIENT_PARAM_IS_ERR = GENERIC_CODE + 2;
    /** 玩家阵型未找到 */
    public static final int NOT_FOND_FORMATION = GENERIC_CODE + 3;
    /** 交换位置错误 */
    public static final int FORMATION_EXCHANGE_ERROR = GENERIC_CODE + 4;
    /** 必须有一个英雄上阵 */
    public static final int FORMATION_MUST_HAVE_ONE_HERO = GENERIC_CODE + 5;
    /** 非法字符 */
    public static final int CONTAINS_ILLEGAL_CHARACTERS = GENERIC_CODE + 6;
    /** 操作太频繁了 */
    public static final int THE_OPERATION_IS_TOO_FREQUENT = GENERIC_CODE + 7;
    /** 玩家信息不存在 */
    public static final int NOR_FONT_PLAYERINFO = GENERIC_CODE + 8;
    /** 尚未登录 */
    public static final int NOT_LOGIN = GENERIC_CODE + 9;
    /** 目前没有其他聊天房间 */
    public static final int NOW_SOLE_CHAT_ROOM = GENERIC_CODE + 10;
    /** 拦截：消息号 */
    public static final int INTERCEPT_MESSAGE_CODE = GENERIC_CODE + 11;
    /** 拦截：IP */
    public static final int INTERCEPT_IP = GENERIC_CODE + 12;
    /** 拦截：pid */
    public static final int INTERCEPT_PID = GENERIC_CODE + 13;
    /** 数据错误 */
    public static final int PLAYER_DATA_ERROR = GENERIC_CODE + 14;
    /** 购买次数不足 */
    public static final int NOT_ENOUGH_PURCHASES = GENERIC_CODE + 15;
    /** 拦截：禁言 */
    public static final int INTERCEPT_CHAT = GENERIC_CODE + 16;
    /** 字符过长 */
    public static final int THE_CHARACTERS_ARE_TOO_LONG = GENERIC_CODE + 17;
    /** 报警了死循环了 */
    public static final int UNLIMITED_LOOP = GENERIC_CODE + 18;
    /** 网络错误 */
    public static final int NET_ERROR = GENERIC_CODE + 19;
    // -----------------------------------------约会系统-------------------------------------------------
    private static final int DATING_CODE = BASE_CODE + 1000;
    /** 约会系统:没有找到当前剧本 */
    public static final int SCRIPT_IS_NULL = DATING_CODE + 1;
    /** 约会系统:当前没有主线约会可以进行 */
    public static final int MAIN_DATING_IS_NULL = DATING_CODE + 2;
    /** 约会系统:选择的节点没有找到 */
    public static final int DAILY_DATING_SELECTED_NODE_IS_NULL = DATING_CODE + 3;
    /** 约会系统:分支节点没有找到 */
    public static final int DAILY_DATING_BRANCH_NODE_IS_NULL = DATING_CODE + 4;
    /** 约会系统:分支异常 */
    public static final int BRANCH_NODE_ERR = DATING_CODE + 5;
    /** 约会系统:剧本中没有这个节点 */
    public static final int NODE_NOT_IN_SCRIPT = DATING_CODE + 6;
    /** 约会系统:有未完成的约会 */
    public static final int HAVE_NOT_FINISH_SCRIPT = DATING_CODE + 7;
    /** 约会系统:该时段预定约会排满 */
    public static final int THIS_TIME_FRAME_DATING_IS_FULL = DATING_CODE + 8;
    /** 约会系统:非接受邀请状态，不能进行约会 */
    public static final int DATING_IS_NO_ACCEPT_STATE = DATING_CODE + 9;
    /** 约会系统:该预定约会未接受邀请 */
    public static final int THIS_RESERVE_DATING_IS_NOT_ACCEPT = DATING_CODE + 10;
    /** 约会系统：日常约会次数已经用完 */
    public static final int DAILY_DATING_COUNT_IS_ZERO = DATING_CODE + 11;
    /** 约会系统：预定约会时间未到 */
    public static final int IS_NOT_DATING_TIME_YET = DATING_CODE + 12;
    /** 约会系统：约会类型错误 */
    public static final int DATING_TYPE_WRONG = DATING_CODE + 13;
    /** 约会系统：触发约会，已经完成的约会 */
    public static final int COMPLETED_DATING = DATING_CODE + 14;
    /** 约会系统：触发约会，无效的剧本id，改剧本未触发 */
    public static final int NOT_TRIGGER_SCRIPT = DATING_CODE + 15;
    /**约会过时**/
    public static final int DATING_IS_OBSOLETE =DATING_CODE+16;
    /**预定约会不存在**/
    public static final int RESERVE_DATING_IS_NOT_EXIST=DATING_CODE+17;
    /** 精力不足 */
    public static final int NOT_ENERGY = 900790;

    // -----------------------------------------副本系统-------------------------------------------------
    /** 副本系统 */
    private static final int DUNGEON_CODE = BASE_CODE + 2000;
    /** 副本系统:前置关卡未通关 */
    public static final int PRE_LEVEL_NOT_PASS = DUNGEON_CODE + 1;
    /** 副本系统:关卡所需资源不足 */
    public static final int GOODS_IS_NOT_ENOUGH = DUNGEON_CODE + 2;
    /** 副本系统:战斗次数超上限 */
    public static final int FIGHT_COUNT_IS_UPPER_LIMIT = DUNGEON_CODE + 3;
    /** 副本系统:主线关卡已经通关，不能重复进入 */
    public static final int MAIN_LINE_CHAPTER_IS_PASSED = DUNGEON_CODE + 4;
    /** 副本系统:未到关卡开启时间 */
    public static final int DUNGEON_LEVEL_IS_NOT_OPEN = DUNGEON_CODE + 5;
    /** 副本系统:副本组战斗次数用尽 */
    public static final int NOT_MORE_FIGHT_COUNT = DUNGEON_CODE + 6;
    /** 副本系统:玩家等级不足 */
    public static final int PLAYER_LEVEL_IS_NOT_ENOUGH = DUNGEON_CODE + 7;
    /** 副本系统:购买次数已达上限 */
    public static final int BUY_COUNT_IS_LIMIT = DUNGEON_CODE + 8;
    /** 副本系统:副本组奖励已经领取 */
    public static final int LEVEL_GROUP_REWARD_IS_GETTED = DUNGEON_CODE + 9;
    /** 副本系统:背包空间不足 */
    public static final int BAG_SPACE_IS_NOT_ENOUGH = DUNGEON_CODE + 10;
    /** 副本系统:星级不足，不能领奖 */
    public static final int STAR_NUM_IS_NOT_ENOUGH = DUNGEON_CODE + 11;
    /** 组队副本：玩家在队伍中，不能重复组队 */
    public static final int PLAYER_IN_TEAM = DUNGEON_CODE + 12;
    /** 组队副本：队伍已满 */
    public static final int TEAM_IS_FULL = DUNGEON_CODE + 13;
    /** 组队副本:没有匹配到队伍 */
    public static final int NO_MATCHING_TEAM = DUNGEON_CODE + 14;
    /** 组队副本:没有找到队伍 */
    public static final int NO_TEAM = DUNGEON_CODE + 15;
    /** 副本：助战玩家尚未冷却 */
    public static final int HELP_FIGHTER_IS_NOT_COLD_DOWN = DUNGEON_CODE + 16;
    /**副本：当前副本不匹配*/
    public static final int CURRENT_DUNGEON_NOT_MATCH = DUNGEON_CODE + 17;
    /**副本：英雄限定仅允许配置的数据*/
    public static final int LIMITED_HEROS_SETTED_ONLY = DUNGEON_CODE + 18;
    /**副本：英雄限定选择了非法的配置*/
    public static final int LIMITED_HEROS_SETTED_NOT_EXIST = DUNGEON_CODE + 19;
    /**副本：英雄限定选择了禁止的玩家英雄*/
    public static final int LIMITED_HEROS_SELECTED_FORBIDDEN = DUNGEON_CODE + 20;
    /**副本：英雄限定选择了重复的玩家英雄*/
    public static final int LIMITED_HEROS_SELECTED_DUPLICATE = DUNGEON_CODE + 21;
    
    /**副本：不能进行快速战斗*/
    public static final int QUICK_BATTLE_MOD_INVALID = DUNGEON_CODE + 29;
    /**副本：不能进行决斗模式*/
    public static final int DUEL_MOD_INVALID = DUNGEON_CODE + 30;



    // -----------------------------------------英雄系统-------------------------------------------------
    private static final int HERO_CODE = BASE_CODE + 3000;
    /** 英雄未找到 */
    public static final int NOT_FOND_HERO = HERO_CODE + 1;
    /** 已达最大突破等级 */
    public static final int MAX_ADVANCE_LVL = HERO_CODE + 2;
    /** 已有该英雄 */
    public static final int ALREADY_HAVE_HERO = HERO_CODE + 3;
    /** 天使已达最大等级 */
    public static final int MAX_ANGEL_LVL = HERO_CODE + 4;
    /** 天使技能点不足 */
    public static final int ANGEL_SKILL_POINT_LACKING = HERO_CODE + 5;
    /** 天使加点条件不满足 */
    public static final int ANGEL_SKILL_CONDITION_NOT_SATISFIED = HERO_CODE + 6;
    /** 该位置已达最大加点 */
    public static final int MAX_SKILL_ANGEL_ADDBIT = HERO_CODE + 7;
    /** 已达最大进阶等级 */
    public static final int MAX_QUALITY_LVL = HERO_CODE + 8;
    /** 天使无需重置 */
    public static final int DONT_NEED_TO_RESET = HERO_CODE + 9;
        /** 天使页找不到 */
    public static final int NOT_FOND_SKILL_STRATEGY = HERO_CODE + 10;
    /** 技能未解锁 */
    public static final int SKILL_IS_NO_UNLOCK = HERO_CODE + 11;
    /** 没有装备槽可以装备技能 */
    public static final int NOT_ENOUGH_SKILL_POSITION = HERO_CODE + 12;
    /** 技能类型不对 */
    public static final int SKILL_TYPE_IS_NOT_MATCH = HERO_CODE + 13;
    /** 无法激活结晶 */
    public static final int UN_ACTIVE_CRYSTAL = HERO_CODE + 14;
    /** 结晶已激活 */
    public static final int CRYSTAL_HAS_ACTIVED = HERO_CODE + 15;
    
    //-----------------------------------------道具系统-------------------------------------------------
    private static final int ITEM_CODE = BASE_CODE + 4000;
    /** 没有足够的背包 */
    public static final int SPACE_IS_NOT_ENOUGH = ITEM_CODE + 1;
    
    /** 道具不足 */
    public static final int ITEM_NOT_ENOUGH = ITEM_CODE + 2;
    /** 道具类型不正确 */
    public static final int ITEM_TYPE_ERROR = ITEM_CODE + 3;

    //-----------------------------------------精灵系统-------------------------------------------------
    private static final int ROLE_CODE = BASE_CODE + 5000;
    /** 精灵未找到 */
    public static final int NOT_FOND_ROLE = ROLE_CODE + 1;
    /** 精灵没有配置该礼品 */
    public static final int THIS_ROLE_NOT_FOND_GIFT = ROLE_CODE + 2;
    /** 礼品没有解锁 */
    public static final int GIFT_NOT_UNLOCK = ROLE_CODE + 3;
    /** 不能使用 */
    public static final int NOT_CAN_USE = ROLE_CODE + 4;
    /** 此时装和该精灵不匹配 */
    public static final int NOT_EQUIP_DRESS = ROLE_CODE + 5;
    /** 你没有该时装 */
    public static final int NOT_FOND_DRESS = ROLE_CODE + 6;
    /** 精灵状态错误 */
    public static final int ROLE_STATUS_ERROR = ROLE_CODE + 7;
    /** 当日触摸次数达到上限 */
    public static final int HAVE_NO_TOUCH_TIMES = ROLE_CODE + 8;
    

    // -----------------------------------------城建系统-------------------------------------------------
    private static final int BUILDING_CODE = BASE_CODE + 6000;
    /** 城市已经解锁 */
    public static final int CITY_IS_UNLOCK = BUILDING_CODE + 1;
    /** 玩家等级不足，不能解锁 */
    public static final int CAN_NOT_UNLOCK_PLAYER_LEVEL = BUILDING_CODE + 2;
    /** 关卡未通关，不能解锁 */
    public static final int CAN_NOT_UNLOCK_DUNGEON = BUILDING_CODE + 3;
    /** 建筑已经解锁 */
    public static final int BUILDING_IS_UNLOCK = BUILDING_CODE + 4;
    /** 建筑并非空闲状态 */
    public static final int BUILDING_IS_NOT_FREE = BUILDING_CODE + 5;
    /** 建筑尚未解锁 */
    public static final int BUILDING_IS_NOT_UNLOCK = BUILDING_CODE + 6;
    /** 建筑已经是最高级 */
    public static final int BUILDING_IS_LEVEL_LIMIT = BUILDING_CODE + 7;
    /** 不满足打工要求1：至少派遣N个精灵 */
    public static final int WORK_CONDITION_1 = BUILDING_CODE + 8;
    /** 不满足打工要求2：队伍中必须包含某个看板娘对应的精灵（英雄） */
    public static final int WORK_CONDITION_2 = BUILDING_CODE + 9;
    /** 不满足打工要求3：队伍中精灵至少达到N级 */
    public static final int WORK_CONDITION_3 = BUILDING_CODE + 10;
    /** 不满足打工要求4：队伍中不能包含同一个看板娘对应的多个精灵（英雄） */
    public static final int WORK_CONDITION_4 = BUILDING_CODE + 11;
    /** 不满足打工要求5：队伍中必须包含某个看板娘对应的N个精灵（英雄） */
    public static final int WORK_CONDITION_5 = BUILDING_CODE + 12;
    /** 有英雄正在打工，一个精灵不能打多份工 */
    public static final int ROLE_IS_WORKING = BUILDING_CODE + 13;
    /** 非收获状态，不能收获 */
    public static final int BUILDING_IS_NOT_GAIN_STATE = BUILDING_CODE + 14;
    /** 精灵id错误 */
    public static final int ROLE_ID_IS_ERR = BUILDING_CODE + 15;
    /** 打工人数超过建筑工位需求 */
    public static final int WORK_NUM_IS_TOO_MUCH = BUILDING_CODE + 16;
    /** 城市尚未解锁 */
    public static final int CITY_IS_NOT_UNLOCK = BUILDING_CODE + 17;
    /** 没有道具，不能进入城市 */
    public static final int HAVE_NOT_ITEM_CAN_NOT_ENTER = BUILDING_CODE + 18;
    /** 道具不足，不能升级建筑 */
    public static final int ITEM_NOT_ENOUGH_CAN_NOT_UPGRADE_BUILD = BUILDING_CODE + 19;
    /** 玩家等级不足，无法升级建筑 */
    public static final int CAN_NOT_UPGRADE_BUILDING_PLAYER_LEVEL = BUILDING_CODE + 20;
    /** 副本关卡未过，无法升级建筑 */
    public static final int CAN_NOT_UPGRADE_BUILDING_DUNGEON_LEVLE = BUILDING_CODE + 21;
    /** 建筑不存在 **/
    public static final int BUILDING_IS_ERROR = BUILDING_CODE + 22;
    /**建筑参数不一致**/
    public static final int BUILDING_PARAMETER_IS_ERROR = BUILDING_CODE + 23;
    /**道具不足不能玩游戏**/
    public static final int ITEM_NOT_ENOUGH_CAN_NOT_PLAYER_GAME = BUILDING_CODE + 24;
    /**打工未完成**/
    public static final int BUILDING_WORK_NOT_FINISH = BUILDING_CODE + 25;
    /**打工的精灵的心情不足**/
    public static final int BUILDING_WORK_ROLE_MOOD_NOT_ENOUGH = BUILDING_CODE + 26;
    /**打工的精灵的好感度不足**/
    public static final int BUILDING_WORK_ROLE_FAVOR_NOT_ENOUGH = BUILDING_CODE + 27;
    /**料理不存在**/
    public static final int CUISINE_IS_ERROR = BUILDING_CODE + 28;
    /**材料不足**/
    public static final int CUISINE_MATERIALS_IS_NOT_ENOUGH=BUILDING_CODE + 29;
    /**料理未完成**/
    public static final int CUISINE_MATERIALS_IS_NOT_COMPLETE=BUILDING_CODE + 30;
    /**材料能力不足**/
    public static final int CUISINE_ABILITY_IS_NOT_ENOUGH=BUILDING_CODE + 31;
    /**兼职没有完成**/
    public static final int BUILDING_JOB_IS_NOT_COMPLETE=BUILDING_CODE + 32;
    /**兼职任务不存在**/
    public static final int BUILDING_JOB_IS_NOT_EXIST=BUILDING_CODE + 33;
    /**兼职任务时间点不对**/
    public static final int BUILDING_JOB_IS_NOT_RIGHT_TIME=BUILDING_CODE + 34;
    /**精力不足**/
    public static final int BUILDING_JOB_IS_NOT_ENOUGH_STRENGTH=BUILDING_CODE + 35;
    /**要制作的手工id不存在**/
    public static final int BUILDING_MANUAL_ID_ERROR=BUILDING_CODE+36;
    /**制作手工的能力不足**/
    public static final int BUILDING_MANUAL_ABILITY_IS_NOT_ENOUGH=BUILDING_CODE + 37;
    /**材料不足**/
    public static final int BUILDING_MANUAL_MATERIALS_IS_NOT_ENOUGH=BUILDING_CODE + 38;
    /**要制作的手工未完成**/
    public static final int BUILDING_MANUAL_IS_NOT_COMPLETE=BUILDING_CODE + 39;
    // -----------------------------------------聊天-------------------------------------------------
    private static final int CHAT_CODE = BASE_CODE + 7000;
    /** 私聊对象不存在 */
    public static final int PRIVATE_CHAT_NOT_FOND = CHAT_CODE + 1;
    /** 聊天频道不存在 */
    public static final int NOT_FOND_CHAT_TYPE = CHAT_CODE + 2;
    /** 聊天房间不存在 */
    public static final int NOT_FOND_CHAT_ROOM = CHAT_CODE + 3;
    /** 私聊对象不在线 */
    public static final int PRIVATE_NOT_ONLINE = CHAT_CODE + 4;
    /** 目标房间人数已满 */
    public static final int CHAT_ROOM_IS_FULL = CHAT_CODE + 5;
    
  //-----------------------------------------商品-------------------------------------------------
    private static final int SHOP_CODE = BASE_CODE + 8000;
    /** 商品信息不存在 */
    public static final int NOT_FOND_COMMODITY = SHOP_CODE + 1;
    /** 此商品已达购买上限 */
    public static final int BUY_MAX_LIMIT = SHOP_CODE + 2;
    /** 物品不能出售 */
    public static final int CAN_NOT_SELL = SHOP_CODE + 3;
    /** 商城未开启 */
    public static final int STORE_NOT_OPEN = SHOP_CODE + 4;
    /** 商品未开启 */
    public static final int COMMODITY_NOT_OPEN = SHOP_CODE + 5;
    /** 装备使用中，不能出售 */
    public static final int EQUIP_IN_USE_CAN_NOT_SELL = SHOP_CODE + 6;

    // -----------------------------------------灵装-------------------------------------------------
    private static final int EQUIPMENT_CODE = BASE_CODE + 9000;
    /** heroId错误，没有找到Hero */
    public static final int HERO_ID_ERR = EQUIPMENT_CODE + 1;
    /** 位置错误，大于2或小于0，值范围：0~2 */
    public static final int POSITION_ERR = EQUIPMENT_CODE + 2;
    /** 装备id错误，没有找到该装备 */
    public static final int EQUIPMENT_ID_ERR = EQUIPMENT_CODE + 3;
    /** 该位置没有装备 */
    public static final int POSITION_NO_EQUIPMENT = EQUIPMENT_CODE + 4;
    /** cost过载 */
    public static final int COST_OVERLOAD = EQUIPMENT_CODE + 5;
    /** 没有临时属性，无法替换 */
    public static final int NO_REPLACE_ATTR = EQUIPMENT_CODE + 6;
    /** 有临时属性，不能洗炼 */
    public static final int HAVE_REPLACE_ATTR = EQUIPMENT_CODE + 7;
    /** 灵装已被装备 */
    public static final int ALREADY_EQUIP = EQUIPMENT_CODE + 8;
    /** 灵装特殊属性位置错误 */
    public static final int SPECIAL_ATTR_INDEX_ERR = EQUIPMENT_CODE + 9;

    // -----------------------------------------好友-------------------------------------------------
    private static final int FRIEND_CODE = BASE_CODE + 10000;
    /** 你们已经是好友 */
    public static final int YOU_ARE_ALREADY_FRIENDS = FRIEND_CODE + 1;
    /** 好友信息不存在 */
    public static final int NOT_FOND_FRIEND = FRIEND_CODE + 2;
    /** 你没有屏蔽该玩家 */
    public static final int NOT_FOND_SHIELD_FRIEND = FRIEND_CODE + 3;
    /** 没有找到好友申请信息 */
    public static final int NOT_FOND_FRIEND_APPLY = FRIEND_CODE + 4;
    /** 已达好友上限 */
    public static final int HAS_REACHED_FRIENDS_LIMIT = FRIEND_CODE + 5;
    /** 黑名单已达上限 */
    public static final int HAS_REACHED_BLACK_LIMIT = FRIEND_CODE + 6;
    /** 今天已经赠送过了 */
    public static final int TODAY_HAS_BEEN_PRESENTED = FRIEND_CODE + 7;
    /** 没有可以领取的奖励 */
    public static final int NO_REWARD_TO_RECEIVE = FRIEND_CODE + 8;
    /** 已达最大领取次数 */
    public static final int MAX_RECEIVE_COUNT = FRIEND_CODE + 9;
    /** 你被对方屏蔽 */
    public static final int YOU_HAVE_BEEN_SHIELDED = FRIEND_CODE + 10;
    /** 对方好友已达上限 */
    public static final int TARGET_HAS_REACHED_FRIENDS_LIMIT = FRIEND_CODE + 11;
    /** 申请失败！对方的申请列表已满！ */
    public static final int TARGET_APPLY_FULL = FRIEND_CODE + 12;

    // ----------------------------------------- 拦截
    // -------------------------------------------------
    private static final int INTERCEPT_CODE = BASE_CODE + 11000;

    // ----------------------------------------- 邮件
    // -------------------------------------------------
    private static final int MAIL_CODE = BASE_CODE + 12000;
    /** 邮件附件还没领取 */
    public static final int NOT_RECEIVE_ITEMS = MAIL_CODE + 1;
    /** 邮件状态不正确 */
    public static final int MAIL_STATUS_ERROR = MAIL_CODE + 2;

    // ----------------------------------------- 召唤
    // -------------------------------------------------
    private static final int SUMMON_CODE = BASE_CODE + 13000;
    /** 该质点正在合成 */
    public static final int COMPOSE_ING = SUMMON_CODE + 1;
    /** 合成消耗资源不足 */
    public static final int COMPOSE_COST_IS_NOT_ENOUGH = SUMMON_CODE + 2;
    /** 合成未完成 */
    public static final int COMPOSE_IS_NOT_FINISH = SUMMON_CODE + 3;



    // ----------------------------------------- 礼包码
    // -------------------------------------------------
    private static final int GIFT_CODE_CODE = BASE_CODE + 14000;
    /** 礼包码错误 */
    public static final int GIFT_CODE_ERR = GIFT_CODE_CODE + 1;
    /** 你已领取过该礼包码 */
    public static final int GIFT_CODE_ALREADY_GAIN = GIFT_CODE_CODE + 2;
    /** 礼包码:平台错误 */
    public static final int GIFT_CODE_PLATFORM_ERR = GIFT_CODE_CODE + 3;
    /** 礼包码:渠道错误 */
    public static final int GIFT_CODE_CHANNEL_ERR = GIFT_CODE_CODE + 4;
    /** 礼包码:未生效（开始时间之前） */
    public static final int GIFT_CODE_BEFORE_START = GIFT_CODE_CODE + 5;
    /** 礼包码:已经失效（结束时间之后） */
    public static final int GIFT_CODE_AFTER_END = GIFT_CODE_CODE + 6;
    /** 礼包码:已被领完 */
    public static final int GIFT_CODE_GAIN_OVER = GIFT_CODE_CODE + 7;

    // ----------------------------------------- 任务
    // -------------------------------------------------
    private static final int TASK_CODE = BASE_CODE + 15000;
    /** 任务状态错误 */
    public static final int TASK_STATUS_ERROR = TASK_CODE + 1;

    // ----------------------------------------- 充值
    // -------------------------------------------------
    private static final int ORDER_CODE = BASE_CODE + 16000;
    /** 订单已经交付 */
    public static final int ORDER_DELIVERED = ORDER_CODE + 1;
    /** 未到开始时间 */
    public static final int BEFORE_THE_START_TIME = ORDER_CODE + 2;
    /** 超过结束时间 */
    public static final int MORE_THAN_END_TIME = ORDER_CODE + 3;
    /** 已达购买次数上限 */
    public static final int BUY_COUNT_BEYOND_UPPER_LIMIT = ORDER_CODE + 4;
    /** 年卡不能续费 */
    public static final int YEAR_CARD_CAN_NOT_BUY = ORDER_CODE + 5;
    /** 请先购买月卡 */
    public static final int BUY_MONTH_CARD_FIRST_PLEASE = ORDER_CODE + 6;
    /** 不符合购买规则 */
    public static final int NOT_CONFORM_TO_THE_RULES = ORDER_CODE + 7;
    /** 没有购买月卡，不能领奖励 */
    public static final int NON_BUY_MONTH_CARD = ORDER_CODE + 8;
    /** 月卡已经过期 */
    public static final int MONTH_CARD_OUT_OF_DATE = ORDER_CODE + 9;
    /** 今日已领 */
    public static final int TODAY_GAINED = ORDER_CODE + 10;
    /** 玩家等级不满足购买条件 */
    public static final int PLAYER_LEVEL_ERR = ORDER_CODE + 11;

    // ----------------------------------------- 签到
    // -------------------------------------------------
    private static final int SIGN_CODE = BASE_CODE + 17000;
    /** 今日已签到 */
    public static final int TODAY_SIGNED = SIGN_CODE + 1;
    /** 签到已达上限 */
    public static final int SIGN_MAX = SIGN_CODE + 2;

    // ----------------------------------------- 抓娃娃
    // -------------------------------------------------
    private static final int GASHAPON_CODE = BASE_CODE + 18000;
    /** 抓娃娃CD中 */
    public static final int GASHAPON_CD = GASHAPON_CODE + 1;
    /** 抓娃娃刷新CD中 */
    public static final int GASHAPON_REFRESH_CD = GASHAPON_CODE + 2;
    /** 抓娃娃超时 */
    public static final int GASHAPON_TIMEOUT = GASHAPON_CODE + 3;
    /** 抓娃娃次数不足 */
    public static final int GASHAPON_COUNT_OVER = GASHAPON_CODE + 4;
    // ----------------------------------------- 活动
    // -------------------------------------------------
    private static final int ACTIVITY_CODE = BASE_CODE + 19000;
    /** 活动不存在 */
    public static final int NOT_FOND_ACTIVITY = ACTIVITY_CODE + 1;
    /** 活动条目不存在 */
    public static final int NOT_FOND_ACTIVITY_ENTRY = ACTIVITY_CODE + 2;
    /** 条目状态错误 */
    public static final int ACTIVITY_ENTRY_STATUS_ERROR = ACTIVITY_CODE + 3;
    /** 条目已经被领取 */
    public static final int ACTIVITY_ITEM_GOT_REWARD = ACTIVITY_CODE + 4;
    /** 条目领取失败 */
    public static final int ACTIVITY_ITEM_GET_REWARD_FAIL = ACTIVITY_CODE + 5;
    /**商品已售空**/
    public static final int ACTIVITY_ITEM_SOLD_OUT=ACTIVITY_CODE+6;

    // ----------------------------------------- 无尽回廊
    // -------------------------------------------------
    private static final int ENDLESS_CODE = BASE_CODE + 20000;
    /** 关卡不匹配 */
    public static final int ENDLESS_STAGE_NOT_MATCH = ENDLESS_CODE + 1;

    // ----------------------------------------- 外传约会
    // -------------------------------------------------
    private static final int OUTSIDE_DATING_CODE = BASE_CODE + 21000;
    /** 约会数据不存在 */
    public static final int OUTSIDE_DATING_NOT_EXIST = OUTSIDE_DATING_CODE + 1;
    /** 存档不存在 */
    public static final int OUTSIDE_DATING_ARCHIVE_NOT_EXIST = OUTSIDE_DATING_CODE + 2;
    /** 建筑不可用 */
    public static final int OUTSIDE_DATING_BUILDING_INVALID = OUTSIDE_DATING_CODE + 3;
    /** 入口时间不可用 */
    public static final int OUTSIDE_DATING_DATE_INVALID = OUTSIDE_DATING_CODE + 4;
    /** 剧本不可用 */
    public static final int OUTSIDE_DATING_SCRIPT_INVALID = OUTSIDE_DATING_CODE + 5;
    /** 信息不可用 */
    public static final int OUTSIDE_DATING_MESSAGE_INVALID = OUTSIDE_DATING_CODE + 6;
    /** 前置剧本不可用 */
    public static final int OUTSIDE_DATING_PRE_SCRIPT_INVALID = OUTSIDE_DATING_CODE + 7;
    /** 后置剧本不可用 */
    public static final int OUTSIDE_DATING_NEXT_SCRIPT_INVALID = OUTSIDE_DATING_CODE + 8;
    /** 道具不足 */
    public static final int OUTSIDE_DATING_ITEM_NOT_ENOUGH = OUTSIDE_DATING_CODE + 9;
    /** 功能未开放 */
    public static final int OUTSIDE_DATING_OUT_OF_TIME = OUTSIDE_DATING_CODE + 10;
    /** 存档不可用 */
    public static final int OUTSIDE_DATING_ARCHIVE_INVALID = OUTSIDE_DATING_CODE + 11;
    /** 精灵未激活 */
    public static final int OUTSIDE_DATING_ROLE_INVALID = OUTSIDE_DATING_CODE + 12;
    /** 外传概要不可用 */
    public static final int OUTSIDE_DATING_CFG_INVALID = OUTSIDE_DATING_CODE + 13;

    // ----------------------------------------- 评价系统
    // -------------------------------------------------
    private static final int COMMENT_CODE = BASE_CODE + 22000;
    /** 今日已评价过该精灵，明日再来吧 */
    public static final int HOER_COMMENT_EXISI_TODAY = COMMENT_CODE + 1;
    /** 今日已评价过该质点，明日再来吧 */
    public static final int EQUIP_COMMENT_EXISI_TODAY = COMMENT_CODE + 2;
    /** 输入了非法字符 */
    public static final int COMMENT_ILLEGAL = COMMENT_CODE + 3;
    /** 你太自恋了，不能对自己的评价点赞 */
    public static final int COMMENT_CAN_NOT_LIKE_SELF = COMMENT_CODE + 4;
    /** 评论太短了，走点心吧*/
    public static final int COMMENT_TOO_SHORT = 600014;
    /** 评论不能多于40个汉字*/
    public static final int COMMENT_TOO_LANG = 600015;
    

    // ----------------------------------------- 队伍
    // -------------------------------------------------
    private static final int TEAM_CODE = BASE_CODE + 40000;
    /** 你还没有队伍 */
    public static final int NOT_YET_JOIN_TEAM = TEAM_CODE + 1;
    /** 没有权限 */
    public static final int PERMISSION_DENIED = TEAM_CODE + 2;
    /** 队伍没找到 */
    public static final int NOT_FOND_TEAM = TEAM_CODE + 3;
    /** 队伍成员已满 */
    public static final int TEAM_FULL = TEAM_CODE + 4;
    /** 还有队员尚未准备 */
    public static final int NOT_ALL_READY = TEAM_CODE + 5;
    /** 已存在队伍 */
    public static final int ALREADY_EXIST_TEAM = TEAM_CODE + 6;
    /** 队伍已开始战斗 */
    public static final int TEAM_RUNING = TEAM_CODE + 7;
    /** 已存在匹配中 */
    public static final int ALREADY_EXIST_MATCH = TEAM_CODE + 8;
    /** 准备中不能修改 */
    public static final int READY_NOT_ALLOWED_CHANGE = TEAM_CODE + 9;
    /** 精灵重复,无法开始战斗 **/
    public static final int REPEATED_HERO = TEAM_CODE + 10;
    /** 剩余房间不足 */
    public static final int ROOM_LACKING = TEAM_CODE + 11;
    
    // /** 队长不能准备 */
    // public static final int TEAM_LEADER_NOT_READY = TEAM_CODE + 5;

    // ----------------------------------------- 战斗
    // -------------------------------------------------
    private static final int FIGHT_CODE = BASE_CODE + 41000;
    /** 战斗不存在 */
    public static final int NOT_FOND_FIGHT = FIGHT_CODE + 2;
    /** 非等待状态不能加入 */
    public static final int FIGHT_NON_WAITING = FIGHT_CODE + 3;
    /** 你不属于该战斗 */
    public static final int YOU_DOES_NOT_BELONG_FIGHT = FIGHT_CODE + 4;
    /** 复活次数已用尽 */
    public static final int REVIVE_COUNT_EXHAUST = FIGHT_CODE + 5;
    /** 战斗状态错误 */
    public static final int FIGHT_STATUS_ERROR = FIGHT_CODE + 6;

}
