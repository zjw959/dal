package logic.medal.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SMedalMsg;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.medal.MedalManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SMedalMsg.ReqActivateMedals.class)
public class MReqMedalInfoHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqMedalInfoHandler.class);

    @Override
    public void action() throws Exception {
        Player player = PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        MedalManager manager = player.getMedalManager();
        manager.reqActivateMedals();
    }

}
