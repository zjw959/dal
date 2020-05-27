package logic.store.handler;
import message.MHandler;
import message.MessageHandler;
import exception.AbstractLogicModelException;
import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SStoreMsg.BuyGoods;
@MHandler(messageClazz = org.game.protobuf.c2s.C2SStoreMsg.BuyGoods.class)
public class MBuyGoodsHandler extends MessageHandler {
	private static final Logger LOGGER = Logger.getLogger(MBuyGoodsHandler.class);
	@Override
	public void action() throws AbstractLogicModelException {
		logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
		if (player == null) {
		    LOGGER.error(this.getClass().getName()+" can not find player ");
		    return;
		}
		org.game.protobuf.c2s.C2SStoreMsg.BuyGoods msg = (BuyGoods) getMessage().getData();
		player.getStortManager().buy(player, msg.getCid(), msg.getNum());
		
	}
}