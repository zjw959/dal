package logic.constant;

public class LoginErrorCode {
	/** 服务器未知异常 */
	public static final int UNKNOWN = 99999;
	/** 缺少参数 */
	public static final int MISS_PARAM = 100001;
	/** 激活码错误 */
	public static final int ACTIVATE_CODE_ERR = 100002;
	/** 激活码已失效 */
	public static final int ACTIVATE_CODE_USED = 100003;
	/** 用户名格式不正确 */
	public static final int USER_NAME_INVALID = 100004;
	/** 密码格式不正确 */
	public static final int PASSWORD_INVALID = 100005;
	/** 缺少操作系统类型参数 */
	public static final int MISS_OS_TYPE = 100006;
	/** 平台类型不正确 */
	public static final int PLATFORM_TYPE_IS_ERR = 100007;
	/** 处理中,请稍后再试.. */
	public static final int ACCOUNT_DEALING = 100008;
	/** 用户名已经被占用 */
	public static final int ACCOUNT_NAME_ALREADY_USED = 100009;
	/** 缺少用户名 */
	public static final int MISS_ACCOUNT_ID = 100010;
	/** 缺少appId */
	public static final int MISS_APP_ID = 100011;
	/** 账号创建器类型错误 */
	public static final int CREATOR_TYPE_ERR = 100012;
	/** 参数检查器类型错误 */
	public static final int PARAM_CHECKER_TYPE_ERR = 100013;
	/** 平台账号校验器类型错误 */
	public static final int PLATFORM_ACCOUNT_CHECKER_TYPE_ERR = 100014;
	/** 没有找到游戏服务器 */
	public static final int CAN_NOT_FIND_GAME_SERVER = 100015;
	/** 缺少客户端版本号 */
	public static final int MISS_CLIENT_VERSION = 100016;
	/** 客户端版本号不正确 */
	public static final int CLIENT_VERSION_ERR = 100017;
	/** token无效 */
	public static final int TOKEN_IS_OUT_OF_TIME = 100018;
	/** 缺少ip */
	public static final int MISS_PARAM_IP = 100019;
	/** 缺少port */
	public static final int MISS_PARAM_PORT = 100020;
    /** 缺少name */
	public static final int MISS_PARAM_NAME = 100021;
    /** 缺少id */
	public static final int MISS_PARAM_ID = 100022;
    /** 状态码不正确 */
	public static final int STATE_CODE_IS_ERR = 100023;
	/** 账号在其他地方登陆 */
	public static final int OFF_SITE_LANDING = 100024;
	/** 密码错误 */
	public static final int PASSWORD_ERR = 100025;
	/** 服务器ID错误 */
	public static final int GAME_SERVER_ID_IS_ERR = 100026;
	/** 玩家被冻结 */
	public static final int INTERCEPT_PLAYER = 100027;
	/** 登陆超时 */
	public static final int LOGIN_TIME_OUT = 100028;
	/** 平台登录异常 */
	public static final int PLATFORM_CHECK_IN_ERR = 100029;
	/** 没有token参数 */
	public static final int NO_TOKEN = 100030;
	/** 缺少channelId */
	public static final int MISS_CHANNEL_ID = 100031;
	/** 没有找到账号 */
	public static final int NOT_FIND_ACCOUNT = 100032;
	/** 远程验证失败 */
	public static final int REMOTE_VERIFYER_ERROR = 100033;
	
	/** 未知渠道 */
	public static final int NOT_FOND_CHANNEL = 100034;
	/** 该渠道没有改包 */
	public static final int NOT_FOND_CHANNEL_APP = 100035;
	/** 服务器维护中 */
	public static final int STATE_AEGIS = 100036;
	/** 账号被封停 */
	public static final int ACCOUNT_INTERCEPT = 100037;
	/** pid错误 */
	public static final int PID_IS_WRONG = 100038;
	/** 未冻结，不能登录 */
	public static final int NOT_FREEZE = 100039;
	/** 游戏测试阶段，仅限封测用户登录 */
	public static final int NOT_TEST_PLAYER = 100040;
    /** 当前登录排队人数较多，请稍后再试 */
    public static final int LOGIN_QUEUE_IS_MAX = 100041;
    /** 被系统强制下线 */
    public static final int FORCE_OFFLINE = 100042;
    /** 当前服务器繁忙，请稍后再试 */
    public static final int SERVER_BUSY = 100043;
    /** 达到防沉迷时间限制 */
    public static final int ANTI_ADDICTION = 100044;
    /** 初始化账号错误 **/
    public static final int INIT_PLAYER = 100045;
}
