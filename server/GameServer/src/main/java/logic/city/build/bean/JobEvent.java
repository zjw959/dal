package logic.city.build.bean;

/**
 * 
 * 兼职事件
 * 
 * @author lihongji
 *
 */
public class JobEvent {

    /** 建筑id **/
    int buildingId;
    /** 结束时间 **/
    long etime;
    /** 兼职id **/
    int jobId;
    /** 刷新时间 **/
    long refreshTime;
    /** 白天还是黑夜 **/
    int type;


    public JobEvent() {

    }

    /***
     * 
     * @param buildingId 建筑id
     * @param funId 功能id
     * @param etime 结束时间
     * @param jobId 兼职id
     */
    public JobEvent init(int buildingId, long etime, int jobId, int refreshTime, int type) {
        this.buildingId = buildingId;
        this.etime = etime;
        this.jobId = jobId;
        this.refreshTime = refreshTime;
        this.type = type;
        return this;
    }

    /** 清空信息 **/
    public void clear() {
        this.buildingId = 0;
        this.etime = 0;
        this.jobId = 0;
        this.refreshTime = 0;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }


    public long getEtime() {
        return etime;
    }

    public void setEtime(long etime) {
        this.etime = etime;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public long getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(long refreshTime) {
        this.refreshTime = refreshTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


}
