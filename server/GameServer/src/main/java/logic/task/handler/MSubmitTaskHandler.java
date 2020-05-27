package logic.task.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2STaskMsg.SubmitTask;

import exception.AbstractLogicModelException;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2STaskMsg.SubmitTask.class)
public class MSubmitTaskHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MSubmitTaskHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        org.game.protobuf.c2s.C2STaskMsg.SubmitTask msg = (SubmitTask) getMessage().getData();
        player.getTaskManager().submitTasks(player, msg.getTaskCid());
    }
}
