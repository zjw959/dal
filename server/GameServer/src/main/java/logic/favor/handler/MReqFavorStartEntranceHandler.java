package logic.favor.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqStartEntranceEvent;

import data.GameDataManager;
import data.bean.FavorCfgBean;
import logic.favor.structs.FavorDatingConst;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SExtraDatingMsg.ReqStartEntranceEvent.class)
public class MReqFavorStartEntranceHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqFavorStartEntranceHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        ReqStartEntranceEvent msg = (ReqStartEntranceEvent) getMessage().getData();
        // 副本约会
        if (msg.getDatingType() == FavorDatingConst.DATING_TYPE_NOVEL) {
            player.getNovelDatingManager().reqStartEntrance(player, msg.getDatingValue(),
                    msg.getEntranceId());
            return;
        }
        FavorCfgBean favorBean = GameDataManager.getFavorCfgBean(msg.getDatingValue());
        if (favorBean == null) {
            return;
        }
        player.getFavorDatingManager().reqStartEntrance(player, favorBean.getRole(),
                msg.getDatingValue(), msg.getEntranceId());
    }
}
