package logic.chasm.handler;

import org.game.protobuf.c2s.C2SFightMsg;
import org.game.protobuf.c2s.C2SFightMsg.ReqPullNetFrame;

import logic.support.LogicScriptsUtils;
import message.MHandler;
import message.MessageHandler;
import net.Session;

@MHandler(messageClazz = C2SFightMsg.ReqPullNetFrame.class)
public class ReqPullNetFrameHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Session session = (Session) getGameData();
        C2SFightMsg.ReqPullNetFrame msg = (ReqPullNetFrame) getMessage().getData();
        LogicScriptsUtils.getChasmScript().reqPullNetFrame(session, msg);
    }

}
