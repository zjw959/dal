package net.kcp;

import com.google.protobuf.GeneratedMessage;

import io.netty.buffer.ByteBuf;
import message.MessageResClazz;
import message.SMessage;
import message.SMessageFactory;
import net.kcp.KcpOnUdp;
import net.kcp.constant.KcpConstant;

public class Transfer {
    public static void send(KcpOnUdp kcp, SMessageFactory factory, GeneratedMessage.Builder<?> builder) {
        int msgId = MessageResClazz.getInstance().getMsgId(builder.getClass());
        SMessage sMessage = factory.fetchSMessage(msgId, builder.build().toByteArray());
        ByteBuf buffer = KcpCodec.encoder(sMessage, (int[])kcp.getSessionValueMap().get(KcpConstant.ENCRYPTION_KEYS));
        kcp.send(buffer);
    }
}
