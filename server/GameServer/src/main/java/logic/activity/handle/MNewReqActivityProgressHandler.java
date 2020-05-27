package logic.activity.handle;

import org.apache.log4j.Logger;

import exception.AbstractLogicModelException;
import logic.activity.ActivityCmdUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SActivityMsg.NewReqActivityProgress.class)
public class MNewReqActivityProgressHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MNewReqActivityProgressHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        ActivityCmdUtils.getDefault().sendAllActivityRecordToPlayer(player);
    }
}
