package logic.msgBuilder;

import gm.constant.PayConstant;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logic.pay.bean.MonthCard;

import org.game.protobuf.s2c.S2CItemMsg;
import org.game.protobuf.s2c.S2CRechargeMsg;
import org.game.protobuf.s2c.S2CShareMsg;

import utils.DateEx;
import cn.hutool.core.date.DateUtil;
import data.GameDataManager;
import data.bean.MonthCardCfgBean;
import data.bean.RechargeCfgBean;
import data.bean.RechargeGiftBagCfgBean;

/**
 * 
 * @Description 充值消息构建器
 * @author LiuJiang
 * @date 2018年7月14日 下午4:30:37
 *
 */
public class PayMsgBuilder {
    public static S2CRechargeMsg.GetOrderNo.Builder buildGetOrderNoMsg(String orderNo) {
        S2CRechargeMsg.GetOrderNo.Builder builder = S2CRechargeMsg.GetOrderNo.newBuilder();
        builder.setOrderNo(orderNo);
        return builder;
    }

    public static S2CRechargeMsg.RechargeSuccess.Builder buildRechargeSuccessMsg(int cid) {
        S2CRechargeMsg.RechargeSuccess.Builder builder =
                S2CRechargeMsg.RechargeSuccess.newBuilder();
        builder.setCid(cid);
        return builder;
    }

    public static S2CRechargeMsg.GetMonthCardInfo.Builder buildGetMonthCardInfoMsg(MonthCard mc,
            S2CShareMsg.ChangeType changeType) {
        S2CRechargeMsg.GetMonthCardInfo.Builder builder =
                S2CRechargeMsg.GetMonthCardInfo.newBuilder();
        builder.setCt(changeType);
        if (mc == null || mc.getCid() == 0) {
            builder.setCardCid(0).setLastGainDate(0).setSurplusGainCount(0);
        } else {
            builder.setCardCid(mc.getCid())
                    .setLastGainDate((int) (mc.getLastGainTime() / 1000))
                    .setSurplusGainCount(
                            (int) DateUtil.betweenDay(new Date(), mc.getEndTime() == 0 ? new Date()
                                    : new Date(mc.getEndTime()), true));
        }
        return builder;
    }


    public static S2CRechargeMsg.BuyRecordInfo.Builder buildBuyRecordInfo(int cid, int buyCount,
            S2CShareMsg.ChangeType ct) {
        S2CRechargeMsg.BuyRecordInfo.Builder builder = S2CRechargeMsg.BuyRecordInfo.newBuilder();
        builder.setBuyCount(buyCount).setCid(cid).setCt(ct);
        return builder;
    }

    public static S2CRechargeMsg.GetBuyRecordInfo.Builder buildGetBuyRecordInfoMsg(
            Map<Integer, Integer> limits, S2CShareMsg.ChangeType ct) {
        S2CRechargeMsg.GetBuyRecordInfo.Builder builder =
                S2CRechargeMsg.GetBuyRecordInfo.newBuilder();
        for (Entry<Integer,Integer> entry : limits.entrySet()) {
            int cid = entry.getKey();
            int buyCount = entry.getValue();
            builder.addInfo(buildBuyRecordInfo(cid, buyCount, ct));
        }
        return builder;
    }

    public static S2CRechargeMsg.BuyMonthCardInfo.Builder buildBuyMonthCardInfoMsg(MonthCard mc,
            S2CItemMsg.ItemList itemList, S2CShareMsg.ChangeType ct) {
        S2CRechargeMsg.BuyMonthCardInfo.Builder builder =
                S2CRechargeMsg.BuyMonthCardInfo.newBuilder();
        builder.setMonthCardInfo(buildGetMonthCardInfoMsg(mc, ct));
        if (itemList != null) {
            builder.setItemList(itemList);
        }
        return builder;
    }


