package logic.friend.handler;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import thread.base.GameMessageInnerHandler;

/**
 * 
 * @Description 获取好友信息（异步）
 * @author LiuJiang
 * @date 2018年6月30日 下午4:54:29
 *
 */
public class LGetFriendsInfoHandler extends GameMessageInnerHandler {
    // private static final Logger LOGGER = Logger.getLogger(LGetFriendsInfoHandler.class);

    public LGetFriendsInfoHandler(int playerId, int rmsgId) {
        super(playerId, rmsgId);
    }

    @Override
    public void action() throws Exception {
        Player player = PlayerManager.getPlayerByPlayerId(playerId);
        if (player == null || !player.isOnline()) {
            return;// 玩家已离线
        }
        player.getFriendManager().getFriendsInfo(rmsgId);
    }

}
