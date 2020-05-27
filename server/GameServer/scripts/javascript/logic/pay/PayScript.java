package javascript.logic.pay;

import gm.constant.PayConstant;
import gm.db.pay.bean.PayDBBean;
import gm.db.pay.dao.PayDao;
import gm.utils.PayUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.EAcrossDayType;
import logic.constant.EEventType;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.GameErrorCode;
import logic.constant.ItemConstantId;
import logic.functionSwitch.FunctionSwitchService;
import logic.login.platformVerify.ChannelType;
import logic.msgBuilder.PayMsgBuilder;
import logic.pay.IPayScript;
import logic.pay.PayManager;
import logic.pay.PayService;
import logic.pay.bean.MonthCard;
import logic.support.LogBeanFactory;
import logic.support.MessageUtils;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SRechargeMsg.GetOrderNo;
import org.game.protobuf.s2c.S2CRechargeMsg.BuyMonthCardInfo;
import org.game.protobuf.s2c.S2CRechargeMsg.BuyRecordInfo;
import org.game.protobuf.s2c.S2CRechargeMsg.GetBuyRecordInfo.Builder;
import org.game.protobuf.s2c.S2CShareMsg;

import server.ServerConfig;
import thread.log.LogProcessor;
import utils.CommonUtil;
import utils.DateEx;
import utils.ExceptionEx;
import utils.ToolMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import data.GameDataManager;
import data.bean.MonthCardCfgBean;
import data.bean.RechargeCfgBean;
import data.bean.RechargeGiftBagCfgBean;

public class PayScript extends IPayScript {
    private static final Logger LOGGER = Logger.getLogger(PayScript.class);
    @Override
    public int getScriptId() {
        return EScriptIdDefine.PAY_SCRIPT.Value();
    }

    /** 系统自动定时拉取需处理的订单 */
    @Override
    protected void systemGetPays() {
        long nowTime = System.currentTimeMillis();
        long lastEndTime = PayService.getInstance().getLastEndTime();
        long startTime = lastEndTime;
        if (lastEndTime == -1) {// 首次获取
            startTime = nowTime - PayService.DELAY;
        }
        long endTime = nowTime;
        PayService.getInstance().setLastEndTime(endTime);
        // 获取指定时间段内的未处理的有效订单（有充值回调的）
        int distime = 5000;// 误差处理
        Date startDate = new Date(startTime - distime);
        Date endDate = new Date(endTime + distime);
        List<PayDBBean> pays =
                PayDao.selectPaysByTimeAndStatus(startDate, endDate,
                        PayConstant.STATUS_CALL_BACK.getCode());
        Map<Integer, List<PayDBBean>> paysMap = new HashMap<Integer, List<PayDBBean>>();
        for (PayDBBean pay : pays) {
            int playerId = (int) pay.getPlayer_id();
            List<PayDBBean> list = paysMap.get(playerId);
            if (list == null) {
                list = Lists.newArrayList();
                paysMap.put(playerId, list);
            }
            list.add(pay);
        }
        // 处理在线玩家订单，离线订单在玩家登录时处理
        Player player = null;
        for (Entry<Integer, List<PayDBBean>> entry : paysMap.entrySet()) {
            int playerId = entry.getKey();
            player = PlayerManager.getPlayerByPlayerId(playerId);
            if (player != null && player.isOnline()) {
                // 处理充值订单发货
                List<PayDBBean> payList = entry.getValue();
                handlePays(player, payList);
            }
        }
    }

    /** 玩家登录时拉取需处理的订单 */
    @Override
    protected void loginGetPays(int playerId) {
        Player player = PlayerManager.getPlayerByPlayerId(playerId);
        if (player == null || !player.isOnline()) {
            return;
        }
        List<PayDBBean> pays =
                PayDao.selectPaysByPlayerIdAndStatus(playerId,
                        PayConstant.STATUS_CALL_BACK.getCode());
        if (pays != null && pays.size() > 0) {
            handlePays(player, pays);
        }
    }

