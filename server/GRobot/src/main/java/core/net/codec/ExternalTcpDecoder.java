package core.net.codec;

import java.util.List;

import org.apache.log4j.Logger;

import core.net.message.SMessage;
import core.robot.RobotThread;
import core.robot.RobotThreadFactory;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import net.codec.util.CheckSumUtil;
import net.codec.util.EncryptionAndDecryptionUtil;
import net.codec.util.IChannelConstants;
import net.codec.util.ProtocolConstants;

/**
 * TCP协议解码
 */
public class ExternalTcpDecoder extends ByteToMessageDecoder {
    private static final Logger LOGGER = Logger.getLogger(ExternalTcpDecoder.class);

    /**
     * 获得解密钥密
     * 
     * @param ctx
     * @return
     */
    private int[] getDecryptionKeys(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        // 取得解密密钥
        Attribute<int[]> decKeys = channel.attr(IChannelConstants.DECRYPTION_KEYS);
        int[] dKeys = null;
        if ((dKeys = decKeys.get()) == null) {
            // 获取默认解密密钥
            Attribute<int[]> decryptionKeysAttribute =
                    channel.attr(IChannelConstants.DECRYPTION_KEYS_ATTRIBUTE_KEY);
            int[] decryptionKeys = null;
            if ((decryptionKeys = decryptionKeysAttribute.get()) == null) {
                // 设置默认解密密钥
                decryptionKeys = EncryptionAndDecryptionUtil.getDefaultCustomDecryptionKeys();
                decryptionKeysAttribute.set(decryptionKeys);
            }
            return decryptionKeys;
        }
        return dKeys;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
            throws Exception {
        try {
            if (in.readableBytes() < 6) {
                return;
            }
            // 将当前的readerIndex备份到markReaderIndex中
            in.markReaderIndex();

            // 取得解密钥密
            int[] decryptKey = getDecryptionKeys(ctx);

            int flag = 0;
            int length = 0;

            byte[] validateData = new byte[6];
            in.readBytes(validateData);

            int[] copyDecryptKeys = new int[decryptKey.length];
            System.arraycopy(decryptKey, 0, copyDecryptKeys, 0, copyDecryptKeys.length);
            // 包头解密操作
            validateData = EncryptionAndDecryptionUtil.decryptCustom(validateData, copyDecryptKeys);
            // 包头BUF
            ByteBuf validateBuf = Unpooled.wrappedBuffer(validateData);
            // 取得分包标识
            flag = validateBuf.readShort();
            // 取得包长
            length = validateBuf.readInt();

            // 如果分包标识不同
            if (flag != ProtocolConstants.FLAG) {
                LOGGER.error("decode keys error. close session. ctxId" + ctx.hashCode()
                        + ",channelId:" + ctx.channel().id());
                // 跳过全部内容
                in.skipBytes(in.readableBytes());
                ctx.close();
                return;
            }

            // 如果能读内容不足4个字节
            if (in.readableBytes() < ProtocolConstants.HEAD_WITHOUT_FLAG_AND_LENGTH_SIZE) {
                // 重置读取标识在开始位置
                in.resetReaderIndex();
                return;
            }

            // 做兼容最大允许接受65535字节
            // length = length & 0x00FFFF;
            // 如果包长为0 跳过全部 视为非法包
            if (length <= 0) {
                in.skipBytes(in.readableBytes());
                ctx.close();
                return;
            }

            int bodyLength = length - ProtocolConstants.DEFAULT_HEADER_SIZE;
            int fixLength = length - ProtocolConstants.FLAG_AND_LENGTH_DATA_SIZE;

            // 是否读取完一个完整包内容
            if (fixLength > in.readableBytes()) {
                in.resetReaderIndex();
                return;
            }

            // 消息体整体进行解密操作
            in.resetReaderIndex();

            // in.nioBuffer()返回的ByteBuffer是从当前读索引开始的拷贝
            EncryptionAndDecryptionUtil.decryptCustom(in.nioBuffer(), 0, length, decryptKey);

            // 解析数据
            flag = in.readShort();
            length = in.readInt();

            // 校验和
            short checksum = in.readShort();
            int checksumContentLength = length - ProtocolConstants.CHECKSUM_SKIP_SIZE_FOR_CONTENT;
            bodyLength = length - ProtocolConstants.DEFAULT_HEADER_SIZE - 4;
            short calculateCheckSum =
                    CheckSumUtil.calculate(in.nioBuffer(), 0, checksumContentLength);

            if (calculateCheckSum != checksum) {
                // 跳过当前指令数据内容
                in.skipBytes(checksumContentLength);
            }

            // 指令号
            short msgId = in.readShort();
            in.readInt();
            // 前置步骤已经验证过可读数据长度,并且存在空消息体的消息,这时候的in.isReadable()是不可用的
            // if (in.isReadable()) {
            // 验证校验和是否有效
            byte[] body = new byte[bodyLength];
            in.readBytes(body);

            RobotThread robot;
            if (msgId / 100 == 256) {
                robot = RobotThreadFactory.getFightRobot(ctx.channel().id().asLongText());
            } else {
                robot = RobotThreadFactory.getRobot(ctx.channel().id().asLongText());
            }
            robot.getWindow().addReceiveMsgs();
            robot.addRespMsg(new SMessage(msgId, body, 0));     
        } catch (Exception ex) {
            LOGGER.error(ex, ex);
        }
    }
}
