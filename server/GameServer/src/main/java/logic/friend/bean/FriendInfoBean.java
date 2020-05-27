package logic.friend.bean;


/**
 * 
 * @Description 好友信息
 * @author LiuJiang
 * @date 2018年6月12日 上午10:42:48
 *
 */
public class FriendInfoBean {
	private int pid;			// 玩家ID
	private int leaderCid;		// 英雄CID(队长)
	private String name;			// 名字
	private int fightPower;		// 战力
	private int lvl;			// 等级
    private long lastLoginTime; // 最后登录时间
    private long lastHandselTime; // 是否能够赠送
	private boolean receive;			// 是否能够领取
	private int status;			// 状态:1:好友,2:屏蔽,3:申请
	private boolean online;			// 是否在线
    private long createTime; //
	
	private int ct = 0;
	
	private int friendAction; // 好友行为 1：添加好友 2：删除好友

    public FriendInfoBean() {
    }

    public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getLeaderCid() {
		return leaderCid;
	}
	public void setLeaderCid(int leaderCid) {
		this.leaderCid = leaderCid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getFightPower() {
		return fightPower;
	}
	public void setFightPower(int fightPower) {
		this.fightPower = fightPower;
	}
	public int getLvl() {
		return lvl;
	}
	public void setLvl(int lvl) {
		this.lvl = lvl;
	}
	
	public boolean isReceive() {
		return receive;
	}
	public void setReceive(boolean receive) {
		this.receive = receive;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public int getCt() {
		return ct;
	}
	public void setCt(int ct) {
		this.ct = ct;
	}
	public int getFriendAction() {
		return friendAction;
	}
	public void setFriendAction(int friendAction) {
		this.friendAction = friendAction;
	}

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public long getLastHandselTime() {
        return lastHandselTime;
    }

    public void setLastHandselTime(long lastHandselTime) {
        this.lastHandselTime = lastHandselTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

}
