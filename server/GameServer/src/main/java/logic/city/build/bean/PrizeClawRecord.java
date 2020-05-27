package logic.city.build.bean;

/**
 * 
 * 抓娃娃游戏记录器
 * 
 * @author lihongji
 *
 */
public class PrizeClawRecord {

    /** 娃娃池的id **/
    private int poolId = 0;
    /** 记录娃娃的排列信息 **/
    private String pool = "";
    /** 结束时间 **/
    private long endTime = 0;
    /** 刷新时间 **/
    private long refreshCD = 0;


    public int getPoolId() {
        return poolId;
    }

    public void setPoolId(int poolId) {
        this.poolId = poolId;
    }

    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getRefreshCD() {
        return refreshCD;
    }

    public void setRefreshCD(long refreshCD) {
        this.refreshCD = refreshCD;
    }


}
