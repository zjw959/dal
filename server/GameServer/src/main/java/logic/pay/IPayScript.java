package logic.pay;

import logic.character.bean.Player;
import logic.constant.EAcrossDayType;

import org.game.protobuf.c2s.C2SRechargeMsg.GetOrderNo;
import org.game.protobuf.s2c.S2CRechargeMsg.GetBuyRecordInfo;

import script.IScript;

public abstract class IPayScript implements IScript {

    /** 系统自动定时拉取需处理的订单 */
    protected abstract void systemGetPays();

    /** 玩家登录时拉取需处理的订单 */
    protected abstract void loginGetPays(int playerId);

    /** 获取充值订单号 */
    protected abstract String getOrderNo(Player player, GetOrderNo msg);

    /** tick */
    protected abstract void tick(PayManager payManager);

    /** acrossDay */
    protected abstract void acrossDay(Player player, PayManager payManager, EAcrossDayType type,
            boolean isNotify);

    protected abstract GetBuyRecordInfo.Builder reqGetBuyRecordInfo(PayManager payManager);

}
