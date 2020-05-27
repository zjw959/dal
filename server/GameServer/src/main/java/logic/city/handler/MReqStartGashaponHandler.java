package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CNewBuildingMsg;
import exception.AbstractLogicModelException;
import logic.city.PrizeClawGameManager;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 
 * 请求开始抓娃娃 code = 2063
 * 
 * @author lihongji
 *
 */

@MHandler(messageClazz = org.game.protobuf.c2s.C2SNewBuildingMsg.ReqStartGashapon.class)
public class MReqStartGashaponHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqStartGashaponHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        PrizeClawGameManager manager = player.getPrizeClawGameManager();
        manager.setPrizeClawGameEndTime();
        S2CNewBuildingMsg.RespStartGashapon.Builder builder =
                NewBuildingMsgBuilder.buildRespStartGashaponMsg(manager.getPrizeClawGameEndTime());
        MessageUtils.send(player, builder);
    }
}
