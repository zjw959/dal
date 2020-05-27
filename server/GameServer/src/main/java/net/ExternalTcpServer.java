package net;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import net.codec.ExternalTcpDecoder;
import net.codec.ExternalTcpEncoder;
import net.tcp.BaseTcpServer;

public class ExternalTcpServer extends BaseTcpServer {
    public ExternalTcpServer() {}

    @Override
    public ChannelInboundHandlerAdapter getBusinessHandler() {
        return new BusinessServerHandler();
    }

    @Override
    public ByteToMessageDecoder getTcpDecoder() {
        return new ExternalTcpDecoder();
    }

    @Override
    public MessageToByteEncoder<Object> getTcpEncoder() {
        return new ExternalTcpEncoder();
    }
}
