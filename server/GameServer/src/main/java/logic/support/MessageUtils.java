package logic.support;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CLoginMsg;
import org.game.protobuf.s2c.S2CPlayerMsg.ResTipInfo;

import com.google.protobuf.GeneratedMessage;

import exception.AbstractLogicModelException;
import exception.LogicModelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.ENotifyType;
import message.MessageResClazz;
import message.SMessage;
import message.SMessageFactory;
import server.GameServer;
import utils.ChannelUtils;
import utils.ExceptionEx;


/**
 * message send utils
 */
public class MessageUtils {
    public static void send(ChannelHandlerContext ctx, final SMessage sMessage) {
        _send(ctx, sMessage, false, null);
    }

    public static void send(ChannelHandlerContext ctx, final GeneratedMessage.Builder<?> builder,
            SMessageFactory factory) {
        _send(ctx, builder, factory, false, null);
    }

    public static void send(Player player, final GeneratedMessage.Builder<?> builder) {
        _send(player, builder, player.getSMsgFatory(), false, null);
    }

    /**
     * 返回空消息体
     */
    public static AbstractLogicModelException returnEmptyBody() {
        throw new LogicModelException(null, 0, null);
    }

    /**
     * 返回错误编码给客户端(前提条件不满足)
     */
    public static AbstractLogicModelException throwCondtionError(int errCode, String... info) {
        StringBuilder sb = new StringBuilder();
        if (info.length != 0) {
            for (int j = 0; j < info.length; j++) {
                sb.append(info[j]);
            }
        }
        throw new LogicModelException(ConstDefine.LOG_ERROR_CONDITION_PREFIX, errCode,
                sb.toString());
    }

    /**
     * 返回错误编码给客户端(程序逻辑异常)
     */
    public static AbstractLogicModelException throwLogicError(int errCode, Exception e,
            String... info) {
        StringBuilder sb = new StringBuilder();
        if (info.length != 0) {
            for (int j = 0; j < info.length; j++) {
                sb.append(info[j]);
            }
        }
        if (e != null) {
            sb.append("\n").append(ExceptionEx.e2s(e));
        }
        throw new LogicModelException(ConstDefine.LOG_ERROR_LOGIC_PREFIX, errCode, sb.toString());
    }

    public static AbstractLogicModelException throwConfigError(int errCode, String... info) {
        StringBuilder sb = new StringBuilder();
        if (info.length != 0) {
            for (int j = 0; j < info.length; j++) {
                sb.append(info[j]);
            }
        }
        throw new LogicModelException(ConstDefine.LOG_ERROR_CONFIG_PREFIX, errCode, sb.toString());
    }

    /**
     * 一般提示（无需确认）
     */
    public static void sendTip(Player player, String content) {
        sendPrompt(player, content, ENotifyType.TIP);
    }

    /**
     * 弹框提示（确认后关闭）
     */
    public static void sendPop(Player player, String content) {
        sendPrompt(player, content, ENotifyType.POPUP);
    }

    /**
     * 弹框提示（确认后返回登录界面）
     */
    public static void sendPopRelogin(Player player, String content) {
        sendPrompt(player, content, ENotifyType.POPUP_RELOGIN);
    }

    /**
     * 退出游戏
     */
    public static void sendPopQuitGame(Player player, String content) {
        sendPrompt(player, content, ENotifyType.POPUP_QUIT);
    }

    /**
     * 提示
     */
    public static void sendPrompt(Player player, String content, ENotifyType type) {
        ResTipInfo.Builder builder = ResTipInfo.newBuilder();
        builder.setStatus(type.getVal());
        builder.setContent(content);
        send(player, builder);
    }

    /**
     * 提示
     */
    public static void sendPrompt(ChannelHandlerContext ctx, SMessageFactory factory,
            String content, ENotifyType type) {
        ResTipInfo.Builder builder = ResTipInfo.newBuilder();
        builder.setStatus(type.getVal());
        builder.setContent(content);
        send(ctx, builder, factory);
    }

    public static String _logError(Player player, String logType, int len,
            boolean isLogicException, String... args) {
        StringBuilder sb = new StringBuilder();
        if (args.length != 0) {
            if (logType == null) {
                logType = "";
            }
            for (int j = 0; j < args.length; j++) {
                sb.append(args[j]);
            }
            LOGGER.error(logType + sb.toString() + (player == null ? "" : player.logInfo())
                    + (isLogicException ? "" : ExceptionEx.currentThreadTraces(len)));
        }

        if (!GameServer.getInstance().isTestServer()) {
            return null;
        }
        return sb.toString();
    }

    private static void _send(ChannelHandlerContext ctx, final GeneratedMessage.Builder<?> builder,
            SMessageFactory factory, boolean isClose, String reason) {
        int msgId = MessageResClazz.getInstance().getMsgId(builder.getClass());
        SMessage sendMsg = factory.fetchSMessage(msgId, builder.build().toByteArray());
        _send(ctx, sendMsg, isClose, reason);
    }

    private static void _send(Player player, final GeneratedMessage.Builder<?> builder,
            SMessageFactory factory, boolean isColse, String reason) {
        if (player.getCtx() == null) {
            return;
        }
        int msgId = MessageResClazz.getInstance().getMsgId(builder.getClass());
        SMessage sMessage = factory.fetchSMessage(msgId, builder.build().toByteArray());
        _send(player.getCtx(), sMessage, isColse, reason);
    }

    private static void _send(ChannelHandlerContext ctx, final SMessage sMessage, boolean isClose,
            String reason) {
        if (GameServer.getInstance().isTestServer()
                && !GameServer.getInstance().isRootDrangServer()) {
            if (sMessage.getId() != S2CLoginMsg.Pong.MsgID.eMsgID_VALUE) {
                LOGGER.debug("msgRes, id:" + sMessage.getId() + ", status:" + sMessage.getStatus()
                        + ", ctxId:"
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

    private static final Logger LOGGER = Logger.getLogger(MessageUtils.class);
}
