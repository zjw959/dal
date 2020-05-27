package logic.login.service;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.log4j.Logger;

import io.netty.channel.ChannelHandlerContext;
import logic.login.bean.LoginCheckBean;
import logic.login.handler.LReqLoginCheckHandler;
import thread.sys.base.SysService;

public class LoginCheckService extends SysService {
    private static final Logger LOGGER = Logger.getLogger(LoginCheckService.class);

    protected LoginCheckService() {
        requestConfig = RequestConfig.custom().setSocketTimeout(10 * 1000)
                .setConnectTimeout(10 * 1000).build();
        int process = Runtime.getRuntime().availableProcessors();
        if (process >= 16) {
            process = 4;
        } else {
            if (process < 4) {
                process = 1;
            } else {
                process = 2;
            }
        }
        client = HttpAsyncClients.custom()
                .setDefaultIOReactorConfig(
                        IOReactorConfig.custom().setIoThreadCount(process).build())
                .build();
        client.start();
    }

    private CloseableHttpAsyncClient client;
    private final RequestConfig requestConfig;
    /** 登录队列 */
    private ConcurrentLinkedQueue<LoginCheckBean> queue =
            new ConcurrentLinkedQueue<LoginCheckBean>();
    /** 上个玩家进入游戏的时间 */
    private long lastEnterTime;
    /** 预计单个排队时间（秒） */
    private int queueUnitTime;


    public int getSize() {
        return queue.size();
    }

    public long getLastEnterTime() {
        return lastEnterTime;
    }

    public void setLastEnterTime(long lastEnterTime) {
        this.lastEnterTime = lastEnterTime;
    }

    public int getQueueUnitTime() {
        return queueUnitTime;
    }

    public void setQueueUnitTime(int queueUnitTime) {
        this.queueUnitTime = queueUnitTime;
    }

    public CloseableHttpAsyncClient getClient() {
        return client;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public ConcurrentLinkedQueue<LoginCheckBean> getQueue() {
        return queue;
    }

    /**
     * 请求登录验证(异步)
     * 
     * @param token
     * @param ctx
     * @param url
     * @param parameter
     * @param method
     */
    public void reqLoginCheckAysnc(String token, ChannelHandlerContext ctx, String url,
            Object parameter, String method, int antiStatus, boolean isReconnect) {
        this.getProcess().executeInnerHandler(
                new LReqLoginCheckHandler(new LoginCheckBean(token, ctx, url, parameter, method,
                        antiStatus, isReconnect)));
    }

    public static LoginCheckService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        LoginCheckService instance;

        private Singleton() {
            instance = new LoginCheckService();
        }

        LoginCheckService getInstance() {
            return instance;
        }
    }
}
