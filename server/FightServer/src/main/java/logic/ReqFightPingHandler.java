package logic;

import org.game.protobuf.c2s.C2SFightMsg;

import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SFightMsg.ReqFightPing.class)
public class ReqFightPingHandler extends MessageHandler {

    @Override
    public void action() throws Exception {}

}
