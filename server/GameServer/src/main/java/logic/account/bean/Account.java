package logic.account.bean;

import logic.login.platformVerify.ChannelType;

/**
 * 账号
 */
public class Account{
	
	/**
	* id
	*/
	private String id;
	
	/**
	* 平台账号id
	*/
	private String accountId;
	
	/**
	* 渠道type
	*/
	private ChannelType channelType;
	
	/**
	* 渠道APPID
	*/
	private int channelAppId;
	
	/**
	* 密码（是MD5加密后的）
	*/
	private String password;
	
	/**
	* 用户手动绑定的邮箱
	*/
	private String email;
	
	/**
	* 设备token
	*/
	private String deviceToken;
	
	/**
	* 设备名称
	*/
	private String deviceName;
	
	/**
	* 设备唯一ID
	*/
	private String deviceId;
	
	/**
	* SDK类型
	*/
	private String sdk;
	
	/**
	* SDK版本
	*/
	private String sdkVersion;
	
	/**
	* 系统名称
	*/
	private String osName;
	
	/**
	* 系统版本
	*/
	private String osVersion;
	
	/**
	* 手机号（限定唯一并且填充时进行唯一检测）
	*/
	private String phoneNumber;
	
	/**
	* 客户端版本号
	*/
	private String clientVersion;
	
	/**
	* 密保问题
	*/
	private String question;
	
	/**
	* 密保答案
	*/
	private String answer;
	
	/**
	* 管理员账号
	*/
	private boolean admin;
	
	/**
	* 游戏服id
	*/
	private int gameServerId;
	
	/**
	* 语言
	*/
	private String language;
	
	/**
	* ip地址
	*/
	private String ip;
	
	/**
	* 最后上线时间
	*/
	private java.util.Date lastOnlineTime;
	
	/**
	* 最后下线时间
	*/
	private java.util.Date lastOfflineTime;
	
	/**
	* 封禁状态（账号冻结、禁言等）
	*/
	private int state;
	
	/**
	* 在线状态
	*/
	private int onlineState;
	
	/**
	* 是否激活
	*/
	private boolean activate;
	
	/**
	* 创建时间
	*/
	private java.util.Date createDate;
	
	/**
	* 修改时间
	*/
	private java.util.Date modifiedDate;
	
	public Account(){
	}
	
	public Account(String id, String accountId, ChannelType channelType, int channelAppId, String password, String email, String deviceToken, String deviceName, String deviceId, String sdk, String sdkVersion, String osName, String osVersion, String phoneNumber, String clientVersion, String question, String answer, boolean admin, int gameServerId, String language, String ip, java.util.Date lastOnlineTime, java.util.Date lastOfflineTime, int state, int onlineState, boolean activate) {
		super();
		this.id = id;
		this.accountId = accountId;
		this.channelType = channelType;
		this.channelAppId = channelAppId;
		this.password = password;
		this.email = email;
		this.deviceToken = deviceToken;
		this.deviceName = deviceName;
		this.deviceId = deviceId;
		this.sdk = sdk;
		this.sdkVersion = sdkVersion;
		this.osName = osName;
		this.osVersion = osVersion;
		this.phoneNumber = phoneNumber;
		this.clientVersion = clientVersion;
		this.question = question;
		this.answer = answer;
		this.admin = admin;
		this.gameServerId = gameServerId;
		this.language = language;
		this.ip = ip;
		this.lastOnlineTime = lastOnlineTime;
		this.lastOfflineTime = lastOfflineTime;
		this.state = state;
		this.onlineState = onlineState;
		this.activate = activate;
		this.createDate = new java.util.Date();
		this.modifiedDate = new java.util.Date();
	}
	
	/**
	 * id
	 */
	public String getId() {return id;}
	
	/**
	 * id
	 */
	public Account setId(String id) {this.id = id;return this;}
	
	/**
	 * 平台账号id
	 */
	public String getAccountId() {return accountId;}
	
	/**
	 * 平台账号id
	 */
	public Account setAccountId(String accountId) {this.accountId = accountId;return this;}
	

    public ChannelType getChannelType() {
        return channelType;
    }

    public void setChannelType(ChannelType channelType) {
        this.channelType = channelType;
    }

    /**
	 * 渠道APPID
	 */
	public int getChannelAppId() {return channelAppId;}
	
	/**
	 * 渠道APPID
	 */
	public Account setChannelAppId(int channelAppId) {this.channelAppId = channelAppId;return this;}
	
	/**
	 * 密码（是MD5加密后的）
	 */
	public String getPassword() {return password;}
	
	/**
	 * 密码（是MD5加密后的）
	 */
	public Account setPassword(String password) {this.password = password;return this;}
	
	/**
	 * 用户手动绑定的邮箱
	 */
	public String getEmail() {return email;}
	
	/**
	 * 用户手动绑定的邮箱
	 */
	public Account setEmail(String email) {this.email = email;return this;}
	
	/**
	 * 设备token
	 */
	public String getDeviceToken() {return deviceToken;}
	
