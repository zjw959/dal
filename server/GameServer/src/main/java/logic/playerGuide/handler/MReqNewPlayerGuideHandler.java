package logic.playerGuide.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SPlayerMsg;
import org.game.protobuf.c2s.C2SPlayerMsg.ReqNewPlayerGuide;

import exception.AbstractLogicModelException;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SPlayerMsg.ReqNewPlayerGuide.class)
public class MReqNewPlayerGuideHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqNewPlayerGuideHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SPlayerMsg.ReqNewPlayerGuide msg = (ReqNewPlayerGuide) getMessage().getData();
        player.getPlayerGuideManager().reqNewPlayerGuide(msg.getData());;
    }
}
