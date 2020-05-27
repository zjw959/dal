package logic.chasm.handler;

import org.game.protobuf.c2s.C2SFightMsg;

import logic.support.LogicScriptsUtils;
import message.MHandler;
import message.MessageHandler;
import net.Session;

@MHandler(messageClazz = C2SFightMsg.ReqReviveFight.class)
public class ReqReviveFightHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Session session = (Session) getGameData();
        LogicScriptsUtils.getChasmScript().reqReviveFight(session);
    }

}
