package logic.pay.handler;

import logic.character.bean.Player;
import logic.msgBuilder.PayMsgBuilder;
import logic.pay.PayService;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SRechargeMsg;
import org.game.protobuf.c2s.C2SRechargeMsg.GetOrderNo;
import org.game.protobuf.s2c.S2CRechargeMsg;

/**
 * 
 * @Description 获取游戏内充值订单号
 * @author LiuJiang
 * @date 2018年7月14日 下午8:17:25
 *
 */
@MHandler(messageClazz = C2SRechargeMsg.GetOrderNo.class)
public class MReqGetOrderNoHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();
        GetOrderNo msg = (GetOrderNo) getMessage().getData();
        String orderNo = PayService.getInstance().getOrderNo(player, msg);
        S2CRechargeMsg.GetOrderNo.Builder builder = PayMsgBuilder.buildGetOrderNoMsg(orderNo);
        MessageUtils.send(player, builder);
    }
}
