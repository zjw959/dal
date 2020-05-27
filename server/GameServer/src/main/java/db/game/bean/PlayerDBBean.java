package db.game.bean;

import java.io.Serializable;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class PlayerDBBean implements Serializable, Cloneable {
    private static final long serialVersionUID = 8408403884630780690L;

    private int playerid;
    // 角色名
    private String playername;
    // 账号名
    private String username;
    // 渠道id
    private String channelid;
    // 渠道Appid
    private String channelappid;
    // 当前登录服务器ID
    private int currentserver;
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
    // 系统钻石
    private long systemdiamond;
    // 充值钻石
    private long rechargediamond;
    // 体力
    private int strength;


    // 战斗力
    // TODO 暂时保留,确定不入库则为仅redis适用
    private int fightpower;

    /**
     * 用户功能数据
     */
    private String data;

    /**
     * 用户是否在线,注意该字段不入库,只入redis
     */
    private boolean isOnline;

    /**
     * 用户描述,注意该字段不入库,只入redis
     */
    private String describe;

    /**
     * 用户头像heroid,注意该字段不入库,只入redis
     */
    private int heroId;

    /**
     * 英雄皮肤id,注意该字段不入库,只入redis
     */
    private int skinCid;

    public PlayerDBBean(){
    }
    
    public int getPlayerId() {
        return playerid;
    }

    public void setPlayerId(int playerid) {
        this.playerid = playerid;
    }

    public String getPlayername() {
        return playername;
    }

    public void setPlayername(String playername) {
        this.playername = playername;
    }

    public String getUsername() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    public int getCurrentServer() {
        return currentserver;
    }

    public void setCurrentServer(int currentserver) {
        this.currentserver = currentserver;
    }

    public long getCreatetime() {
        return createtime;
    }

    public void setCreateTime(long createtime) {
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

    public long getSystemDiamond() {
        return systemdiamond;
    }

    public void setSystemDiamond(long systemDiamond) {
        this.systemdiamond = systemDiamond;
    }

    public long getRechargeDiamond() {
        return rechargediamond;
    }

    public void setRechargeDiamond(long rechargeDiamond) {
        this.rechargediamond = rechargeDiamond;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getFightpower() {
        return fightpower;
    }

    public void setFightpower(int fightpower) {
        this.fightpower = fightpower;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public String getChannelId() {
        return channelid;
    }

    public void setChannelId(String channelid) {
        this.channelid = channelid;
    }

    public String getChannelAppId() {
        return channelappid;
    }

    public void setChannelAppId(String channelappid) {
        this.channelappid = channelappid;
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
	
	public boolean getIsOnline() {
        return this.isOnline;
    }

    public int getHeroId() {
        return this.heroId;
    }

    public void setHeroId(int helpFightHeroCid) {
        this.heroId = helpFightHeroCid;
    }

    public int getSkinCid() {
        return skinCid;
    }

    public void setSkinCid(int skinCid) {
        this.skinCid = skinCid;
    }

    public String getDescribe() {
        return this.describe;
    }

    public void setDescribe(String remark) {
        this.describe = remark;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
