package net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import message.SMessage;
import org.apache.log4j.Logger;

public class InnerTcpEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (!(msg instanceof SMessage)) {
            LOGGER.error("从服务器发送的消息类型未知：" + msg.getClass().getName());
            return;
        }

        SMessage sMessage = (SMessage) msg;
        int msgId = sMessage.getId();
        byte[] msgData = sMessage.getData();

        out.writeInt(msgData.length + 4);
        out.writeInt(msgId);
        out.writeBytes(msgData);
    }

    private static final Logger LOGGER = Logger.getLogger(ExternalTcpEncoder.class);
}
