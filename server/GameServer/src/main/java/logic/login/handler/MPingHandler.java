package logic.login.handler;

import org.game.protobuf.c2s.C2SLoginMsg;
import org.game.protobuf.s2c.S2CLoginMsg;

import exception.AbstractLogicModelException;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;
import message.SMessage;

@MHandler(messageClazz = C2SLoginMsg.Ping.class)
public class MPingHandler extends MessageHandler {
    @Override
    public void action() throws AbstractLogicModelException {
        SMessage msg = new SMessage(S2CLoginMsg.Pong.MsgID.eMsgID_VALUE, new byte[0]);
        MessageUtils.send(getCtx(), msg);
    }
}
