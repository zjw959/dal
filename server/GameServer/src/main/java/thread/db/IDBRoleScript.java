package thread.db;

import java.util.List;

import db.game.bean.PlayerDBBean;
import io.netty.channel.ChannelHandlerContext;
import logic.character.bean.Player;
import script.IScript;
import thread.base.GameBaseProcessor;
import thread.base.GameInnerHandler;
import thread.player.hanlder.base.LRoleBaseOfflineHandler;

public abstract class IDBRoleScript implements IScript {
    public abstract void updateRole(PlayerDBBean playerDBBean, GameBaseProcessor cbProce,
            GameInnerHandler cbHandler);

    public abstract void updateRoleBatch(List<PlayerDBBean> roleBeans, boolean isOffLine);

    public abstract void roleInfo(LRoleBaseOfflineHandler handler, GameBaseProcessor cbProce,
            boolean isView);

    public abstract void DBLoginDataVerify(ChannelHandlerContext ctx, boolean isReconnect);

    public abstract void createPlayer(ChannelHandlerContext ctx, Player player);
}
