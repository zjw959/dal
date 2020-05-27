package logic.elementCollection.handler;

import org.game.protobuf.c2s.C2SElementCollectMsg;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SElementCollectMsg.GetAllElement.class)
public class MReqElementCollectionHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            return;
        }
        player.getElementCollectionManager().reqGetElementCollection();
    }


}
