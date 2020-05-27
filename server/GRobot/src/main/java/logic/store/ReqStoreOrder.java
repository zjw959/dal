package logic.store;

public interface ReqStoreOrder {
    public int REQ_GET_STORE_LIST = 1;
    /** 获取所有购买道具GM */
    public int REQ_BUY_ITEM_GM = 2;
    /** 请求购买记录 */
    public int BUY_HISTORY = 3;
    /** 购买 */
    public int BUY_GOODS = 4;
    /** 删除 */
    public int DELETE_GOODS = 5;
}
