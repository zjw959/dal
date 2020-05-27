package logic.chasm.handler;

import org.game.protobuf.c2s.C2SFightMsg;
import org.game.protobuf.c2s.C2SFightMsg.ReqEndFight;

import logic.support.LogicScriptsUtils;
import message.MHandler;
import message.MessageHandler;
import net.Session;

@MHandler(messageClazz = C2SFightMsg.ReqEndFight.class)
public class ReqEndFightHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Session session = (Session) getGameData();
        C2SFightMsg.ReqEndFight reqEndFight = (ReqEndFight) getMessage().getData();
        LogicScriptsUtils.getChasmScript().reqEndFight(session.getFightRoom(), reqEndFight);
    }

}
