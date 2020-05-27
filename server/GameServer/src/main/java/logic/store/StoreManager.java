package logic.store;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SStoreMsg.SellGoods;
import org.game.protobuf.s2c.S2CStoreMsg;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import data.GameDataManager;
import data.bean.CommodityCfgBean;
import data.bean.StoreCfgBean;
import exception.AbstractLogicModelException;
import logic.basecore.IAcrossDay;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.ITick;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.EAcrossDayType;
import logic.constant.StoreConstant;
import logic.msgBuilder.StoreMsgBuilder;
import logic.store.bean.CommodityBuyRecord;
import logic.store.bean.PlayerStore;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import utils.CronSequenceGenerator;
import utils.ExceptionEx;

public class StoreManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter, ITick, IAcrossDay {

    private Map<Integer, PlayerStore> storeMap = new HashMap<Integer, PlayerStore>();
    private Map<Integer, CommodityBuyRecord> buyRecord = new HashMap<Integer, CommodityBuyRecord>();
    private static final Logger LOGGER = Logger.getLogger(StoreManager.class);

    public Table<Integer, Integer, StoreGrid> getMaping() {
        // if(maping == null || maping.isEmpty()){
        Table<Integer, Integer, StoreGrid> maping = HashBasedTable.create();
        List<CommodityCfgBean> beanList = GameDataManager.getCommodityCfgBeans();
        for (CommodityCfgBean bean : beanList) {
            loadCacheCallBack(bean, maping);
        }
        // }
        return maping;
    }

    /**
     * 获取商店数据
     * 
     * @param session
     * @param storeInfo
     * @throws AbstractLogicModelException
     */
    public void getStoreInfo(Player player, List<Integer> cids) {
        LogicScriptsUtils.getIStoreScript().getStoreInfo(player, player.getStortManager(), cids);
    }

    /**
     * 购买商品
     * 
     * @param player
     * @param cid
     * @param num
     * @param handler 购买成功处理
     */
    public void buy(Player player, int cid, int num) {
        LogicScriptsUtils.getIStoreScript().buy(player, player.getStortManager(), cid, num);

    }

    public void refreshStore(Player player, int cid) {
        LogicScriptsUtils.getIStoreScript().refreshStore(player, player.getStortManager(), cid);
    }

    public void randomCommodity(boolean isLevelUp) {
        // 重置商店
        List<StoreCfgBean> storeCfgList = GameDataManager.getStoreCfgBeans();
        for (StoreCfgBean storeCfg : storeCfgList) {
            PlayerStore store = storeMap.get(storeCfg.getId());
            if (store == null) {
                continue;
            }
            List<CommodityCfgBean> commodityList = LogicScriptsUtils.getIStoreScript()
                    .randomCommodity(player, this, storeCfg.getId(), isLevelUp);
            for (CommodityCfgBean bean : commodityList) {
                store.getCommodityIdList().add(bean.getId());
            }
            // 推送商店信息
            List<StoreCfgBean> storeCfgs = Lists.newArrayList();
            storeCfgs.add(storeCfg);
            S2CStoreMsg.RefreshStore storeInfo =
                    StoreMsgBuilder.createRefreshStoreMsg(player, this, storeCfgs);
            MessageUtils.send(player, storeInfo.toBuilder());
        }

    }

    /**
     * 购买记录
     */
    public void getCommodityBuyLog(Player player) {
        LogicScriptsUtils.getIStoreScript().getCommodityBuyLog(player, player.getStortManager());
    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        LogicScriptsUtils.getIStoreScript().autoRefresh(player, player.getStortManager());
    }

    public void sell(Player player, List<SellGoods> sellList, int goodCount) {
        LogicScriptsUtils.getIStoreScript().sell(player, sellList, goodCount);
    }

    public PlayerStore getStoreById(int id) {
        return storeMap.get(id);
    }

    public CommodityBuyRecord getBuyRecord(int id) {
        return buyRecord.get(id);
    }

    public void putBuyRecord(CommodityBuyRecord record) {
        buyRecord.put(record.getCommodityId(), record);
    }

    public Map<Integer, CommodityBuyRecord> getAllRecord() {
        return buyRecord;
    }

    public Map<Integer, PlayerStore> getStore() {
        return storeMap;
    }

    public void loadCacheCallBack(CommodityCfgBean t, Table<Integer, Integer, StoreGrid> maping) {
        StoreGrid shopGrid = maping.get(t.getStoreId(), t.getGrid());
        if (shopGrid == null) {
            List<CommodityCfgBean> cfgs = Lists.newArrayList();
            cfgs.add(t);
            shopGrid = new StoreGrid(t.getGrid(), t.getWeight(), cfgs);
            maping.put(t.getStoreId(), t.getGrid(), shopGrid);
        } else {
            shopGrid.getCfgs().add(t);
            shopGrid.setTotalWeight(shopGrid.getTotalWeight() + t.getWeight());
        }
    }