    /**
     * 处理充值订单并发货
     * 
     * 调用此方法的线程是充值线程
     * */
    @SuppressWarnings("unchecked")
    protected void handlePays(Player player, List<PayDBBean> pays) {
        PayManager payManager = player.getPayManager();
        // 处理充值订单并发货
        for (PayDBBean pay : pays) {
            int rechargeId = pay.getItem_id();
            pay.setStatus(PayConstant.STATUS_FINISH.getCode());
            pay.setModify_time(new Date());
            // 数据库更新成功后再发货
            if (PayDao.updatePay(pay)) {
                RechargeCfgBean bean = GameDataManager.getRechargeCfgBean(rechargeId);
                int goodsId = bean.getGoodsId();
                boolean isFirstRecharge = payManager.isFirst(rechargeId);
                // 添加购买记录及限购处理
                addBuyRecord(player, payManager, rechargeId);
                // 累计充值
                payManager.setTotalPay(payManager.getTotalPay() + pay.getPay_amount());
                // 发货
                Map<Integer, Integer> items = new HashMap<Integer, Integer>();
                if (pay.getPay_amount() != pay.getSell_amount()) {// 购买商品金额与实际支付金额不匹配，则按比例发放钻石(1元=10钻)
                    items.put(ItemConstantId.RECHARGE_DIAMOND,
                            (int) (pay.getPay_amount() / 100f * 10));
                } else {
                    if (bean.getType() == PayConstant.TYPE_MONTH_CARD.getCode()) {// 月卡类道具
                        MonthCardCfgBean mbean = GameDataManager.getMonthCardCfgBean(goodsId);
                        items = mbean.getPresentItem();// 购买立即获得奖励
                        long startTime = pay.getCreate_time().getTime();
                        // 月卡处理
                        S2CShareMsg.ChangeType ct = S2CShareMsg.ChangeType.UPDATE;
                        MonthCard mc = payManager.getMonthCard();
                        mc.setCid(goodsId);
                        mc.setLastGainTime(startTime);
                        if (mc.getEndTime() < startTime) {
                            mc.setEndTime(startTime + mbean.getDays() * DateUtils.MILLIS_PER_DAY);
                            ct = S2CShareMsg.ChangeType.ADD;
                        } else {
                            mc.setEndTime(mc.getEndTime() + mbean.getDays()
                                    * DateUtils.MILLIS_PER_DAY);
                        }
                        // 触发月卡任务事件
                        Map<String, Object> in = Maps.newHashMap();
                        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.GAIN_MONTH_CARD);
                        player._fireEvent(in, EEventType.OTHER_EVENT.value());
                        BuyMonthCardInfo.Builder builder =
                                PayMsgBuilder.buildBuyMonthCardInfoMsg(mc, null, ct);
                        MessageUtils.send(player, builder);
                    } else {// 充值礼包类道具
                        RechargeGiftBagCfgBean gbean =
                                GameDataManager.getRechargeGiftBagCfgBean(goodsId);
                        if (isFirstRecharge) {
                            items = gbean.getFirstBuyItem();
                        } else {
                            items = gbean.getItem();
                        }
                    }
                    MessageUtils.send(player, PayMsgBuilder.buildRechargeSuccessMsg(rechargeId));
                }
                if (items.size() > 0) {
                    player.getBagManager().addItems(items, true, EReason.ITEM_RECHARGE);
                }
                LOGGER.info("pay handle success, pay:" + pay.toString());
                LogProcessor.getInstance().sendLog(LogBeanFactory.createPayLog(player, pay));
            } else {
                LOGGER.error(ConstDefine.LOG_ERROR_LOGIC_PREFIX
                        + "pay handle err, update pay fail,  pay:" + pay.toString() + "\n"
                        + ExceptionEx.currentThreadTracesWithOutMineLine());
            }
        }
    }

    @Override
    protected String getOrderNo(Player player, GetOrderNo msg) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.RECHARGE)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:recharge");
        }
        PayManager payManager = player.getPayManager();
        int rechargeId = msg.getGoodsId();
        RechargeCfgBean cfg = GameDataManager.getRechargeCfgBean(rechargeId);
        if (cfg == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "pay rechargeId err, rechargeId:" + rechargeId);
        }
        // 距离上次操作时间太短(内网测试充值时会出现，正常情况)
        if (System.currentTimeMillis() - payManager.getLastGetOrderNoTime() < 3000) {
            MessageUtils.throwCondtionError(GameErrorCode.THE_OPERATION_IS_TOO_FREQUENT,
                    "pay operation is too frequent, rechargeId:" + rechargeId + ", playerId:"
                            + player.getPlayerId());
        }

        // 检查购买条件限制
        if (cfg.getType() == PayConstant.TYPE_MONTH_CARD.getCode()) {// 月卡类型
            // 本次测试不开放月卡购买
            MessageUtils.throwCondtionError(216012, "本次测试不开放月卡购买");

        // MonthCardCfgBean cardCfg = GameDataManager.getMonthCardCfgBean(cfg.getGoodsId());
        // MonthCard monthCard = payManager.getMonthCard();
        // if (monthCard.getCid() != 0) {
        // MonthCardCfgBean nowMonthCardCfg =
        // GameDataManager.getMonthCardCfgBean(monthCard.getCid());
        // if (nowMonthCardCfg.getUpgradeId() != cardCfg.getId()) {// 不符合购买规则
        // MessageUtils.throwCondtionError(GameErrorCode.NOT_CONFORM_TO_THE_RULES,
        // "not conform the rules to buy month card");
        // }
        // } else {
        // if (cardCfg.getType() != PayConstant.CARD_TYPE_MONTH_CARD.getCode()) {// 请先购买月卡
        // MessageUtils.throwCondtionError(GameErrorCode.BUY_MONTH_CARD_FIRST_PLEASE,
        // "must buy month card first");
        // }
        // }
        } else {// 礼包类型
            RechargeGiftBagCfgBean giftBagCfg =
                    GameDataManager.getRechargeGiftBagCfgBean(cfg.getGoodsId());
            // 1.等级校验
            int[] levelLimit = giftBagCfg.getPlayerLevel();
            if (levelLimit != null && levelLimit.length > 0) {
                if (player.getLevel() > levelLimit[0] || player.getLevel() < levelLimit[1]) {
                    MessageUtils.throwCondtionError(GameErrorCode.PLAYER_LEVEL_ERR,
                            "level not match");
                }
            }
            // 2.校验购买限制时间
            Calendar startTime = null;
            if (giftBagCfg.getStartDate() != null && giftBagCfg.getStartDate().length > 0) {
                startTime = DateEx.getAssignCalendar(giftBagCfg.getStartDate());
            }
            Calendar endTime = null;
            if (giftBagCfg.getEndDate() != null && giftBagCfg.getEndDate().length > 0) {
                endTime = DateEx.getAssignCalendar(giftBagCfg.getEndDate());
            }
            long now = System.currentTimeMillis();
            if (startTime != null && now < startTime.getTimeInMillis()) {
                MessageUtils.throwCondtionError(GameErrorCode.BEFORE_THE_START_TIME,
                        "sell is not begin");
            }
            if (endTime != null && now >= endTime.getTimeInMillis()) {
                MessageUtils.throwCondtionError(GameErrorCode.MORE_THAN_END_TIME, "sell is end");
            }

            // 3.校验购买次数,配置购买次数为0则不限购
            if (giftBagCfg.getBuyCount() != 0) {
                int resetType = giftBagCfg.getResetType();
                Map<Integer, Integer> limitMap = Maps.newHashMap();
                if (resetType == PayConstant.RESET_TYPE_NO.getCode()) {// 永久限购
                    limitMap = payManager.getForeverLimits();
                } else if (resetType == PayConstant.RESET_TYPE_DAILY.getCode()) {// 每日限购
                    limitMap = payManager.getDayLimits();
                } else if (resetType == PayConstant.RESET_TYPE_WEEK.getCode()) {// 每周限购
                    limitMap = payManager.getWeekLimits();
                } else if (resetType == PayConstant.RESET_TYPE_MONTH.getCode()) {// 每月限购
                    limitMap = payManager.getMonthLimits();
                }
                boolean isLimit = ToolMap.getInt(rechargeId, limitMap) >= giftBagCfg.getBuyCount();
                if (isLimit) {
                    MessageUtils.throwCondtionError(GameErrorCode.BUY_COUNT_BEYOND_UPPER_LIMIT,
                            "buy count beyond limit");
                }
            }
        }

        // 订单号生成规则：“日期+玩家id” eg:20180714172435+pid
        String orderNo =
                DateEx.format(new Date(), DateEx.fmt_num_yyyy_MM_dd_HH_mm_ss)
                        + player.getPlayerId();
        // 售价（单位：分）
        int sell_amount = (int) (cfg.getPrice() * 100);
        boolean b =
                PayUtils.createPay(orderNo, player.getChannelId(), player.getChannelAppId(),
                        player.getUserName(), player.getPlayerId(), rechargeId, sell_amount);
        if (!b) {
            MessageUtils.throwLogicError(GameErrorCode.PLAYER_DATA_ERROR, null,
                    "pay intsert order error,rechargeId:" + rechargeId);
        } else {
            if (ServerConfig.getInstance().isTestServer()
                    && ChannelType.getChannelType(player.getChannelId()) == ChannelType.LOCAL_TEST) {
                // 本地测试充值,直接设置回调成功,方便测试用
                int pay_amount = sell_amount;
                String channelOrderId = player.getChannelId() + "_" + orderNo;
                PayUtils.updatePayByCallBack(channelOrderId, orderNo, player.getPlayerId(),
                        pay_amount, new Date(), "test", "htnonce", "httoken");
            }
        }
        payManager.setLastGetOrderNoTime(System.currentTimeMillis());
        return orderNo;
    }

    @Override
    protected void tick(PayManager payManager) {
        MonthCard monthCard = payManager.getMonthCard();
        // 检查清理月卡超时
        if (monthCard.getEndTime() > 0 && System.currentTimeMillis() > monthCard.getEndTime()) {
            monthCard.clear();
        }
    }

    @Override
    protected void acrossDay(Player player, PayManager payManager, EAcrossDayType type,
            boolean isNotify) {
        if (type == EAcrossDayType.GAME_ACROSS_DAY) {// 6点跨天
            payManager.getDayLimits().clear();
            // 触发月卡任务事件
            Map<String, Object> in = Maps.newHashMap();
            in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.GAIN_MONTH_CARD);
            player._fireEvent(in, EEventType.OTHER_EVENT.value());
        }
        if (type == EAcrossDayType.GAME_ACROSS_WEEK) {// 周一6点跨周
            payManager.getWeekLimits().clear();
        }
        if (type == EAcrossDayType.GAME_ACROSS_MONTH) {// 1号6点跨月
            payManager.getMonthLimits().clear();
        }
    }

    /** 添加某道具购买记录 */
    private void addBuyRecord(Player player, PayManager payManager, int rechargeId) {
        RechargeCfgBean cfg = GameDataManager.getRechargeCfgBean(rechargeId);
        if (cfg == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "pay rechargeId err, rechargeId:" + rechargeId);
        }
        CommonUtil.changeMap(payManager.getBuyMap(), rechargeId, 1);
        // 限购
        if (cfg.getType() == PayConstant.TYPE_GIFT_BAG.getCode()) {// 礼包类型
            RechargeGiftBagCfgBean giftBagCfg =
                    GameDataManager.getRechargeGiftBagCfgBean(cfg.getGoodsId());
            if (giftBagCfg.getBuyCount() != 0) {
                int resetType = giftBagCfg.getResetType();
                Map<Integer, Integer> limitMap = null;
                if (resetType == PayConstant.RESET_TYPE_NO.getCode()) {// 永久限购
                    limitMap = payManager.getForeverLimits();
                } else if (resetType == PayConstant.RESET_TYPE_DAILY.getCode()) {// 每日限购
                    limitMap = payManager.getDayLimits();
                } else if (resetType == PayConstant.RESET_TYPE_WEEK.getCode()) {// 每周限购
                    limitMap = payManager.getWeekLimits();
                } else if (resetType == PayConstant.RESET_TYPE_MONTH.getCode()) {// 每月限购
                    limitMap = payManager.getMonthLimits();
                }
                if (limitMap != null) {
                    CommonUtil.changeMap(limitMap, rechargeId, 1);
                }
            }
        }
        if (player.isOnline()) {// 通知客户端刷新购买次数
            S2CShareMsg.ChangeType ct = S2CShareMsg.ChangeType.UPDATE;
            int buyCount = ToolMap.getInt(rechargeId, payManager.getBuyMap());
            if (buyCount == 1) {
                ct = S2CShareMsg.ChangeType.ADD;
            }
            BuyRecordInfo.Builder builder =
                    PayMsgBuilder.buildBuyRecordInfo(rechargeId, buyCount, ct);
            MessageUtils.send(player, builder);
        }
    }

    @Override
    protected Builder reqGetBuyRecordInfo(PayManager payManager) {
        Map<Integer, Integer> limits = Maps.newHashMap();
        CommonUtil.changeMap(limits, payManager.getForeverLimits());
        CommonUtil.changeMap(limits, payManager.getDayLimits());
        CommonUtil.changeMap(limits, payManager.getWeekLimits());
        CommonUtil.changeMap(limits, payManager.getMonthLimits());

        List<RechargeCfgBean> rechargeCfgs = GameDataManager.getRechargeCfgBeans();
        Set<Integer> limitSet = Sets.newHashSet();
        for (RechargeCfgBean cfg : rechargeCfgs) {
            if (cfg.getType() != PayConstant.TYPE_GIFT_BAG.getCode()) {
                continue;
            }
            RechargeGiftBagCfgBean giftCfg =
                    GameDataManager.getRechargeGiftBagCfgBean(cfg.getGoodsId());
            if (giftCfg.getBuyCount() != 0) {
                limitSet.add(cfg.getId());
            }
        }

        for (Entry<Integer, Integer> entry : payManager.getBuyMap().entrySet()) {
            if (limitSet.contains(entry.getKey())) {
                continue;
            }
            CommonUtil.changeMap(limits, entry.getKey(), entry.getValue());
        }

        S2CShareMsg.ChangeType ct = S2CShareMsg.ChangeType.DEFAULT;
        return PayMsgBuilder.buildGetBuyRecordInfoMsg(limits, ct);
    }

}
