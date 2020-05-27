package logic.city.handler;

import org.apache.log4j.Logger;
import exception.AbstractLogicModelException;
import logic.city.build.bean.PrizeClawRecord;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/**
 * 
 * 请求刷新娃娃蛋池 code = 2065
 * 
 * @author lihongji
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SNewBuildingMsg.ReqRefreshGashaponPool.class)
public class MReqRefreshGashaponPoolHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqRefreshGashaponPoolHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        player.getPrizeClawGameManager().refreshPrizeClawGame();
        PrizeClawRecord record = player.getPrizeClawGameManager().getPrizeClawRecordInfo();
        MessageUtils.send(player, NewBuildingMsgBuilder.buildRespRefreshGashaponPoolMsg(record));
    }
}
