package logic;

import org.game.protobuf.c2s.C2SLoginMsg;

import exception.AbstractLogicModelException;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SLoginMsg.Ping.class)
public class PingHandler extends MessageHandler {
    @Override
    public void action() throws AbstractLogicModelException {}
}
