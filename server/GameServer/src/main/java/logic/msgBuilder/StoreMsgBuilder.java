package logic.msgBuilder;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.game.protobuf.s2c.S2CStoreMsg;
import org.game.protobuf.s2c.S2CStoreMsg.CommodityBuyLogs;

import com.google.common.collect.Lists;

import cn.hutool.core.util.StrUtil;
import data.bean.CommodityCfgBean;
import data.bean.StoreCfgBean;
import logic.character.bean.Player;
import logic.constant.StoreConstant;
import logic.store.StoreManager;
import logic.store.StoreService;
import logic.store.bean.CommodityBuyRecord;
import logic.store.bean.PlayerStore;
import utils.CronSequenceGenerator;


/**
 * @author : DengYing
 * @CreateDate : 2017年9月13日 上午11:48:06
 * @Description ：Please describe this document
 */
public class StoreMsgBuilder {
    /**
     * 创建单个商城信息
     * 
     * @param player
     * @param storeCid
     * @return
     */
    public static S2CStoreMsg.StoreInfo createStoreInfoMsg(Player player, StoreManager stortManager,
            StoreCfgBean storeCfg) {
        S2CStoreMsg.StoreInfo.Builder shopInfo = S2CStoreMsg.StoreInfo.newBuilder();
        PlayerStore store = player.getStortManager().getStoreById(storeCfg.getId());

        // 取得具体某个商店
        shopInfo.setCid(storeCfg.getId());
        shopInfo.setTodayRefreshCount(store == null ? 0 : store.getTodayRefreshCount());
        shopInfo.setTotalRefreshCount(store == null ? 0 : store.getTotalRefreshCount());
        shopInfo.setNextRefreshTime(0);

        // 取得玩家商城商品
        if (storeCfg.getCommoditySupplyType() == StoreConstant.COMMODITY_SUPPLY_RANDOM_TYPE) {
            List<CommodityCfgBean> commodityCfgs =
                    stortManager.getPlayerShopCommodity(storeCfg.getId(), player);
            for (CommodityCfgBean commodityCfg : commodityCfgs) {
                shopInfo.addCommoditys(commodityCfg.getId());
            }
        } else if (storeCfg.getCommoditySupplyType() == StoreConstant.COMMODITY_SUPPLY_FIXATION_TYPE
                && store == null) {
            Date nextRefreshDate = null;
            Date nowDate = new Date();
            if (StrUtil.isNotBlank(storeCfg.getAutoRefreshCorn())) {
                CronSequenceGenerator cronSequenceGenerator =
                        new CronSequenceGenerator(storeCfg.getAutoRefreshCorn());
                nextRefreshDate = StoreService.getInstance().getNextfreshDate(nowDate, null,
                        cronSequenceGenerator);
            }
            store = new PlayerStore(0, storeCfg.getId(), Lists.newArrayList(), 0, 0, new Date(),
                    nextRefreshDate);
            stortManager.getStore().put(storeCfg.getId(), store);
        }

        if (store == null) {
            store = player.getStortManager().getStoreById(storeCfg.getId());
        }
        Date nowDate = new Date();
        if (StrUtil.isNotBlank(storeCfg.getAutoRefreshCorn())) {
            if (store != null) {
                if (store.getNextRefreshTime() == null) {
                    setNextRefreshDate(nowDate, storeCfg.getAutoRefreshCorn(), store);
                }
                shopInfo.setNextRefreshTime((int) (store.getNextRefreshTime().getTime() / 1000));
            }
        }
        return shopInfo.build();
    }

    public static void setNextRefreshDate(Date nowDate, String refreshCorn, PlayerStore store) {
        CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(refreshCorn);
        Date nextRefreshDate = StoreService.getInstance().getNextfreshDate(nowDate,
                store.getLastAutoRefreshTime(), cronSequenceGenerator);
        store.setNextRefreshTime(nextRefreshDate);
    }

    /**
     * 创建商店更新信息
     * 
     * @param player
     * @param storeCid
     * @return
     */
    public static S2CStoreMsg.RefreshStore createRefreshStoreMsg(Player player,
            StoreManager stortManager, List<StoreCfgBean> stores) {
        S2CStoreMsg.RefreshStore.Builder builder = S2CStoreMsg.RefreshStore.newBuilder();
        // 商城信息
        for (StoreCfgBean cfg : stores) {
            builder.addStores(createStoreInfoMsg(player, stortManager, cfg));
        }
        return builder.build();
    }