    // 取得玩家商城商品信息
    public List<CommodityCfgBean> getPlayerShopCommodity(int storeCid, Player player) {
        PlayerStore store = storeMap.get(storeCid);
        List<CommodityCfgBean> commoditys = Lists.newArrayList();
        Date nowDate = new Date();
        Date nextRefreshDate = null;
        if (store == null) {
            StoreCfgBean storeCfg = GameDataManager.getStoreCfgBean(storeCid);
            if (storeCfg != null && StrUtil.isNotBlank(storeCfg.getAutoRefreshCorn())) {
                CronSequenceGenerator cronSequenceGenerator =
                        new CronSequenceGenerator(storeCfg.getAutoRefreshCorn());
                nextRefreshDate = StoreService.getInstance().getNextfreshDate(nowDate, null,
                        cronSequenceGenerator);
            }
            store = new PlayerStore(0, storeCid, Lists.newArrayList(), 0, 0, new Date(),
                    nextRefreshDate);
            commoditys.addAll(LogicScriptsUtils.getIStoreScript().randomCommodity(player, this,
                    storeCid, false));

            for (CommodityCfgBean commodityCfg : commoditys) {
                store.getCommodityIdList().add(commodityCfg.getId());
            }
            storeMap.put(storeCid, store);

        } else {
            List<Integer> removedCfgList = Lists.newArrayList();
            store.getCommodityIdList().forEach(id -> {
                int commodityId = Integer.parseInt(id.toString());
                CommodityCfgBean commodityCfg = GameDataManager.getCommodityCfgBean(commodityId);
                // 商品配置被删除
                if (commodityCfg == null) {
                    removedCfgList.add(commodityId);
                    return;
                }
                commoditys.add(commodityCfg);
            });

            if (CollUtil.isNotEmpty(removedCfgList)) {
                store.getCommodityIdList().removeAll(removedCfgList);
                storeMap.put(storeCid, store);
            }
        }
        return commoditys;
    }


    /**
     * 检查商品是否开启
     */
    public boolean checkCommodityOpen(Player player, CommodityCfgBean commodityCfg) {
        int openType = commodityCfg.getOpenContType();
        switch (openType) {
            case StoreConstant.STORE_PLV_OPEN_CONT:
                if (player.getLevel() < commodityCfg.getOpenContVal()) {
                    return false;
                }
                break;
            case StoreConstant.STORE_DNU_OPEN_CONT:
                if (player.getDungeonManager().checkDungeonPass(commodityCfg.getOpenContVal())) {
                    return false;
                }
                break;
        }

        Date nowDate = new Date();
        int minute = 0;
        if (commodityCfg.getSellTimeType() == StoreConstant.STORE_OPEN_TIME_TODAY) {
            minute = (int) DateUtil.between(DateUtil.beginOfDay(nowDate), nowDate, DateUnit.MINUTE);
        } else if (commodityCfg.getSellTimeType() == StoreConstant.STORE_OPEN_TIME_WEEK) {
            // 修正周1为每周第一天
            minute = (int) (DateUtil.between(DateUtil.beginOfWeek(nowDate), nowDate,
                    DateUnit.MINUTE) + (StoreConstant.TIME_DAY / StoreConstant.TIME_MINUTE));
        } else {
            return true;
        }
        int[] openTime = commodityCfg.getSellTime();
        for (int i = 0; i < openTime.length; i = +2) {
            int b = openTime[i];
            int e = openTime[i + 1];
            if (minute >= b && minute <= e) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查购买记录
     */
    public boolean checkBuyRecord(int commodityId, int num) {
        CommodityCfgBean commodityCfg = GameDataManager.getCommodityCfgBean(commodityId);
        if (commodityCfg.getLimitType() == StoreConstant.LIMIT_NO_TYPE) {
            return true;
        }

        // 限制购买次数
        if (commodityCfg.getLimitType() != StoreConstant.LIMIT_NO_TYPE) {
            CommodityBuyRecord commodityBuyRecord = buyRecord.get(commodityId);

            int nowBuyCount = commodityBuyRecord == null ? 0 : commodityBuyRecord.getNowBuyCount();

            int totalBuyCount =
                    commodityBuyRecord == null ? 0 : commodityBuyRecord.getTotalBuyCount();

            int alreadyBuyNum = 0;

            if (commodityCfg.getLimitType() == StoreConstant.LIMIT_SERVER_TYPE) {
                // 查看个人最大购买数量
                if (alreadyBuyNum + num > commodityCfg.getSerLimit()) {
                    return false;
                }

            } else if (commodityCfg.getLimitType() == StoreConstant.LIMIT_REFRESH_TIME_TYPE
                    || commodityCfg.getLimitType() == StoreConstant.LIMIT_TO_DAY_TYPE) {
                alreadyBuyNum = nowBuyCount;
            } else if (commodityCfg.getLimitType() == StoreConstant.LIMIT_TOTAL_TYPE) {
                alreadyBuyNum = totalBuyCount;
            }

            // 超过购买限制
            if (alreadyBuyNum + num > commodityCfg.getLimitVal()) {
                return false;
            }

        }
        return true;
    }

    @Override
    public void tick() {
        try {
            autoRefresh();
        } catch (Exception e) {
            LOGGER.error(ConstDefine.LOG_ERROR_LOGIC_PREFIX + ExceptionEx.e2s(e));
        }
    }

    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        try {
            if (type == EAcrossDayType.GAME_ACROSS_DAY || type == EAcrossDayType.GAME_ACROSS_WEEK
                    || type == EAcrossDayType.GAME_ACROSS_MONTH) {
                LogicScriptsUtils.getIStoreScript().checkAcrossDMY(player, this, type);
            }
        } catch (Exception e) {
            LOGGER.error(ConstDefine.LOG_ERROR_LOGIC_PREFIX + ExceptionEx.e2s(e));
        }
    }

    public static void main(String[] args) {
        int randomWeight = RandomUtil.randomInt(0, 10000);
    }
}
