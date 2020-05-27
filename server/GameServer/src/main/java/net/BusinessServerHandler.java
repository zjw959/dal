package net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.character.handler.LDisconnectedHandler;
import logic.constant.ConstDefine;
import logic.login.service.LoginService;
import logic.login.struct.ChannelInfo;
import message.MessageHandler;
import net.codec.util.EncryptionAndDecryptionUtil;
import net.codec.util.IChannelConstants;

import org.apache.log4j.Logger;

import server.GameServer;
import server.MessageDispatchService;
import thread.player.PlayerProcessorManager;
import utils.ChannelUtils;
import utils.ExceptionEx;


/**
 * netty到业务逻辑分发 注意,该类为shareable对象
 */
@Sharable
public class BusinessServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 接收消息处理
     * 
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (GameServer.getInstance().isShutDown()) {
            LOGGER.info("server is shutdown, discard message.");
            return;
        }

        if (msg instanceof MessageHandler) {
            MessageHandler messageHandler = (MessageHandler) msg;
            messageHandler.setCtx(ctx);
            MessageDispatchService.getInstance().dispatchMessage(messageHandler);
        } else {
            ByteBuf buf = (ByteBuf) msg;
            int msgId = buf.readInt();
            LOGGER.error("未处理的网络消息: " + msgId + " " + ChannelUtils.logInfo(ctx));
        }
    }

    /**
     * 连接建立处理
     * 
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (GameServer.getInstance().isShutDown()) {
            ctx.close();
            return;
        }
        ctx.channel().attr(AttributeKeys.CONNECT_TIME).set(System.currentTimeMillis());
        ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).set(LoginService.CTXState.CONNECT);
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
        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "channelActive." + ChannelUtils.logInfo(ctx));
    }

    /**
     * 连接断开处理
     * 
     * @param ctx
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        try {
            if (ctx == null) {
                LOGGER.error(ConstDefine.LOG_LOGIN_PREFIX + "channel disconnected. ctx is null");
                return;
            }

            LoginService.getInstance().ctxs.remove(ctx.hashCode());

            String onlineTime = "";
            LoginService.CTXState status = ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).get();
            if (!GameServer.getInstance().isShutDown() && status == null) {
                LOGGER.error(ConstDefine.LOG_LOGIN_PREFIX
                        + "channel disconnected. can not find channel state. can not do offline");
                return;
            }
            Long begin = ctx.channel().attr(AttributeKeys.CONNECT_TIME).get();
            if (begin != null) {
                onlineTime = " onlinetime:" + (System.currentTimeMillis() - begin) / 1000 + " sec";
            }
            LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "channel disconnected."
                    + ChannelUtils.logInfo(ctx) + onlineTime);

            // sdk返回则会存在
            ChannelInfo info = ctx.channel().attr(AttributeKeys.CHANNEL_INFO).get();
            if (info != null) {
                LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "channel disconnected. hasinfo. state:"
                        + status + ChannelUtils.logInfo(ctx));

                Player player = PlayerManager.getPlayerByCtx(ctx);
                if (status == LoginService.CTXState.NORMAL) {
                    if (player != null) {
                        if (!ctx.equals(player.getCtx())) {
                            LOGGER.error(ConstDefine.LOG_LOGIN_PREFIX
                                    + "channel disconnected. ctx is not equals."
                                    + ChannelUtils.logInfo(ctx) + player.logInfo());
                        }

                        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX
                                + "channel disconnected. hasplayer, begin logout"
                                + player.logInfo());
                        PlayerProcessorManager.getInstance().addPlayerHandler(player.getLineIndex(),
                                new LDisconnectedHandler(ctx));
                    } else {
                        LOGGER.error(ConstDefine.LOG_LOGIN_PREFIX
                                + "channel disconnected. ctx is noraml,but player is null. state:"
                                + status + ChannelUtils.logInfo(ctx));
                    }
                } else {
                    if (player != null) {
                        LOGGER.error(ConstDefine.LOG_LOGIN_PREFIX
                                + "channel disconnected. ctx is not normal,but player is not null. state:"
                                + status + ChannelUtils.logInfo(ctx) + player.logInfo());
                    }
                }
            } else if (status != LoginService.CTXState.CONNECT
                    && status != LoginService.CTXState.VERIFY_ACCOUNT_FINISH
                    && status != LoginService.CTXState.SKIP_VERIFY_DB
                    && status != LoginService.CTXState.BE_REPLACE_ACCOUNT) {
                if (!GameServer.getInstance().isShutDown()) {
                    LOGGER.error(ConstDefine.LOG_LOGIN_PREFIX
                            + "channel disconnected. has not info. state:" + status
                            + ChannelUtils.logInfo(ctx));
                }
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
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
        // if (GameServer.getInstance().isIDEMode()) {
        // LOGGER.error(ConstDefine.LOG_LOGIN_PREFIX + "channel disconnected. exceptionCaught."
        // + ExceptionEx.t2s(cause));
        // } else {
        LOGGER.info(
                ConstDefine.LOG_LOGIN_PREFIX + "channel exceptionCaught."
                        + ChannelUtils.logInfo(ctx));
        // }
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
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "channel disconnected. triggered idle."
                        + ChannelUtils.logInfo(ctx));
                ctx.close();
                return;
            }
        }
        super.userEventTriggered(ctx, evt);
    }


    private static final Logger LOGGER = Logger.getLogger(BusinessServerHandler.class);
}
