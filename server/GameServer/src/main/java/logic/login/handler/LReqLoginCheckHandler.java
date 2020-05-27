package logic.login.handler;

import logic.login.bean.LoginCheckBean;
import logic.login.service.ILoginCheckScript;
import logic.support.LogicScriptsUtils;
import thread.base.GameInnerHandler;

/**
 * 
 * @Description ReqLoginCheckHandler
 * @author LiuJiang
 * @date 2018年6月25日 下午9:09:32
 *
 */
public class LReqLoginCheckHandler extends GameInnerHandler {
    private final LoginCheckBean bean;


    public LReqLoginCheckHandler(LoginCheckBean bean) {
        this.bean = bean;
    }

    public LoginCheckBean getBean() {
        return bean;
    }

    @Override
    public void action() {
        ILoginCheckScript script = LogicScriptsUtils.getLoginCheckScript();
        script.reqLoginCheck(bean);
    }

}
