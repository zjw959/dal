package logic.friend.handler;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import thread.base.GameMessageInnerHandler;

/**
 * 获取推荐好友
 */
public class LGetRecommendHandler extends GameMessageInnerHandler {
    private boolean isCheckCD;

    public LGetRecommendHandler(int playerId, int rmsgId, boolean isCheckCD) {
        super(playerId, rmsgId);
        this.isCheckCD = isCheckCD;
    }

    @Override
    public void action() throws Exception {
        Player player = PlayerManager.getPlayerByPlayerId(playerId);
        if (player == null || !player.isOnline()) {
            return;// 玩家已离线
        }
        player.getFriendManager().getRecommendFriends(isCheckCD, true);
    }

}
