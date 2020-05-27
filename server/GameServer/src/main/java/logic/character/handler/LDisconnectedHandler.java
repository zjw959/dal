package logic.character.handler;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import thread.base.GameInnerHandler;

public class LDisconnectedHandler extends GameInnerHandler {

    private static final Logger LOGGER = Logger.getLogger(LDisconnectedHandler.class);

    private ChannelHandlerContext ctx;

    public LDisconnectedHandler(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void action() {
        Player player = PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(ConstDefine.LOG_ERROR_PROGRAMMER_PREFIX
                    + "channel disconnected . and not find player in playerManager");
            return;
        }
        player.logout();
    }
}
