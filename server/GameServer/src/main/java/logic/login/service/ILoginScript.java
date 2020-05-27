package logic.login.service;

import org.game.protobuf.c2s.C2SLoginMsg;

import db.game.bean.PlayerDBBean;
import io.netty.channel.ChannelHandlerContext;
import logic.character.bean.Player;
import logic.login.bean.LoginCheckBean;
import logic.login.service.LoginService.PlayerFrom;
import script.IScript;

/**
 * login logic script inteface
 */
public interface ILoginScript extends IScript {

    void reqLogin(ChannelHandlerContext ctx, C2SLoginMsg.EnterGame loginMsg);

    void reqReconnect(ChannelHandlerContext ctx, C2SLoginMsg.ReqReconnect msg);

    void innerLoginCheckSuccCallBack(LoginCheckBean bean);

    void innnerLoginDataVerifyBack(ChannelHandlerContext ctx, boolean success,
            int reason, boolean isReconnect);

    void innerCreatePlayerDBBack(ChannelHandlerContext ctx, boolean success,
            PlayerDBBean playerBean, Player player, boolean isReconnect);

    void initPlayer(PlayerDBBean playerBean, ChannelHandlerContext ctx, Player player,
            PlayerFrom playerFrom, boolean isReconnect);

    void loginFailed(ChannelHandlerContext ctx, int status, String note);

    void loginSuccess(Player player, boolean isFirstLogin);

    void resLoginQueue(ChannelHandlerContext ctx, int queue, int queueTime);

    void tick();

    void logout(Player Player);

    public String ctxInfo(ChannelHandlerContext ctx);
}
