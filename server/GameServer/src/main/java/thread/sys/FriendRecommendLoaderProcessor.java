package thread.sys;

import org.apache.log4j.Logger;

import logic.constant.ConstDefine;
import logic.friend.FriendService;
import logic.friend.handler.LSystemGetRecommendsHandler;
import thread.BaseHandler;
import thread.sys.base.SysFunctionProcessor;

/**
 * 
 * @Description 好友推荐自动加载线程
 * @author hongfu.wang
 * @date 2018年7月3日 下午9:34:00
 *
 */
public class FriendRecommendLoaderProcessor extends SysFunctionProcessor {
    private static final Logger LOGGER = Logger.getLogger(FriendRecommendLoaderProcessor.class);

    private FriendRecommendLoaderProcessor() {
        super(FriendRecommendLoaderProcessor.class.getSimpleName());
        _initTimer(1000, 1000, true);
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
        services.add(FriendService.getInstance());
    }

    @Override
    protected void checkPerSecond() {
        this.executeInnerHandler(new LSystemGetRecommendsHandler());
    }
}
