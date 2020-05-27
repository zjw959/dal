package thread.sys;

import org.apache.log4j.Logger;

import logic.constant.ConstDefine;
import logic.pay.PayService;
import logic.pay.handler.LSystemGetPaysHandler;
import server.GameServer;
import thread.BaseHandler;
import thread.sys.base.SysFunctionProcessor;

/**
 * 
 * @Description 充值支付线程
 * @author LiuJiang
 * @date 2018年7月14日 上午10:34:00
 *
 */
public class PayProcessor extends SysFunctionProcessor {
    private static final Logger LOGGER = Logger.getLogger(PayProcessor.class);
    private PayProcessor() {
        super(PayProcessor.class.getSimpleName());
        if (GameServer.getInstance().isTestServer()) {
            PayService.DELAY = 5 * 1000;// IDE环境下的拉取时间间隔
        }
        _initTimer(PayService.DELAY, PayService.DELAY, true);
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
        services.add(PayService.getInstance());
    }

    @Override
    protected void checkPerSecond() {
        this.executeInnerHandler(new LSystemGetPaysHandler());
    }
}
