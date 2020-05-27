/**
 * Auto generated, do not edit it
 *
 * 缓存监听器
 */
package data.listener;

import data.GameDataManager;
import java.util.Map;


public class BaseGoodsListener implements GameDataListener
{
    @Override
    public void beforeLoad(GameDataManager gameDataManager) throws Exception {
        
    }

    @Override
    public void postLoad(GameDataManager gameDataManager) throws Exception {
        Map<Integer, data.bean.BaseGoods> baseGoodsCache = gameDataManager.getInstancebaseGoodsCacheKV();
	baseGoodsCache.clear();


	Map<Integer, data.bean.DressCfgBean> dressCfgContainerKV = gameDataManager.getInstanceDressCfgBeanKV();
	for(Map.Entry<Integer, data.bean.DressCfgBean> entry : dressCfgContainerKV.entrySet()) {
	    data.bean.BaseGoods preValue = baseGoodsCache.putIfAbsent(entry.getKey(), entry.getValue());
	    if(preValue != null) {
	        throw new IllegalAccessException("duplicate key ["+ entry.getKey() +"] exists, previous value ["+ preValue +"]. Current value ["+ entry.getValue() +"]");
	    }
	}

	Map<Integer, data.bean.EquipmentCfgBean> equipmentCfgContainerKV = gameDataManager.getInstanceEquipmentCfgBeanKV();
	for(Map.Entry<Integer, data.bean.EquipmentCfgBean> entry : equipmentCfgContainerKV.entrySet()) {
	    data.bean.BaseGoods preValue = baseGoodsCache.putIfAbsent(entry.getKey(), entry.getValue());
	    if(preValue != null) {
	        throw new IllegalAccessException("duplicate key ["+ entry.getKey() +"] exists, previous value ["+ preValue +"]. Current value ["+ entry.getValue() +"]");
	    }
	}

	Map<Integer, data.bean.HeroCfgBean> heroCfgContainerKV = gameDataManager.getInstanceHeroCfgBeanKV();
	for(Map.Entry<Integer, data.bean.HeroCfgBean> entry : heroCfgContainerKV.entrySet()) {
	    data.bean.BaseGoods preValue = baseGoodsCache.putIfAbsent(entry.getKey(), entry.getValue());
	    if(preValue != null) {
	        throw new IllegalAccessException("duplicate key ["+ entry.getKey() +"] exists, previous value ["+ preValue +"]. Current value ["+ entry.getValue() +"]");
	    }
	}

	Map<Integer, data.bean.HeroSkinCfgBean> heroSkinCfgContainerKV = gameDataManager.getInstanceHeroSkinCfgBeanKV();
	for(Map.Entry<Integer, data.bean.HeroSkinCfgBean> entry : heroSkinCfgContainerKV.entrySet()) {
	    data.bean.BaseGoods preValue = baseGoodsCache.putIfAbsent(entry.getKey(), entry.getValue());
	    if(preValue != null) {
	        throw new IllegalAccessException("duplicate key ["+ entry.getKey() +"] exists, previous value ["+ preValue +"]. Current value ["+ entry.getValue() +"]");
	    }
	}

	Map<Integer, data.bean.ItemCfgBean> itemCfgContainerKV = gameDataManager.getInstanceItemCfgBeanKV();
	for(Map.Entry<Integer, data.bean.ItemCfgBean> entry : itemCfgContainerKV.entrySet()) {
	    data.bean.BaseGoods preValue = baseGoodsCache.putIfAbsent(entry.getKey(), entry.getValue());
	    if(preValue != null) {
	        throw new IllegalAccessException("duplicate key ["+ entry.getKey() +"] exists, previous value ["+ preValue +"]. Current value ["+ entry.getValue() +"]");
	    }
	}

	Map<Integer, data.bean.ItemTimeCfgBean> itemTimeCfgContainerKV = gameDataManager.getInstanceItemTimeCfgBeanKV();
	for(Map.Entry<Integer, data.bean.ItemTimeCfgBean> entry : itemTimeCfgContainerKV.entrySet()) {
	    data.bean.BaseGoods preValue = baseGoodsCache.putIfAbsent(entry.getKey(), entry.getValue());
	    if(preValue != null) {
	        throw new IllegalAccessException("duplicate key ["+ entry.getKey() +"] exists, previous value ["+ preValue +"]. Current value ["+ entry.getValue() +"]");
	    }
	}

	Map<Integer, data.bean.MedalCfgBean> medalCfgContainerKV = gameDataManager.getInstanceMedalCfgBeanKV();
	for(Map.Entry<Integer, data.bean.MedalCfgBean> entry : medalCfgContainerKV.entrySet()) {
	    data.bean.BaseGoods preValue = baseGoodsCache.putIfAbsent(entry.getKey(), entry.getValue());
	    if(preValue != null) {
	        throw new IllegalAccessException("duplicate key ["+ entry.getKey() +"] exists, previous value ["+ preValue +"]. Current value ["+ entry.getValue() +"]");
	    }
	}

	Map<Integer, data.bean.RoomCfgBean> roomCfgContainerKV = gameDataManager.getInstanceRoomCfgBeanKV();
	for(Map.Entry<Integer, data.bean.RoomCfgBean> entry : roomCfgContainerKV.entrySet()) {
	    data.bean.BaseGoods preValue = baseGoodsCache.putIfAbsent(entry.getKey(), entry.getValue());
	    if(preValue != null) {
	        throw new IllegalAccessException("duplicate key ["+ entry.getKey() +"] exists, previous value ["+ preValue +"]. Current value ["+ entry.getValue() +"]");
	    }
	}

    }
}