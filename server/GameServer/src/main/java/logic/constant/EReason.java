package logic.constant;

/**
 * 所有操作原因定义: 第一二位表示 系统标志 第三四位表示 系统中的具体操作原因编号
 */
public enum EReason {
    START(1000),

    /** 创建角色 **/
    CREATE(8888),
    /** GM邮件 **/
    MAIL_GM(6666),

    // 10 道具系统
    ITEM_CLIENT_USE(1001), // 客户端请求使用
    ITEM_CREAT_ROLE(1002), // 创建角色初始化
    ITEM_SELL(1003), // 道具出售
    ITEM_GM(1004), // gm 道具
    ITEM_HERO(1005), // 获得英雄
    ITEM_USE(1006), // 使用道具
    ITEM_AUTO_USE(1007), // 道具自动使用
    ITEM_REWARD_MAIL(1008), // 领取邮件奖励
    ITEM_AUTO_REWARD_MAIL(1009), // 自动提取邮件奖励
    ITEM_RECHARGE(1010), // 充值获得
    ITEM_GIFT_CODE(1011), // 礼包码兑换获得
    ITEM_BUY_RESOURCE(1012), // 购买资源
    ITEM_DUEL_BRAND(1013), // 决斗牌转换

    // 11 英雄操作Reason
    HERO_LEVEL_UP(1101), // 英雄升级
    HERO_BREAK_UP(1102), // 英雄突破
    HERO_COMPOUND(1103), // 英雄合成
    HERO_QUALITY_UP(1104), // 英雄进阶
    HERO_GAIN(1105), // 英雄获取
    HERO_ANGEL_AWAKE(1106), // 觉醒天使
    HERO_CRYSTAL_ACTIVE(1107), // 激活结晶
    HERO_ANGEL_LEVEL_UP(1108), //天使技能升级
    HERO_ANGEL_DEGRADE(1109),  //天使技能降级
    HERO_ANGEL_RESET(1110),    //天使技能重置

    // 12 章节关卡
    DUNGEON_CHAPTER_OPENBOX(1201), // 章节开box
    DUNGEON_DUNGEON_OPENBOX(1202), // 章节开box
    DUNGEON_DUNGEON_PASS(1203), // 副本通关box
    DUNGEON_DUNGEON_RESET(1204), // 重置副本
    DUNGEON_DUNGEON_BUYCOUNT(1205), // 购买次数
    DUNGEON_HELP_FIGHT(1206), // 助战
    DUNGEON_CHAPTER_SCRIPT_PASS(1207), // 章末剧情奖励
    DUNGEON_ENDLESS_SETTLED(1208), // 无尽关卡结算
    DUNGEON_ENDLESS_PASS(1209), // 无尽关卡通关
    DUNGEON_DAILY_STAR(1210), // 日常关卡星级奖励


    ROLE_ACTIVE(1301), // 激活看板娘

    ROOM_UNLOCK(1401), // 房间解锁

    // 15 召唤
    SUMMON(1501), // 召唤
    COMPOSE_SUMMON(1502), // 合成召唤

    FRIEND_POINT_RECV(1601), // 领取友情点

    EQUIP(1700), // 装备

    TOUCH(1800), // 精灵互动


    TASK(1900), // 任务
    DAILY_REWARD(1902), // 日常邮件

    CHASM_RELIVE(2001), // 深渊复活
    CHASM_THROUGH(2002), // 通关深渊
    CHASM_START(2003),   // 开始深渊

    // 约会
    DATING_NODE(2201), // 约会节点
    DATING_SETTLE(2202), // 约会结算
    DATING_BEGIN(2203), // 约会开始
    DATING_INVITATION(2204), // 约会邀请应答
    
    
    DAILY_DATING(2206),//日常约会
    
    DAILY_DATING_STIME(2207), // 日常约会开始
    DUNGEON_DATING_STIME(2208),//关卡约会开始
    PHONE_DATING_STIME(2209),//手机约会开始
    TRIP_DATING_STIME(2210),//出游约会开始
    TRIGGER_DATING_STIME(2211),//事件约会开始
    RESERVER_DATING_STIME(2212),//预定约会开始时间
    
    
    RESERVER_DATING_SYSTEM_STIME(2213),//系统产生预定约会的时间
    RESERVER_DATING_ACCPET(2214),//玩家接受预定约会
    RESERVER_DATING_NOT_ACCPET(2215),//玩家拒绝约会
    RESERVER_DATING_SYSTEM_LATE(2216),//系统已触发预定约会，约会邀请时间到了(系统自动删除)
    RESERVER_DATING_LATE(2217),//玩家约会迟到(已经接受要求)
    TRIP_DATING_SYSTEM_STIME(2218),//系统产生出游约会的时间
    
    
    
