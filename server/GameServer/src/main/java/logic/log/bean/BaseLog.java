package logic.log.bean;

import java.util.Calendar;

import org.apache.commons.lang.BooleanUtils;

import com.ServerListManager;
import com.google.gson.JsonElement;

import io.netty.channel.ChannelHandlerContext;
import logic.character.bean.Player;
import logic.login.struct.ChannelInfo;
import net.AttributeKeys;
import utils.TimeUtil;

public class BaseLog {

    private String eventType; // 事件类型，固定为 role
    private String eventName; // 事件名称，固定为空值
    private String eventTime;// 产生时间，格式：'2018-01-01 00:00:00'
    private String htuid = "";// 黑桃uid
    private String pfid = "";// 平台id
    private String platformid = "-1";
    private int sid;// 区服id
    private int roleId;// 角色id
    private String roleName;// 角色名
    private int roleLevel;// 角色等级
    private long payAmount;// 充值金额
    private long createTime;// 创角时间
    private long lastLoginTime;// 最后登录时间
    private int isOnline;// 是否在线
    private String osType = "";// 操作系统类型，1安卓 2iOS
    private String imei = "";// 安卓 IMEI
    private String androidId = "";// 安卓 AndroidID
    private String idfa = "";// iOS IDFA
    private String idfv = "";// iOS IDFV
    private String ip; // 客户端IP
    private JsonElement property;

    protected BaseLog(Player player, String eventType, String eventName) {
        this.eventType = eventType;
        this.eventName = eventName;
        this.eventTime = TimeUtil.getDateString(Calendar.getInstance().getTime());
        this.sid = ServerListManager.getMyServerInfo().getServerGroup();
        this.roleId = player.getPlayerId();
        this.roleName = player.getPlayerName();
        this.roleLevel = player.getLevel();
        this.payAmount = player.getPayManager().getTotalPay();
        this.createTime = player.getCreateTime();
        this.lastLoginTime = player.getLastLoginTime();
        this.isOnline = BooleanUtils.toInteger(player.isOnline());
        this.ip = player.getIP();

        ChannelHandlerContext _ctx = player.getCtx();
        if (_ctx != null) {
            ChannelInfo _channelInfo = _ctx.channel().attr(AttributeKeys.CHANNEL_INFO).get();
            if (_channelInfo != null) {
                String htuid = _channelInfo.getAccountId();
                if (htuid != null) {
                    this.htuid = htuid;
                }
                String pfid = _channelInfo.getChannelId();
                if (pfid != null) {
                    this.pfid = pfid;
                }
                String _platformid = _channelInfo.getChannelAppId();
                if (_platformid != null && !_platformid.equals("")) {
                    this.platformid = _platformid;
                } else {
                    this.platformid = "-1";
                }
                String osType = _channelInfo.getDeviceType();
                if (osType != null) {
                    this.osType = osType;
                }
                String imei = _channelInfo.getImei();
                if (imei != null) {
                    this.imei = imei;
                }
                String androidId = _channelInfo.getAndroidid();
                if (androidId != null) {
                    this.androidId = androidId;
                }
                String idfa = _channelInfo.getIdfa();
                if (idfa != null) {
                    this.idfa = idfa;
                }
            }
        }
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getHtuid() {
        return htuid;
    }

    public void setHtuid(String htuid) {
        this.htuid = htuid;
    }

    public String getPfid() {
        return pfid;
    }

    public void setPfid(String pfid) {
        this.pfid = pfid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getRoleName() {
        return roleName;
    }



    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }



    public int getRoleLevel() {
        return roleLevel;
    }



    public void setRoleLevel(int roleLevel) {
        this.roleLevel = roleLevel;
    }



    public void setPayAmount(int payAmount) {
        this.payAmount = payAmount;
    }



    public int getIsOnline() {
        return isOnline;
    }



    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }



    public String getOsType() {
        return osType;
    }



    public void setOsType(String osType) {
        this.osType = osType;
    }



    public String getImei() {
        return imei;
    }



    public void setImei(String imei) {
        this.imei = imei;
    }



    public String getAndroidId() {
        return androidId;
    }



    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }



    public String getIdfa() {
        return idfa;
    }



    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }



    public String getIdfv() {
        return idfv;
    }



    public void setIdfv(String idfv) {
        this.idfv = idfv;
    }



    public String getIp() {
        return ip;
    }



    public void setIp(String ip) {
        this.ip = ip;
    }



    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public long getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(long payAmount) {
        this.payAmount = payAmount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }


    public void setProperty(JsonElement jsonElement) {
        this.property = jsonElement;
    }

    public String getPlatformid() {
        return platformid;
    }

    public void setPlatformid(String platformid) {
        this.platformid = platformid;
    }

}
