package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CNewBuildingMsg;
import exception.AbstractLogicModelException;
import logic.city.build.bean.PrizeClawRecord;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 
 * 获取抓娃娃信息 code = 2062
 * 
 * @author lihongji
 *
 */

@MHandler(messageClazz = org.game.protobuf.c2s.C2SNewBuildingMsg.ReqGetGashaponInfo.class)
public class MReqGetGashaponInfoHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqGetGashaponInfoHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        PrizeClawRecord record = player.getPrizeClawGameManager().getPrizeClawRecordInfo();
        S2CNewBuildingMsg.GetGashaponInfo.Builder build =
                NewBuildingMsgBuilder.buildGashaponInfoMsg(record).toBuilder();
        MessageUtils.send(player, build);


    }
}
