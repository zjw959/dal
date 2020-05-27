package logic.favor.handler;

import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFavorDatingTestInfo;
import org.game.protobuf.s2c.S2CExtraDatingMsg.ResFavorDatingTestInfo;

import logic.favor.FavorDatingManager;
import logic.favor.structs.FavorDatingData;
import logic.msgBuilder.FavorDatingBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFavorDatingTestInfo.class)
public class MReqTestFavorInfoHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            return;
        }
        ReqFavorDatingTestInfo msg = (ReqFavorDatingTestInfo) getMessage().getData();
        int roleId = msg.getRoleId();
        int favorDatingId = msg.getFavorDatingId();
        if (player.getRoleManager().getRole(roleId) == null) {
            return;
        }
        FavorDatingManager mng = player.getFavorDatingManager();
        FavorDatingData favorData = mng.getFavorDatingData(player, roleId, favorDatingId, true);
        ResFavorDatingTestInfo.Builder builder = ResFavorDatingTestInfo.newBuilder();
        builder.addAllSignList(favorData.getEventFlag());
        builder.addAllItems(FavorDatingBuilder.getBag(favorData));
        builder.addAllQualityInfo(FavorDatingBuilder.getQuality(roleId, player));
        MessageUtils.send(player, builder);
        // FavorDatingBuilder.
        // ResFavorDatingTestInfo.Builder builder = ResFavorDatingTestInfo.newBuilder();
    }

}
