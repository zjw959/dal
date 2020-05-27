package javascript.logic.store;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.game.protobuf.c2s.C2SStoreMsg.SellGoods;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;
import org.game.protobuf.s2c.S2CStoreMsg;
import org.game.protobuf.s2c.S2CStoreMsg.CommodityBuyLogs;
import org.game.protobuf.s2c.S2CStoreMsg.StoreDataInfo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import data.GameDataManager;
import data.bean.BaseGoods;
import data.bean.CommodityCfgBean;
import data.bean.EquipmentCfgBean;
import data.bean.ItemCfgBean;
import data.bean.StoreCfgBean;
import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import logic.constant.EEventType;
import logic.constant.EFunctionType;
import logic.constant.EItemType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.GameErrorCode;
import logic.constant.StoreConstant;
import logic.functionSwitch.FunctionSwitchService;
import logic.item.ItemUtils;
import logic.item.bean.EquipItem;
import logic.item.bean.Item;
import logic.msgBuilder.StoreMsgBuilder;
import logic.store.IStoreScript;
import logic.store.StoreGrid;
import logic.store.StoreManager;
import logic.store.StoreService;
import logic.store.bean.CommodityBuyRecord;
import logic.store.bean.PlayerStore;
import logic.support.MessageUtils;
import utils.CronSequenceGenerator;
import utils.ToolMap;

