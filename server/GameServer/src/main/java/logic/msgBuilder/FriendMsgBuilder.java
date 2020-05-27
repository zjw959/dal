package logic.msgBuilder;

import java.util.ArrayList;
import java.util.List;

import org.game.protobuf.s2c.S2CFriendMsg;
import org.game.protobuf.s2c.S2CFriendMsg.FriendInfo;
import org.game.protobuf.s2c.S2CFriendMsg.RespFriends;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CShareMsg.PlayerSynopsis;

import logic.character.bean.Player;
import logic.dungeon.DungeonManager;
import logic.friend.FriendManager;
import logic.friend.bean.FriendInfoBean;
import logic.friend.bean.FriendView;
import logic.support.MessageUtils;
import utils.DateEx;

/**
 * 
 * @Description 好友信息构建器
 * @author LiuJiang
 * @date 2018年7月11日 上午11:34:15
 *
 */
public class FriendMsgBuilder {
    /**
     * 创建单个好友信息
     */
    public static FriendInfo.Builder createFriendInfo(FriendInfoBean info) {
        S2CFriendMsg.FriendInfo.Builder builder = S2CFriendMsg.FriendInfo.newBuilder();
        builder.setCt(ChangeType.valueOf(info.getCt())).setPid(info.getPid())
                .setName(info.getName()).setLeaderCid(info.getLeaderCid())
                .setFightPower(info.getFightPower()).setLvl(info.getLvl())
                .setLastLoginTime((int) (info.getLastLoginTime() / 1000))
                .setLastHandselTime((int) (info.getLastHandselTime() / 1000))
                .setReceive(info.isReceive()).setStatus(info.getStatus())
                .setOnline(info.isOnline()).setTime((int) (info.getCreateTime() / 1000));
        return builder;
    }

    /**
     * 创建多个好友信息
     */
    public static List<FriendInfo.Builder> createFriendInfo(List<FriendInfoBean> infos) {
        List<FriendInfo.Builder> list = new ArrayList<>(infos.size());
        for (FriendInfoBean friendInfo : infos) {
            list.add(createFriendInfo(friendInfo));
        }
        return list;
    }


    /**
     * 创建单个推荐好友信息
     */
    public static FriendInfo.Builder createRecommendFriendInfo(FriendView player) {
        S2CFriendMsg.FriendInfo.Builder builder = S2CFriendMsg.FriendInfo.newBuilder();
        builder.setCt(ChangeType.DEFAULT).setPid(player.getPlayerId())
                .setName(player.getPlayerName()).setLeaderCid(player.getHelpFightHeroCid())
                .setFightPower(player.getHelpHeroFightPower()).setLvl(player.getLevel())
                .setLastLoginTime((int) (player.getLastLoginTime() / DateEx.TIME_SECOND))
                .setOnline(player.isOnline());
        return builder;
    }

    /**
     * 创建多个推荐好友信息
     */
    public static List<FriendInfo.Builder> createRecommendFriendInfo(List<FriendView> players) {
        List<FriendInfo.Builder> list = new ArrayList<>(players.size());
        for (FriendView p : players) {
            list.add(createRecommendFriendInfo(p));
        }
        return list;
    }

    public static RespFriends.Builder createRespFriendsMsg(Player player,
            List<FriendInfo.Builder> friends) {
        FriendManager manager = player.getFriendManager();
        // 设置助战好友cd
        DungeonManager dm = player.getDungeonManager();
        RespFriends.Builder resp = RespFriends.newBuilder();
        for (FriendInfo.Builder b : friends) {
            b.setHelpCDtime((int) (dm.getHelpFightCD(b.getPid()) / 1000));
            resp.addFriends(b);
        }
        resp.setLastReceiveTime((int) (manager.getLastRecvGiftTime() / 1000));
        resp.setReceiveCount(manager.getRecvGiftCount());
        return resp;
    }

    /**
     * 发送助战玩家信息（路人）
     * 
     * @param player
     * @param players
     */
    public static void sendHelpFightPlayers(Player player, List<FriendView> players) {
        org.game.protobuf.s2c.S2CPlayerMsg.RepsHelpFightPlayers.Builder helpFightPlayersBuilder =
                org.game.protobuf.s2c.S2CPlayerMsg.RepsHelpFightPlayers.newBuilder();
        PlayerSynopsis.Builder builder = null;
        for (FriendView _player : players) {
            builder = PlayerSynopsis.newBuilder();
            builder.setPid(_player.getPlayerId());
            builder.setName(_player.getPlayerName());
            builder.setLvl(_player.getLevel());
            builder.setHelpHeroCid(_player.getHelpFightHeroCid());
            builder.setColdDownTime(0);
            helpFightPlayersBuilder.addPlayers(builder);
        }
        MessageUtils.send(player, helpFightPlayersBuilder);
    }
}
