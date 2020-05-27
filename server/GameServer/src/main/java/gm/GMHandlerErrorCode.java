package gm;

/**
 * @author King
 *
 */
public class GMHandlerErrorCode {
	// 找不到玩家
	public static final int SUCESS = 0;

	// 找不到玩家
	public static final int ERROR_NO_PLAYER = 1;
	
	// 找不到系统开关
	public static final int ERROR_NO_SWITCH = 2;
	
	// 重新载入csv失败
	public static final int ERROR_LOAD_CSV = 3;
	
	// 重新载入class失败
	public static final int ERROR_LOAD_CLASS = 4;

    // 修改最大在线人数失败
    public static final int ERROR_MAX_ONLINE_NUM = 5;

	// 活动相关
	
	

	// 找不到HTTP命令
	public static final int ERROR_NO_API = -1;
}
