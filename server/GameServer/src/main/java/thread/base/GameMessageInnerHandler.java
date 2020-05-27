package thread.base;


/**
 * 需要将异常情况告知客户端的服务器内部逻辑handler
 */
public abstract class GameMessageInnerHandler extends GameInnerHandler {
    /** 玩家id */
    protected int playerId;
    /** 消息号id */
    protected int rmsgId;

    public GameMessageInnerHandler(int playerId, int rmsgId) {
        this.playerId = playerId;
        this.rmsgId = rmsgId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getRmsgId() {
        return rmsgId;
    }

}
