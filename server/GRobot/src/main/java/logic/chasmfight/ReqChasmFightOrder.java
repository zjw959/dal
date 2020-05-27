package logic.chasmfight;

public interface ReqChasmFightOrder {
    /** 请求进入深渊 */
    public static final int REQ_ENTER_CHASM = 1;
    /** 请求加入GM道具 */
    public static final int REQ_GM_ITEMS = 2;
    /** 请求创建队伍 */
    public static final int REQ_CREATE_TEAM = 3;
    /** 请求开始战斗 */
    public static final int REQ_CHASM_START_FIGHT = 4;
    /** 请求进入战斗 */
    public static final int REQ_ENTER_FIGHT = 5;
    /** 请求战斗操作 */
    public static final int REQ_OPERATE_FIGHT = 6;
    /** 请求战斗操作(无限次) */
    public static final int REQ_OPERATE_FIGHT_UNLIMITED = 7;
    /** 请求结束战斗 */
    public static final int REQ_END_FIGHT = 8;
}
