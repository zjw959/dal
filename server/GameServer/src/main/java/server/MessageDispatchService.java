package server;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.c2s.C2SLoginMsg;

import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.gloabl.GlobalService;
import logic.login.service.LoginService;
import message.MessageHandler;
import message.MessageHandlerFactory;
import thread.base.GameInnerHandler;
import thread.player.PlayerProcessorManager;
import thread.sys.base.SysService;
import utils.ChannelUtils;
import utils.ExceptionEx;
import utils.MessageIdParser;

/**
 * 消息分发service
 */
public class MessageDispatchService extends SysService {
    private static final Logger LOGGER = Logger.getLogger(MessageDispatchService.class);

    /**
     * 服务器功能枚举
     */
    public static class FunEnum {
        /** 核心功能 */
        public static final int CORE_FUNCTION = 1;
    }

    public void dispatchMessage(MessageHandler messageHandler) {
        if (!GameServer.getInstance().isShutDown()) {
            this.getProcess().executeInnerHandler(new LMessageDispathHandler(messageHandler));
        }
    }

    class LMessageDispathHandler extends GameInnerHandler {
        private MessageHandler messageHandler;

        public LMessageDispathHandler(MessageHandler messageHandler) {
            this.messageHandler = messageHandler;
        }

        @Override
        public void action() throws Exception {

            if (messageHandler == null) {
                return;
            }

            if (messageHandler.getMessage() != null) {
                short msgId = messageHandler.getMessage().getId();
                byte fun = MessageIdParser.getFunction(msgId);
                if (GameServer.getInstance().isTestServer()) {
                    // 跳过心跳功能打印
                    if (msgId != C2SLoginMsg.Ping.MsgID.eMsgID_VALUE) {
                        Player player = PlayerManager.getPlayerByCtx(messageHandler.getCtx());
                        LOGGER.debug("msgReq{id:" + msgId + ",ctxId:"
                                + messageHandler.getCtx().hashCode() + ",name: "
                                + messageHandler.getClass().getName() + "." + "("
                                + messageHandler.getClass().getSimpleName() + ".java:1) "
                                + (player == null ? ""
                                        : ",player:" + player.getUserName() + " "
                                                + player.getPlayerName())
                                + "}");
                    }
                }
                switch (fun) {
                    case FunEnum.CORE_FUNCTION:
                        if (msgId == C2SLoginMsg.EnterGame.MsgID.eMsgID_VALUE
                                || msgId == C2SLoginMsg.ReqReconnect.MsgID.eMsgID_VALUE) {
                            // 登录、重连
                            _doLogin(messageHandler);
                        } else if (msgId == C2SLoginMsg.Ping.MsgID.eMsgID_VALUE) {
                            // 心跳
                            doHeartbeat(messageHandler);
                        } else if (msgId == C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE) {
                            _doReqSendChat(messageHandler);
                        } else {
                            _doPlayer(messageHandler);
                        }
                        break;
                    default:
                        _doPlayer(messageHandler);
                        break;
                }
            } else {
                LOGGER.error("未分发处理的Handler: " + messageHandler + ",ctx:"
                        + ChannelUtils.logInfo(messageHandler.getCtx()));
            }
        }
    };

    /**
     * 心跳处理
     * 
     * @param handler
     */
    private void doHeartbeat(MessageHandler handler) {
        try {
            handler.action();
        } catch (Exception e) {
            LOGGER.error("doHeartbeat: " + ExceptionEx.e2s(e));
        }
        MessageHandlerFactory.getInstance().recycleHandler(handler);
    }

    /**
     * 向player线程池中投递消息handler
     * 
     * @param messageHandler
     */
    private void _doPlayer(MessageHandler messageHandler) {
        PlayerProcessorManager.getInstance().addMsgHandler(messageHandler);
    }


    /**
     * 向player线程池中投递消息handler
     * 
     * @param messageHandler
     * @throws Exception
     */
    private void _doReqSendChat(MessageHandler messageHandler) throws Exception {
        messageHandler.action();
    }

    /**
     * 向登录线程投递登录消息handler
     * 
     * @param messageHandler
     */
    private void _doLogin(MessageHandler messageHandler) {
        LoginService.getInstance().getProcess().executeMessageHandler(messageHandler);
    }

    /**
     * 向系统线程投递登录消息handler
     * 
     * @param messageHandler
     */
    private void _doGlobal(MessageHandler messageHandler) {
        GlobalService.getInstance().getProcess().executeMessageHandler(messageHandler);
    }

    public static MessageDispatchService getInstance() {
        return Singleton.INSTANCE.getProcessor();
    }

    /**
     * 用枚举来实现单例
     */
    private enum Singleton {
        INSTANCE;
        MessageDispatchService processor;

        Singleton() {
            this.processor = new MessageDispatchService();
        }

        MessageDispatchService getProcessor() {
            return processor;
        }
    }
}
