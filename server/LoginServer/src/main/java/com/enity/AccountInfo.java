package com.enity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_u_account")
public class AccountInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 652886997609405425L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;//自增id
	private Integer playerId;//加密之后的玩家id 用户中心给用户注册的唯一标示   @see : PlayerIdAdaptor
	private String accountId;//用户的渠道id
	private String channelId;//用户的渠道
    private String channelAppId;// 渠道APPID
    private String mobile;// 手机号
	private int serverId;//上一次登陆的服务器
	private Date createTime;//创号时间
	private @Transient String token;//登陆令牌
	private @Transient String deviceName;//设备名称
	private @Transient String deviceId;//设备唯一ID
	private @Transient String sdk;//SDK类型
	private @Transient String sdkVersion;//SDK版本
	private @Transient String osName;//系统名称
	private @Transient String osVersion;//系统版本
	private @Transient String clientVersion;//客户端版本号
	private @Transient String ip;//ip地址
    private @Transient boolean isAdminLogin;// 是否是托管登录
    private @Transient boolean isWhiteUser;// 是否是白名单账号
    private @Transient String networkType;//
    private @Transient String networkCarrier;//
    private @Transient String screenWidth;//
    private @Transient String screenHeight;//
    private @Transient String appVersion;// 客户端版本号
    private @Transient String devicebrand;// 设备品牌
    private @Transient String idfa;//
    private @Transient String imei;//
    private @Transient String androidid;//


	public AccountInfo() {
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getPlayerId() {
		return playerId;
	}
	public void setPlayerId(Integer playerId) {
		this.playerId = playerId;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getChannelAppId() {
		return channelAppId;
	}
	public void setChannelAppId(String channelAppId) {
		this.channelAppId = channelAppId;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getSdk() {
		return sdk;
	}
	public void setSdk(String sdk) {
		this.sdk = sdk;
	}
	public String getSdkVersion() {
		return sdkVersion;
	}
	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}
	public String getOsName() {
		return osName;
	}
	public void setOsName(String osName) {
		this.osName = osName;
	}
	public String getOsVersion() {
		return osVersion;
	}
	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}
	public String getClientVersion() {
		return clientVersion;
	}
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}

    public boolean isAdminLogin() {
        return isAdminLogin;
    }

    public void setAdminLogin(boolean isAdminLogin) {
        this.isAdminLogin = isAdminLogin;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public String getNetworkCarrier() {
        return networkCarrier;
    }

    public void setNetworkCarrier(String networkCarrier) {
        this.networkCarrier = networkCarrier;
    }

    public String getScreenWidth() {
        return screenWidth;
    }

    public void setScreenWidth(String screenWidth) {
        this.screenWidth = screenWidth;
    }

    public String getScreenHeight() {
        return screenHeight;
    }

    public void setScreenHeight(String screenHeight) {
        this.screenHeight = screenHeight;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public boolean isWhiteUser() {
        return isWhiteUser;
    }

    public void setWhiteUser(boolean isWhiteUser) {
        this.isWhiteUser = isWhiteUser;
    }

    public String getDevicebrand() {
        return devicebrand;
    }

    public void setDevicebrand(String devicebrand) {
        this.devicebrand = devicebrand;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getAndroidid() {
        return androidid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public void setAndroidid(String androidid) {
        this.androidid = androidid;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    @Override
	public String toString() {
		return "AccountInfo [id=" + id + ", playerId=" + playerId + ", accountId=" + accountId + ", channelId="
				+ channelId + ", serverId=" + serverId + ", createTime=" + createTime + ", token=" + token
				+ ", channelAppId=" + channelAppId + ", deviceName=" + deviceName + ", deviceId=" + deviceId + ", sdk="
				+ sdk + ", sdkVersion=" + sdkVersion + ", osName=" + osName + ", osVersion=" + osVersion
                + ", clientVersion=" + clientVersion + ", ip=" + ip + ", isAdminLogin="
                + isAdminLogin + ", networkType=" + networkType + ", networkCarrier="
                + networkCarrier + ", screenWidth=" + screenWidth + ", screenHeight="
                + screenHeight + ", appVersion=" + appVersion + ", isWhiteUser=" + isWhiteUser
                + ", devicebrand=" + devicebrand + ", idfa=" + idfa + ", imei=" + imei
                + ", androidid=" + androidid + ", mobile=" + mobile
                + "]";
	}
}
