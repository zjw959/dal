package logic.friend.handler;

import logic.friend.FriendService;
import thread.base.GameInnerHandler;


/**
 * 
 * @Description 系统获取好友推荐列表线程
 * @author hongfu.wang
 * @date 2018-08-09
 *
 */
public class LSystemGetRecommendsHandler extends GameInnerHandler {

    @Override
    public void action() throws Exception {
        FriendService.getInstance().reloadPlayerLvlWhenExpired();
    }
}
