package logic.friend.handler;

import logic.support.LogicScriptsUtils;
import thread.base.GameInnerHandler;

/**
 * 
 * @Description 清除过期好友申请数据（异步）
 * @author hongfu.wang
 * @date 2018-08-16
 *
 */
public class LClearExpireFriendApplyHandler extends GameInnerHandler {

    private int playerId;

    public LClearExpireFriendApplyHandler(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getIFriendScript().clearExpireFriendApply(playerId);
    }
}
