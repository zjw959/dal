package logic.pay.handler;

import logic.pay.PayService;
import thread.base.GameInnerHandler;


/**
 * 
 * @Description 系统获取有效充值订单(定时从pay数据库拉取)
 * @author LiuJiang
 * @date 2018年7月14日 下午3:24:58
 *
 */
public class LSystemGetPaysHandler extends GameInnerHandler {

    @Override
    public void action() throws Exception {
        PayService.getInstance().systemGetPays();
    }
}
