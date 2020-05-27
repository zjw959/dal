package message;

import io.netty.channel.ChannelHandlerContext;
import thread.BaseHandler;
import thread.IAfterExecHanler;

/**
 * 所有客户端消息上行的入口
 * 
 * 该类在运行时可被替换的(可热更新)
 */
public abstract class MessageHandler extends BaseHandler implements IAfterExecHanler {
    /** Handler所携带的消息 **/
    protected RMessage message;
    /** 会话上下文 **/
    protected ChannelHandlerContext ctx;

    /** gameData **/
    private Object gameData = null;

    public void setGameData(Object gameData) {
        this.gameData = gameData;
    }

    public Object getGameData() {
        return gameData;
    }

    public RMessage getMessage() {
        return message;
    }

    public void setMessage(RMessage message) {
        this.message = message;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void afterAction() {
        this.gameData = null;
        if (message != null) {
            MessageHandlerFactory.getInstance().recycleHandler(this);
        }
    }
}
