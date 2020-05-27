package net.http;

import io.netty.channel.ChannelInboundHandlerAdapter;

public class HttpServer extends BaseHttpServer {
    @Override
    public ChannelInboundHandlerAdapter getHttpServerHandler() {
        return new HttpServerImpl();
    }
}
