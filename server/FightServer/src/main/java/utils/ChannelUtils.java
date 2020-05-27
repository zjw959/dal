package utils;

import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;

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
    }


    /**
     * 日志信息
     * 
     * @param ctx
     * @return
     */
    public static String logInfo(ChannelHandlerContext ctx) {
        return null;
    }
}
