package thread.sys;

import logic.login.handler.LTickLoginHandler;
import logic.login.service.LoginService;
import thread.sys.base.SysFunctionProcessor;

/**
 * 玩家登陆线程
 */
public class LoginProcessor extends SysFunctionProcessor {
    protected LoginProcessor() {
        super(LoginProcessor.class.getSimpleName());
        _initTimer(1000, 1000, true);
    }

    @Override
    protected void registerService() {
        services.add(LoginService.getInstance());
    }

    @Override
    protected void checkPerSecond() {
        this.executeInnerHandler(new LTickLoginHandler());
    }
}
