package kafka.team.param.g2g;

/**
 * 
 * @Description 通知结构-单个好友补充信息
 * @author LiuJiang
 * @date 2018年6月21日 下午3:22:26
 *
 */
public class NotifyFriendEx {
    /** 目标玩家id */
    public int tarPlayerId;
    /** 状态 */
    public int status;
    /** 关系创建时间 */
    public long createTime;
    /** 上次赠送友情点时间 */
    public long lastSendTime;
    /** 是否可以领取友情点 */
    public boolean canRecv;

    public NotifyFriendEx(int tarPlayerId, int status, long createTime, long lastSendTime,
            boolean canRecv) {
        this.tarPlayerId = tarPlayerId;
        this.status = status;
        this.createTime = createTime;
        this.lastSendTime = lastSendTime;
        this.canRecv = canRecv;
    }
}
