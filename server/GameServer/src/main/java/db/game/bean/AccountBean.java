/**
 * Auto generated, Don't be modify it!
 */
package db.game.bean;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class AccountBean {
	private long roleid;
	// 角色名
	private String rolename;
	// 账号名
	private String username;
	// 平台
	private String platform;
	// 当前登录服务器ID
	private int currentserver;
	// 创建角色服务器ID
	private int createserver;
	// 创建角色时间
	private long createtime;
	// 账号被封
	private int isforbid;
	// ip
	private String ip;
	// 角色等级
	private int level;
	// VIP等级
	private int viplevel;
	// 经验值
	private long exp;
	// GM等级
	private int gmlevel;
	// 登录时间
	private long logintime;
	// 上次离线时间
	private long offlinetime;
	// 总在线时长
	private long onlinetime;
	// 上次登录时长
	private long lastlogintime;
	// 金币
	private long gold;
	// 钻石
	private long rmb;
	// 战斗力
	private long fightpower;

	/**
	 * 用户功能数据
	 */
	private String data;

	public long getRoleId() {
		return roleid;
	}

	public void setRoleid(long roleid) {
		this.roleid = roleid;
	}

	public String getRolename() {
		return rolename;
	}

	public void setRolename(String rolename) {
		this.rolename = rolename;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getCurrentServer() {
		return currentserver;
	}

	public void setCurrentserver(int currentserver) {
		this.currentserver = currentserver;
	}

	public int getCreateServer() {
		return createserver;
	}

	public void setCreateserver(int createserver) {
		this.createserver = createserver;
	}

	public long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getViplevel() {
		return viplevel;
	}

	public void setViplevel(int viplevel) {
		this.viplevel = viplevel;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public int getGmlevel() {
		return gmlevel;
	}

	public void setGmlevel(int gmlevel) {
		this.gmlevel = gmlevel;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public long getLogintime() {
		return logintime;
	}

	public void setLogintime(long logintime) {
		this.logintime = logintime;
	}

	public long getOfflinetime() {
		return offlinetime;
	}

	public void setOfflinetime(long offlinetime) {
		this.offlinetime = offlinetime;
	}

	public long getOnlinetime() {
		return onlinetime;
	}

	public void setOnlinetime(long onlinetime) {
		this.onlinetime = onlinetime;
	}

	public long getLastlogintime() {
		return lastlogintime;
	}

	public void setLastlogintime(long lastlogintime) {
		this.lastlogintime = lastlogintime;
	}

	public long getGold() {
		return gold;
	}

	public void setGold(long gold) {
		this.gold = gold;
	}

	public long getDiamond() {
		return rmb;
	}

	public void setDiamond(long diamond) {
		this.rmb = diamond;
	}

	public long getFightpower() {
		return fightpower;
	}

	public void setFightpower(long fightpower) {
		this.fightpower = fightpower;
	}

	public String toString() {
		return ReflectionToStringBuilder.toString(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public int getIsForbid() {
		return isforbid;
	}

	public void setIsForbid(int isforbid) {
		this.isforbid = isforbid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUserName() {
		return this.username;
	}
}
