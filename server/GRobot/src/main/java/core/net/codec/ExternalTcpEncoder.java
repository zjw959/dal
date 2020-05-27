package core.net.codec;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SFightMsg;
import org.game.protobuf.c2s.C2SLoginMsg;

import core.net.message.SMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import net.codec.util.CheckSumUtil;
import net.codec.util.EncryptionAndDecryptionUtil;
import net.codec.util.IChannelConstants;
import net.codec.util.ProtocolConstants;

/**
 * TCP协议编码
 * */
public class ExternalTcpEncoder extends MessageToByteEncoder<Object> {
	private static final Logger LOGGER = Logger
			.getLogger(ExternalTcpEncoder.class);

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception {
		if (msg == null) {
			LOGGER.error("待发送的消息是空的");
			return;
		}

		if (msg instanceof SMessage) {
			SMessage sMsg = (SMessage) msg;
			byte[] body = sMsg.getData();
			int bodyLen = body == null ? 0 : body.length;
			int contentLength = bodyLen + ProtocolConstants.DEFAULT_HEADER_SIZE;
			out.writeShort(ProtocolConstants.FLAG);
			out.writeInt(contentLength);
			int backWriterIndex = out.writerIndex();
			out.writeShort(0);
			out.writeShort(sMsg.getId());
			if (body != null) {
				out.writeBytes(body);
			}
			int lastWriterIndex = out.writerIndex();
			int checkSum = CheckSumUtil.calculate(out.nioBuffer(),
					ProtocolConstants.CHECKSUM_SKIP_SIZE_FOR_CONTENT,
					contentLength
							- ProtocolConstants.CHECKSUM_SKIP_SIZE_FOR_CONTENT);
			out.writerIndex(backWriterIndex);
			out.writeShort(checkSum);
			out.writerIndex(lastWriterIndex);

			Attribute<int[]> attach = ctx.channel().attr(
                    IChannelConstants.ENCRYPTION_KEYS);
			int[] encryptKeys = attach.get();
			EncryptionAndDecryptionUtil.encryptCustom(out.nioBuffer(),
					encryptKeys, out.readableBytes());
			
			//登陆
			if(sMsg.getId() == C2SLoginMsg.EnterGame.MsgID.eMsgID_VALUE) {
			    ctx.channel().attr(IChannelConstants.DECRYPTION_KEYS).set(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
			    ctx.channel().attr(IChannelConstants.ENCRYPTION_KEYS).set(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
			}
			if(sMsg.getId() == C2SFightMsg.ReqEnterFight.MsgID.eMsgID_VALUE) {
			    ctx.channel().attr(IChannelConstants.DECRYPTION_KEYS).set(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
                ctx.channel().attr(IChannelConstants.ENCRYPTION_KEYS).set(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
			}
		} else {
			LOGGER.error("从服务器发送的消息类型未知:" + msg.getClass().getName());
		}
	}

}
