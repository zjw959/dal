package logic.marquee.handler;

import logic.marquee.MarqueeService;
import thread.base.GameInnerHandler;


/**
 * 
 * @Description 跑马灯广播handler
 * @author LiuJiang
 * @date 2018年7月31日 上午11:00:40
 *
 */
public class LNoticeMarqueesHandler extends GameInnerHandler {

    @Override
    public void action() throws Exception {
        MarqueeService.getInstance().tickNoticeMarquee();
    }
}
