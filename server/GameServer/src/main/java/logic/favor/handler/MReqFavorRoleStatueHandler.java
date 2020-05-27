package logic.favor.handler;

import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFavorDatingRoleStatue;

import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFavorDatingRoleStatue.class)
public class MReqFavorRoleStatueHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            return;
        }
        ReqFavorDatingRoleStatue msg = (ReqFavorDatingRoleStatue) getMessage().getData();
        player.getFavorDatingManager().reqRoleStatue(player);
    }

}
