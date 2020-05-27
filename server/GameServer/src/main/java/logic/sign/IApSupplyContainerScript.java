package logic.sign;

import script.IScript;

/***
 * 体力补给
 * 
 * @author lihongji
 *
 */
public abstract class IApSupplyContainerScript implements IScript {

    /** 初始化数据 **/
    public abstract void init();

    /** 检测是否能够刷新 **/
    public abstract boolean checkSendEnergyTime(long[] energyTime, long nextTime);

    /** 推送 **/
    public abstract void push(long energyTime[], long nextTime);
}
