package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.c2s.C2SNewBuildingMsg.ReqGiveUpJob;
import exception.AbstractLogicModelException;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 
 * 放弃兼职
 * 
 * @author lihongji
 *
 */

@MHandler(messageClazz = org.game.protobuf.c2s.C2SNewBuildingMsg.ReqGiveUpJob.class)
public class MReqGiveUpJob extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqGiveUpJob.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SNewBuildingMsg.ReqGiveUpJob req = (ReqGiveUpJob) getMessage().getData();
        int buildingId = req.getBuildingId();
        player.getPartTimeManager().giveUpJob();
        MessageUtils.send(player, NewBuildingMsgBuilder.giveUpJob(buildingId, player));
    }
}
