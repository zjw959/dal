package logic.chasm.handler;

import org.game.protobuf.c2s.C2STeamMsg;
import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2STeamMsg.ReqCancelMatch.class)
public class MReqCancelMatchHandler extends MessageHandler {
    @Override
    public void action() throws Exception {
        Player player = (Player) getGameData();
        LogicScriptsUtils.getChasmScript().reqCancelMatch(player);
    }

}
