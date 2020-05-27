package logic.task.handler;

import org.apache.log4j.Logger;

import exception.AbstractLogicModelException;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2STaskMsg.ReqTasks.class)
public class MReqTasksHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqTasksHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        player.getTaskManager().reqTasks(player);

    }
}
