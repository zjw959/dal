package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.c2s.C2SNewBuildingMsg.ReqDoHandWork;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 
 * 请求手工制作 code = 2081
 * 
 * @author lihongji
 *
 */

@MHandler(messageClazz = org.game.protobuf.c2s.C2SNewBuildingMsg.ReqDoHandWork.class)
public class MReqDoHandWorkHandler extends MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MReqDoHandWorkHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SNewBuildingMsg.ReqDoHandWork req = (ReqDoHandWork) getMessage().getData();
        player.getManualManager().createManualEvent(req.getManualId());
        MessageUtils.send(player, NewBuildingMsgBuilder.getHandWorkEvent(player));
    }


}
