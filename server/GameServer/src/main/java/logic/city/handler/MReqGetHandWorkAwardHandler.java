package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.c2s.C2SNewBuildingMsg.ReqGetHandWorkAward;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 
 * 领取手工制作奖励 code=2083
 * 
 * @author lihongji
 *
 */


@MHandler(messageClazz = org.game.protobuf.c2s.C2SNewBuildingMsg.ReqGetHandWorkAward.class)
public class MReqGetHandWorkAwardHandler extends MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MReqGetHandWorkAwardHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SNewBuildingMsg.ReqGetHandWorkAward req = (ReqGetHandWorkAward) getMessage().getData();
        MessageUtils.send(player, player.getManualManager().getAward(req.getManualId()));
    }
}
