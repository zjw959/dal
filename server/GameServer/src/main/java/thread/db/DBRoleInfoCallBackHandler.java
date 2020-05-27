package thread.db;

import org.apache.log4j.Logger;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;
import thread.base.GameBaseProcessor;
import thread.base.LBaseHandler;
import thread.player.hanlder.base.LRoleBaseOfflineHandler;

/**
 * 从数据库中查询玩家信息并回调到指定线程
 * 
 * 注意: 该查询是直接从数据库中查询用户的完整数据,没有缓存,慎用.
 */
public class DBRoleInfoCallBackHandler extends DBBaseHandler {
    private static final Logger LOGGER = Logger.getLogger(DBRoleInfoCallBackHandler.class);

    private final LRoleBaseOfflineHandler handler;
    private final GameBaseProcessor cbProce;
    private final boolean isView;

    public DBRoleInfoCallBackHandler(boolean isView, LRoleBaseOfflineHandler handler,
            GameBaseProcessor cp) {
        this.handler = handler;
        this.cbProce = cp;
        this.isView = isView;
    }

    @Override
    public void action() {
        LogicScriptsUtils.getDB_ROLE().roleInfo(handler, cbProce, isView);
    }

    public LRoleBaseOfflineHandler getHandler() {
        return handler;
    }

    public GameBaseProcessor getCbProce() {
        return cbProce;
    }

    public boolean isView() {
        return isView;
    }
}


/**
 * 查询离线玩家,如果不存在则加入本地(JVM)缓存
 */
@Deprecated
class LOffLinePlayerRegister extends LBaseHandler {

    public LOffLinePlayerRegister(Player player) {
        super(player);
    }

    @Override
    public void action() {
        if (PlayerManager.getPlayerByPlayerId(player.getPlayerId()) == null) {
            PlayerManager.register(player);
        }
    }
}
