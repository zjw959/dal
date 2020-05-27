package logic.pay;

import java.util.Map;

import logic.basecore.IAcrossDay;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.constant.EAcrossDayType;
import logic.msgBuilder.PayMsgBuilder;
import logic.pay.bean.MonthCard;
import logic.support.LogicScriptsUtils;

import org.game.protobuf.s2c.S2CRechargeMsg.GetBuyRecordInfo;
import org.game.protobuf.s2c.S2CRechargeMsg.GetMonthCardInfo;
import org.game.protobuf.s2c.S2CShareMsg;

import utils.ToolMap;

import com.google.common.collect.Maps;

/**
 * 
 * @Description 充值管理器
 * @author LiuJiang
 * @date 2018年7月12日 下午6:38:14
 *
 */
public class PayManager extends PlayerBaseFunctionManager implements IRoleJsonConverter, IAcrossDay {
    // private static final Logger LOGGER = Logger.getLogger(PayManager.class);
    /** 每周限购下次刷新时间 */
    long nextWeekTime;
    /** 充值道具累计购买次数(用于判定是否首充) */
    private Map<Integer, Integer> buyMap = Maps.newConcurrentMap();
    /** 永久限购道具已买次数 */
    private Map<Integer, Integer> foreverLimits = Maps.newConcurrentMap();
    /** 每日限购道具已买次数 */
    private Map<Integer, Integer> dayLimits = Maps.newConcurrentMap();
    /** 每周限购道具已买次数 */
    private Map<Integer, Integer> weekLimits = Maps.newConcurrentMap();
    /** 每月限购道具已买次数 */
    private Map<Integer, Integer> monthLimits = Maps.newConcurrentMap();
    /** 月卡 */
    private MonthCard monthCard = new MonthCard();
    /** 累计充值（单位：分） */
    long totalPay;
    /** 上次请求订单号时间 */
    transient long lastGetOrderNoTime;

    @Override
    public void tick() {
        LogicScriptsUtils.getPayScript().tick(this);
    }

    @Override
    public void loginInit() {
        // 登录时处理离线未处理的充值订单(交给充值线程处理)
        PayService.getInstance().loginGetPays(player.getPlayerId());
    }

    /** 是否首充某道具 */
    public boolean isFirst(int rechargeId) {
        return ToolMap.getInt(rechargeId, buyMap) == 0;
    }

    /** 获取月卡 */
    public MonthCard getMonthCard() {
        return monthCard;
    }

    /** 累计充值 */
    public long getTotalPay() {
        return totalPay;
    }

    /** 累计充值 */
    public void setTotalPay(long totalPay) {
        this.totalPay = totalPay;
    }

    /**
     * 获取限购商品购买次数信息
     */
    public GetBuyRecordInfo.Builder reqGetBuyRecordInfo() {
        return LogicScriptsUtils.getPayScript().reqGetBuyRecordInfo(this);
    }

    /**
     * 获取月卡信息
     */
    public GetMonthCardInfo.Builder reqGetMonthCardInfo() {
        S2CShareMsg.ChangeType ct = S2CShareMsg.ChangeType.DEFAULT;
        return PayMsgBuilder.buildGetMonthCardInfoMsg(monthCard, ct);
    }

    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        LogicScriptsUtils.getPayScript().acrossDay(this.getPlayer(), this, type, isNotify);
    }

    public long getNextWeekTime() {
        return nextWeekTime;
    }

    public Map<Integer, Integer> getBuyMap() {
        return buyMap;
    }

    public Map<Integer, Integer> getForeverLimits() {
        return foreverLimits;
    }

    public Map<Integer, Integer> getDayLimits() {
        return dayLimits;
    }

    public Map<Integer, Integer> getWeekLimits() {
        return weekLimits;
    }

    public Map<Integer, Integer> getMonthLimits() {
        return monthLimits;
    }

    public void setMonthCard(MonthCard monthCard) {
        this.monthCard = monthCard;
    }

    public long getLastGetOrderNoTime() {
        return lastGetOrderNoTime;
    }

    public void setLastGetOrderNoTime(long lastGetOrderNoTime) {
        this.lastGetOrderNoTime = lastGetOrderNoTime;
    }

}
