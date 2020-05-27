package logic.support;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CFightMsg;

import com.google.protobuf.GeneratedMessage;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import message.MessageResClazz;
import message.SMessage;
import message.SMessageFactory;
import server.ServerConfig;
import utils.ChannelUtils;
import utils.ExceptionEx;

public class MessageUtils {
    public static void send(ChannelHandlerContext ctx, final SMessage sMessage) {
        _send(ctx, sMessage, false, null);
    }

    public static void send(ChannelHandlerContext ctx, final GeneratedMessage.Builder<?> builder,
            SMessageFactory factory) {
        _send(ctx, builder, factory, false);
    }

    private static void _send(ChannelHandlerContext ctx, final SMessage sMessage, boolean isClose,
            String reason) {
        if (ServerConfig.getInstance().getIsTestServer()) {
            if (sMessage.getId() != S2CFightMsg.NotifyNetFrame.MsgID.eMsgID_VALUE && sMessage.getId() != S2CFightMsg.RespFightPong.MsgID.eMsgID_VALUE) {
                LOGGER.debug("msgRes, id:" + sMessage.getId() + ",ctxId:"
                        + (ctx != null ? ctx.hashCode() : ""));
            }
        }

        if (!ChannelUtils.isDisconnectChannel(ctx)) {
            ChannelFuture future = null;
            if (isClose) {
                try {
                    future = ctx.writeAndFlush(sMessage).sync();
                } catch (InterruptedException e) {
                    LOGGER.error("send close Exception." + ExceptionEx.e2s(e));
                }
            } else {
                future = ctx.writeAndFlush(sMessage);
            }
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    sMessage.reuse();
                    if (isClose) {
                        ChannelUtils.closeChannel(ctx,
                                "send Complete close,reason:" + reason == null ? "" : reason);
                    }
                    future.removeListener(this);
                }
            });
            return;
        }
    }
    
    private static void _send(ChannelHandlerContext ctx, final GeneratedMessage.Builder<?> builder,
            SMessageFactory factory, boolean isClose) {
        int msgId = MessageResClazz.getInstance().getMsgId(builder.getClass());
        SMessage sendMsg = factory.fetchSMessage(msgId, builder.build().toByteArray());
        _send(ctx, sendMsg, isClose, null);
    }
    
    private static final Logger LOGGER = Logger.getLogger(MessageUtils.class);
}
