package logic.character.handler;

import logic.character.bean.Player;
import thread.base.GameInnerHandler;

public class LCloseSessionHandler extends GameInnerHandler {

    private Player player;

    public LCloseSessionHandler(Player player) {
        this.player = player;
    }

    @Override
    public void action() {
        this.player.logout();
    }
}

