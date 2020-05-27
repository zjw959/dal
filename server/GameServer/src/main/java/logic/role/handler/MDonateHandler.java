package logic.role.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SRoleMsg;

import exception.AbstractLogicModelException;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SRoleMsg.Donate.class)
public class MDonateHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MDonateHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }

        C2SRoleMsg.Donate msg = (C2SRoleMsg.Donate) getMessage().getData();
        player.getRoleManager().donate(msg.getRoleId(), msg.getItemCid(), msg.getNum());
    }
}
