package logic.favor.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqChooseEntranceEvent;

import logic.favor.structs.FavorDatingConst;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SExtraDatingMsg.ReqChooseEntranceEvent.class)
public class MReqFavorChooseEventHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqFavorChooseEventHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        ReqChooseEntranceEvent msg = (ReqChooseEntranceEvent) getMessage().getData();
        if (msg.getDatingType() == FavorDatingConst.DATING_TYPE_NOVEL) {
            player.getNovelDatingManager().reqChooseEntrance(player, msg);
            return;
        }
        player.getFavorDatingManager().reqChooseEntrance(player, msg);
    }
}
