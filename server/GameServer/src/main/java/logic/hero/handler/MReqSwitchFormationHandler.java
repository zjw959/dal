package logic.hero.handler;

import org.game.protobuf.c2s.C2SPlayerMsg;

import logic.character.bean.Player;
import logic.hero.HeroManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SPlayerMsg.ReqSwitchFormation.class)
public class MReqSwitchFormationHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) getGameData();
        C2SPlayerMsg.ReqSwitchFormation msg = (C2SPlayerMsg.ReqSwitchFormation) getMessage().getData();
        HeroManager manager = player.getHeroManager();
        manager.reqSwitchFormation(msg);
    }

}
