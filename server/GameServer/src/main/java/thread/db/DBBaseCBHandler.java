package thread.db;

import thread.base.GameBaseProcessor;
import thread.base.GameInnerHandler;

/**
 * 数据库执行完毕后回调到指定线程
 */
public abstract class DBBaseCBHandler extends DBBaseHandler {

    /** 回调线程 **/
    protected GameBaseProcessor cbProce = null;
    /** 如果有回调则回调 **/
    protected GameInnerHandler cbHandler = null;

    public DBBaseCBHandler() {}

    /**
     * 
     * @param player
     * @param cp 回调线程
     * @param callBackhandler 回调处理逻辑
     */
    public DBBaseCBHandler(GameBaseProcessor cp, GameInnerHandler callBackhandler) {
        this.cbProce = cp;
        this.cbHandler = callBackhandler;
    }

    public GameInnerHandler getHandler() {
        return cbHandler;
    }
}
