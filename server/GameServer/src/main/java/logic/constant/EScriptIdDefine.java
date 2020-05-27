package logic.constant;

/**
 * @rules 脚本编号 脚本Id分配规则为: 编号 0 为禁用id 1. 99 （包含99）以下的ID为特殊脚本。 2. 100 - 199 为道具使用脚本 3. 其余功能脚本规则为:
 *        1000(4位功能Id 对应消息ID) + 01 (2位方法Id) 比如: 10010 为某个功能的脚本Id
 * @since 1.0.0
 */
public enum EScriptIdDefine {
    // 注意,临时脚本以负数开头
    // 分布时没有的脚本一定要注意id序号,不能重复进行覆盖.
    /**
     * 类加载器检查类
     */
    LOADCHECK_SCRIPT(1), SHUTDOWN_SCRIPT(2),
    /**
     * http 命令处理脚本
     */
    HTTPEXECUTE_SCRIPTID(10), HTTP_TEST(11), FIX_BUG(12), FIX_BUG_LOGIC(13), GM_HTTP_SCRIPTID(
            14), PROGRAM_HTTP_SCRIPTID(15), FIX_BUG_PlAYER(16),
    
    // 100 - 199 为道具使用脚本
    ITEM_BASIC(100),
    /** 英雄 **/
    ITEM_HERO(101),
    /** 装备 **/
    ITEM_EQUIP(102),
    /** 代币 **/
    ITEM_TOKEN(105),   
    /** 材料 **/
    ITEM_MATERIALS(106),
    /** 礼包 **/
    ITEM_PACKAGE(107),
    /** 掉落 **/
    ITEM_DROP(121),
    /** 入包 **/
    PUT_TRIGGER(200),
    /** TICK 清理已断开连接（状态异常的连接） **/
    PlAYER(201),
    // ————————————————————————————

    /**
     * 公共方法脚本
     */
    COMMON_SCRIPT(10000),
    /**
     * 修复脚本定时器
     */
    COMMON_FIX_TIMER_SCRIPT(10001),
    /**
     * 登录逻辑处理脚本
     */
    LOGIN_SCRIPT(100101),
    /**
     * 登录检查逻辑处理脚本
     */
    LOGIN_CHECK_SCRIPT(100102),
    /** 背包处理脚本 */
    BACKPACK_SCRIPT(100501),

    /** 英雄脚本处理器 */
    HERO_SCRIPT(100301),

    /** 装备脚本处理器 */
    EQUIP_SCRIPT(100401),
    /** 抽奖处理脚本 */
    SUMMON_SCRIPT(100302),
    /** 图鉴处理脚本 */
    ELEMENT_COLLECTION_SCRIPT(100303),
    /** 触发事件与结果 */
    TRIGGER_EVENT_SCRIPT(100304),
    /** 商店处理器 */
    STORE_SCRIPT(100801),
    
    /** 签到 **/
    MONTH_SIGN_SCRIPT(100851),
    /** 次日奖励 **/
    TOMORROW_SIGN_SCRIPT(100852),
    /** 体力补给 **/
    ENERGY_SIGN_SCRIPT(100853),
    /** 体力补给容器 **/
    ENERGY_CONTAINER_SCRIPT(100854),
    /** 信息管理器 **/
    INFO_MANAGER_SCRIPT(100855),
    /**7日登录管理**/
    SEVEN_MANAGER_SCRIPT(100856),
    
    
    /** 任务处理器 */
    TASK_SCRIPT(100901),
    
    /** 邮件脚本 */
    MAILMANAGER_SCRIPT(100601), MAILSERVICE_SCRIPT(100602), MAIL_SCRIPT(100603),
    /** 副本 */
    DUNGEON_ACT_SCRIPT(100701), DUNGEON_MAIN_SCRIPT(100702), DUNGEON_GENERAL_SCRIPT(100703),
    DUNGEON_DATING_SCRIPT(100704), DUNGEON_MANAGER_SCRIPT(100705), DUNGEON_BASE_SCRIPT(100706),
    DUNGEON_CHECK_SCRIPT(100707),
    /** 队伍脚本 */
    TEAM_SCRIPT(100711),
    /** 深渊副本 */
    CHASM_DUNGEON_SCRIPT(100712),
    /** 无尽副本 */
    ENDLESS_DUNGEON_SCRIPT(100713),
    /** 主线约会 **/
    FAVOR_DATING_SCRIPT(100870),
    /** 副本约会 **/
    NOVEL_DATING_SCRIPT(100871),
    /** 支付 */
    PAY_SCRIPT(101001),
    /** 礼包码兑换 */
    GIFT_CODE_SCRIPT(101101),
    
    
    
    /**城市信息**/
    CITY_INFO_SCRIPT(101401),
    /**城市精灵**/
    CITY_ROLE_SCRIPT(101403),
    /**城市料理**/
    CITY_CUISINE_SCRIPT(101404),
    /**城市手工**/
    CITY_MANUAL_SCRIPT(101405),
    /**城市建筑**/
    CITY_NEWBUILDING_SCRIPT(101406),
    /**城市兼职**/
    CITY_PART_JOB_SCRIPT(101407),
    
    /**约会触发器**/
    DATING_TRIGGER_SCRIPT(101501),
    /**预定约会工具脚本**/
    BASERESERVEDATING_SCRIPT(101502),
    /**出游约会工具脚本**/
    BASETRIPDATING_SCRIPT(101503),
    /**预定约会**/
    RESERVEDATINGTRIGGER_SCRIPT(101504),
    /**出游约会**/
    TRIPDATING_SCRIPT(101505),
    /**约会管理器**/
    DATINGMANAGER_SCRIPT(101506),
    /**约会逻辑处理**/
    DATINGSERVICE_SCRIPT(101507),
    /**约会逻辑处理器**/
    DATING_HANDLER_SCRIPT(101508),
    /**日常处理器**/
    DAILLY_HANDLERHANDLER_SCRIPT(101509),
    /**关卡处理器**/
    DUNGEONG_HANDLER_SCRIPT(101510),
    /**手机约会**/
    PHONE_HANDLERE_SCRIPT(101511),
    /**预定约会**/
    RESERVE_HANDLER_SCRIPT(101512),
    /**事件约会**/
    TRIGGER_HANDLER_SCRIPT(101513),
    /**出游约会**/
    TTRIP_HANDLER_SCRIPT(101514),
    
    /**活动**/
    ACTIVITYMANAGER_SCRIPT(101601),
    /**活动数据管理**/
    ACTIVITYDATEPROVIDE_SCRIPT(101602),
    /**活动协议**/
    ACTIVITYCMDUTILS_SCRIPT(101603),
    /**商品活动helper**/
    ACTIVITY_SHOP_HELPER_SCRIPT(101604),
    /**任务活动helper**/
    ACTIVITY_TASK_HELPER_SCRIPT(101605),
    /** 活动重载lua **/
    ACTIVITY_RELOAD_LUA_SCRIPT(101606),
    
    /** 好友脚本 */
    FRIEND_SCRIPT(101201),
    
    /** 评价脚本 */
    COMMENT_SCRIPT(101301),

    /** 数据库脚本 **/
    DB_ROLE(200000),

    /**
     * 脚本ID结束
     */
    END_SCRIPT;

    private final int value;

    private static class Counter {
        private static int nextValue = 0;
    }

    EScriptIdDefine() {
        this(++Counter.nextValue);
    }

    EScriptIdDefine(int value) {
        this.value = value;
        Counter.nextValue = value;
    }

    public int Value() {
        return this.value;
    }
}
