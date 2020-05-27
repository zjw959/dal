package logic.pay;

import logic.character.bean.Player;
import logic.pay.handler.LLoginPaysHandler;
import logic.support.LogicScriptsUtils;

import org.game.protobuf.c2s.C2SRechargeMsg.GetOrderNo;

import thread.sys.base.SysService;

/**
 * 
 * @Description 充值服务类
 * @author LiuJiang
 * @date 2018年7月12日 下午6:36:36
 *
 */
public class PayService extends SysService {
    /** 定时获取有效充值订单的时间间隔 */
    public static int DELAY = 5 * 1000;
    /** 上次获取未处理充值订单的截止时间 */
    private long lastEndTime = -1;

    /**
     * 系统自动定时拉取需处理的订单
     * */
    public void systemGetPays() {
        LogicScriptsUtils.getPayScript().systemGetPays();
    }

    /** 玩家登录时拉取需处理的订单 */
    public void loginGetPays(int playerId) {
        this.getProcess().executeInnerHandler(new LLoginPaysHandler(playerId));
    }

    /** 玩家登录时拉取需处理的订单 */
    public void _handleLoginGetPays(int playerId) {
        LogicScriptsUtils.getPayScript().loginGetPays(playerId);
    }

    /** 获取充值订单号 */
    public String getOrderNo(Player player, GetOrderNo msg) {
        return LogicScriptsUtils.getPayScript().getOrderNo(player, msg);
    }

    /** 上次获取未处理充值订单的截止时间 */
    public long getLastEndTime() {
        return lastEndTime;
    }

    /** 上次获取未处理充值订单的截止时间 */
    public void setLastEndTime(long lastEndTime) {
        this.lastEndTime = lastEndTime;
    }


    public static PayService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        PayService instance;

        private Singleton() {
            instance = new PayService();
        }

        PayService getInstance() {
            return instance;
        }
    }

}
