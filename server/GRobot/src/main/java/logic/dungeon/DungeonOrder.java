package logic.dungeon;

public interface DungeonOrder {
    /** 请求关卡信息 */
    public static final int REQ_DUNGEON_INFO = 1;
    /** 请求限定英雄信息 */
    public static final int REQ_LIMITED_HEROS = 2;
    /** 请求购买关卡次数 */
    public static final int REQ_BUY_COUNTS = 3;
    /** 请求关卡开始 */
    public static final int REQ_DUNGEON_START = 4;
    /** 请求关卡结束 */
    public static final int REQ_DUNGEON_END = 5;
    /** 请求关卡组奖励 */
    public static final int REQ_GROUP_REWARD = 6;
    /** 请求恢复上次关卡消耗 */
    public static final int REQ_RWARD_COST_BACK = 7;
}
