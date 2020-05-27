package logic.login.handler;

import db.game.bean.PlayerDBBean;
import io.netty.channel.ChannelHandlerContext;
import logic.character.bean.Player;
import logic.login.service.ILoginScript;
import logic.support.LogicScriptsUtils;
import thread.base.GameInnerHandler;

/**
 * database create role result Handler
 */
public class LLoginCreatePlayerResHandler extends GameInnerHandler {
    private ChannelHandlerContext ctx;
    private boolean success;
    private PlayerDBBean playerBean;
    private Player player;
    private boolean isReconnect;


    public LLoginCreatePlayerResHandler(ChannelHandlerContext ctx, boolean success,
            PlayerDBBean playerBean, Player player, boolean isReconnect) {
        this.ctx = ctx;
        this.success = success;
        this.playerBean = playerBean;
        this.player = player;
        this.isReconnect = isReconnect;

    }

    @Override
    public void action() {
        ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
        script.innerCreatePlayerDBBack(ctx, success, playerBean, player, isReconnect);
    }
}
