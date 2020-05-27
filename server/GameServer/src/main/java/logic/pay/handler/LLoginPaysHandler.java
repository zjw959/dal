package logic.pay.handler;

import logic.pay.PayService;
import thread.base.GameInnerHandler;

/**
 * 
 * @Description 玩家登录时处理未完成的充值订单(充值线程)
 * @author LiuJiang
 * @date 2018年7月2日 下午6:24:58
 *
 */
public class LLoginPaysHandler extends GameInnerHandler {
    /** 玩家id */
    int playerId;

    public LLoginPaysHandler(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public void action() throws Exception {
        PayService.getInstance()._handleLoginGetPays(playerId);
    }
}
