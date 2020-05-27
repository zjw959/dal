package logic.medal.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SMedalMsg;
import org.game.protobuf.c2s.C2SMedalMsg.ReqTakeOffMedal;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.medal.MedalManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SMedalMsg.ReqTakeOffMedal.class)
public class MReqMedalTakeOffHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqMedalTakeOffHandler.class);

    @Override
    public void action() throws Exception {
        Player player = PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SMedalMsg.ReqTakeOffMedal msg = (ReqTakeOffMedal) getMessage().getData();
        MedalManager manager = player.getMedalManager();
        manager.reqTakeOffMedal(msg.getCid());
    }

}
