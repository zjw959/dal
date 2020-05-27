package logic.friend.handler;

import java.util.List;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.friend.FriendManager;
import logic.friend.bean.FriendInfoBean;
import thread.base.GameMessageInnerHandler;

/**
 * 
 * @Description LGetFriendsInfoFinishHandler
 * @author LiuJiang
 * @date 2018年6月30日 下午4:54:41
 *
 */
public class LGetFriendsInfoFinishHandler extends GameMessageInnerHandler {
    List<FriendInfoBean> infos;

    public LGetFriendsInfoFinishHandler(int playerId, int rmsgId, List<FriendInfoBean> infos) {
        super(playerId, rmsgId);
        this.infos = infos;
    }

    @Override
    public void action() throws Exception {
        Player player = PlayerManager.getPlayerByPlayerId(playerId);
        if (player == null || !player.isOnline()) {
            return;// 玩家已离线
        }
        FriendManager manager = player.getFriendManager();
        manager.resGetFriendsInfoMsg(infos);
    }

}
