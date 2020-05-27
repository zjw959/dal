package logic.login.bean;

import io.netty.channel.ChannelHandlerContext;

/**
 * 
 * @Description LoginCheckBean
 * @author LiuJiang
 * @date 2018年6月25日 下午8:52:35
 *
 */
public class LoginCheckBean {
    private final boolean isReconnect;
    private final ChannelHandlerContext ctx;
    private final String token;
    private final String url;
    private final Object parameter;
    private final String method;
    private final int antiStatus;

    private long startTime;

    public LoginCheckBean(String token, ChannelHandlerContext ctx, String url, Object parameter,
            String method, int antiStatus, boolean isReconnect) {
        this.ctx = ctx;
        this.token = token;
        this.url = url;
        this.parameter = parameter;
        this.method = method;
        this.startTime = System.currentTimeMillis();
        this.isReconnect = isReconnect;
        this.antiStatus = antiStatus;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public String getToken() {
        return token;
    }

    public boolean isReconnect() {
        return isReconnect;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getUrl() {
        return url;
    }

    public Object getParameter() {
        return parameter;
    }

    public String getRemoteAddress() {
        return ctx.channel().remoteAddress().toString();
    }

    public String getMethod() {
        return method;
    }

    public int getAntiStatus() {
        return antiStatus;
    }
}
