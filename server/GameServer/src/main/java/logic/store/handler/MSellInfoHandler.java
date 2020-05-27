package logic.store.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SStoreMsg.SellInfo;

import exception.AbstractLogicModelException;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SStoreMsg.SellInfo.class)
public class MSellInfoHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MSellInfoHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        org.game.protobuf.c2s.C2SStoreMsg.SellInfo msg = (SellInfo) getMessage().getData();
        player.getStortManager().sell(player, msg.getGoodsList(), msg.getGoodsCount());

    }
}
