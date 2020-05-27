package thread.base;

import logic.character.bean.Player;

/**
 * 在GameInner(玩家,登陆,全局)线程进行处理并需要在指定线程回调的handler
 */
public abstract class LProcessCBHandler extends LBaseHandler {
    /** 回调线程 **/
    protected GameBaseProcessor cbProce;
    /** 如果有回调则回调 **/
    protected LBaseCBHandler cbHandler;

    /**
     * 
     * @param player
     * @param cp 回调线程
     * @param callBackhandler 回调处理逻辑
     */
    public LProcessCBHandler(Player player, GameBaseProcessor cp,
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
