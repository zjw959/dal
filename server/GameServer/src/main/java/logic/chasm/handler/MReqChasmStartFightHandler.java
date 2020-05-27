package logic.chasm.handler;

import org.game.protobuf.c2s.C2SChasmMsg;
import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SChasmMsg.ReqChasmStartFight.class)
public class MReqChasmStartFightHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) getGameData();
        LogicScriptsUtils.getChasmScript().reqChasmStartFight(player);
    }
    
}
