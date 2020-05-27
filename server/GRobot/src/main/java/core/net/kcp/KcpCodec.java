package core.net.kcp;

import java.util.ArrayList;
import java.util.List;

import core.net.message.SMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.codec.util.CheckSumUtil;
import net.codec.util.EncryptionAndDecryptionUtil;
import net.kcp.constant.KcpConstant;

public class KcpCodec {
    public static List<SMessage> decoder(ByteBuf buf, int[] decryptKey) {
        try {
            List<SMessage> msgs = new ArrayList<>();

            while (buf.readableBytes() >= 6) {
                buf.markReaderIndex();
                // 取得包头数据
                byte[] headData = new byte[6];
                buf.readBytes(headData);

                // 克隆钥密
                int[] copyDecryptKeys = new int[decryptKey.length];
                System.arraycopy(decryptKey, 0, copyDecryptKeys, 0, copyDecryptKeys.length);

                // 解密包头
                headData = EncryptionAndDecryptionUtil.decryptCustom(headData, copyDecryptKeys);
                // System.out.println("-> 包头解密成功");
                ByteBuf head = Unpooled.wrappedBuffer(headData);

                // 取得包标记
                short tag = head.readShort();
                // 包长
                int length = head.readInt();

                // 如果包头不匹配
                if (tag != KcpConstant.PACKET_TAG) {
                    // System.out.println("-> 分包标识不匹配");
                    // 跳过全部内容
                    buf.skipBytes(buf.readableBytes());
                    return msgs;
                }

                // 需要读取长度
                int needReadLength =
                        length - (KcpConstant.PACKET_TAG_LENGTH + KcpConstant.PACKET_LENGTH);
                // 长度不够不读取
                if (buf.readableBytes() < needReadLength) {
                    buf.resetReaderIndex();
                    // System.out.println("-> 剩余读取长度不足,延迟收取");
                    break;
                }

                // 重置到消息开始位置
                buf.resetReaderIndex();

                // 包钥密校验
                EncryptionAndDecryptionUtil.decryptCustom(buf.nioBuffer(), 0, length, decryptKey);
                // System.out.println("-> 包解密成功");
                // 取得包标记
                tag = buf.readShort();
                // 包长
                length = buf.readInt();
                // 校验和
                short checksum = buf.readShort();

                int checksumContentLength = length - (KcpConstant.PACKET_TAG_LENGTH
                        + KcpConstant.PACKET_LENGTH + KcpConstant.PACKET_CHECK_SUM);

                short calculateCheckSum =
                        CheckSumUtil.calculate(buf.nioBuffer(), 0, checksumContentLength);

                if (calculateCheckSum != checksum) {
                    // System.out.println("-> 数据校验和不匹配");
                    // 跳过当前指令数据内容
                    buf.skipBytes(checksumContentLength);
                }
                // System.out.println("-> 数据校验和成功");

                int bodyLength = length - (KcpConstant.PACKET_TAG_LENGTH + KcpConstant.PACKET_LENGTH
                        + KcpConstant.PACKET_CHECK_SUM + KcpConstant.PACKET_STATUS_CODE + KcpConstant.PACKET_COMMAND_ID);


                SMessage message = null;
                // 指令号
                short messageId = buf.readShort();
                // 状态码
                buf.readInt();
                if (buf.isReadable()) {
                    // 验证校验和是否有效
                    byte[] body = new byte[bodyLength];
                    buf.readBytes(body);
                    message = new SMessage(messageId, body);
                } else {
                    message = new SMessage(messageId, new byte[]{});
                }
                msgs.add(message);    
            }
            // 丢弃已经读取的数据
            if (!msgs.isEmpty()) {
                buf.discardReadBytes();
            }
            return msgs;
        } finally {
            if(buf.refCnt() > 0) {
                buf.release(buf.refCnt());
            }
        }
    }


    public static ByteBuf encoder(SMessage msg, int[] encryptKeys) {
        int dataLength = msg.getData() == null ? 0 : msg.getData().length;
        ByteBuf buf = Unpooled.buffer();
        // 总包长 = 包分割 + 包长字节数 + 校验和字节数 + 指令号字节数 + 状态码 + 内容
        int contentLength = KcpConstant.PACKET_TAG_LENGTH + KcpConstant.PACKET_LENGTH
                + KcpConstant.PACKET_CHECK_SUM + KcpConstant.PACKET_COMMAND_ID + dataLength;
        buf.writeShort(KcpConstant.PACKET_TAG);
        buf.writeInt(contentLength);
        // 记录写的位置
        int backWriterIndex = buf.writerIndex();
        // 校验和占位
        buf.writeShort(0);
        // 消息码
        buf.writeShort(msg.getId());
        // 状态码
//        buf.writeInt(msg.getStatus());
        if (msg.getData() != null)
            buf.writeBytes(msg.getData());

        int lastWriterIndex = buf.writerIndex();

        int checkSum = CheckSumUtil.calculate(buf.nioBuffer(),
                KcpConstant.PACKET_TAG_LENGTH + KcpConstant.PACKET_LENGTH
                        + KcpConstant.PACKET_CHECK_SUM,
                contentLength - KcpConstant.PACKET_TAG_LENGTH + KcpConstant.PACKET_LENGTH
                        + KcpConstant.PACKET_CHECK_SUM);
        // 定位到之前校验和的占位
        buf.writerIndex(backWriterIndex);
        // 写入校验和
        buf.writeShort(checkSum);
        // 恢复正常写入位置
        buf.writerIndex(lastWriterIndex);

        EncryptionAndDecryptionUtil.encryptCustom(buf.nioBuffer(), encryptKeys,
                buf.readableBytes());
        // 返回实际数据场地
        return buf;
    }
}
