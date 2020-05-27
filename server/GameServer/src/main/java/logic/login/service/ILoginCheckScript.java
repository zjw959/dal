package logic.login.service;

import logic.login.bean.LoginCheckBean;

import org.apache.http.HttpResponse;

import script.IScript;

/**
 * login check logic script inteface
 */
public interface ILoginCheckScript extends IScript {

    /** 加入登录队列 */
    void addQueue(LoginCheckBean bean);

    /** 请求登录验证 */
    void reqLoginCheck(LoginCheckBean bean);

    /** 登录验证回调 */
    void loginCheckCallBack(LoginCheckBean bean, HttpResponse t);

    /** tick 检查登录队列 */
    void tick();
}
