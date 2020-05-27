package thread.base;

import logic.character.bean.Player;

/**
 * 在DB线程进行处理并需要在指定线程回调的handler
 */
public abstract class DBProcessCBHandler extends LBaseHandler {
    /** 回调线程 **/
    protected GameBaseProcessor cbProce;
    /** 如果有回调则回调 **/
    protected LBaseCBHandler cbHandler;

    public DBProcessCBHandler(Player player, GameBaseProcessor cp,
            LBaseCBHandler callBackhandler) {
        super(player);
        this.cbProce = cp;
        this.cbHandler = callBackhandler;
    }

    protected final void _doCallBack(boolean isSuccess) {
        if (this.cbProce != null) {
            this.cbHandler.setSuccess(isSuccess);
            this.cbProce.executeInnerHandler(this.cbHandler);
        }
    }

    public LBaseCBHandler getHandler() {
        return cbHandler;
    }
}
