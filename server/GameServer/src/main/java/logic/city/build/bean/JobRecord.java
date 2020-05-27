package logic.city.build.bean;

import java.util.Map;

public class JobRecord {

    /** 刷新时间 **/
    int refreshTime;
    /** 兼职id **/
    int jobId;
    /** 奖励 **/
    Map<Integer,Integer> gift;
    /** 额外奖励 **/
    Map<Integer,Integer> extraGift;
    /** 兼职类型(是白天还是黑夜) **/
    int type;
    /** 兼职的状态 **/
    int jobType;

    public int getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(int refreshTime) {
        this.refreshTime = refreshTime;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public Map<Integer,Integer> getGift() {
        return gift;
    }

    public void setGift(Map<Integer,Integer> gift) {
        this.gift = gift;
    }

    public Map<Integer,Integer> getExtraGift() {
        return extraGift;
    }

    public void setExtraGift(Map<Integer,Integer> extraGift) {
        this.extraGift = extraGift;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getJobType() {
        return jobType;
    }

    public void setJobType(int jobType) {
        this.jobType = jobType;
    }

}
