/**
 * kcp服务器
 */
package net.kcp;

import java.net.InetSocketAddress;
import java.util.Locale;

import org.apache.log4j.Logger;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.SystemPropertyUtil;

/**
 *
 * @author beykery
 */
public abstract class BaseKcpServer implements Output, KcpListerner {
    private static final Logger LOGGER = Logger.getLogger(BaseKcpServer.class);


    private final DatagramChannel channel;
    private final InetSocketAddress addr;
    private int nodelay;
    private int interval = Kcp.IKCP_INTERVAL;
    private int resend;
    private int nc;
    private int sndwnd = Kcp.IKCP_WND_SND;
    private int rcvwnd = Kcp.IKCP_WND_RCV;
    private int mtu = Kcp.IKCP_MTU_DEF;
    private boolean stream;
    private int minRto = Kcp.IKCP_RTO_MIN;
    private KcpThread[] workers;
    private volatile boolean running;
    private long timeout;
    private EventLoopGroup bossGroup;

    /**
     * server
     *
     * @param port
     * @param workerSize
     * @throws Exception
     */
    public BaseKcpServer(int port, int workerSize) throws Exception {
        if (port <= 0 || workerSize <= 0) {
            throw new IllegalArgumentException("参数非法");
        }

        // 在linux上使用Epoll模型
        String name = SystemPropertyUtil.get("os.name").toLowerCase(Locale.UK).trim();
        boolean isLinux = name.startsWith("linux");
        this.workers = new KcpThread[workerSize];
        
        Class<? extends DatagramChannel> serverChannel;
        if(isLinux) {
            bossGroup = new EpollEventLoopGroup();
            serverChannel = EpollDatagramChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup();
            serverChannel = NioDatagramChannel.class;
        }
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(bossGroup).channel(serverChannel)
                .option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_RCVBUF, 1024 * 1024)// 设置UDP读缓冲区为1M
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024)// 设置UDP写缓冲区为1M
                .handler(new ChannelInitializer<DatagramChannel>() {

                    @Override
                    protected void initChannel(DatagramChannel ch) throws Exception {
                        ChannelPipeline cp = ch.pipeline();
                        cp.addLast(new BaseKcpServer.UdpHandler());
                    }
                });
        ChannelFuture sync = bootstrap.bind(port).syncUninterruptibly();
        channel = (DatagramChannel) sync.channel();
        addr = channel.localAddress();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                bossGroup.shutdownGracefully();
            }
        }));

        // 取出boss线程的分配器,判断当前操作系统有没有影响使用堆外内存
        ByteBufAllocator allocator = sync.channel().alloc();
        boolean isDirect = allocator.isDirectBufferPooled();
        if (!isDirect) {
            stop();
            throw new Exception("该系统的netty的boss线程bytebuf没有采用直接内存");
        }
    }

    /**
     * 开始
     */
    public void start() {
        if (!this.running) {
            this.running = true;
            for (int i = 0; i < this.workers.length; i++) {
                workers[i] = new KcpThread(this, this, addr);
                workers[i].setName("kcp thread " + i);
                workers[i].wndSize(sndwnd, rcvwnd);
                workers[i].noDelay(nodelay, interval, resend, nc);
                workers[i].setMtu(mtu);
                workers[i].setTimeout(timeout);
                workers[i].setMinRto(minRto);
                workers[i].setStream(stream);
                workers[i].start();
            }
        }
    }

    public void stop() {
        LOGGER.info("socket server stop begin...");
        bossGroup.shutdownGracefully();
        LOGGER.info("socket server stop end.");
    }

    /**
     * close
     *
     * @return
     */
    public ChannelFuture close() {
        if (this.running) {
            this.running = false;
            for (KcpThread kt : this.workers) {
                kt.close();
            }
            this.workers = null;
            return this.channel.close();
        }
        return null;
    }

    /**
     * 连接 一旦连接上一个默认地址,则不会再收取其它地址的信息
     *
     * @param addr
     */
    public void connect(InetSocketAddress addr) {
        if (!this.running) {
            this.channel.connect(addr);
        }
    }

    /**
     * kcp call
     *
     * @param msg
     * @param kcp
     * @param user
     */
    @Override
    public void out(ByteBuf msg, Kcp kcp, Object user) {
        DatagramPacket temp = new DatagramPacket(msg, (InetSocketAddress) user, this.addr);
        this.channel.writeAndFlush(temp);
    }

    /**
     * fastest: ikcp_nodelay(kcp, 1, 20, 2, 1) nodelay: 0:disable(default), 1:enable interval:
     * internal update timer interval in millisec, default is 100ms resend: 0:disable fast
     * resend(default), 1:enable fast resend nc: 0:normal congestion control(default), 1:disable
     * congestion control
     *
     * @param nodelay
     * @param interval
     * @param resend
     * @param nc
     */
    public void noDelay(int nodelay, int interval, int resend, int nc) {
        this.nodelay = nodelay;
        this.interval = interval;
        this.resend = resend;
        this.nc = nc;
    }

    /**
     * set maximum window size: sndwnd=32, rcvwnd=32 by default
     *
     * @param sndwnd
     * @param rcvwnd
     */
    public void wndSize(int sndwnd, int rcvwnd) {
        this.sndwnd = sndwnd;
        this.rcvwnd = rcvwnd;
    }

    /**
     * change MTU size, default is 1400
     *
     * @param mtu
     */
    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    /**
     * stream mode
     *
     *
     * @param stream
     */
    public void setStream(boolean stream) {
        this.stream = stream;
    }

    public boolean isStream() {
        return stream;
    }

    public void setMinRto(int minRto) {
        this.minRto = minRto;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return this.timeout;
    }

    /**
     * 发送
     *
     * @param bb
     * @param ku
     */
    public void send(ByteBuf bb, KcpOnUdp ku) {
        ku.send(bb);
    }

    /**
     * receive DatagramPacket
     *
     * @param dp
     */
    private void onReceive(DatagramPacket dp) {
        if (this.running) {
            InetSocketAddress sender = dp.sender();
            int hash = sender.hashCode();
            hash = hash < 0 ? -hash : hash;
            this.workers[hash % workers.length].input(dp);
        } else {
            dp.release();
        }
    }

    /**
     * handler
     */
    public class UdpHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            DatagramPacket dp = (DatagramPacket) msg;
            BaseKcpServer.this.onReceive(dp);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            BaseKcpServer.this.handleException(cause, null);
        }
    }
    
    public String printThreadTaskInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < workers.length; i++) {
            KcpThread thread = workers[i];
            stringBuilder.append(thread.getName());
            stringBuilder.append(" inputs ");
            stringBuilder.append(thread.getMessageSize());
            stringBuilder.append(",");
        }
        return stringBuilder.toString();
    }
}
