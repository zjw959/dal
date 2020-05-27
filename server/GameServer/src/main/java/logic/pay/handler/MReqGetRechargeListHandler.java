package logic.pay.handler;

import logic.character.bean.Player;
import logic.msgBuilder.PayMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SRechargeMsg;
import org.game.protobuf.s2c.S2CRechargeMsg;

import data.GameDataManager;

/**
 * 
 * @Description 获取充值商品列表
 * @author LiuJiang
 * @date 2018年7月14日 下午8:18:07
 *
 */
@MHandler(messageClazz = C2SRechargeMsg.GetRechargeCfg.class)
public class MReqGetRechargeListHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();
        S2CRechargeMsg.GetRechargeCfg.Builder builder =
                PayMsgBuilder.buildRechargeListMsg(GameDataManager.getRechargeCfgBeans());
        MessageUtils.send(player, builder);
    }
}
