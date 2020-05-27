package logic.city.build.bean;

/***
 * 手工事件
 * 
 * @author lihongji
 *
 */
public class ManualEvent {


    int manualId;
    /** 积分 **/
    int integral;
    /** 结束时间 **/
    long etime;
    /** 操作出现的次数 **/
    int times;

    public ManualEvent initManualEvent(int manualId, int integral, long etime) {
        this.manualId = manualId;
        this.integral = integral;
        this.etime = etime;
        return this;
    }

    /** 记录出现的次数 **/
    public void addTimes() {
        times++;
    }

    /** clear **/
    public void clear() {
        this.manualId = 0;
        this.integral = 0;
        this.etime = 0;
        this.times = 0;
    }


    public void addIntegral(int integral) {
        this.integral += integral;
    }


    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public long getEtime() {
        return etime;
    }

    public void setEtime(long etime) {
        this.etime = etime;
    }

    public int getManualId() {
        return manualId;
    }

    public void setManualId(int manualId) {
        this.manualId = manualId;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    
    
}
