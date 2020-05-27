package logic.login.handler;

import logic.login.bean.LoginCheckBean;
import logic.login.service.ILoginCheckScript;
import logic.support.LogicScriptsUtils;
import thread.base.GameInnerHandler;

public class LLoginCheckAddQueueHandler extends GameInnerHandler {
    private final LoginCheckBean loginCheckBean;

    public LLoginCheckAddQueueHandler(LoginCheckBean loginCheckBean) {
        this.loginCheckBean = loginCheckBean;
    }

    @Override
    public void action() {
        ILoginCheckScript script = LogicScriptsUtils.getLoginCheckScript();
        script.addQueue(loginCheckBean);
    }

}
