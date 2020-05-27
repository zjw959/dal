package thread.sys;

import org.apache.log4j.Logger;

import logic.constant.ConstDefine;
import logic.mail.MailService;
import logic.mail.handler.LSystemGetMailsHandler;
import server.GameServer;
import thread.BaseHandler;
import thread.sys.base.SysFunctionProcessor;

/**
 * 
 * @Description 邮件线程
 * @author LiuJiang
 * @date 2018年7月3日 下午9:34:00
 *
 */
public class MailProcessor extends SysFunctionProcessor {
    private static final Logger LOGGER = Logger.getLogger(MailProcessor.class);

    private MailProcessor() {
        super(MailProcessor.class.getSimpleName());
        if (GameServer.getInstance().isTestServer()) {
            MailService.DELAY = 5 * 1000;// IDE环境下的拉取时间间隔
        }
        _initTimer(MailService.DELAY, MailService.DELAY, true);
    }

    @Override
    public void doPrintAction(BaseHandler handler, long doTime) {
        if (doTime > 400L) {
            LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " threadName:" + this.getName() + ",handler:"
                    + handler.getClass().getName() + ",doTime:" + doTime);
        }
    }

    @Override
    protected void registerService() {
        services.add(MailService.getInstance());
    }

    @Override
    protected void checkPerSecond() {
        this.executeInnerHandler(new LSystemGetMailsHandler());
    }
}
