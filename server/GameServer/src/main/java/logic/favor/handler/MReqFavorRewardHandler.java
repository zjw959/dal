package logic.favor.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFavorReward;

import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFavorReward.class)
public class MReqFavorRewardHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqFavorRewardHandler.class);
    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        ReqFavorReward msg = (ReqFavorReward) getMessage().getData();
        player.getFavorDatingManager().reqFavorReward(player, msg);
    }

}
