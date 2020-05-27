package logic.endless.handler;

import logic.msgBuilder.EndlessCloisterMsgBuilder;
import message.MHandler;
import message.MessageHandler;

import org.apache.log4j.Logger;

import exception.AbstractLogicModelException;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SEndlessCloisterMsg.ReqEndlessCloisterInfo.class)
public class MReqEndlessCloisterInfoHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqEndlessCloisterInfoHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }

        org.game.protobuf.s2c.S2CEndlessCloisterMsg.RspEndlessCloisterInfo.Builder builder =
                EndlessCloisterMsgBuilder.createEndlessCloisterInfo(player);
        logic.support.MessageUtils.send(player, builder);
    }
}
