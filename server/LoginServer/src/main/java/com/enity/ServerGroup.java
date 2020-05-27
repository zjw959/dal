package com.enity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_s_server_group")
public class ServerGroup implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5079668097551656038L;
	@Id
	private Integer id;
	private String name;
	private String info;
	private String mailDatabasesIp;
	private String mailDatabasesName;
	private String mailDatabasesPort;
	private String mailDatabasesUser;
	private String mailDatabasesPassword;
    private String payDatabasesIp;
    private String payDatabasesName;
    private String payDatabasesPort;
    private String payDatabasesUser;
    private String payDatabasesPassword;
	private String globalDatabasesIp;
	private String globalDatabasesName;
	private String globalDatabasesPort;
	private String globalDatabasesUser;
    private String globalDatabasesPassword;
    private String gameDatabasesIp;
	private String gameDatabasesName;
	private String gameDatabasesPort;
	private String gameDatabasesUser;
    private String gameDatabasesPassword;
	private int mark;//是否是维护状态  1是0否
    private String csvUrl;// csv配置文件拉取地址
    private String giftCodeVerifyUrl;// 礼包码兑换验证地址
    private String iosCheckVersion;// IOS审核版本号
    private String androidCheckVersion;// 安卓审核版本号
    private int iosCheckGroup;// IOS审核服分组id
    private int androidCheckGroup;// 安卓审核服分组id


	public String getGameDatabasesIp() {
		return gameDatabasesIp;
	}
	public void setGameDatabasesIp(String gameDatabasesIp) {
		this.gameDatabasesIp = gameDatabasesIp;
	}
	public String getGameDatabasesName() {
		return gameDatabasesName;
	}
	public void setGameDatabasesName(String gameDatabasesName) {
		this.gameDatabasesName = gameDatabasesName;
	}
	public String getGameDatabasesPort() {
		return gameDatabasesPort;
	}
	public void setGameDatabasesPort(String gameDatabasesPort) {
		this.gameDatabasesPort = gameDatabasesPort;
	}
	public String getGameDatabasesUser() {
		return gameDatabasesUser;
	}
	public void setGameDatabasesUser(String gameDatabasesUser) {
		this.gameDatabasesUser = gameDatabasesUser;
	}
	public String getGameDatabasesPassword() {
		return gameDatabasesPassword;
	}
	public void setGameDatabasesPassword(String gameDatabasesPassword) {
		this.gameDatabasesPassword = gameDatabasesPassword;
	}
	private String maintenanceNotice;//维护公告
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
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getMailDatabasesIp() {
		return mailDatabasesIp;
	}
	public void setMailDatabasesIp(String mailDatabasesIp) {
		this.mailDatabasesIp = mailDatabasesIp;
	}
	public String getMailDatabasesName() {
		return mailDatabasesName;
	}
	public void setMailDatabasesName(String mailDatabasesName) {
		this.mailDatabasesName = mailDatabasesName;
	}
	public String getMailDatabasesUser() {
		return mailDatabasesUser;
	}
	public void setMailDatabasesUser(String mailDatabasesUser) {
		this.mailDatabasesUser = mailDatabasesUser;
	}
	public String getMailDatabasesPassword() {
		return mailDatabasesPassword;
	}
	public void setMailDatabasesPassword(String mailDatabasesPassword) {
		this.mailDatabasesPassword = mailDatabasesPassword;
	}

    public String getPayDatabasesIp() {
        return payDatabasesIp;
    }

    public void setPayDatabasesIp(String payDatabasesIp) {
        this.payDatabasesIp = payDatabasesIp;
    }

    public String getPayDatabasesName() {
        return payDatabasesName;
    }

    public void setPayDatabasesName(String payDatabasesName) {
        this.payDatabasesName = payDatabasesName;
    }

    public String getPayDatabasesUser() {
        return payDatabasesUser;
    }

    public void setPayDatabasesUser(String payDatabasesUser) {
        this.payDatabasesUser = payDatabasesUser;
    }

    public String getPayDatabasesPassword() {
        return payDatabasesPassword;
    }

    public void setPayDatabasesPassword(String payDatabasesPassword) {
        this.payDatabasesPassword = payDatabasesPassword;
    }
    public String getGlobalDatabasesIp() {
		return globalDatabasesIp;
	}

    public void setGlobalDatabasesIp(String globalDatabasesIp) {
        this.globalDatabasesIp = globalDatabasesIp;
	}

    public String getGlobalDatabasesName() {
		return globalDatabasesName;
	}

    public void setGlobalDatabasesName(String globalDatabasesName) {
        this.globalDatabasesName = globalDatabasesName;
	}

    public String getGlobalDatabasesUser() {
		return globalDatabasesUser;
	}

    public void setGlobalDatabasesUser(String globalDatabasesUser) {
        this.globalDatabasesUser = globalDatabasesUser;
	}

    public String getGlobalDatabasesPassword() {
        return globalDatabasesPassword;
	}

    public void setGlobalDatabasesPassword(String globalDatabasesPassword) {
        this.globalDatabasesPassword = globalDatabasesPassword;
	}
	public String getMaintenanceNotice() {
		return maintenanceNotice;
	}
	public void setMaintenanceNotice(String maintenanceNotice) {
		this.maintenanceNotice = maintenanceNotice;
	}
	public int getMark() {
		return mark;
	}
	public void setMark(int mark) {
		this.mark = mark;
	}
	public String getMailDatabasesPort() {
		return mailDatabasesPort;
	}
	public void setMailDatabasesPort(String mailDatabasesPort) {
		this.mailDatabasesPort = mailDatabasesPort;
	}
	public String getPayDatabasesPort() {
		return payDatabasesPort;
	}
	public void setPayDatabasesPort(String payDatabasesPort) {
		this.payDatabasesPort = payDatabasesPort;
	}
	public String getGlobalDatabasesPort() {
		return globalDatabasesPort;
	}
	public void setGlobalDatabasesPort(String globalDatabasesPort) {
		this.globalDatabasesPort = globalDatabasesPort;
	}

    public String getCsvUrl() {
        return csvUrl;
    }

    public void setCsvUrl(String csvUrl) {
        this.csvUrl = csvUrl;
    }

    public String getGiftCodeVerifyUrl() {
        return giftCodeVerifyUrl;
    }

    public void setGiftCodeVerifyUrl(String giftCodeVerifyUrl) {
        this.giftCodeVerifyUrl = giftCodeVerifyUrl;
    }

    public String getIosCheckVersion() {
        return iosCheckVersion;
    }

    public void setIosCheckVersion(String iosCheckVersion) {
        this.iosCheckVersion = iosCheckVersion;
    }

    public String getAndroidCheckVersion() {
        return androidCheckVersion;
    }

    public void setAndroidCheckVersion(String androidCheckVersion) {
        this.androidCheckVersion = androidCheckVersion;
    }

    public int getIosCheckGroup() {
        return iosCheckGroup;
    }

    public void setIosCheckGroup(int iosCheckGroup) {
        this.iosCheckGroup = iosCheckGroup;
    }

    public int getAndroidCheckGroup() {
        return androidCheckGroup;
    }

    public void setAndroidCheckGroup(int androidCheckGroup) {
        this.androidCheckGroup = androidCheckGroup;
    }
    @Override
	public String toString() {
		return "ServerGroup [id=" + id + ", name=" + name + ", info=" + info + ", mailDatabasesIp=" + mailDatabasesIp
				+ ", mailDatabasesName=" + mailDatabasesName + ", mailDatabasesPort=" + mailDatabasesPort
				+ ", mailDatabasesUser=" + mailDatabasesUser + ", mailDatabasesPassword=" + mailDatabasesPassword
				+ ", payDatabasesIp=" + payDatabasesIp + ", payDatabasesName=" + payDatabasesName
				+ ", payDatabasesPort=" + payDatabasesPort + ", payDatabasesUser=" + payDatabasesUser
				+ ", payDatabasesPassword=" + payDatabasesPassword + ", globalDatabasesIp=" + globalDatabasesIp
				+ ", globalDatabasesName=" + globalDatabasesName + ", globalDatabasesPort=" + globalDatabasesPort
				+ ", globalDatabasesUser=" + globalDatabasesUser + ", globalDatabasesPassword="
				+ globalDatabasesPassword + ", gameDatabasesIp=" + gameDatabasesIp + ", gameDatabasesName="
				+ gameDatabasesName + ", gameDatabasesPort=" + gameDatabasesPort + ", gameDatabasesUser="
				+ gameDatabasesUser + ", gameDatabasesPassword=" + gameDatabasesPassword + ", mark=" + mark
                + ", csvUrl=" + csvUrl + ", giftCodeVerifyUrl=" + giftCodeVerifyUrl
                + ", iosCheckVersion=" + iosCheckVersion + ", iosCheckGroup=" + iosCheckGroup
                + ", androidCheckVersion=" + androidCheckVersion + ", androidCheckGroup="
                + androidCheckGroup
				+ ", maintenanceNotice=" + maintenanceNotice + "]";
	}
	
}
