package logic.city;

import java.util.HashMap;
import java.util.Map;
import logic.basecore.IAcrossDay;
import logic.basecore.ICreateRoleInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.city.build.bean.CityRefreshTime;
import logic.city.script.ICityInfoManagerScript;
import logic.constant.EAcrossDayType;
import logic.support.LogicScriptsUtils;

/****
 * 城市信息管理器
 * 
 * @author lihongji
 *
 */
public class CityInfoManager extends PlayerBaseFunctionManager
        implements ICreateRoleInitialize, IRoleJsonConverter, IAcrossDay {

    /** 城建背包 **/
    Map<Integer, Integer> cityMapPackage = new HashMap<Integer, Integer>();
    /** 城建刷新时间 **/
    CityRefreshTime refreshTime = new CityRefreshTime();


    private ICityInfoManagerScript getManagerScript() {
        return LogicScriptsUtils.getICityInfoManagerScript();
    }

    @Override
    public void createRoleInitialize() {
        getManagerScript().createRoleInitialize(player, cityMapPackage,refreshTime);
    }

    /** ===================城市包裹====================== **/
    /** 增加物品 **/
    public boolean addCityPackageById(int id, int num) {
        return getManagerScript().addCityPackageById(id, num, cityMapPackage);
    }

    /** 减少物品 **/
    public boolean descCityPackageById(int id, int num) {
        return getManagerScript().descCityPackageById(id, num, cityMapPackage);
    }

    /** 获取当前物品的数据 **/
    public int getCityPackageById(int id) {
        return getManagerScript().getCityPackageById(id, cityMapPackage);
    }

    /** 获取当前等级精力上限 */
    public int getLevelMaxCityEnergy() {
       return getManagerScript().getLevelMaxCityEnergy(player);
    }

    /**
     * 改变精力
     * 
     * @param num
     * @param isForce 是否可以强制超过当前等级的精力上限
     * @param isNotify
     * @return
     */
    public boolean changeCityEnergy(int num, boolean isForce) {
        return getManagerScript().changeCityEnergy(num, isForce, player, cityMapPackage,
                refreshTime);
    }


    public int getCityEnergy() {
        return getManagerScript().getCityEnergy(cityMapPackage);

    }


    /** 通知客户端精力变化 */
    public void sendCityEnergyUpdate() {
        getManagerScript().sendCityEnergyUpdate(player, cityMapPackage);
    }


    /** 定时刷新道具 **/
    public void checkFlushItem() {
        getManagerScript().checkFlushItem(player, cityMapPackage);
    }


    /** 通知客户端精力变化 */
    public void sendCityDailyGashapon() {
        getManagerScript().sendCityDailyGashapon(player, cityMapPackage);
    }

    @Override
    public void tick() {
        getManagerScript().tick(player, refreshTime, cityMapPackage);
    }

    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        getManagerScript().acrossDay(type, isNotify, player, cityMapPackage);
    }
}
