package logic.role.handler;
import message.MHandler;
import message.MessageHandler;
import exception.AbstractLogicModelException;
import org.apache.log4j.Logger;
@MHandler(messageClazz = org.game.protobuf.c2s.C2SRoleMsg.TouchRole.class)
public class MTouchRoleHandler extends MessageHandler {
	private static final Logger LOGGER = Logger.getLogger(MTouchRoleHandler.class);
	@Override
	public void action() throws AbstractLogicModelException {
		logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
		if (player == null) {
		    LOGGER.error(this.getClass().getName()+" can not find player ");
		    return;
		}

		player.getRoleManager().touchRole(player);
		
	}
}