package kafka.team.param.g2g;

import java.util.List;

/**
 * 
 * @Description 通知结构
 * @author LiuJiang
 * @date 2018年6月21日 下午3:19:25
 *
 */
public class NotifyObj {
    /**
     * 操作类型
     */
    public int ct;
    /**
     * action类型
     */
    public int friendAction;
    /**
     * 操作玩家id
     * */
    public int playerId;
    /**
     * 目标玩家id
     * */
    public List<NotifyFriendEx> targetPlayers;
    /**
     * 操作时间
     */
    public long time;

    public NotifyObj(int ct, int friendAction, int playerId, List<NotifyFriendEx> targetPlayers,
            long time) {
        this.ct = ct;
        this.friendAction = friendAction;
        this.playerId = playerId;
        this.targetPlayers = targetPlayers;
        this.time = time;
    }
}
