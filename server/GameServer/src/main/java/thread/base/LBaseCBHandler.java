package thread.base;

import logic.character.bean.Player;

/**
 * 回调逻辑
 */
public abstract class LBaseCBHandler extends LBaseHandler {
    public LBaseCBHandler(Player player) {
        super(player);
    }

    private boolean success;

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }
}
