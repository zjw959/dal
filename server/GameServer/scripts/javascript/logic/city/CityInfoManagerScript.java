package javascript.logic.city;

import java.util.List;
import java.util.Map;

import logic.character.bean.Player;
import logic.city.build.bean.CityRefreshTime;
import logic.city.script.ICityInfoManagerScript;
import logic.constant.EAcrossDayType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.ItemConstantId;
import logic.item.ItemUtils;
import logic.item.bean.Item;
import logic.msgBuilder.ItemMsgBuilder;
import logic.support.MessageUtils;

import org.game.protobuf.s2c.S2CItemMsg.ItemList;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import utils.DateEx;
import data.GameDataManager;
import data.bean.ItemRecoverCfgBean;

/***
 * 城市信息脚本
 * 
 * @author lihongji
 *
 */

public class CityInfoManagerScript implements ICityInfoManagerScript {

	/**返回脚本的Id**/
	@Override
	public int getScriptId() {
		return EScriptIdDefine.CITY_INFO_SCRIPT.Value();
	}

	/** 增加城市道具 **/
	@Override
	public boolean addCityPackageById(int id, int num, Map<Integer, Integer> cityMapPackage) {
		if (num <= 0)
			return descCityPackageById(id, -num, cityMapPackage);
		if (cityMapPackage.get(id) == null) {
			cityMapPackage.put(id, num);
			return true;
		}
		long leftNum = cityMapPackage.get(id);
		if ((leftNum + num) > Integer.MAX_VALUE)
			cityMapPackage.put(id, Integer.MAX_VALUE);
		else
			cityMapPackage.put(id, (int) (leftNum + num));
		return true;

	}

	/** 减少城市道具 **/
	@Override
	public boolean descCityPackageById(int id, int num, Map<Integer, Integer> cityMapPackage) {
		if (num <= 0)
			return false;
		if (cityMapPackage.get(id) == null)
			return false;
		int leftNum = cityMapPackage.get(id);
		cityMapPackage.put(id, leftNum - num > 0 ? leftNum - num : 0);
		return true;
	}

	/**获取道具**/
	@Override
	public int getCityPackageById(int id, Map<Integer, Integer> cityMapPackage) {
		if (cityMapPackage.get(id) == null)
			return 0;
		return cityMapPackage.get(id);
	}

	/** 获取精力的上线 **/
	@Override
	public int getLevelMaxCityEnergy(Player player) {
		int max = GameDataManager.getLevelUpCfgBean(player.getLevel()).getMaxVigour();
		return max;
	}

	/** 检测精力 **/
	@Override
	public void cityEnergyTick(Player player, CityRefreshTime refreshTime, Map<Integer, Integer> cityMapPackage) {
		if (refreshTime.getLastRecoverCityEnergyTime() == 0) {
			return;
		}
		int max = getLevelMaxCityEnergy(player);// 当前等级精力上限
		int now = getCityEnergy(cityMapPackage);
		if (now >= max) {
			return;
		}
		ItemRecoverCfgBean bean = getItemRecoverCfg(ItemConstantId.CITY_ENERGY);
		long nowTime = System.currentTimeMillis();
		long _time = nowTime - refreshTime.getLastRecoverCityEnergyTime();
		if (_time >= (bean.getCooldown() * DateEx.TIME_SECOND)) {
			int _times = (int) (_time / (bean.getCooldown() * DateEx.TIME_SECOND));
			int _recover = _times * bean.getRecoverCount();
			boolean change = changeCityEnergy(_recover, false, player, cityMapPackage, refreshTime);
			if (change) {
				sendCityEnergyUpdate(player, cityMapPackage);
			}
			refreshTime.setLastRecoverCityEnergyTime(System.currentTimeMillis());
		}
	}

	/** 改变精力 **/
	@Override
	public boolean changeCityEnergy(int num, boolean isForce, Player player, Map<Integer, Integer> cityMapPackage,
			CityRefreshTime refreshTime) {
		// 当前等级精力上限
		int levelMax = getLevelMaxCityEnergy(player);
        int cityEnergy = getCityEnergy(cityMapPackage);
		int max = levelMax;
		if (isForce) {
			max = GameDataManager.getBaseGoods(ItemConstantId.CITY_ENERGY).getTotalMax();
        } else {
            if (cityEnergy >= max) {
                return false;
            }
		}
		int total = cityEnergy + num;
		if (total < 0) {
			if (num > 0) {// 超上限
				total = max;
			} else {
				total = 0;
			}
		}
		if (total > max) {
			total = max;
		}
		if (total != cityEnergy) {
			cityEnergy = total;
			cityMapPackage.put(ItemConstantId.CITY_ENERGY, total);
			if (cityEnergy >= levelMax) {// 精力达到当前等级上限，就把上次恢复时间置0
				refreshTime.setLastRecoverCityEnergyTime(0);
			} else {
				if (refreshTime.getLastRecoverCityEnergyTime() == 0) {// 精力未满，且精力之前是饱和状态时，重置上次恢复时间为当前时间
					refreshTime.setLastRecoverCityEnergyTime(System.currentTimeMillis());
				}
			}
			return true;
		}
		return false;
	}

