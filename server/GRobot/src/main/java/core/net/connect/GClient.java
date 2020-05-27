package core.net.connect;

import java.net.InetSocketAddress;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

import UI.window.frame.MainWindow;
import UI.window.panel.ServerPanel;
import conf.RunConf;
import core.Log4jManager;
import core.net.codec.ClientHandler;
import core.net.codec.ExternalTcpDecoder;
import core.net.codec.ExternalTcpEncoder;
import core.robot.GRobotManager;
import core.robot.RobotThread;
import core.robot.RobotThreadFactory;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import logic.constant.ChannelType;
import utils.StrEx;

/**
 * @function 模拟客户端建立socket连接
 */
public class GClient {
    public static Bootstrap bootstrap;
    public static EventLoopGroup group;

    public static void init() {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("Decoder", new ExternalTcpDecoder());
                        socketChannel.pipeline().addLast("Encoder", new ExternalTcpEncoder());
                        socketChannel.pipeline().addLast("BusinessHandler", new ClientHandler());
                    }
                });

        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    }

    public synchronized static void run(MainWindow window, RobotThread ctx) throws Exception {
        long now = System.currentTimeMillis();
        ServerPanel panel = window.getServerPanel();
        String _url = RunConf.loginUrl + "?accountId=" + ctx.getName() + "&channelId="
                + ChannelType.LOCAL_TEST;
        if (!panel.getIsRandomCheckBox()) {
            _url += "&serverName=" + RunConf.choosedServerConf.getName();
        } else {
            // 指定去哪个组
            _url += "&serverGroup=" + panel.getSelectGroup();
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = getRequestConfig();
        HttpGet httpGet = new HttpGet(_url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        // 同步等待回调
        // Log4jManager.getInstance().info(window,
        // "等待用户中心返回,时间:" + (System.currentTimeMillis() - now) + " ms");
        String token = "";
        String ip = null;
        int post = 0;
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log4jManager.getInstance().error(window,
                        "用户中心返回错误码.status:" + response.getStatusLine().getStatusCode());
                return;
            }
            HttpEntity responseEntity = response.getEntity();
            String text = EntityUtils.toString(responseEntity, StrEx.Charset_UTF8);
            JSONObject resultJO = JSONObject.parseObject(text);
            String status = resultJO.getString("status");
            if (!"0".equals(status)) {
                Log4jManager.getInstance().error(window, "用户中心返回.status:" + status);
                return;
            }
            JSONObject data = JSONObject.parseObject(resultJO.getString("data"));
            token = data.getString("token");
            ip = data.getString("gameServerIp");
            post = Integer.parseInt(data.getString("gameServerPort"));
            ctx.setToken(token);
        } finally {
            if (response != null) {
                response.close();
            }
        }

        InetSocketAddress inet = new InetSocketAddress(ip, post);

        if (window.getConsolePanel().isLoginCheck()) {
            ChannelFuture channelFuture = bootstrap.connect(inet);
            // 链接创建工程才会继续下一次
            channelFuture = channelFuture.await();
            _initChanel(window, ctx, channelFuture);
        } else {
            ChannelFuture channelFuture = bootstrap.connect(inet);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    _initChanel(window, ctx, channelFuture);
                }
            });
        }
    }

    protected static void _initChanel(MainWindow window, RobotThread ctx,
            ChannelFuture channelFuture) {
        if (channelFuture.isSuccess()) {
            ctx.channel = channelFuture.channel();
            RobotThreadFactory.putRobot(ctx.channel.id().asLongText(), ctx);
            GRobotManager.instance().addRobot(ctx);
            window.addConnection();
            window.addConnectionTots();
            ctx.run(false);
            return;
        } else {
            Log4jManager.getInstance().error(window, channelFuture.cause().toString());
            return;
        }
    }

    protected static RequestConfig getRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(10000).build();
    }
    
    public static ChannelFuture connect(String ip, int port) {
        InetSocketAddress inet = new InetSocketAddress(ip, port);
        return bootstrap.connect(inet);
    }
}

