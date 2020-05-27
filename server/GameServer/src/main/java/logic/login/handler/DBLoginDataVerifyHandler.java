package logic.login.handler;

import io.netty.channel.ChannelHandlerContext;
import logic.support.LogicScriptsUtils;
import thread.db.DBBaseHandler;
import thread.player.PlayerDBProcessorManager;

/**
 * 登陆数据库查询用户及角色数据
 */
public class DBLoginDataVerifyHandler extends DBBaseHandler {
    private ChannelHandlerContext ctx;
    private boolean isReconnect;

    public DBLoginDataVerifyHandler(ChannelHandlerContext ctx, boolean isReconnect) {
        this.ctx = ctx;
        this.isReconnect = isReconnect;
        PlayerDBProcessorManager.getInstance().addSelectSize();
    }

    @Override
    public void action() {
        LogicScriptsUtils.getDB_ROLE().DBLoginDataVerify(ctx, isReconnect);
    }

    // private static final Logger LOGGER = Logger.getLogger(DBLoginDataVerifyHandler.class);
}
