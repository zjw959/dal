package thread.db;

import java.util.List;

import db.game.bean.PlayerDBBean;
import logic.support.LogicScriptsUtils;
import thread.player.PlayerDBProcessorManager;

/**
 * save player ' rolebean and userbean handler
 */
public class DBUpdateBatchCharacterHandler extends DBBaseHandler {
    // private static final Logger LOGGER = Logger.getLogger(DBUpdateBatchCharacterHandler.class);
    private List<PlayerDBBean> roleBeans;
    private boolean isOffLine;

    public DBUpdateBatchCharacterHandler(List<PlayerDBBean> roleUserBeans, boolean isOffLine) {
        this.roleBeans = roleUserBeans;
        this.isOffLine = isOffLine;

        PlayerDBProcessorManager.getInstance().addUpdateSize(this.roleBeans.size());
    }

    @Override
    public void action() {
        LogicScriptsUtils.getDB_ROLE().updateRoleBatch(roleBeans, isOffLine);
    }
}
