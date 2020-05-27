package logic.role.bean;

/***
 * 
 * 精灵状态
 * 
 * @author lihongji
 *
 */
public class RoleState {

    /** buff消失时间 **/
    long endtime;
    /**状态**/
    int state;

    /** 初始化 **/
    public void init(long endtime, int state) {
        this.endtime = endtime;
        this.state = state;
    }


    public long getEndtime() {
        return endtime;
    }

    public void setEndtime(long endtime) {
        this.endtime = endtime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
