package logic.chasm;

public interface ReqChasmOrder {
    /** 请求进入深渊 */
    public static final int REQ_ENTER_CHASM = 1;
    /** 请求加入GM道具 */
    public static final int REQ_GM_ITEMS = 2;
    /** 请求合成精灵 */
    public static final int REQ_COMPOSE_HERO_ONE = 3;
    /** 请求合成精灵 */
    public static final int REQ_COMPOSE_HERO_TWO = 4;
    /** 请求匹配队伍 */
    public static final int REQ_MATCH_TEAM = 5;
    /** 请求修改队伍状态 */
    public static final int REQ_CHANGE_TEAM_STATUS = 6;
    
//    /** 请求变更队长 */
//    public static final int REQ_TREAT_MEMBER = 5;
    /** 请求切换英雄 */
    public static final int REQ_CHANGE_HERO = 7;
    /** 请求修改成员状态 */
    public static final int REQ_CHANGE_MEMBER_STATUS = 8;
    /** 请求踢人 */
    public static final int REQ_KICK_OUT_MEMBER = 9;
    /** 请求离开队伍 */
    public static final int REQ_LEAVE_TEAM = 10;
}
