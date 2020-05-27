package net.tcp;

import java.net.InetSocketAddress;
import org.apache.log4j.Logger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import logic.chasm.InnerHandler.LChannelInactiveHandler;
import message.MessageHandler;
import net.Session;
import net.codec.util.EncryptionAndDecryptionUtil;
import net.codec.util.IChannelConstants;
import room.FightRoom;
import room.FightRoomManager;
import server.FightServer;
import thread.FightRoomProcessorManager;
import utils.ExceptionEx;

/**
 * netty到业务逻辑分发 注意,该类为shareable对象
 */
@Sharable
public class BusinessServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = Logger.getLogger(BusinessServerHandler.class);

    /**
     * 接收消息处理
     * 
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (FightServer.getInstance().isShutDown()) {
                LOGGER.info("server is shutdown, discard message.");
                return;
            }

            if (msg instanceof MessageHandler) {
                MessageHandler messageHandler = (MessageHandler) msg;
                messageHandler.setCtx(ctx);
                TcpMessageDispatchService.getInstance().dispatchMessage(messageHandler);
            } else {
                ByteBuf buf = (ByteBuf) msg;
                int msgId = buf.readInt();
                LOGGER.error("未处理的网络消息: " + msgId);
            }
        } catch (Exception e) {
            LOGGER.error("消息转发异常:" + ExceptionEx.e2s(e));
        }
    }

    /**
     * 连接建立处理
     * 
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (FightServer.getInstance().isShutDown()) {
            ctx.close();
            return;
        }
        // 取得解密钥匙
        Attribute<int[]> decryptionKeysAttribute =
                ctx.channel().attr(IChannelConstants.DECRYPTION_KEYS_ATTRIBUTE_KEY);
        int[] decryptionKeys = null;
        if ((decryptionKeys = decryptionKeysAttribute.get()) == null) {
            // 设置默认解密密钥
            decryptionKeys = EncryptionAndDecryptionUtil.getDefaultCustomDecryptionKeys();
            decryptionKeysAttribute.set(decryptionKeys);
        }
        // 取得加密钥匙
        int[] encryptionKeys = EncryptionAndDecryptionUtil.getDefaultCustomEncryptionKeys();
        ctx.channel().attr(IChannelConstants.DECRYPTION_KEYS).set(decryptionKeys);
        ctx.channel().attr(IChannelConstants.ENCRYPTION_KEYS).set(encryptionKeys);
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        LOGGER.info("客户端地址:" + clientIP);
    }

    /**
     * 连接断开处理
     * 
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIP = insocket.getAddress().getHostAddress();
        LOGGER.info(clientIP + "断开连接");
        Channel channel = ctx.channel();
        Long fightRoomId = channel.attr(logic.constant.IChannelConstants.FIGHT_ROOM).get();
        if (fightRoomId != null) {
            FightRoom fightRoom = FightRoomManager.getRoomByRoomId(fightRoomId);
            if (fightRoom != null) {
                Session session = new Session();
                session.setKcp(false);
                session.setCtx(ctx);
                session.setFightRoom(fightRoom);
                FightRoomProcessorManager.getInstance().addCommand(fightRoom.getProcessorId(),
                        new LChannelInactiveHandler(session));
            }
        }
    }

    /**
     * 内部异常处理
     * 
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }

    /**
     * 心跳检测
     * 
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }
}
