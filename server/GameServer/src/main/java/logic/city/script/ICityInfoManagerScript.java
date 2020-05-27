package logic.city.script;

import java.util.Map;
import data.bean.ItemRecoverCfgBean;
import logic.character.bean.Player;
import logic.city.build.bean.CityRefreshTime;
import logic.constant.EAcrossDayType;
import script.IScript;

public interface ICityInfoManagerScript extends IScript {

	/** 增加城市道具 **/
	public boolean addCityPackageById(int id, int num, Map<Integer, Integer> cityMapPackage);

	/** 减少城市道具 **/
	public boolean descCityPackageById(int id, int num, Map<Integer, Integer> cityMapPackage);

	/** 获取道具 **/
	public int getCityPackageById(int id, Map<Integer, Integer> cityMapPackage);

	/** 获取精力的上线 **/
	public int getLevelMaxCityEnergy(Player player);

	/** 检测精力 **/
	public void cityEnergyTick(Player player, CityRefreshTime refreshTime, Map<Integer, Integer> cityMapPackage);

	/** 改变精力 **/
	public boolean changeCityEnergy(int num, boolean isForce, Player player, Map<Integer, Integer> cityMapPackage,
			CityRefreshTime refreshTime);

	/** 获取精力恢复的配置 **/
	public ItemRecoverCfgBean getCityEnergyRecoverCfg();

	/** 获取天空币恢复的配置 **/
	public ItemRecoverCfgBean getGashaponCountRecoverCfg();

	/** 通知客户端精力变化 */
	public void sendCityEnergyUpdate(Player player, Map<Integer, Integer> cityMapPackage);

	/** 定时刷新道具 **/
	public void checkFlushItem(Player player, Map<Integer, Integer> cityMapPackage);

	/** 通知客户端精力变化 */
	public void sendCityDailyGashapon(Player player, Map<Integer, Integer> cityMapPackage);

	/** 跨天 **/
	public void acrossDay(EAcrossDayType type, boolean isNotify, Player player, Map<Integer, Integer> cityMapPackage);

	/** 获取精力恢复的配置 **/
	public ItemRecoverCfgBean getItemRecoverCfg(int id);

	public int getCityEnergy(Map<Integer, Integer> cityMapPackage);

	public void tick(Player player, CityRefreshTime refreshTime, Map<Integer, Integer> cityMapPackage);

	public void createRoleInitialize(Player player, Map<Integer, Integer> cityMapPackage, CityRefreshTime refreshTime);

}
