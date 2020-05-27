package logic.pay.handler;

import logic.character.bean.Player;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.game.protobuf.c2s.C2SRechargeMsg;
import org.game.protobuf.s2c.S2CRechargeMsg.GetBuyRecordInfo;

/**
 * 
 * @Description 获取限购商品购买次数信息
 * @author LiuJiang
 * @date 2018年7月18日 下午3:18:07
 *
 */
@MHandler(messageClazz = C2SRechargeMsg.GetBuyRecordInfo.class)
public class MReqGetBuyRecordInfoHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) this.getGameData();
        GetBuyRecordInfo.Builder builder = player.getPayManager().reqGetBuyRecordInfo();
        MessageUtils.send(player, builder);
    }
}
