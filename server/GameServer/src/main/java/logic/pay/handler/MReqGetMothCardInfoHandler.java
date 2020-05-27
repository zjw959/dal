package logic.pay.handler;

import logic.character.bean.Player;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SRechargeMsg;
import org.game.protobuf.s2c.S2CRechargeMsg.GetMonthCardInfo;

/**
 * 
 * @Description 获取月卡信息
 * @author LiuJiang
 * @date 2018年7月18日 下午3:18:07
 *
 */
@MHandler(messageClazz = C2SRechargeMsg.GetMonthCardInfo.class)
public class MReqGetMothCardInfoHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();
        GetMonthCardInfo.Builder builder = player.getPayManager().reqGetMonthCardInfo();
        MessageUtils.send(player, builder);
    }
}
