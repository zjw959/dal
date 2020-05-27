package net.http;

import java.util.Locale;

import org.apache.log4j.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.SystemPropertyUtil;
import net.ServerBootOptionSet;

public abstract class BaseHttpServer {
    public void start(int port) throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();

        Class<? extends ServerChannel> serverChannel = null;

        int defaultBossThread = Runtime.getRuntime().availableProcessors() <= 4 ? 1 : 2;
        DefaultThreadFactory defaultBossThreadFactory = new DefaultThreadFactory("httpBoss");
        int defaultWorkThread = Runtime.getRuntime().availableProcessors() <= 10 ? 2
                : Runtime.getRuntime().availableProcessors() / 5;
        DefaultThreadFactory defaultWorkThreadFactory = new DefaultThreadFactory("httpWork");
        // 在linux上使用Epoll模型
        String name = SystemPropertyUtil.get("os.name").toLowerCase(Locale.UK).trim();
        if (name.startsWith("linux")) {
            bossGroup = new EpollEventLoopGroup(defaultBossThread, defaultBossThreadFactory);
            workerGroup = new EpollEventLoopGroup(defaultWorkThread, defaultWorkThreadFactory);
            serverChannel = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup(defaultBossThread, defaultBossThreadFactory);
            workerGroup = new NioEventLoopGroup(defaultWorkThread, defaultWorkThreadFactory);
            serverChannel = NioServerSocketChannel.class;
        }

        bootstrap.group(bossGroup, workerGroup).channel(serverChannel)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpResponseEncoder())
                                .addLast(new HttpRequestDecoder())
                                // 注意,对分块消息的处理机制 大小测试,例如刷新配置表大数据量等操作.
                                // 特殊需求可以通过4.1 重写handleOversizedMessage方法来解决
                                .addLast(new HttpObjectAggregator(1024 * 1024 * 64))
                                .addLast(adapter);
                    }
                });

        ServerBootOptionSet.setOption(bootstrap);

        // 同步等待绑定成功...
        ChannelFuture future = bootstrap.bind(port).sync();

        // 监听关闭事件
        future.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                // 抛出监听停止事件，注意该事件的执行线程
                LOGGER.info("线程" + Thread.currentThread().getName() + " 执行HTTP监听关闭事件: " + port);
            }
        });

        LOGGER.info("start http server listen on : " + port);
    }

    public void stop() {
        LOGGER.info("http server close begin...");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        LOGGER.info("http server stop end.");
    }

    public abstract ChannelInboundHandlerAdapter getHttpServerHandler();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private final ChannelInboundHandlerAdapter adapter = getHttpServerHandler();

    private static final Logger LOGGER = Logger.getLogger(BaseHttpServer.class);

}
