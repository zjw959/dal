package net;

import org.apache.log4j.Logger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import utils.ExceptionEx;

public class InnserServerHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOGGER = Logger.getLogger(InnserServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf message = (ByteBuf) msg;
        dispatchMsg(message, ctx);
    }

    /**
     * tcp连接建立
     * 
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {}

    /**
     * tcp连接断开
     * 
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("TcpServerHandler内部发生异常 \n" + ExceptionEx.t2s(cause));
        ctx.close();
    }

    /**
     * 服务器内部消息分发，在netty线程池中执行
     * 
     * @param message
     * @param ctx
     */
    public void dispatchMsg(ByteBuf message, ChannelHandlerContext ctx) {}
}
