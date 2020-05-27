package logic.role.handler;
import message.MHandler;
import message.MessageHandler;
import exception.AbstractLogicModelException;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SRoleMsg.ChangeRoom;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SRoleMsg.ChangeRoom.class)
public class MChangeRoomHandler extends MessageHandler {
	private static final Logger LOGGER = Logger.getLogger(MChangeRoomHandler.class);
	@Override
	public void action() throws AbstractLogicModelException {
		logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
		if (player == null) {
		    LOGGER.error(this.getClass().getName()+" can not find player ");
		    return;
		}
		org.game.protobuf.c2s.C2SRoleMsg.ChangeRoom msg = (ChangeRoom) getMessage().getData();
		
		player.getRoleManager().changeRoom(player, msg.getRoleCid(), msg.getRoomCid());
	}
}