package logic.log.bean;

import io.netty.channel.Channel;
import logic.character.bean.Player;
import logic.login.struct.ChannelInfo;
import net.AttributeKeys;

public class LoginLog extends PlayerBaseLog {

    private String osVersion = "";// 操作系统版本
    private String deviceBrand = "";// 设备品牌
    private String deviceModel = "";// 设备型号
    private String screenWidth = "";// 屏幕宽度
    private String screenHeight = "";// 屏幕高度
    private String appVersion = "";// app版本号
    private String srcVersion = "";// 资源版本号
    private String networkType = "";// 网络类型
    private String networkCarrier = "";// 网络运营商
    private String appIdentifier = "";// app包名

    public LoginLog(Player player,String eventName) {
        super(player,eventName);
        Channel channel = player.getCtx().channel();
        ChannelInfo _channelInfo = channel.attr(AttributeKeys.CHANNEL_INFO).get();
        if (_channelInfo != null) {
            this.osVersion = _channelInfo.getOsVersion();
            this.deviceModel = _channelInfo.getDeviceName();
            this.deviceBrand = _channelInfo.getDevicebrand(); 
            this.appVersion = _channelInfo.getAppVersion();
            this.appIdentifier = _channelInfo.getChannelAppId(); 
            this.networkType = _channelInfo.getNetworkType();
            this.networkCarrier = _channelInfo.getNetworkCarrier();
            this.screenWidth = _channelInfo.getScreenWidth();
            this.screenHeight = _channelInfo.getScreenHeight();
            this.srcVersion = _channelInfo.getClientVersion(); 
        }
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
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

    public String getSrcVersion() {
        return srcVersion;
    }

    public void setSrcVersion(String srcVersion) {
        this.srcVersion = srcVersion;
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

    public String getAppIdentifier() {
        return appIdentifier;
    }

    public void setAppIdentifier(String appIdentifier) {
        this.appIdentifier = appIdentifier;
    }
}
