package logic.store.bean;

import java.util.List;

/**
 * 玩家商城
 */
public class PlayerStore {

    /**
     * id
     */
    private long id;

    /**
     * create_date
     */
    private java.util.Date createDate;

    /**
     * modified_date
     */
    private java.util.Date modifiedDate;

    /**
     * 商店类型
     */
    private int storeId;

    /**
     * 商品列表
     */
    private List<Integer> commodityIdList;

    /**
     * 今日刷新次数
     */
    private int todayRefreshCount;

    /**
     * 总刷新次数
     */
    private int totalRefreshCount;

    /**
     * 最后刷新时间
     */
    private java.util.Date lastAutoRefreshTime;
    /**
     * 下一次刷新时间
     */
    private java.util.Date nextRefreshTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public java.util.Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(java.util.Date createDate) {
        this.createDate = createDate;
    }

    public java.util.Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(java.util.Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public List<Integer> getCommodityIdList() {
        return commodityIdList;
    }

    public void setCommodityIdList(List<Integer> commodityIdList) {
        this.commodityIdList = commodityIdList;
    }

    public int getTodayRefreshCount() {
        return todayRefreshCount;
    }

    public void setTodayRefreshCount(int todayRefreshCount) {
        this.todayRefreshCount = todayRefreshCount;
    }

    public int getTotalRefreshCount() {
        return totalRefreshCount;
    }

    public void setTotalRefreshCount(int totalRefreshCount) {
        this.totalRefreshCount = totalRefreshCount;
    }

    public java.util.Date getLastAutoRefreshTime() {
        return lastAutoRefreshTime;
    }

    public void setLastAutoRefreshTime(java.util.Date lastAutoRefreshTime) {
        this.lastAutoRefreshTime = lastAutoRefreshTime;
    }

    public PlayerStore(long id, int storeId, List<Integer> commodityIdList, int todayRefreshCount,
            int totalRefreshCount, java.util.Date lastAutoRefreshTime,
            java.util.Date nextRefreshTime) {
        super();
        this.id = id;
        this.createDate = new java.util.Date();
        this.modifiedDate = new java.util.Date();
        this.storeId = storeId;
        this.commodityIdList = commodityIdList;
        this.todayRefreshCount = todayRefreshCount;
        this.totalRefreshCount = totalRefreshCount;
        this.lastAutoRefreshTime = lastAutoRefreshTime;
        this.nextRefreshTime = nextRefreshTime;
    }

    public void reset() {
        this.todayRefreshCount = 0;
    }

    public java.util.Date getNextRefreshTime() {
        return nextRefreshTime;
    }

    public void setNextRefreshTime(java.util.Date nextRefreshTime) {
        this.nextRefreshTime = nextRefreshTime;
    }

}
