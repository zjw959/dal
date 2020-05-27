package logic.login.struct;

import db.game.bean.PlayerDBBean;

/**
 * 客户端连接信息,保存在Channel上
 */
public class ChannelInfo {
    /** 渠道Id **/
    private String channelId;
    /** 渠道AppId **/
    private String channelAppId;
    /** 设备类型 **/
    private String deviceType;
    /** 渠道用户名 **/
    private String accountId;
    /** 客户端的IP地址 **/
    private String ipAddress;
    /** roleId */
    private long roleId;
    /** sdk **/
    private String sdk;
    /** sdk版本 **/
    private String sdkVersion;
    /** 客户端版本号 **/
    private String clientVersion;

    /** 手机系统名 **/
    private String osName;
    /** 手机系统版本 **/
    private String osVersion;

    /** 设备token **/
    private String deviceToken;
    /** 设备名称 **/
    private String deviceName;
    /** 设备唯一ID **/
    private String deviceId;
    /** 手机号 **/
    private String mobile;
    /** 认证token **/
    private String token;

    /** 玩家id */
    private int playerId;
    /** 状态,禁言:true */
    private boolean keepSilent;
    /** 防沉迷状态 **/
    private int antiAddiction;
    /** 是否是托管登录 */
    private boolean isAdminLogin;
    /** 是否是白名单用户 */
    private boolean isWhiteUser;

    private String networkType;
    private String networkCarrier;
    private String screenWidth;
    private String screenHeight;
    private String appVersion;

    private String devicebrand;// 设备品牌
    private String idfa;//
    private String imei;//
    private String androidid;//


    private PlayerDBBean roleBean;

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelAppId() {
        return channelAppId;
    }

    public void setChannelAppId(String channelAppId) {
        this.channelAppId = channelAppId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
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

    public void setClientVersion(String version) {
        clientVersion = version;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public PlayerDBBean getRoleBean() {
        return roleBean;
    }

    public void setRoleBean(PlayerDBBean roleBean) {
        this.roleBean = roleBean;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public boolean isKeepSilent() {
        return keepSilent;
    }

    public void setKeepSilent(boolean keepSilent) {
        this.keepSilent = keepSilent;
    }

    public int getAntiAddiction() {
        return antiAddiction;
    }

    public void setAntiAddiction(int antiAddiction) {
        this.antiAddiction = antiAddiction;
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

    public void setAndroidid(String androidid) {
        this.androidid = androidid;
    }

    public boolean isWhiteUser() {
        return isWhiteUser;
    }

    public void setWhiteUser(boolean isWhiteUser) {
        this.isWhiteUser = isWhiteUser;
    }

    public String getFullUserName() {
        return (getChannelId() + "_" + getAccountId()).toLowerCase();
    }
}
