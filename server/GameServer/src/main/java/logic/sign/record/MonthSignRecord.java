package logic.sign.record;

/***
 * 
 * 月签到记录器
 * 
 * @author lihongji
 *
 */
public class MonthSignRecord {


    public static int CAN_GET = 1, HAVA_GET = 2;

    private int id;

    int month;
    /** 领奖的位置 **/
    int index;
    /** 今天是否可以领取奖励 **/
    int type;

    public MonthSignRecord() {

    }

    /** 判断释放需要 **/
    public void checkMonthSignRecord() {

    }

    /** 增加记录 **/
    public void addRecord(int month) {
        this.type = HAVA_GET;
        this.month = month;
    }
    
    public void addAbleAward(int month){
        this.type = CAN_GET;
        this.month = month;
        index++;
    }

    /** 重置记录(直接领奖) **/
    public void reset() {
        id = 0;
        month = 0;
        type = CAN_GET;
        index=1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    

}
