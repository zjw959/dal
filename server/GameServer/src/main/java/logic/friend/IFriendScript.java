package logic.friend;

import java.util.List;

import org.game.protobuf.c2s.C2SFriendMsg.ReqOperate;

import logic.character.bean.Player;
import logic.friend.bean.FriendInfoBean;
import script.IScript;

public abstract class IFriendScript implements IScript {
    public abstract void notifyFriendsSelfUpdate(int playerId);

    public abstract void getRecommendFriends(Player player, boolean isCheckCD, boolean isSend)
            throws Exception;

    public abstract void process(String json);

    public abstract boolean isFriend(int playerId,int pid);

    public abstract void resGetFriendsInfoMsg(Player player, List<FriendInfoBean> infos);

    public abstract void operate(Player player, ReqOperate msg) throws Exception;

    public abstract void reloadPlayerLvlWhenExpired() throws Exception ;
    
    public abstract void loadRecommendsFromDb(int level) throws Exception ;

    public abstract void getFriendsInfo(Player player, int rmsgId) ;

    public abstract void clearExpireFriendApply(int playerId);
}
