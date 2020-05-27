package logic.login.handler;

import io.netty.channel.ChannelHandlerContext;
import logic.login.service.ILoginScript;
import logic.support.LogicScriptsUtils;
import thread.base.GameInnerHandler;

/**
 * 账号数据检查结果Handler
 */
public class LLoginDataVerifyResHandler extends GameInnerHandler {
    private ChannelHandlerContext ctx;
    private int reason;
    private boolean success;
    private boolean isReconnect;


    public LLoginDataVerifyResHandler(ChannelHandlerContext ctx, boolean success,
            int reason, boolean isReconnect) {
        this.ctx = ctx;
        this.reason = reason;
        this.success = success;
        this.isReconnect = isReconnect;
    }

    @Override
    public void action() {
        ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
        script.innnerLoginDataVerifyBack(ctx, success, reason, isReconnect);
    }
}
