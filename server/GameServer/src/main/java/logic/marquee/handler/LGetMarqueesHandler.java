package logic.marquee.handler;

import logic.marquee.MarqueeService;
import thread.base.GameInnerHandler;


/**
 * 
 * @Description 系统获取跑马灯(定时从数据库拉取)
 * @author LiuJiang
 * @date 2018年7月31日 上午11:00:40
 *
 */
public class LGetMarqueesHandler extends GameInnerHandler {

    @Override
    public void action() throws Exception {
        MarqueeService.getInstance().tickGetMarquee();
    }
}
