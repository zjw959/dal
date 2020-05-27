package logic;

/**
 * 主要处理登陆流程
 * 
 * 不要在登陆里面获取道具 在功能模块内获取自己需要的道具 并销毁
 */
public interface ReqOnceOrder {
    /** 请求登录 */
    public static final int REQ_LOGIN = 1;
    /** 玩家改名 */
    public static final int REQ_CHANGE_NAME = 2;
    /** 清除非使用道具 **/
    public static final int REQ_REMOVE_UNUSEDITEMS = 3;
    /** 不要 登陆里面 获取道具 **/
    /** 在功能模块内获取自己需要的道具 并销毁 **/
    /** 获取背包 并销毁没有使用的道具 */
    public static final int REQ_ITEM_LIST = 4;
    /** 请求得到英雄列表 */
    public static final int REQ_GET_HERO_LIST = 5;
    /** 进入房间 **/
    public static final int REQ_CHAT_INIT_INFO = 6;
    /** 更换房间 **/
    public static final int REQ_CHAT_CHANGE_ROOM = 7;
    /** 请求得到任务列表 */
    public static final int REQ_GET_TASK_LIST = 8;
    /** 请求得到商店信息 */
    // public static final int REQ_GET_STORE_LIST = 7;
    /** 请求得到邮件 */
    public static final int REQ_GET_MAILS = 9;
    /**请求建筑信息**/
    public static final int REQ_GET_CITY=10;
    /**获取所有的签到信息**/
    public static final int REQ_GET_SIGNINFO=11;
    /**登录步骤**/
    public static final int REQ_GET_FRIEND = 12;
}
