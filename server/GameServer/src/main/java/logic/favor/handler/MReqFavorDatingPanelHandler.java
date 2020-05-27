package logic.favor.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFavorDatingPanel;

import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SExtraDatingMsg.ReqFavorDatingPanel.class)
public class MReqFavorDatingPanelHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqFavorDatingPanelHandler.class);
    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        ReqFavorDatingPanel msg = (ReqFavorDatingPanel) getMessage().getData();
        player.getFavorDatingManager().reqFavorDatingPanel(player, msg.getRoleId());
    }

}
