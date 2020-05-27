package logic.login.handler;

import logic.login.service.ILoginScript;
import logic.support.LogicScriptsUtils;
import thread.base.GameInnerHandler;

/**
 * @Description 登录队列处理
 *
 */
public class LTickLoginHandler extends GameInnerHandler {

    @Override
    public void action() {
        ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
        script.tick();
    }
}
