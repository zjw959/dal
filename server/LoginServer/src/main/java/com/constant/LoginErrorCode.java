package com.constant;

/**
 * 
 * @Description 登录异常代码
 * @Description 为了方便以后定位问题  所有的错误码都统一定义到该枚举下面
 * @author LiuJiang
 * @date 2018年6月27日 下午5:15:16
 *
 */
public enum LoginErrorCode {
    /** 成功 */
    SUCCESS(0, "SUCCESS"),
    /** 服务器未知异常 */
    UNKNOWN_EXCEPTION(99999, "UNKNOWN_EXCEPTION"),
    /** 缺少参数 */
    MISS_PARAM(100001, "MISS_PARAM"),
    /** 激活码错误 */
    ACTIVATE_CODE_ERR(100002, "ACTIVATE_CODE_ERR"),
    /** 激活码已失效 */
    ACTIVATE_CODE_USED(100003, "ACTIVATE_CODE_USED"),
    /** 用户名格式不正确 */
    USER_NAME_INVALID(100004, "USER_NAME_INVALID"),
    /** 密码格式不正确 */
    PASSWORD_INVALID(100005, "PASSWORD_INVALID"),
    /** 缺少操作系统类型参数 */
    MISS_OS_TYPE(100006, "MISS_OS_TYPE"),
    /** 平台类型不正确 */
    PLATFORM_TYPE_IS_ERR(100007, "PLATFORM_TYPE_IS_ERR"),
    /** 处理中,请稍后再试.. */
    ACCOUNT_DEALING(100008, "ACCOUNT_DEALING"),
    /** 用户名已经被占用 */
    ACCOUNT_NAME_ALREADY_USED(100009, "ACCOUNT_NAME_ALREADY_USED"),
    /** 缺少用户名 */
    MISS_ACCOUNT_ID(100010, "MISS_ACCOUNT_ID"),
    /** 缺少appId */
    MISS_APP_ID(100011, "MISS_APP_ID"),
    /** 账号创建器类型错误 */
    CREATOR_TYPE_ERR(100012, "CREATOR_TYPE_ERR"),
    /** 参数检查器类型错误 */
    PARAM_CHECKER_TYPE_ERR(100013, "PARAM_CHECKER_TYPE_ERR"),
    /** 平台账号校验器类型错误 */
    PLATFORM_ACCOUNT_CHECKER_TYPE_ERR(100014, "PLATFORM_ACCOUNT_CHECKER_TYPE_ERR"),
    /** 没有找到游戏服务器 */
    CAN_NOT_FIND_GAME_SERVER(100015, "CAN_NOT_FIND_GAME_SERVER"),
    /** 缺少客户端版本号 */
    MISS_CLIENT_VERSION(100016, "MISS_CLIENT_VERSION"),
    /** 客户端版本号不正确 */
    CLIENT_VERSION_ERR(100017, "CLIENT_VERSION_ERR"),
    /** token无效 */
    TOKEN_IS_OUT_OF_TIME(100018, "TOKEN_IS_OUT_OF_TIME"),
    /** 缺少ip */
    MISS_PARAM_IP(100019, "MISS_PARAM_IP"),
    /** 缺少port */
    MISS_PARAM_PORT(100020, "MISS_PARAM_PORT"),
    /** 缺少name */
    MISS_PARAM_NAME(100021, "MISS_PARAM_NAME"),
    /** 缺少id */
    MISS_PARAM_ID(100022, "MISS_PARAM_ID"),
    /** 状态码不正确 */
    STATE_CODE_IS_ERR(100023, "STATE_CODE_IS_ERR"),
    /** 账号在其他地方登陆 */
    OFF_SITE_LANDING(100024, "OFF_SITE_LANDING"),
    /** 密码错误 */
    PASSWORD_ERR(100025, "PASSWORD_ERR"),
    /** 服务器ID错误 */
    GAME_SERVER_ID_IS_ERR(100026, "GAME_SERVER_ID_IS_ERR"),
    /** 玩家被冻结 */
    INTERCEPT_PLAYER(100027, "INTERCEPT_PLAYER"),
    /** 登陆超时 */
    LOGIN_TIME_OUT(100028, "LOGIN_TIME_OUT"),
    /** 平台登录异常 */
    PLATFORM_CHECK_IN_ERR(100029, "PLATFORM_CHECK_IN_ERR"),
    /** 没有token参数 */
    NO_TOKEN(100030, "NO_TOKEN"),
    /** 缺少channelId */
    MISS_CHANNEL_ID(100031, "MISS_CHANNEL_ID"),
    /** 没有找到账号 */
    NOT_FIND_ACCOUNT(100032, "NOT_FIND_ACCOUNT"),
    /** 远程验证失败 */
    REMOTE_VERIFYER_ERROR(100033, "REMOTE_VERIFYER_ERROR"),
    /** 未知渠道 */
    NOT_FOND_CHANNEL(100034, "NOT_FOND_CHANNEL"),
    /** 该渠道没有改包 */
    NOT_FOND_CHANNEL_APP(100035, "NOT_FOND_CHANNEL_APP"),
    /** 服务器维护中 */
    STATE_AEGIS(100036, "STATE_AEGIS"),
    /** 账号被封停 */
    ACCOUNT_INTERCEPT(100037, "ACCOUNT_INTERCEPT"),
    /** pid错误 */
    PID_IS_WRONG(100038, "PID_IS_WRONG"),
    /** 未冻结，不能登录 */
    NOT_FREEZE(100039, "NOT_FREEZE"),
    /** 游戏测试阶段，仅限封测用户登录 */
    NOT_TEST_PLAYER(100040, "NOT_TEST_PLAYER"),
	/** 没有服务器名称--必填 */
    SERVER_NAME_MISS(100041, "SERVER_NAME_MISS"), 
    /** 没有服务器IP--必填 */
    SERVER_IP_MISS(100042, "SERVER_IP_MISS"), 
    /** 服务器分组找不到 */
    SERVER_GROUP_MISS(100043, "SERVER_GROUP_MISS"), 
    ;
	
	
	

    /** 异常编号 */
    int code;
    /** 异常信息 */
    String msg;

    LoginErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