    DAILY_DATING_ETIME(2251), // 日常约会结束
    DUNGEON_DATING_ETIME(2252),//关卡约会结束
    PHONE_DATING_ETIME(2253),//手机约会结束
    TRIP_DATING_ETIME(2254),//日常约会结束
    TRIGGER_DATING_ETIME(2255),//事件约会结束
    RESERVER_DATING_ETIME(2256),//预定约会结束时间
    
    DUNGEON_LOG_ACTIVITY_START(2301),// 副本日志：活动关卡开始
    DUNGEON_LOG_ACTIVITY_OVER(2302),// 副本日志：活动关卡结束
    DUNGEON_LOG_DATING_START(2303),// 副本日志：约会关卡开始
    DUNGEON_LOG_DATING_OVER(2304),// 副本日志：约会关卡结束
    DUNGEON_LOG_GENERAL_START(2305),// 副本日志：普通关卡开始
    DUNGEON_LOG_GENERAL_OVER(2306),// 副本日志：普通关卡结束
    DUNGEON_LOG_ENDLESS_START(2307),// 副本日志：无尽关卡开始
    DUNGEON_LOG_ENDLESS_OVER(2308),// 副本日志：无尽关卡结束
    DUNGEON_LOG_KABALA_START(2307),// 副本日志：卡巴拉关卡开始
    DUNGEON_LOG_KABALA_OVER(2308),// 副本日志：卡巴拉关卡结束

    // 城市副本约会
    NOVEL_DATING_REWARD(2501), // 副本城市约会奖励
    // 主线约会
    FAVOR_DATING_ADD(2601), // 主线约会道具增加
    FAVOR_DATING_REMOVE(2602), // 主线约会道具删除
    FAVOR_DATING_NODE(2603), // 开始主线约会剧本号
    FAVOR_DATING_SETTLE(2604), // 结束主线约会剧本号

    /** 商店购买 */
    STORE(2301),

    /** 活动领取奖励 月签到 **/
    MONTH_SIGN(2501),
    /** 活动领取奖励 7日签到 **/
    SEVEN_SIGN(2602),
    /** 次日登录奖励 **/
    TOMORROW_SIGN(2703),
    /** 次日登录奖励 **/
    APSUPPLY_SIGN(2804),
    /** 活动商店购买 **/
    ACTIVITY_SHOPPING(2905), ACTIVITY_TASK(2906),

    /** 兼职获取 **/
    GIVE_BUILDING_PTJOB(3001),
    /** 系统刷新城市背包中特有的物品 **/
    BUILDING_SYSTEM_FLUSH(3002),
    /** 领取cook奖励 **/
    GIVE_BY_COOK(3003), GIVE_BUILDING_GAME_PRIZECLAW(3004),
    /** 放弃兼职 **/
    BUILDING_GIVEUP_JOB(3005),
    /** 领取手工奖励 **/
    MANUAL_AWARD(3006),
    /** 手工扣除 **/
    MANUAL_COST(3007),
    /** 料理扣除 **/
    CUISINE_COST(3008),
    /**城市兼职开始时间**/
    PARTTIME_JOB_STARTTIME(3009),
    /**城市兼职放弃时间**/
    PARTTIME_JOB_GIVETIME(3010),
    /**城市兼职结束时间**/
    PARTTIME_JOB_ENDTIME(3011),
    /**城市手工开始时间**/
    MANUAL_STARTTIME(3012),
    /**城市手工结束时间**/
    MANUAL_ENDTIME(3013),
    /**料理开始时间**/
    CUISINE_STARTTIME(3014),
    /**料理结束时间**/
    CUISINE_ENDTIME(3015),
    /** 抓娃娃 **/
    PRIZE_CLAW(3016),


    END(9999);
    private final int value;

    EReason(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public boolean compare(int value) {
        return this.value == value;
    }

    public static EReason getReason(int value) {
        for (EReason type : EReason.values()) {
            if (type.value == value) {
                return type;
            }
        }
        return null;
    }
}
