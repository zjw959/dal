package logic.chasm.handler;

import org.game.protobuf.c2s.C2SFightMsg;
import org.game.protobuf.c2s.C2SFightMsg.ReqOperateFight;

import logic.support.LogicScriptsUtils;
import message.MHandler;
import message.MessageHandler;
import net.Session;
import server.FightServer;

@MHandler(messageClazz = C2SFightMsg.ReqOperateFight.class)
public class ReqOperateFightHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        FightServer.RECEIVE_FRAME_DATA.incrementAndGet();
        Session session = (Session) getGameData();
        ReqOperateFight operate = (ReqOperateFight) getMessage().getData();
        LogicScriptsUtils.getChasmScript().reqOperateFight(session, operate);
    }
    
}
