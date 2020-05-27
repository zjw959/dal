package logic.character.handler;

import java.util.List;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.friend.FriendManager;
import logic.friend.bean.FriendView;
import logic.msgBuilder.FriendMsgBuilder;
import thread.base.GameMessageInnerHandler;

/**
 * 获取助战玩家
 */
public class LGetHelpFightPlayersHandler extends GameMessageInnerHandler {

    public LGetHelpFightPlayersHandler(int playerId, int rmsgId) {
        super(playerId, rmsgId);
    }

    @Override
    public void action() throws Exception {
        Player player = PlayerManager.getPlayerByPlayerId(playerId);
        if (player == null || !player.isOnline()) {
            return;// 玩家已离线
        }
        FriendManager fm = player.getFriendManager();
        // fm.getRecommendFriends(false);// 重新获取推荐好友
        List<FriendView> players = fm.getRecommends();
        if (players == null) {
            fm.getRecommendFriends(false, false);
            players = fm.getRecommends();
        }
        FriendMsgBuilder.sendHelpFightPlayers(player, players);
    }

}
