package logic.login.struct;


public interface ParamKey {


	//-------------------登录 开始-------------------//

	/** 激活码 */
	String ACTIVATE_KEY = "activateKey";
	/** 密码 */
	String PASSWORD = "password";
	/** 账号id */
	String ACCOUNT_ID = "accountId";
	String MOBILE = "mobile";	// 手机号
	String LANGUAGE = "language";//语言
	String CLIENT_VERSION = "version"; // 版本号
	String DAL_BETA_TYPE = "dal_beta_type";//测试用户类型
	
	String OS_NAME = "osName"; // 设备系统名称
	String OS_VERSION = "osVersion"; // 设备系统版本
	
	String DEVICE_ID = "deviceid"; // 设备ID唯一标识
	String DEVICE_TOKEN = "token"; // 设备token
	String DEVICE_NAME = "deviceName"; // 设备名称
	
	String CHANNEL_ID = "channelId"; // 渠道标识
	String CHANNEL_APP_ID = "channelAppId"; // 渠道包AppKey
	
    String PLAYER_ID = "playerId"; // playerId
	
	String SDK = "sdk"; // 第三方接入类型。如：PP、91等
	String SDK_VERSION = "sdkVersion"; // SDK版本
	String CLIENT_IP = "clientIp";//客户端ip
	String PID = "pid";//管理员登录:pid

    String ANTI_ADDICTION = "antiAddiction";// 防沉迷
    String IS_ADMIN_LOGIN = "isAdminLogin";// 是否是托管登录
    String IS_WHITE_USER = "isWhiteUser";// 是否是白名单账号

    String NET_WORK_TYPE = "networkType";//
    String NET_WORK_CARRIER = "networkCarrier";//
    String SCREEN_WIDTH = "screenWidth";//
    String SCREEN_HEIGHT = "screenHeight";//
    String APP_VERSION = "appVersion";//
    String DEVICE_BRAND = "devicebrand";// 设备品牌
    String IDFA = "idfa";//
    String IMEI = "imei";//
    String ANDROID_ID = "androidid";//
	
	/** token */
	String TOKEN = "token";
	/** 游戏服务器ip */
	String GAME_SERVER_IP = "gameServerIp";
	/** 游戏服务器端口 */
	String GAME_SERVER_PORT = "gameServerPort";
	/** 开服时间 */
	String OPEN_SERVER_DATE = "openServerDate";
	/** 需要激活 */
	String NEED_ACTIVATE = "needActivate";
	//-------------------登录 结束-------------------//

	//-------------------游戏服 开始-------------------//
	String SERVER_IP = "ip";
	String SERVER_PORT = "port";
	String SERVER_NAME = "name";
    String SERVER_ID = "serverId";
	String SERVER_STATE = "state";
	String SERVER_IS_RECOMMAND = "recommand";
	String SERVER_OPEN_DATE = "openDate";
	//-------------------游戏服 结束-------------------//
	/** 数量 */
	String NUM = "num";

	/**
	 * 冻结截止时间
	 */
	String FREEZE_DEADLINE = "freezeDeadline";



}
