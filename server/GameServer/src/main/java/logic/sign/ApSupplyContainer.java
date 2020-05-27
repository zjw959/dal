package logic.sign;

import java.util.concurrent.atomic.AtomicBoolean;
import logic.support.LogicScriptsUtils;

/***
 * 体力管理容器
 * 
 * @author lihongji
 *
 */
public class ApSupplyContainer {

    /** 当前容器运行状态 */
    AtomicBoolean running = new AtomicBoolean(false);
    /** 时间点 **/
    long energyTime[];

    /** 下次刷新时间 **/
    long nextTime;

    long[] sendEnergyTime = new long[2];

    int giftId;
    
    /** 初始化 **/
    public void init() {
        getManagerScript().init();
    }

    private IApSupplyContainerScript getManagerScript() {
        return LogicScriptsUtils.getIApSupplyContainerScript();
    }


    /** 定时检测 **/
    public void tick() {
        if (!running.compareAndSet(false, true))
            return;
        try {
            push();
        } finally {
            running.set(false);
        }

    }

    //推送
    public void push() {
        getManagerScript().push(energyTime, nextTime);
    }

    /** 初始化下一次的刷新时间 **/
    public void initData(long[] sendEnergyTime, long nextTime, int giftId) {
        this.sendEnergyTime = sendEnergyTime;
        this.nextTime = nextTime;
        this.giftId = giftId;
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {
        INSTANCE;
        ApSupplyContainer apSupplyContainer;

        Singleton() {
            this.apSupplyContainer = new ApSupplyContainer();
        }

        ApSupplyContainer getApSupplyContainer() {
            return apSupplyContainer;
        }
    }

    /**
     * 获取单例对象
     * 
     * @return
     */
    public static ApSupplyContainer getInstance() {
        return Singleton.INSTANCE.getApSupplyContainer();
    }


    public long[] getEnergyTime() {
        return energyTime;
    }


    public void setEnergyTime(long[] energyTime) {
        this.energyTime = energyTime;
    }


    public long getNextTime() {
        return nextTime;
    }


    public void setNextTime(long nextTime) {
        this.nextTime = nextTime;
    }


    public long[] getSendEnergyTime() {
        return sendEnergyTime;
    }


    public void setSendEnergyTime(long[] sendEnergyTime) {
        this.sendEnergyTime = sendEnergyTime;
    }


    public int getGiftId() {
        return giftId;
    }


    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }



}
