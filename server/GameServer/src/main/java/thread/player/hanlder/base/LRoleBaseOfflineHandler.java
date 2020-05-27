package thread.player.hanlder.base;

import logic.character.bean.Player;
import thread.base.GameInnerHandler;

/**
 * 玩家离线handler
 */
public abstract class LRoleBaseOfflineHandler extends GameInnerHandler {
    /**
     * 查询结果
     */
    private Player player;
    /**
     * 请求查询的角色id
     */
    private int senderRoleId;
    /**
     * 被查询的角色id
     */
    private int queryRoleId;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getSenderRoleId() {
        return senderRoleId;
    }

    public void setSenderRoleId(int senderRoleId) {
        this.senderRoleId = senderRoleId;
    }

    public int getQueryRoleId() {
        return queryRoleId;
    }

    public void setQueryRoleId(int queryRoleId) {
        this.queryRoleId = queryRoleId;
    }

}
