package logic.login.handler;

import logic.constant.LoginErrorCode;
import logic.login.bean.LoginCheckBean;
import logic.login.service.ILoginCheckScript;
import logic.login.service.ILoginScript;
import logic.support.LogicScriptsUtils;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

/**
 * 
 * @Description 登录验证回调
 * @author LiuJiang
 * @date 2018年6月25日 下午8:54:49
 *
 */
public class LoginCheckHttpCallBack implements FutureCallback<HttpResponse> {
    private final LoginCheckBean bean;

    public LoginCheckHttpCallBack(LoginCheckBean bean) {
        this.bean = bean;
    }

    @Override
    public void completed(HttpResponse t) {
        ILoginCheckScript script = LogicScriptsUtils.getLoginCheckScript();
        script.loginCheckCallBack(bean, t);
    }

    @Override
    public void failed(Exception excptn) {
        ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
        script.loginFailed(bean.getCtx(), LoginErrorCode.LOGIN_TIME_OUT,
                "login failed. token=" + bean.getToken() + " result=" + excptn.toString());
    }

    @Override
    public void cancelled() {
        ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
        script.loginFailed(bean.getCtx(), LoginErrorCode.LOGIN_TIME_OUT,
                "login failed. token=" + bean.getToken() + " result=" + "cancelled");
    }
}
