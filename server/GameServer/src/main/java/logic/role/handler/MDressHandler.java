package logic.role.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SRoleMsg;
import org.game.protobuf.c2s.C2SRoleMsg.Dress;

import exception.AbstractLogicModelException;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SRoleMsg.Dress.class)
public class MDressHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MDressHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SRoleMsg.Dress msg = (Dress) getMessage().getData();
        player.getRoleManager().dress(msg.getRoleId(), msg.getItemId());

    }
}