public class StoreScript extends IStoreScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.STORE_SCRIPT.Value();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void buy(Player player, StoreManager stortManager, int cid, int num) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.STORE)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:StoreBuy");
        }
        CommodityCfgBean commodityCfg = GameDataManager.getCommodityCfgBean(cid);
        if (commodityCfg == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_COMMODITY, "商品信息不存在 id:" + cid);
            return;
        }

        StoreCfgBean storeCfg = GameDataManager.getStoreCfgBean(commodityCfg.getStoreId());
        if (storeCfg == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_COMMODITY, "商品信息不存在 id:" + cid);
            return;
        }
        if (!checkCommodityOpen(player, commodityCfg)) {
            MessageUtils.throwCondtionError(GameErrorCode.COMMODITY_NOT_OPEN, "商品未开启");
            return;
        }

        if (!checkBuyRecord(stortManager, cid, num)) {
            MessageUtils.throwCondtionError(GameErrorCode.BUY_MAX_LIMIT, "已达购买上限");
            return;
        }

        // 累计扣除
        int[] priceTypes = commodityCfg.getPriceType();
        int[] priceVals = commodityCfg.getPriceVal();

        Map<Integer, Integer> costMap = Maps.newHashMap();
        for (int i = 0; i < priceVals.length; i++) {
            int costNum = ToolMap.getInt(priceTypes[i], costMap) + (priceVals[i] * num);
            costMap.put(priceTypes[i], costNum);
        }

        Map<Integer, Integer> goodsNum = Maps.newHashMap();
        // 累计获得
        Map<Integer, Integer> goodsMap = commodityCfg.getGoods();
        for (Entry<Integer, Integer> goods : goodsMap.entrySet()) {
            goodsNum.put(goods.getKey(), goods.getValue() * num);
        }

        // 扣除物品
        boolean isEnough = player.getBagManager().removeItemsByTemplateIdWithCheck(costMap, true,
                EReason.STORE);
        if (!isEnough) {
            // 所需消耗道具不足
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "资源不足");
            return;
        }

        // 发货
        player.getBagManager().addItems(goodsNum, true, EReason.STORE);

        // 添加个人购买记录
        addBuyRecordAndPush(player, stortManager, commodityCfg, num);

        // 购买事件
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.STORE_BUY);
        in.put(EventConditionKey.COMMODITY_CID, cid);
        in.put(EventConditionKey.STORE_CID, commodityCfg.getStoreId());
        player._fireEvent(in, EEventType.OTHER_EVENT.value());
        // GameEventPlugin.syncSubmit(event);

        org.game.protobuf.s2c.S2CStoreMsg.BuyGoods.Builder builder =
                org.game.protobuf.s2c.S2CStoreMsg.BuyGoods.newBuilder();
        builder.setCid(cid);
        builder.setNum(num);
        MessageUtils.send(player, builder);
    }

    @Override
    protected void refreshStore(Player player, StoreManager stortManager, int cid) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.STORE)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:Store");
        }
        StoreCfgBean storeCfg = GameDataManager.getStoreCfgBean(cid);
        PlayerStore store = stortManager.getStoreById(cid);
        int refreshCostId = storeCfg.getRefreshCostId();
        int[] refreshCostNum = storeCfg.getRefreshCostNum();
        int todayRefreshCount = store.getTodayRefreshCount();
        int totalRefreshCount = store.getTotalRefreshCount();
        if (todayRefreshCount > refreshCostNum.length) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "今日已达到最大刷新次数");
            return;
        }

        int index = Math.min(refreshCostNum.length - 1, todayRefreshCount);
        // 道具检查
        boolean isEnough = player.getBagManager().removeItemsByTemplateIdWithCheck(
                cn.hutool.core.map.MapUtil.of(refreshCostId, refreshCostNum[index]), true,
                EReason.EQUIP);
        if (!isEnough) {
            // 所需消耗道具不足
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "资源不足");
            return;
        }

        store.setTodayRefreshCount(todayRefreshCount + 1);
        store.setTotalRefreshCount(totalRefreshCount + 1);

        // 重置商店
        Set<CommodityCfgBean> affect =
                resetStore(player, stortManager, storeCfg, false, true, false, false);
        // 推送购买记录更新
        CommodityBuyLogs commodityBuyLogs = StoreMsgBuilder.createBuyRecordInfo(player, affect);
        MessageUtils.send(player, commodityBuyLogs.toBuilder());

        // 推送商店信息
        List<StoreCfgBean> storeCfgs = Lists.newArrayList();
        storeCfgs.add(storeCfg);
        S2CStoreMsg.RefreshStore storeInfo =
                StoreMsgBuilder.createRefreshStoreMsg(player, stortManager, storeCfgs);
        MessageUtils.send(player, storeInfo.toBuilder());
    }

    @Override
    protected void getCommodityBuyLog(Player player, StoreManager stortManager) {
        Set<CommodityCfgBean> set = new HashSet<>();
        // 个人限购
        Set<Entry<Integer, CommodityBuyRecord>> entries = stortManager.getAllRecord().entrySet();
        for (Entry<Integer, CommodityBuyRecord> entry : entries) {
            CommodityCfgBean cfg = GameDataManager.getCommodityCfgBean(entry.getKey());
            // 商品被删除
            if (cfg == null) {
                continue;
            }
            set.add(cfg);
        }
        CommodityBuyLogs buyLogs = StoreMsgBuilder.createBuyRecordInfo(player, set);
        MessageUtils.send(player, buyLogs.toBuilder());

    }

    @Override
    protected void getStoreInfo(Player player, StoreManager stortManager, List<Integer> cids) {
        StoreDataInfo.Builder storeDataInfo = StoreDataInfo.newBuilder();
        for (Integer cid : cids) {
            StoreCfgBean store = GameDataManager.getStoreCfgBean(cid);
            if (store == null)
                continue;
            storeDataInfo
                    .addStores(StoreMsgBuilder.createStoreInfoMsg(player, stortManager, store));
        }
        MessageUtils.send(player, storeDataInfo);

    }

    /**
     * 自动刷新
     * 
     * @throws ParseException
     */
    @Override
    public void autoRefresh(Player player, StoreManager stortManager) {
        List<StoreCfgBean> storeCfgList = GameDataManager.getStoreCfgBeans();
        Date nowDate = new Date();

        for (StoreCfgBean storeCfg : storeCfgList) {
            PlayerStore store = stortManager.getStoreById(storeCfg.getId());
            if (store == null) {
                continue;
            }
            // 商店
            if (StrUtil.isNotBlank(storeCfg.getAutoRefreshCorn())) {
                if (store.getNextRefreshTime() == null) {
                    setNextRefreshDate(nowDate, storeCfg.getAutoRefreshCorn(), store);
                }

                if (store.getNextRefreshTime().before(nowDate)) {
                    store.setLastAutoRefreshTime(nowDate);
                    setNextRefreshDate(nowDate, storeCfg.getAutoRefreshCorn(), store);

                    // 重置商店
                    Set<CommodityCfgBean> affect =
                            resetStore(player, stortManager, storeCfg, false, true, false, false);

                    // 推送购买记录更新
                    CommodityBuyLogs commodityBuyLogs =
                            StoreMsgBuilder.createBuyRecordInfo(player, affect);
                    MessageUtils.send(player, commodityBuyLogs.toBuilder());

                    // 推送商店信息
                    List<StoreCfgBean> storeCfgs = Lists.newArrayList();
                    storeCfgs.add(storeCfg);
                    S2CStoreMsg.RefreshStore storeInfo =
                            StoreMsgBuilder.createRefreshStoreMsg(player, stortManager, storeCfgs);
                    MessageUtils.send(player, storeInfo.toBuilder());
                }
            } else {

            }
        }
    }

    public void setNextRefreshDate(Date nowDate, String refreshCorn, PlayerStore store) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(refreshCorn);
        Date nextRefreshDate = StoreService.getInstance().getNextfreshDate(nowDate,
                store.getLastAutoRefreshTime(), cronSequenceGenerator);
        store.setNextRefreshTime(nextRefreshDate);
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

                break;
        }

        Date nowDate = new Date();
        int minute = 0;
        if (commodityCfg.getSellTimeType() == StoreConstant.STORE_OPEN_TIME_TODAY) {
            minute = (int) DateUtil.between(DateUtil.beginOfDay(nowDate), nowDate, DateUnit.MINUTE);
        } else if (commodityCfg.getSellTimeType() == StoreConstant.STORE_OPEN_TIME_TODAY) {
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
    public boolean checkBuyRecord(StoreManager stortManager, int commodityId, int num) {
        CommodityCfgBean commodityCfg = GameDataManager.getCommodityCfgBean(commodityId);
        if (commodityCfg.getLimitType() == StoreConstant.LIMIT_NO_TYPE) {
            return true;
        }

        // 限制购买次数
        if (commodityCfg.getLimitType() != StoreConstant.LIMIT_NO_TYPE) {
            CommodityBuyRecord commodityBuyRecord = stortManager.getBuyRecord(commodityId);

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

    /**
     * 添加商品购买信息&推送至客户端
     */
    public void addBuyRecordAndPush(Player player, StoreManager stortManager,
            CommodityCfgBean commodityCfg, int num) {
        // 限购物品添加限购记录
        if (commodityCfg.getLimitType() != StoreConstant.LIMIT_NO_TYPE) {
            int cid = commodityCfg.getId();
            CommodityBuyRecord commodityBuyRecord = stortManager.getBuyRecord(cid);

            if (commodityBuyRecord == null) {
                commodityBuyRecord = new CommodityBuyRecord(0L, cid, num, num);
                stortManager.putBuyRecord(commodityBuyRecord);
            } else {
                commodityBuyRecord.setNowBuyCount(commodityBuyRecord.getNowBuyCount() + num);
                commodityBuyRecord.setTotalBuyCount(commodityBuyRecord.getTotalBuyCount() + num);
            }

            // 是否是全服限购商品

            if (commodityCfg.getLimitType() == StoreConstant.LIMIT_SERVER_TYPE) {
                // 避免延迟，临时修改及时反馈,后面再说
                // ServerLimitCommodityBuyRecordCache.me().changeNum(commodityCfg.getId(), num);
            }
            // 推送更新
            Set<CommodityCfgBean> set = new HashSet<>(1);
            set.add(commodityCfg);
            CommodityBuyLogs commodityBuyLogs = StoreMsgBuilder.createBuyRecordInfo(player, set);
            MessageUtils.send(player, commodityBuyLogs.toBuilder());
        }
    }

    /**
     * 重置商店
     * 
     * @param player 玩家
     * @param storeCfg 商店
     * @param isNextDay 是否跨天
     * @param auto 是否是系统自动重置
     * @return
     */
    public Set<CommodityCfgBean> resetStore(Player player, StoreManager stortManager,
            StoreCfgBean storeCfg, boolean isNextDay, boolean auto, boolean isNextWeek,
            boolean isNextMonth) {
        // 受影响的商品
        Set<CommodityCfgBean> affect = Sets.newHashSet();

        switch (storeCfg.getCommoditySupplyType()) {
            case StoreConstant.COMMODITY_SUPPLY_FIXATION_TYPE:
                List<CommodityCfgBean> cfgs = getCfgsByStoreId(storeCfg.getId());

                for (CommodityCfgBean commodityCfg : cfgs) {
                    if (clearCommodityBuyRecord(commodityCfg, stortManager, isNextDay, auto,
                            isNextWeek, isNextMonth)) {
                        affect.add(commodityCfg);
                    }

                    // 检测购买记录
                    CommodityBuyRecord _record = stortManager.getBuyRecord(commodityCfg.getId());
                    if (_record != null && commodityCfg
                            .getLimitType() == StoreConstant.LIMIT_REFRESH_TIME_TYPE) {
                        if (_record.getNowBuyCount() != 0) {
                            _record.setNowBuyCount(0);
                            affect.add(commodityCfg);
                        }
                    }
                }
                break;
            case StoreConstant.COMMODITY_SUPPLY_RANDOM_TYPE:
                // 取得玩家商店信息
                PlayerStore store = stortManager.getStoreById(storeCfg.getId());
                if (store == null) {
                    return affect;
                }

                for (Integer commodityId : store.getCommodityIdList()) {
                    CommodityCfgBean commodityCfg =
                            GameDataManager.getCommodityCfgBean((Integer) commodityId);
                    // 商品配置信息被删除
                    if (commodityCfg == null) {
                        continue;
                    }
                    // 清除商品的购买信息
                    if (clearCommodityBuyRecord(commodityCfg, stortManager, isNextDay, auto,
                            isNextWeek, isNextMonth)) {
                        affect.add(commodityCfg);
                    }
                }

                // 清空历史商品信息
                store.getCommodityIdList().clear();

                // 重新刷新随机商店商品
                List<CommodityCfgBean> randoms =
                        randomCommodity(player, stortManager, storeCfg.getId(), false);

                for (CommodityCfgBean commodityCfg : randoms) {
                    store.getCommodityIdList().add(commodityCfg.getId());
                    // 检测购买记录
                    CommodityBuyRecord _record = stortManager.getBuyRecord(commodityCfg.getId());
                    if (_record != null && commodityCfg
                            .getLimitType() == StoreConstant.LIMIT_REFRESH_TIME_TYPE) {
                        if (_record.getNowBuyCount() != 0) {
                            _record.setNowBuyCount(0);
                            affect.add(commodityCfg);
                        }

                    }
                }

                break;
        }

        return affect;
    }

    public List<CommodityCfgBean> getCfgsByStoreId(int storeId) {
        List<CommodityCfgBean> totalList = GameDataManager.getCommodityCfgBeans();
        Iterator<CommodityCfgBean> iter = totalList.iterator();
        List<CommodityCfgBean> tempList = new ArrayList<CommodityCfgBean>();
        while (iter.hasNext()) {
            CommodityCfgBean bean = (CommodityCfgBean) iter.next();
            if (storeId == bean.getStoreId()) {
                tempList.add(bean);
            }
        }
        return tempList;
    }

    /**
     * 清空当前阶段玩家商城购买记录
     */
    private boolean clearCommodityBuyRecord(CommodityCfgBean commodityCfg,
            StoreManager stortManager, boolean isNextDay, boolean auto, boolean isNextWeek,
            boolean isNextMonth) {
        // 不是限购商品不清理
        if (commodityCfg.getLimitType() == StoreConstant.LIMIT_NO_TYPE) {
            return false;
        }
        boolean flag = false;
        switch (commodityCfg.getLimitType()) {
            case StoreConstant.LIMIT_REFRESH_TIME_TYPE:
                if (auto) {
                    flag = true;
                    break;
                } else {
                    return false;
                }
            case StoreConstant.LIMIT_TO_DAY_TYPE:
                if (!isNextDay) {
                    return false;
                } else {
                    flag = true;
                    break;
                }
            case StoreConstant.LIMIT_TOTAL_TYPE:
                return false;
            case StoreConstant.LIMIT_SERVER_TYPE: // 全服限购清除自己的购买记录
                if (auto) {
                    flag = true;
                    break;
                } else {
                    return false;
                }
            case StoreConstant.LIMIT_WEEK_TYPE:
                if (!isNextWeek) {
                    return false;
                } else {
                    flag = true;
                    break;
                }
            case StoreConstant.LIMIT_MONTH_TYPE:
                if (!isNextMonth) {
                    return false;
                } else {
                    flag = true;
                    break;
                }
            default:
                return false;
        }
        if (!flag) {
            return false;
        }
        CommodityBuyRecord CommodityBuyRecord = stortManager.getBuyRecord(commodityCfg.getId());
        if (CommodityBuyRecord != null) {
            CommodityBuyRecord.setNowBuyCount(0);
            return true;
        }

        return false;
    }

    /**
     * 随机商品信息
     *
     * @param shopType
     * @return
     */
    @Override
    public List<CommodityCfgBean> randomCommodity(Player player, StoreManager stortManager,
            int shopType, boolean isLevelUp) {
        List<CommodityCfgBean> commoditys = Lists.newArrayList();
        // 取得商店的所有格子信息
        Map<Integer, StoreGrid> shopCommodity = stortManager.getMaping().row(shopType);
        PlayerStore store = stortManager.getStoreById(shopType);

        for (Entry<Integer, StoreGrid> entry : shopCommodity.entrySet()) {
            StoreGrid shopGrid = entry.getValue();
            List<CommodityCfgBean> cfgs = shopGrid.getCfgs();
            // 筛选
            List<CommodityCfgBean> reach = Lists.newArrayList();
            int totalWeight = 0;
            for (CommodityCfgBean commodityCfg : cfgs) {
                if (isLevelUp) {
                    boolean isExist = isExist(store.getCommodityIdList(), commodityCfg.getId());
                    if (isExist) {
                        continue;
                    }
                }
                if (checkCommodityOpen(player, commodityCfg)) {
                    reach.add(commodityCfg);
                    totalWeight += commodityCfg.getWeight();
                }
            }
            if (totalWeight == 0) {
                continue;
            }
            int randomWeight = RandomUtil.randomInt(0, totalWeight);
            totalWeight = 0;
            for (CommodityCfgBean commodityCfg : reach) {
                totalWeight += commodityCfg.getWeight();
                if (totalWeight >= randomWeight) {
                    commoditys.add(commodityCfg);
                    break;
                }
            }
        }
        return commoditys;
    }

    public boolean isExist(List<Integer> commodityList, int commodityId) {
        CommodityCfgBean newCfg = GameDataManager.getCommodityCfgBean(commodityId);
        for (Integer id : commodityList) {
            CommodityCfgBean oldCfg = GameDataManager.getCommodityCfgBean(id);
            if (newCfg.getGrid() == oldCfg.getGrid()) {
                return true;
            }
        }
        return false;
    }

    // 取得玩家商城商品信息
    public List<CommodityCfgBean> getPlayerShopCommodity(int storeCid, StoreManager stortManager,
            Player player) {
        PlayerStore store = stortManager.getStoreById(storeCid);
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
            commoditys.addAll(randomCommodity(player, stortManager, storeCid, false));

            for (CommodityCfgBean commodityCfg : commoditys) {
                store.getCommodityIdList().add(commodityCfg.getId());
            }
            stortManager.getStore().put(storeCid, store);

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
                stortManager.getStore().put(storeCid, store);
            }
        }
        return commoditys;
    }


    @SuppressWarnings("unchecked")
    @Override
    protected void sell(Player player, List<SellGoods> sellList, int goodCount) {
        org.game.protobuf.s2c.S2CStoreMsg.SellInfo.Builder builder =
                org.game.protobuf.s2c.S2CStoreMsg.SellInfo.newBuilder();
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.ITEM_SELL)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:ItemSell");
        }
        if (goodCount <= 0) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "无消耗材料");
            return;
        }
        BagManager bagManager = player.getBagManager();
        Map<Integer, Integer> give = Maps.newHashMap();
        Map<Long, Integer> cost = Maps.newHashMap();
        boolean code = true;
        for (SellGoods goods : sellList) {
            Item item = bagManager.getItemCopy(Long.valueOf(goods.getId()));
            if (item == null || item.getNum() < goods.getNum() || goods.getNum() <= 0) {
                code = false;
                break;
            }
            Map<Integer, Integer> sellMap = null;
            BaseGoods bean = GameDataManager.getBaseGoods(item.getTemplateId());
            if (bean.getSuperType() == EItemType.DRESS.getValue()) {
                code = false;
                break;
            } else if (bean.getSuperType() == EItemType.EQUIP.getValue()) {
                if (((EquipItem) item).getLock()) {
                    // 如果被锁定情况下,不能被出售
                    code = false;
                    break;
                }
                EquipmentCfgBean cfg = GameDataManager.getEquipmentCfgBean(bean.getId());
                sellMap = new HashMap<>(cfg.getSellProfit());
            } else {
                ItemCfgBean cfg = GameDataManager.getItemCfgBean(bean.getId());
                sellMap = new HashMap<>(cfg.getSellProfit());
                ItemUtils.autoMap(sellMap, goods.getNum());
            }

            ItemUtils.mergeItemMap(give, sellMap);
            cost.put(Long.valueOf(goods.getId()), goods.getNum());
        }
        if (!code) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "无消耗材料");
            return;
        }
        if (!bagManager.removeItemsByIds(cost, true, EReason.ITEM_SELL)) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "扣除失败");
            return;
        }
        bagManager.addItems(give, true, EReason.ITEM_SELL);

        builder.setSuccess(true);
        give.forEach((k, v) -> {
            builder.addRewards(RewardsMsg.newBuilder().setId((Integer) k).setNum((Integer) v));
        });
        MessageUtils.send(player, builder);

    }

    @Override
    protected void checkAcrossDMY(Player player, StoreManager stortManager, EAcrossDayType type) {
        boolean isNextDay = false;
        boolean isNextWeek = false;
        boolean isNextMonth = false;
        if (type == EAcrossDayType.GAME_ACROSS_DAY) {
            acrossDayReset(player, stortManager);
            isNextDay = true;
        } else if (type == EAcrossDayType.GAME_ACROSS_WEEK) {
            isNextWeek = true;
        } else if (type == EAcrossDayType.GAME_ACROSS_MONTH) {
            isNextMonth = true;
        }
        acrossDMY(player, stortManager, isNextDay, isNextWeek, isNextMonth);
    }

    private void acrossDayReset(Player player, StoreManager stortManager) {
        List<StoreCfgBean> storeCfgList = GameDataManager.getStoreCfgBeans();
        List<StoreCfgBean> notifyStore = new ArrayList<>();
        for (StoreCfgBean storeCfg : storeCfgList) {
            PlayerStore store = stortManager.getStoreById(storeCfg.getId());
            if (store == null || storeCfg.getRefreshCostId() == 0) {
                continue;
            }
            store.reset();
            notifyStore.add(storeCfg);
        }
        // 推送商店信息
        S2CStoreMsg.RefreshStore storeInfo =
                StoreMsgBuilder.createRefreshStoreMsg(player, stortManager, notifyStore);
        MessageUtils.send(player, storeInfo.toBuilder());
    }

    private void acrossDMY(Player player, StoreManager stortManager, boolean isNextDay,
            boolean isNextWeek, boolean isNextMonth) {
        List<StoreCfgBean> storeCfgList = GameDataManager.getStoreCfgBeans();
        for (StoreCfgBean storeCfg : storeCfgList) {
            PlayerStore store = stortManager.getStoreById(storeCfg.getId());
            if (store == null) {
                continue;
            }
            Set<CommodityCfgBean> affect = resetStore(player, stortManager, storeCfg, isNextDay,
                    true, isNextWeek, isNextMonth);

            // 推送购买记录更新
            CommodityBuyLogs commodityBuyLogs = StoreMsgBuilder.createBuyRecordInfo(player, affect);
            MessageUtils.send(player, commodityBuyLogs.toBuilder());

        }
    }
}
