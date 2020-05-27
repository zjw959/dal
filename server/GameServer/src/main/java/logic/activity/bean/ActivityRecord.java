package logic.activity.bean;

import java.util.Date;
import logic.activity.constant.ActivityRecordStatus;

/***
 * 
 * @author 玩家活动记录
 *
 */
public class ActivityRecord {

    private int activityId;

    /** 条目id **/
    private int itermId;

    /** 该条目进度 **/
    private int progress;

    /** 该条目领取次数 **/
    private int gotCount;

    /** 额外信息 **/
    private String extra;

    /** 刷新时间 **/
    private Date refreshTime;

    /** 最后修改时间 **/
    private Date lastUpdateTime;

    /** 条目领奖状态 {@link ActivityRecordStatus} **/
    private int status;

    public ActivityRecord() {
        super();
    }

    public ActivityRecord(int activityId, int itermId) {
        this.activityId = activityId;
        this.itermId = itermId;
        this.progress = 0;
        this.gotCount = 0;
        lastUpdateTime = new Date();
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public int getItermId() {
        return itermId;
    }

    public void setItermId(int itermId) {
        this.itermId = itermId;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getGotCount() {
        return gotCount;
    }

    public void setGotCount(int gotCount) {
        this.gotCount = gotCount;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Date getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(Date refreshTime) {
        this.refreshTime = refreshTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }


    public void addGotCount() {
        this.gotCount++;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
