package logic.heroskill;

public interface ReqHeroSkillOrder {
    /** 请求加入GM道具 */
    public static final int REQ_GM_ITEMS = 1;
    /** 请求升级技能 */
    public static final int REQ_UPGRADE_SKILL = 2;
    /** 请求修改名字 */
    public static final int REQ_MODIFY_STRATEGY_NAME = 3;
    /** 请求装备 */
    public static final int REQ_EQUIP_PASSIVE_SKILL = 4;
    /** 卸下被动技能 */
    public static final int REQ_UNLOAD_PASSIVE_SKILL = 5;
    /** 请求觉醒天使 */
    public static final int REQ_WAKE_ANGEL = 6;
    /** 请求使用天使页 */
    public static final int REQ_USE_SKILL_STRATEGY = 7;
    /** 请求激活结晶 */
    public static final int REQ_ACTIVE_CRYSTAL = 8;
}
