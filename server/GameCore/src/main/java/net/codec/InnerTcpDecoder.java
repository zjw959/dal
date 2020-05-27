package net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;

import java.util.List;

public class InnerTcpDecoder extends ByteToMessageDecoder {
    // 接收的包大小最大值（单位：字节）
    private final int MAX_RECV_SIZE = 1024 * 1024 * 1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {
        if (in.readableBytes() < 4) {
            // 不足4个字节
            return;
        }

        // 读取包长度
        int bufLen = in.getInt(in.readerIndex());
        if (bufLen > MAX_RECV_SIZE || bufLen <= 0) {
            LOGGER.error("接收的包大小超过限制:" + bufLen + "字节，最大值:" + MAX_RECV_SIZE + "字节");
            ctx.close();
            return;
        }

        if ((in.readableBytes() - 4) < bufLen) {
            // 数据不够
            return;
        } else {
            in.readInt();
        }

        out.add(in.readBytes(bufLen));
    }

    private static final Logger LOGGER = Logger.getLogger(ExternalTcpDecoder.class);
}
