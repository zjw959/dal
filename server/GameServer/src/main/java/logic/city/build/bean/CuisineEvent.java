package logic.city.build.bean;

/***
 * 
 * 料理事件
 * 
 * @author lihongji
 *
 */
public class CuisineEvent {

    int cuisineId;
    /** 积分 **/
    int integral;
    /** 结束时间 **/
    long etime;
    /**qte出现的次数**/
    int qteTime;
    
    public CuisineEvent initCuisineEvent(int cuisineId, int integral, long etime) {
        this.cuisineId = cuisineId;
        this.integral = integral;
        this.etime = etime;
        return this;
    }
    
    /**记录qte出现的次数**/
    public void addQteTime()
    {
        qteTime++;
    }

    /** clear **/
    public void clear() {
        this.cuisineId = 0;
        this.integral = 0;
        this.etime = 0;
        this.qteTime=0;
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

    public int getCuisineId() {
        return cuisineId;
    }

    public void setCuisineId(int cuisineId) {
        this.cuisineId = cuisineId;
    }


    public int getQteTime() {
        return qteTime;
    }


    public void setQteTime(int qteTime) {
        this.qteTime = qteTime;
    }
    

}
