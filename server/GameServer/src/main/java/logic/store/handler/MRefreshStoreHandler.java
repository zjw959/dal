package logic.store.handler;

import message.MHandler;
import message.MessageHandler;
import exception.AbstractLogicModelException;
import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SStoreMsg.RefreshStore;
@MHandler(messageClazz = org.game.protobuf.c2s.C2SStoreMsg.RefreshStore.class)
public class MRefreshStoreHandler extends MessageHandler {
	private static final Logger LOGGER = Logger.getLogger(MRefreshStoreHandler.class);
	@Override
	public void action() throws AbstractLogicModelException {
		logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
		if (player == null) {
		    LOGGER.error(this.getClass().getName()+" can not find player ");
		    return;
		}

		org.game.protobuf.c2s.C2SStoreMsg.RefreshStore msg = (RefreshStore) getMessage().getData();
		player.getStortManager().refreshStore(player, msg.getCid());
		
	}
}