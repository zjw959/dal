package logic.activity.handle;

import org.apache.log4j.Logger;

import exception.AbstractLogicModelException;
import logic.activity.ActivityCmdUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SActivityMsg.NewReqActivityItems.class)
public class MNewReqActivityItemsHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MNewReqActivityItemsHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        ActivityCmdUtils.getDefault().sendAllActivityItemToPlayer(player);
    }
}
