package net.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;

public abstract class BaseTcpClient {
    public Channel connect(String host, int port) {
        if (bootstrap == null)
            bootstrap = getBootstrap();
        try {
            ChannelFuture future = bootstrap.connect(host, port).sync();
            future.awaitUninterruptibly();
            if (!future.isSuccess()) {
                LOGGER.error("连接失败: " + host + "/" + port);
                return null;
            }
        } catch (InterruptedException e) {
            LOGGER.error(e, e);
        }
        return null;
    }

    public Bootstrap getBootstrap() {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("decoder", getTcpDecoder());
                pipeline.addLast("encoder", getTcpEncoder());
                pipeline.addLast("BusinessHandler", getBizHandler());
            }
        });
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        return bootstrap;
    }

    public abstract ChannelInboundHandlerAdapter getBizHandler();

    public abstract ByteToMessageDecoder getTcpDecoder();

    public abstract MessageToByteEncoder<Object> getTcpEncoder();

    private Bootstrap bootstrap = null;

    private static final Logger LOGGER = Logger.getLogger(BaseTcpClient.class);

}
