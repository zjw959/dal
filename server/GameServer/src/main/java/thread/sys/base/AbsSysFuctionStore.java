package thread.sys.base;

import logic.character.bean.Player;

/**
 * 系统功能数据存储接口
 * 
 * 当前架构下暂无适用场景
 */
@Deprecated
public abstract class AbsSysFuctionStore extends SysService {

    /**
     * 线程开启 系统初始化方法 通常用于全数据库加载
     * 
     * @param sysFunctionProcessor
     */
    public abstract void initialize();

    /** 线程关闭 系统全数据回存 */
    public abstract void save();

    /** 登陆过程中的缓存合并 **/
    public void loginOfflineObjs(Player player) {

    }

}