    @SuppressWarnings("unchecked")
    public static S2CRechargeMsg.GetRechargeCfg.Builder buildRechargeListMsg(
            List<RechargeCfgBean> cfgList) {
        S2CRechargeMsg.GetRechargeCfg.Builder builder = S2CRechargeMsg.GetRechargeCfg.newBuilder();
        for (RechargeCfgBean cfg : cfgList) {
            S2CRechargeMsg.RechargeCfg.Builder rechargeCfgBuilder =
                    S2CRechargeMsg.RechargeCfg.newBuilder();
            rechargeCfgBuilder.setId(cfg.getId()).setPrice(cfg.getPrice());
            if (cfg.getType() == PayConstant.TYPE_MONTH_CARD.getCode()) {// 月卡类
                MonthCardCfgBean monthCardCfg =
                        GameDataManager.getMonthCardCfgBean(cfg.getGoodsId());
                S2CRechargeMsg.MonthCardCfg.Builder monthCardCfgBuilder =
                        S2CRechargeMsg.MonthCardCfg.newBuilder();
                monthCardCfgBuilder.setUpgradeId(monthCardCfg.getUpgradeId())
                        .setDays(monthCardCfg.getDays()).setType(monthCardCfg.getType())
                        .setIcon(monthCardCfg.getIcon()).setName(monthCardCfg.getName())
                        .setDes1(monthCardCfg.getDes1() != null ? monthCardCfg.getDes1() : "")
                        .setName2(monthCardCfg.getName2() != null ? monthCardCfg.getName2() : "")
                        .setDes3(monthCardCfg.getDes3() != null ? monthCardCfg.getDes3() : "");
                for (Object o : monthCardCfg.getGainItem().entrySet()) {
                    Map.Entry<Integer, Integer> e = (Map.Entry<Integer, Integer>) o;
                    S2CShareMsg.RewardsMsg.Builder gainItemBuilder =
                            S2CShareMsg.RewardsMsg.newBuilder();
                    gainItemBuilder.setId(e.getKey()).setNum(e.getValue());
                    monthCardCfgBuilder.addGainItem(gainItemBuilder);
                }
                for (Object o : monthCardCfg.getPresentItem().entrySet()) {
                    Map.Entry<Integer, Integer> e = (Map.Entry<Integer, Integer>) o;
                    S2CShareMsg.RewardsMsg.Builder presentItemBuilder =
                            S2CShareMsg.RewardsMsg.newBuilder();
                    presentItemBuilder.setId(e.getKey()).setNum(e.getValue());
                    monthCardCfgBuilder.addPresentItem(presentItemBuilder);
                }
                monthCardCfgBuilder.setRechargeCfg(rechargeCfgBuilder);
                builder.addMonthCardCfg(monthCardCfgBuilder);
            } else {// 礼包类
                RechargeGiftBagCfgBean rCfg =
                        GameDataManager.getRechargeGiftBagCfgBean(cfg.getGoodsId());
                S2CRechargeMsg.RechargeGiftBagCfg.Builder rechargeGiftBagCfgBuilder =
                        S2CRechargeMsg.RechargeGiftBagCfg.newBuilder();
                rechargeGiftBagCfgBuilder.setRechargeCfg(rechargeCfgBuilder)
                        .setBuyCount(rCfg.getBuyCount()).setResetType(rCfg.getResetType())
                        .setResetDate(rCfg.getResetDate()).setType(rCfg.getType())
                        .setName(rCfg.getName()).setIcon(rCfg.getIcon()).setTag(rCfg.getTag())
                        .setTagDes(rCfg.getTagDes() != null ? rCfg.getTagDes() : "")
                        .setTagDes2(rCfg.getTagDes2() != null ? rCfg.getTagDes2() : "")
                        .setDes1(rCfg.getDes1() != null ? rCfg.getDes1() : "")
                        .setDes2(rCfg.getDes2() != null ? rCfg.getDes2() : "")
                        .setOrder(rCfg.getOrder())
                        .setName2(rCfg.getName2() != null ? rCfg.getName2() : "")
                        .setDes3(rCfg.getDes3() != null ? rCfg.getDes3() : "");

                for (Object o : rCfg.getItem().entrySet()) {
                    Map.Entry<Integer, Integer> e = (Map.Entry<Integer, Integer>) o;
                    S2CShareMsg.RewardsMsg.Builder itemBuilder =
                            S2CShareMsg.RewardsMsg.newBuilder();
                    itemBuilder.setId(e.getKey()).setNum(e.getValue());
                    rechargeGiftBagCfgBuilder.addItem(itemBuilder);
                }

                for (Object o : rCfg.getFirstBuyItem().entrySet()) {
                    Map.Entry<Integer, Integer> e = (Map.Entry<Integer, Integer>) o;
                    S2CShareMsg.RewardsMsg.Builder itemBuilder =
                            S2CShareMsg.RewardsMsg.newBuilder();
                    itemBuilder.setId(e.getKey()).setNum(e.getValue());
                    rechargeGiftBagCfgBuilder.addFirstBuyItem(itemBuilder);
                }

                if (rCfg.getStartDate() != null && rCfg.getStartDate().length != 0) {
                    rechargeGiftBagCfgBuilder.setStartDate((int) (DateEx.getAssignCalendar(
                            rCfg.getStartDate()).getTimeInMillis() / 1000));
                }
                if (rCfg.getEndDate() != null && rCfg.getEndDate().length != 0) {
                    rechargeGiftBagCfgBuilder.setEndDate((int) (DateEx.getAssignCalendar(
                            rCfg.getEndDate()).getTimeInMillis() / 1000));
                }
                if (rCfg.getPlayerLevel() != null && rCfg.getPlayerLevel().length > 0) {
                    for (int level : rCfg.getPlayerLevel()) {
                        rechargeGiftBagCfgBuilder.addPlayerLevel(level);
                    }
                }
                builder.addRechargeGiftBagCfg(rechargeGiftBagCfgBuilder);
            }
        }
        return builder;
    }
}