	/**
	 * 设备token
	 */
	public Account setDeviceToken(String deviceToken) {this.deviceToken = deviceToken;return this;}
	
	/**
	 * 设备名称
	 */
	public String getDeviceName() {return deviceName;}
	
	/**
	 * 设备名称
	 */
	public Account setDeviceName(String deviceName) {this.deviceName = deviceName;return this;}
	
	/**
	 * 设备唯一ID
	 */
	public String getDeviceId() {return deviceId;}
	
	/**
	 * 设备唯一ID
	 */
	public Account setDeviceId(String deviceId) {this.deviceId = deviceId;return this;}
	
	/**
	 * SDK类型
	 */
	public String getSdk() {return sdk;}
	
	/**
	 * SDK类型
	 */
	public Account setSdk(String sdk) {this.sdk = sdk;return this;}
	
	/**
	 * SDK版本
	 */
	public String getSdkVersion() {return sdkVersion;}
	
	/**
	 * SDK版本
	 */
	public Account setSdkVersion(String sdkVersion) {this.sdkVersion = sdkVersion;return this;}
	
	/**
	 * 系统名称
	 */
	public String getOsName() {return osName;}
	
	/**
	 * 系统名称
	 */
	public Account setOsName(String osName) {this.osName = osName;return this;}
	
	/**
	 * 系统版本
	 */
	public String getOsVersion() {return osVersion;}
	
	/**
	 * 系统版本
	 */
	public Account setOsVersion(String osVersion) {this.osVersion = osVersion;return this;}
	
	/**
	 * 手机号（限定唯一并且填充时进行唯一检测）
	 */
	public String getPhoneNumber() {return phoneNumber;}
	
	/**
	 * 手机号（限定唯一并且填充时进行唯一检测）
	 */
	public Account setPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;return this;}
	
	/**
	 * 客户端版本号
	 */
	public String getClientVersion() {return clientVersion;}
	
	/**
	 * 客户端版本号
	 */
	public Account setClientVersion(String clientVersion) {this.clientVersion = clientVersion;return this;}
	
	/**
	 * 密保问题
	 */
	public String getQuestion() {return question;}
	
	/**
	 * 密保问题
	 */
	public Account setQuestion(String question) {this.question = question;return this;}
	
	/**
	 * 密保答案
	 */
	public String getAnswer() {return answer;}
	
	/**
	 * 密保答案
	 */
	public Account setAnswer(String answer) {this.answer = answer;return this;}
	
	/**
	 * 管理员账号
	 */
	public boolean isAdmin() {return admin;}
	
	/**
	 * 管理员账号
	 */
	public Account setAdmin(boolean admin) {this.admin = admin;return this;}
	
	/**
	 * 语言
	 */
	public String getLanguage() {return language;}
	
	/**
	 * 语言
	 */
	public Account setLanguage(String language) {this.language = language;return this;}
	
	/**
	 * ip地址
	 */
	public String getIp() {return ip;}
	
	/**
	 * ip地址
	 */
	public Account setIp(String ip) {this.ip = ip;return this;}
	
	/**
	 * 最后上线时间
	 */
	public java.util.Date getLastOnlineTime() {return lastOnlineTime;}
	
	/**
	 * 最后上线时间
	 */
	public Account setLastOnlineTime(java.util.Date lastOnlineTime) {this.lastOnlineTime = lastOnlineTime;return this;}
	
	/**
	 * 最后下线时间
	 */
	public java.util.Date getLastOfflineTime() {return lastOfflineTime;}
	
	/**
	 * 最后下线时间
	 */
	public Account setLastOfflineTime(java.util.Date lastOfflineTime) {this.lastOfflineTime = lastOfflineTime;return this;}
	
	/**
	 * 封禁状态（账号冻结、禁言等）
	 */
	public int getState() {return state;}
	
	/**
	 * 封禁状态（账号冻结、禁言等）
	 */
	public Account setState(int state) {this.state = state;return this;}
	
	/**
	 * 在线状态
	 */
	public int getOnlineState() {return onlineState;}
	
	/**
	 * 在线状态
	 */
	public Account setOnlineState(int onlineState) {this.onlineState = onlineState;return this;}
	
	/**
	 * 是否激活
	 */
	public boolean isActivate() {return activate;}
	
	/**
	 * 是否激活
	 */
	public Account setActivate(boolean activate) {this.activate = activate;return this;}
	
	/**
	 * 创建时间
	 */
	public java.util.Date getCreateDate() {return createDate;}
	
	/**
	 * 创建时间
	 */
	protected Account setCreateDate(java.util.Date createDate) {this.createDate = createDate;return this;}
	
	/**
	 * 修改时间
	 */
	public java.util.Date getModifiedDate() {return modifiedDate;}
	
	/**
	 * 修改时间
	 */
	protected Account setModifiedDate(java.util.Date modifiedDate) {this.modifiedDate = modifiedDate;return this;}
    
}