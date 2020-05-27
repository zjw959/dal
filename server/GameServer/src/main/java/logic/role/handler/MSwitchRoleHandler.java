package logic.role.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SRoleMsg;

import exception.AbstractLogicModelException;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SRoleMsg.SwitchRole.class)
public class MSwitchRoleHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MSwitchRoleHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SRoleMsg.SwitchRole msg = (C2SRoleMsg.SwitchRole) getMessage().getData();

        player.getRoleManager().switchRole(msg.getRoleId());
    }
}
