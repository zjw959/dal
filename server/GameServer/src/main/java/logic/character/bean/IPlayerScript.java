package logic.character.bean;

import script.IScript;
import thread.player.PlayerProcessor;

public abstract class IPlayerScript implements IScript {

    /** 检查清理断开的连接（状态异常） */
    protected abstract void clearClosedSession(Player player);

    protected abstract void tick(Player player, long currentTime);

    public abstract void saveBackTick(PlayerProcessor ps);
    
    /** 必须根据tick处理的bug 尽量使用fixBug中的 **/
    protected abstract void fixTickBug(Player player, long currentTime);

    protected abstract void initFixBug(Player player);
}
