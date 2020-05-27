package com.enity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.JSONObject;
import com.service.ServerListManager;

@Entity
@Table(name="t_s_server_list")
public class ServerInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4731331638489192703L;
	
	private @Id Integer id;//服务器id
    private int type;// 服务器类型 1-游戏服 2-战斗服
	private String name;//服务器名称
	private Integer mark;//是否是维护状态  1是0否
	private Date openTime;//服务器开启时间
	private Date lastUpdateTime;//服务器修改时间
	private String gameServerExternalIp;//游戏服务器IP
	private Integer gameServerTcpPort;//游戏服务器提供给客户端直连的端口
	private String gameServerInternalIp;//服务器提供给内网访问的地址
	private Integer gameServerHttpPort;//服务器提供给内网访问的端口
	private Integer serverGroup;//服务器分组
    private int splitFlowNum;// 服务器分流阀值
    private int maxOnlineNum;// 服务器最大在线人数
    private int isTest;// 是否是测试服
	//非表字段
	private @Transient int onlineNum;//在线人数
	private @Transient long onlineNumTime;//通知时间 --如果长时间未通知在线人数就当服务器有可能宕机了
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getMark() {
		return mark;
	}
	public void setMark(Integer mark) {
		this.mark = mark;
	}
	public Date getOpenTime() {
		return openTime;
	}
	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getGameServerExternalIp() {
		return gameServerExternalIp;
	}
	public void setGameServerExternalIp(String gameServerExternalIp) {
		this.gameServerExternalIp = gameServerExternalIp;
	}
	public Integer getGameServerTcpPort() {
		return gameServerTcpPort;
	}
	public void setGameServerTcpPort(Integer gameServerTcpPort) {
		this.gameServerTcpPort = gameServerTcpPort;
	}
	public String getGameServerInternalIp() {
		return gameServerInternalIp;
	}
	public void setGameServerInternalIp(String gameServerInternalIp) {
		this.gameServerInternalIp = gameServerInternalIp;
	}
	public Integer getGameServerHttpPort() {
		return gameServerHttpPort;
	}
	public void setGameServerHttpPort(Integer gameServerHttpPort) {
		this.gameServerHttpPort = gameServerHttpPort;
	}
	public Integer getServerGroup() {
		return serverGroup;
	}
	public void setServerGroup(Integer serverGroup) {
		this.serverGroup = serverGroup;
	}
	public int getOnlineNum() {
		return onlineNum;
	}
	public void setOnlineNum(int onlineNum) {
		this.onlineNum = onlineNum;
	}
	public int getMaxOnlineNum() {
		return maxOnlineNum;
	}
	public void setMaxOnlineNum(int maxOnlineNum) {
		this.maxOnlineNum = maxOnlineNum;
	}
	public long getOnlineNumTime() {
		return onlineNumTime;
	}
	public void setOnlineNumTime(long onlineNumTime) {
		this.onlineNumTime = onlineNumTime;
	}

    public int getSplitFlowNum() {
        return splitFlowNum;
    }

    public void setSplitFlowNum(int splitFlowNum) {
        this.splitFlowNum = splitFlowNum;
    }
    public int getIsTest() {
        return isTest;
    }
    public void setIsTest(int isTest) {
        this.isTest = isTest;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    /** 当前在线人数所在档位 */
    public int getOnlineNumDegree() {
        return onlineNum / ServerListManager.distribute_degree;
    }

	@Override
	public String toString() {
        return "ServerInfo [id=" + id + ",type=" + type + ", name=" + name + ", mark=" + mark
                + ", openTime=" + openTime + ", lastUpdateTime=" + lastUpdateTime
                + ", gameServerExternalIp=" + gameServerExternalIp + ", gameServerTcpPort="
                + gameServerTcpPort + ", gameServerInternalIp=" + gameServerInternalIp
                + ", gameServerHttpPort=" + gameServerHttpPort + ", serverGroup=" + serverGroup
                + ", splitFlowNum=" + splitFlowNum + ", maxOnlineNum=" + maxOnlineNum
                + ", onlineNum=" + onlineNum + ", onlineNumTime=" + onlineNumTime + ",isTest="
                + isTest + "]";
    }
	
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json = (JSONObject) JSONObject.toJSON(this);
        return json;
    }

	public String getHttpUrl() {
		String url = "http://"+this.gameServerInternalIp+":"+this.gameServerHttpPort;
		return url;
	}
}
