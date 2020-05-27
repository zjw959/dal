package thread.sys;

import logic.login.handler.LTickLoginQueueHandler;
import logic.login.service.LoginCheckService;
import thread.sys.base.SysFunctionProcessor;

/**
 * 
 * @Description 登录验证线程
 * @author LiuJiang
 * @date 2018年6月22日 下午8:48:23
 *
 */
public class LoginCheckProcessor extends SysFunctionProcessor {

    private LoginCheckProcessor() {
        super(LoginCheckProcessor.class.getSimpleName());
        _initTimer(1000, 1000, false);
    }

    @Override
    protected void registerService() {
        services.add(LoginCheckService.getInstance());
    }

    @Override
    protected void checkPerSecond() {
        this.executeInnerHandler(new LTickLoginQueueHandler());
    }


}
