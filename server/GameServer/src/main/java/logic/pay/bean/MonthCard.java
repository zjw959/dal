package logic.pay.bean;


/**
 * 
 * @Description 月卡
 * @author LiuJiang
 * @date 2018年7月14日 下午3:39:59
 *
 */
public class MonthCard {
    /** 配置模板id */
    int cid;
    /** 上次领取时间 */
    long lastGainTime;
    /** 失效时间 */
    long endTime;

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public long getLastGainTime() {
        return lastGainTime;
    }

    public void setLastGainTime(long lastGainTime) {
        this.lastGainTime = lastGainTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void clear() {
        this.cid = 0;
        this.endTime = 0;
    }
}
