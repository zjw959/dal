package logic.login.handler;

import logic.login.service.ILoginCheckScript;
import logic.support.LogicScriptsUtils;
import thread.base.GameInnerHandler;

/**
 * 
 * @Description 登录队列处理
 * @author LiuJiang
 * @date 2018年7月27日 下午2:09:32
 *
 */
public class LTickLoginQueueHandler extends GameInnerHandler {

    @Override
    public void action() {
        ILoginCheckScript script = LogicScriptsUtils.getLoginCheckScript();
        script.tick();
    }
}
