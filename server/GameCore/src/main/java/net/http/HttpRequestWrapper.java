package net.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.IOException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import utils.StrEx;

/**
 * 封装Http请求
 */
public class HttpRequestWrapper {
    private static final Logger LOGGER = Logger.getLogger(HttpRequestWrapper.class);
    /** 会话 */
    private ChannelHandlerContext ctx;
    /*** post或者get完整参数 */
    private HashMap<String, String> params;
    private String url;
    private String ip;
    private String data;

    public HttpRequestWrapper(FullHttpRequest request, ChannelHandlerContext ctx)
            throws IOException {
    	
    	 ByteBuf bf =request.content();
         byte[] byteArray = new byte[bf.capacity()];  
         bf.readBytes(byteArray);  
         String result = new String(byteArray);
    	
        this.params = HttpParser.parse(request);
        this.url = request.getUri();
        this.ip = ctx.channel().remoteAddress().toString();
        this.ctx = ctx;
        this.data = new String(result);
        
        
    }

    /**
     * 判断Http 请求是否可用 不可用判断连接是否可用 连接可用关闭连接
     * 
     * @return
     */
    public boolean invalid() {
        if (params != null && !params.isEmpty() && ctx != null && !url.isEmpty() && !ip.isEmpty()) {
            return false;
        }
        return true;
    }

    public void response(HttpContentType contentType, String result) {
        try {
            FullHttpResponse response =
                    new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                            Unpooled.wrappedBuffer(result.getBytes(StrEx.Charset_UTF8)));
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType);
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH,
                    response.content().readableBytes());
            ChannelFuture future = ctx.writeAndFlush(response);
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    channelFuture.channel().close();
                }
            });
        } catch (Exception ex) {
            LOGGER.error("http.response failed", ex);
        }
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public enum HttpContentType {
        Text("text/plain; charset=UTF-8");
        String value;

        HttpContentType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public enum HttpRet {
        OK(0, "SUCCESS"), INVALID(-1, "INVALID_COMMAND"), FAILED(-2, "FAILED"), ILLEGAL(-3,
                "INVALID_CLIENT");
        private final int code;
        private final String str;

        HttpRet(int code, String val) {
            this.code = code;
            this.str = val;
        }

        public String str() {
            return str;
        }

        public String desc() {
            return "{\"result\":" + code + ",\"desc\":\"" + str + "\"}";
        }
    }

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
