package net.http;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;

import org.apache.log4j.Logger;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import utils.ExceptionEx;

/**
 * http server handler extends ChannelInboundHandlerAdapter
 * 
 * 注意,该类为一个Sharable对象,所有channel共享该对象,具有非共享状态（如成员变量）
 * 
 * Sharable:如果未指定此注释,则每次将其添加到管道时都必须创建一个新的处理程序实例. 因为它具有非共享状态（如成员变量）。
 */
@ChannelHandler.Sharable
public abstract class HttpServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            close(ctx, HttpResponseStatus.BAD_REQUEST, "BAD_REQUEST");
            return;
        }

        FullHttpRequest request = (FullHttpRequest) msg;
        String uri = request.getUri();
        // chrome等浏览器的自动获取网站ico
        if (uri.endsWith(".ico")) {
            close(ctx, NOT_FOUND, "404 NOT FOUND");
            return;
        }

        HttpRequestWrapper wrapper = null;
        try {
            wrapper = new HttpRequestWrapper(request, ctx);
        } catch (Exception e) {
            LOGGER.error("http server Wrapper err. exception :" + ExceptionEx.e2s(e));
            close(ctx, HttpResponseStatus.EXPECTATION_FAILED, "wrapper err");
        }

        try {
            _handleHttpRequest(wrapper);
        } catch (Exception e) {
            LOGGER.error("http server do request. exception :" + ExceptionEx.e2s(e));
            close(ctx, HttpResponseStatus.EXPECTATION_FAILED, "request err");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error(ExceptionEx.t2s(cause));
        ctx.close();
    }

    /**
     * 推送消息后并关闭连接
     * 
     * @param ctx
     * @param status 状态
     * @param informations 推送的消息
     */
    public static void close(ChannelHandlerContext ctx, HttpResponseStatus status,
            String informations) {
        FullHttpResponse response = null;
        if (informations == null || informations.isEmpty()) {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status);
        } else {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status,
                    Unpooled.wrappedBuffer(informations.getBytes()));
        }
        response.headers().set(CONTENT_TYPE, "text/html");
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
                response.content().readableBytes());
        ChannelFuture future = ctx.channel().writeAndFlush(response);
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                channelFuture.channel().close();
            }
        });
    }

    protected abstract void _handleHttpRequest(HttpRequestWrapper httpRequestWrapper);

    private static final Logger LOGGER = Logger.getLogger(HttpServerHandler.class);
}
