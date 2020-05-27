package logic.login.handler;

import org.game.protobuf.c2s.C2SLoginMsg;

import exception.AbstractLogicModelException;
import logic.login.service.ILoginScript;
import logic.support.LogicScriptsUtils;
import message.MHandler;
import message.MessageHandler;

/**
 * 请求登录消息Handler
 */
@MHandler(messageClazz = C2SLoginMsg.EnterGame.class)
public class MReqLoginHandler extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        C2SLoginMsg.EnterGame msg = (C2SLoginMsg.EnterGame) getMessage().getData();
        ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
        script.reqLogin(ctx, msg);
    }
}