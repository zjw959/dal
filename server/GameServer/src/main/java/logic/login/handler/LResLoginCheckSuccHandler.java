package logic.login.handler;

import logic.login.bean.LoginCheckBean;
import logic.login.service.ILoginScript;
import logic.support.LogicScriptsUtils;
import thread.base.GameInnerHandler;

/**
 * 
 * @Description LResLoginCheckSuccHandler
 * @author LiuJiang
 * @date 2018年6月25日 下午9:23:10
 *
 */
public class LResLoginCheckSuccHandler extends GameInnerHandler {
    private final LoginCheckBean loginCheckBean;

    public LResLoginCheckSuccHandler(LoginCheckBean loginCheckBean) {
        this.loginCheckBean = loginCheckBean;
    }

    @Override
    public void action() {
        ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
        script.innerLoginCheckSuccCallBack(loginCheckBean);
    }
}
