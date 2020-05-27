package utils;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import logic.constant.ConstDefine;
import logic.login.service.ILoginScript;
import logic.support.LogicScriptsUtils;
import server.GameServer;


/**
 * 消息工具类 服务端发送消息的方法封装
 */
public class ChannelUtils {
    private final static Logger LOGGER = Logger.getLogger(ChannelUtils.class);

    /**
     * 判断Netty连接是否有效
     * 
     * @param ctx
     * @return
     */
    public static boolean isDisconnectChannel(ChannelHandlerContext ctx) {
        // copy一份避免多线程问题
        ChannelHandlerContext tmp = ctx;
        return tmp == null || tmp.channel() == null || !tmp.channel().isActive()
                || !tmp.channel().isOpen();
    }

    /**
     * 慎用
     * 
     * 主动跨线程同步调用 关闭一个ctx, 附带一个说明
     * 
     * @param ctx
     * @param reason
     */
    public static void closeChannel(ChannelHandlerContext ctx, String reason) {
        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "主动关闭连接." + logInfo(ctx) + ". reason:" + reason
                + ExceptionEx.currentThreadTracesSingleOne(1));

        if (ctx != null) {
            try {
                // 跨线程同步回调
                boolean isA = ctx.channel().isActive();
                boolean isB = ctx.channel().isOpen();
                if (isA == false || isB == false) {
                    LOGGER.error(ConstDefine.LOG_LOGIN_PREFIX + "ctx not active, manual close ctx."
                            + logInfo(ctx));
                    try {
                        GameServer.getInstance().get_externalTcpServer().getBusinessHandler()
                                .channelInactive(ctx);
                    } catch (Exception e) {
                        LOGGER.error(
                                ConstDefine.LOG_LOGIN_PREFIX + "ctx not active, manual close ctx."
                                        + logInfo(ctx) + ExceptionEx.e2s(e));
                    }
                }
                ctx.close().sync();
            } catch (InterruptedException e) {
                LOGGER.error(ConstDefine.LOG_LOGIN_PREFIX + "主动关闭连接异常:" + ExceptionEx.e2s(e));
            }
        } else {
            LOGGER.warn("请求关闭null的ctx");
        }
    }


    /**
     * 日志信息
     * 
     * @param ctx
     * @return
     */
    public static String logInfo(ChannelHandlerContext ctx) {
        if (ctx == null) {
            return "{\"ctxInfo\": null}";
        }
        ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
        return script.ctxInfo(ctx);
    }
}
