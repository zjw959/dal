package core.event;

/**
 * @function 功能类型枚举(所有的可重复执行的功能都用此枚举)
 */
public enum FunctionType {

    /** 空功能类型，单次请求事件和响应事件调用此选项 **/
    /**
     * 极少的功能处理完以后不会再被运行, 比如精灵,商店.这种功能运行次数为10,并且在逻辑中会执行删除 --> robot.removeCurrentFun();
     **/
    /** (TODO BUG) 标记则不会在功能选择中显示出来 形如:CHASM("深渊(TODO BUG)", 10) **/
    NULL(null), SUMMON("召唤", 100), EQUIP("装备", 100), CHAT("聊天", 100), CHAT_CHANGE("聊天房间更换",
            2), FRIEND("好友", 100), MAIL("邮件", 100), TASK("任务", 10), HERO("精灵", 10), HERO_SKILL(
                    "精灵技能", 10), STORE("商店", 10), REMOVEITEM("清空道具", 1), CHASM("深渊(TODO BUG)",
                            10), CHASM_FIGHT("深渊战斗(TODO BUG)", 10), ROLE("精灵互动", 10), GIFT(
                                    "礼包码(TODO BUG)", 10), CITY("城市", 10), SIGNACTIVITY("签到活动",
                                            10), DATING("约会", 10), DUNGEON("关卡", 100), MAINDATING(
                                                    "主线约会", 100), ENDLESS("无尽回廊", 100),COMMENT("评价", 100),
                                    ACTIVITY("活动",100);

    private FunctionType(String fName, Object... obj) {
        this.fName = fName;
        if (obj != null && obj.length > 0) {
            this.fNum = Integer.parseInt(obj[0].toString());
        }
    }

    /**
     * 功能名称（用于UI显示）
     */
    public String fName;

    /**
     * 功能可执行次数（默认100 0为不限制）
     */
    public int fNum = 100;
}
