package logic.sign.record;

/***
 * 
 * 体力补给记录器
 * 
 * @author lihongji
 *
 */
public class ApSupplyRecord {

    /** 补给时间 **/
    private long apSupplyTime;

    public long getApSupplyTime() {
        return apSupplyTime;
    }

    public void setApSupplyTime(long apSupplyTime) {
        this.apSupplyTime = apSupplyTime;
    }

    /** 重置记录 **/
    public void reset() {
        apSupplyTime = 0;
    }

}
