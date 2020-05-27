package thread.base;

import io.netty.channel.ChannelHandlerContext;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.GameErrorCode;
import logic.support.MessageUtils;
import message.MessageHandler;
import message.RMessage;
import message.SMessage;
import message.SMessageFactory;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SLoginMsg;

import server.GameServer;
import thread.BaseHandler;
import thread.BaseProcessor;
import utils.ExceptionEx;
import exception.AbstractLogicModelException;

/**
 * 游戏内基本逻辑线程
 * 
 * 处理消息与游戏内部逻辑的线程池
 * 
 * 登陆与玩家线程使用
 */
public abstract class GameBaseProcessor extends BaseProcessor {
    private static final Logger LOGGER = Logger.getLogger(GameBaseProcessor.class);

    private final SMessageFactory sMsgFactory = new SMessageFactory(500);

    public GameBaseProcessor(String name) {
        super(name);
    }

    public GameBaseProcessor(String simpleName, int corePoolSize, int maxPoolSize) {
        super(simpleName, corePoolSize, maxPoolSize);
    }

    /**
     * 向该线程执投递MessageHandler
     * 
     * @param messageHandler 消息协议的执行单元
     */
    public final void executeMessageHandler(MessageHandler messageHandler) {
        if (GameServer.getInstance().isIDEMode()) {
            if (!messageHandler.getClass().getSimpleName().startsWith("M")) {
                LOGGER.error("Breached Message Handler Naming Rules Will Be Ignored . For The Correct Naming Rules: Name StartsWith M."
                        + "\n Look up : " + messageHandler.getClass().getName());
                return;
            }
        }
        super.executeHandler(messageHandler);
    }

    // protected void execHandler(BaseHandler handler) {
    // super.execHandler(handler);
    // }

    /**
     * 向线程池投递逻辑handler
     * 
     * @param gameBaseHandler 服务器内存逻辑执行单元
     */
    public final void executeInnerHandler(GameInnerHandler gameBaseHandler) {
        if (GameServer.getInstance().isIDEMode()) {
            if (!gameBaseHandler.getClass().getSimpleName().startsWith("L")) {
                LOGGER.error("Breached Logic Handler  Naming Rules Will Be Ignored . For The Correct Naming Rules: Name StartsWith L."
                        + "\n Look up : " + gameBaseHandler.getClass().getName());
                return;
            }
        }
        super.executeHandler(gameBaseHandler);
    }

    @Override
    public boolean gameBeforeHandler(BaseHandler handler) {
        try {
            if (handler instanceof MessageHandler) {
                MessageHandler messageHandler = (MessageHandler) handler;
                short messageID = messageHandler.getMessage().getId();
                if (messageID == C2SLoginMsg.EnterGame.MsgID.eMsgID_VALUE
                        || messageID == C2SLoginMsg.ReqReconnect.MsgID.eMsgID_VALUE
                        || messageID == C2SLoginMsg.Ping.MsgID.eMsgID_VALUE) {
                    return true;
                }
                ChannelHandlerContext ctx = ((MessageHandler) handler).getCtx();
                if (ctx != null) {
                    Player player = PlayerManager.getPlayerByCtx(ctx);
                    messageHandler.setGameData(player);
                    if (player == null) {
                        LOGGER.warn("message can not find player. msgID:"
                                + messageHandler.getMessage().getId() + ",hanlderName:"
                                + this.getName());
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
            return false;
        }
        return true;
    }

    @Override
    public void gameExceptionHandler(BaseHandler handler, Exception e) {
        try {
            Player player = null;
            ChannelHandlerContext ctx = null;
            int rmsgId = 0;
            int code = GameErrorCode.PLAYER_DATA_ERROR;
            // 需要通知客户端的，必须在对应handler能取到ctx和rmsgId
            if (handler instanceof MessageHandler) {
                MessageHandler msgHandler = (MessageHandler) handler;
                RMessage rmsg = msgHandler.getMessage();
                if (rmsg != null) {
                    rmsgId = rmsg.getId();// 获取到消息号id
                }
                ctx = msgHandler.getCtx();
                if (ctx != null) {
                    player = PlayerManager.getPlayerByCtx(ctx);
                }
                // 清空
                msgHandler.setGameData(null);
            } else if (handler instanceof GameMessageInnerHandler) {
                GameMessageInnerHandler mhandler = (GameMessageInnerHandler) handler;
                rmsgId = mhandler.getRmsgId();// 获取到消息号id
                player = PlayerManager.getPlayerByPlayerId(mhandler.getPlayerId());
                if (player != null && player.isOnline()) {
                    ctx = player.getCtx();
                }
            }

            if (e instanceof AbstractLogicModelException) {// 返回特定错误码给客户端,因为要兼容以前的DAL消息结构
                code = ((AbstractLogicModelException) e).getCode();
                String prefix = ((AbstractLogicModelException) e).getLogPrefix();
                // 记录异常日志
                if (code != 0) {
                    String info =
                            "MessageHandler[" + handler + "] -> returnCode=" + code + ":"
                                    + e.getMessage() + "\n";
                    if (ConstDefine.LOG_ERROR_CONDITION_PREFIX.equals(prefix)) {
                        // 前提条件错误可能会经常出现，因为有些判定客户端没有做，所以不打印堆栈信息，要定位具体问题时可根据MessageHandler及对应code来分析
                        LOGGER.info(prefix + info + (player == null ? "" : player.logInfo()));
                    } else {
                        // 非程序逻辑异常，补全异常堆栈信息
                        if (!ConstDefine.LOG_ERROR_LOGIC_PREFIX.equals(prefix)) {
                            info += ExceptionEx.e2s(e);
                        }
                        MessageUtils._logError(player, prefix, 0, true, info);
                    }
                }
            } else {
                // 记录错误日志
                MessageUtils._logError(player, ConstDefine.LOG_ERROR_EXCEPTION_PREFIX, 0, true,
                        ExceptionEx.e2s(e));
            }
            if (ctx != null && rmsgId > 0) {
                SMessage msg = new SMessage(rmsgId, code);
                MessageUtils.send(ctx, msg);
            }
        } catch (Exception e2) {
            LOGGER.error(ExceptionEx.e2s(e2));
        }
    }

    public SMessageFactory getSMsgFactory() {
        return sMsgFactory;
    }
}
