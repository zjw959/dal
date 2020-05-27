package logic.store.handler;
import message.MHandler;
import message.MessageHandler;
import exception.AbstractLogicModelException;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SStoreMsg.GetStoreInfo;
@MHandler(messageClazz = org.game.protobuf.c2s.C2SStoreMsg.GetStoreInfo.class)
public class MGetStoreInfoHandler extends MessageHandler {
	private static final Logger LOGGER = Logger.getLogger(MGetStoreInfoHandler.class);
	@Override
	public void action() throws AbstractLogicModelException {
		logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
		if (player == null) {
		    LOGGER.error(this.getClass().getName()+" can not find player ");
		    return;
		}
		org.game.protobuf.c2s.C2SStoreMsg.GetStoreInfo msg = (GetStoreInfo) getMessage().getData();
		player.getStortManager().getStoreInfo(player, msg.getCidList());
	}
}