package logic.sign.record;

/***
 * 次日登陆奖励
 * 
 * @author lihongji
 *
 */
public class TomorrowSignRecord {

    public static int CAN_GET = 1, HAVA_GET = 2;

    private int type;

    /** 活动结束时间 **/
    private long endTime;


    /** 重置记录 **/
    public void reset() {
        endTime = 0;
        type = 0;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
