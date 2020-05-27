package logic.login.handler;

import io.netty.channel.ChannelHandlerContext;
import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;
import thread.db.DBBaseHandler;
import thread.player.PlayerDBProcessorManager;

/**
 * database create role Handler
 */
public class DBCreatePlayerHandler extends DBBaseHandler {
    // private static final Logger LOGGER = Logger.getLogger(DBCreatePlayerHandler.class);
    private ChannelHandlerContext ctx;
    private Player player;

    public DBCreatePlayerHandler(ChannelHandlerContext ctx, Player player) {
        this.ctx = ctx;
        this.player = player;

        PlayerDBProcessorManager.getInstance().addInsertSize();
    }

    @Override
    public void action() {
        LogicScriptsUtils.getDB_ROLE().createPlayer(ctx, player);
    }

}
