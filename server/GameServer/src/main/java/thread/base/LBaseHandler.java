package thread.base;

import logic.character.bean.Player;

/**
 * 需要Player的内部逻辑handler
 */
public abstract class LBaseHandler extends GameInnerHandler {
    protected final Player player;

    public LBaseHandler(Player player) {
        this.player = player;
    }
}
