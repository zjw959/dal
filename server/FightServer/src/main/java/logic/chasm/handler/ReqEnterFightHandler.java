package logic.chasm.handler;

import org.game.protobuf.c2s.C2SFightMsg;

import logic.support.LogicScriptsUtils;
import message.MHandler;
import message.MessageHandler;
import net.Session;

@MHandler(messageClazz = C2SFightMsg.ReqEnterFight.class)
public class ReqEnterFightHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Session session = (Session) getGameData();
        C2SFightMsg.ReqEnterFight msg = (C2SFightMsg.ReqEnterFight) getMessage().getData();
        LogicScriptsUtils.getChasmScript().reqEnterFight(session, msg);
    }

}