    /**
     * 创建商品购买信息列表(全部)
     *
     * @param player
     * @return
     */
    public static CommodityBuyLogs createBuyRecordInfo(Player player, Set<CommodityCfgBean> set) {

        CommodityBuyLogs.Builder builder = CommodityBuyLogs.newBuilder();

        S2CStoreMsg.StoreCommodityBuyInfo.Builder buyInfo = null;
        for (CommodityCfgBean commodityCfg : set) {

            if (commodityCfg.getLimitType() == StoreConstant.LIMIT_SERVER_TYPE) {
                buyInfo = S2CStoreMsg.StoreCommodityBuyInfo.newBuilder();
                buyInfo.setCid(commodityCfg.getId());
                buyInfo.setType(2);
                // 后面写
                // ServerLimit sr =
                // ServerLimitCommodityBuyRecordCache.me().get(commodityCfg.getId());
                // buyInfo.setNowBuyCount(sr == null ? 0 :sr.getNowBuyCount());
                // buyInfo.setTotalBuyCount(sr == null ? 0 :sr.getTotalBuyCount());

                buyInfo.setNowBuyCount(0);
                buyInfo.setTotalBuyCount(0);

                builder.addBuyLogs(buyInfo);
            }

            if (player != null) {
                buyInfo = S2CStoreMsg.StoreCommodityBuyInfo.newBuilder();
                buyInfo.setCid(commodityCfg.getId());
                buyInfo.setType(1);
                CommodityBuyRecord commodityBuyRecord =
                        player.getStortManager().getBuyRecord(commodityCfg.getId());
                buyInfo.setNowBuyCount(
                        commodityBuyRecord == null ? 0 : commodityBuyRecord.getNowBuyCount());
                buyInfo.setTotalBuyCount(
                        commodityBuyRecord == null ? 0 : commodityBuyRecord.getTotalBuyCount());
                builder.addBuyLogs(buyInfo);
            }
        }
        return builder.build();
    }

    //
    // /**
    // * 创建商城购买信息(指定商品)
    // * @param player
    // * @param commodityCfgList
    // * @return
    // */
    // public static List<StoreCommodityBuyInfo> createStoreCommodityBuyInfos(Player
    // player,List<CommodityCfg> commodityCfgList){
    // List<StoreCommodityBuyInfo> StoreCommodityBuyInfos = Lists.newArrayList();
    // commodityCfgList.forEach(cfg -> {
    // CommodityBuyRecord commodityBuyRecord =
    // CommodityBuyRecordCache.me().getByPlayerIdCommodityId(player.getId(), cfg.getId());
    // if (commodityBuyRecord==null) {
    // return;
    // }
    // StoreCommodityBuyInfos.add(createCommodityBuyRecord(commodityBuyRecord));
    // });
    // return StoreCommodityBuyInfos;
    // }
    //
    // /**
    // * 创建商城购买信息(单个)
    // *
    // * @param commodityBuyRecord
    // * @return
    // */
    // private static S2CStoreMsg.StoreCommodityBuyInfo createCommodityBuyRecord(CommodityBuyRecord
    // commodityBuyRecord) {
    // S2CStoreMsg.StoreCommodityBuyInfo.Builder buyInfo =
    // S2CStoreMsg.StoreCommodityBuyInfo.newBuilder();
    // buyInfo.setCid(commodityBuyRecord.getCommodityId());
    // buyInfo.setNowBuyCount(commodityBuyRecord.getNowBuyCount());
    // buyInfo.setTotalBuyCount(commodityBuyRecord.getTotalBuyCount());
    // return buyInfo.build();
    // }
    //
    // /**
    // * 创建商城购买信息(单个)
    // *
    // * @param commodityBuyRecord
    // * @return
    // */
    // public static S2CStoreMsg.StoreCommodityBuyInfo
    // createServerLimitCommodityBuyRecord(ServerLimitCommodityBuyRecord commodityBuyRecord) {
    // S2CStoreMsg.StoreCommodityBuyInfo.Builder buyInfo =
    // S2CStoreMsg.StoreCommodityBuyInfo.newBuilder();
    // buyInfo.setCid(commodityBuyRecord.getCommodityId());
    // buyInfo.setNowBuyCount(commodityBuyRecord.getNowBuyCount());
    // buyInfo.setTotalBuyCount(commodityBuyRecord.getTotalBuyCount());
    // return buyInfo.build();
    // }
    //

}
