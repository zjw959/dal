package thread.db;

import db.game.bean.PlayerDBBean;
import logic.support.LogicScriptsUtils;
import thread.base.GameBaseProcessor;
import thread.base.GameInnerHandler;
import thread.player.PlayerDBProcessorManager;

/**
 * save player ' rolebean and userbean handler
 */
public class DBUpdateRoleHandler extends DBBaseCBHandler {
    // private static final Logger LOGGER = Logger.getLogger(DBUpdateRoleHandler.class);
    private PlayerDBBean playerDBBean;

    public DBUpdateRoleHandler(PlayerDBBean roleBean) {
        this.playerDBBean = roleBean;

        PlayerDBProcessorManager.getInstance().addUpdateSize();
    }

    public DBUpdateRoleHandler(PlayerDBBean roleBean, GameBaseProcessor cp,
            GameInnerHandler handler) {
        super(cp, handler);
        this.playerDBBean = roleBean;

        PlayerDBProcessorManager.getInstance().addUpdateSize();
    }


    @Override
    public void action() {
        LogicScriptsUtils.getDB_ROLE().updateRole(playerDBBean, cbProce, cbHandler);
    }
}
