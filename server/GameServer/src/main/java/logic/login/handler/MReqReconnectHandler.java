package logic.login.handler;

import org.game.protobuf.c2s.C2SLoginMsg;

import exception.AbstractLogicModelException;
import logic.login.service.ILoginScript;
import logic.support.LogicScriptsUtils;
import message.MHandler;
import message.MessageHandler;

/**
 * 请求重连消息Handler
 */
@MHandler(messageClazz = C2SLoginMsg.ReqReconnect.class)
public class MReqReconnectHandler extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        C2SLoginMsg.ReqReconnect msg = (C2SLoginMsg.ReqReconnect) getMessage().getData();
        ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
        script.reqReconnect(ctx, msg);
    }
}
