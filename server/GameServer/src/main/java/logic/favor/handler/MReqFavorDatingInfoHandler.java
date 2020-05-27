package logic.favor.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqExtraDatingInfo;

import data.GameDataManager;
import data.bean.FavorCfgBean;
import logic.favor.structs.FavorDatingConst;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SExtraDatingMsg.ReqExtraDatingInfo.class)
public class MReqFavorDatingInfoHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqFavorDatingInfoHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        ReqExtraDatingInfo msg = (ReqExtraDatingInfo) getMessage().getData();
        // 副本约会
        if (msg.getDatingType() == FavorDatingConst.DATING_TYPE_NOVEL) {
            player.getNovelDatingManager().reqNovelDatingInfo(player, msg.getDatingValue());
            return;
        }
        // 主线约会
        FavorCfgBean favorBean = GameDataManager.getFavorCfgBean(msg.getDatingValue());
        if (favorBean == null) {
            return;
        }
        player.getFavorDatingManager().reqFavorDatingInfo(player, favorBean.getRole(),
                msg.getDatingValue());
    }

}
