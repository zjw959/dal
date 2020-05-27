package logic.city.build.bean;

/****
 * 
 * 城市刷新时间
 * @author lihongji
 *
 */
public class CityRefreshTime {

//    /** 6点刷新的 **/
//    private long refreshTime;
    /** 城市精力刷新时间 **/
    private long lastRecoverCityEnergyTime;


//    public long getRefreshTime() {
//        return refreshTime;
//    }
//
//    public void setRefreshTime(long refreshTime) {
//        this.refreshTime = refreshTime;
//    }

    public long getLastRecoverCityEnergyTime() {
        return lastRecoverCityEnergyTime;
    }

    public void setLastRecoverCityEnergyTime(long lastRecoverCityEnergyTime) {
        this.lastRecoverCityEnergyTime = lastRecoverCityEnergyTime;
    }

}