	/** 获取精力恢复的配置 **/
	@Override
	public ItemRecoverCfgBean getCityEnergyRecoverCfg() {
		List<ItemRecoverCfgBean> list = GameDataManager.getItemRecoverCfgBeans();
		for (ItemRecoverCfgBean bean : list) {
			if (bean.getItemId() == ItemConstantId.CITY_ENERGY) {
				return bean;
			}
		}
		return null;
	}

	/** 获取天空币恢复的配置 **/
	@Override
	public ItemRecoverCfgBean getGashaponCountRecoverCfg() {
		List<ItemRecoverCfgBean> list = GameDataManager.getItemRecoverCfgBeans();
		for (ItemRecoverCfgBean bean : list) {
			if (bean.getItemId() == ItemConstantId.DAILY_GASHAPON_COUNT) {
				return bean;
			}
		}
		return null;
	}

	/** 通知客户端精力变化 **/
	@Override
	public void sendCityEnergyUpdate(Player player, Map<Integer, Integer> cityMapPackage) {
		List<Item> items = ItemUtils.createItems(ItemConstantId.CITY_ENERGY, getCityEnergy(cityMapPackage));
		ItemList.Builder itemChange = ItemList.newBuilder();
		ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
		MessageUtils.send(player, itemChange);
	}

	/** 定时刷新道具 **/
	@Override
	public void checkFlushItem(Player player, Map<Integer, Integer> cityMapPackage) {
		// 重置抓娃娃次数
		int num = getCityPackageById(ItemConstantId.DAILY_GASHAPON_COUNT, cityMapPackage);
		ItemRecoverCfgBean bean = getItemRecoverCfg(ItemConstantId.DAILY_GASHAPON_COUNT);
		if (num < bean.getMaxRecoverCount()) {
			player.getBagManager().addItem(ItemConstantId.DAILY_GASHAPON_COUNT, bean.getMaxRecoverCount() - num, true,
					EReason.BUILDING_SYSTEM_FLUSH);
			sendCityDailyGashapon(player, cityMapPackage);
		}
	}

	/** 通知客户端精力变化 **/
	@Override
	public void sendCityDailyGashapon(Player player, Map<Integer, Integer> cityMapPackage) {
		List<Item> items = ItemUtils.createItems(ItemConstantId.DAILY_GASHAPON_COUNT,
				getCityPackageById(ItemConstantId.DAILY_GASHAPON_COUNT, cityMapPackage));
		ItemList.Builder itemChange = ItemList.newBuilder();
		ItemMsgBuilder.addItemMsg(itemChange, ChangeType.ADD, items.get(0));
		MessageUtils.send(player, itemChange);
	}

	/** 跨天 **/
	@Override
	public void acrossDay(EAcrossDayType type, boolean isNotify, Player player, Map<Integer, Integer> cityMapPackage) {
		if (type == EAcrossDayType.GAME_ACROSS_DAY) {
			checkFlushItem(player, cityMapPackage);
		}
	}


	/** 获取精力恢复的配置 **/
	@Override
	public ItemRecoverCfgBean getItemRecoverCfg(int id) {
		List<ItemRecoverCfgBean> list = GameDataManager.getItemRecoverCfgBeans();
		for (ItemRecoverCfgBean bean : list) {
			if (bean.getItemId() == id) {
				return bean;
			}
		}
		return null;
	}

	/** 获取城市精力 **/
	@Override
	public int getCityEnergy(Map<Integer, Integer> cityMapPackage) {
		return getCityPackageById(ItemConstantId.CITY_ENERGY, cityMapPackage);
	}

	@Override
	public void tick(Player player, CityRefreshTime refreshTime, Map<Integer, Integer> cityMapPackage) {
		cityEnergyTick(player, refreshTime, cityMapPackage);
	}

	@Override
	public void createRoleInitialize(Player player, Map<Integer, Integer> cityMapPackage, CityRefreshTime refreshTime) {
		cityMapPackage.put(ItemConstantId.CITY_ENERGY, getLevelMaxCityEnergy(player));
		cityMapPackage.put(ItemConstantId.DAILY_GASHAPON_COUNT, getGashaponCountRecoverCfg().getMaxRecoverCount());
	}

}
