package logic.favor.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqGetEventChoices;

import logic.favor.structs.FavorDatingConst;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SExtraDatingMsg.ReqGetEventChoices.class)
public class MReqFavorEventChoicesHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqFavorEventChoicesHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        ReqGetEventChoices msg = (ReqGetEventChoices) getMessage().getData();
        if (msg.getDatingType() == FavorDatingConst.DATING_TYPE_NOVEL) {
            player.getNovelDatingManager().ReqGetEventChoices(player, msg);
            return;
        }
        player.getFavorDatingManager().reqGetEventChoices(player, msg);
    }
}
