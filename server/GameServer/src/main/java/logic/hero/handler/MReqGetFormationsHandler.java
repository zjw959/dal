package logic.hero.handler;

import org.game.protobuf.c2s.C2SPlayerMsg;

import logic.character.bean.Player;
import logic.hero.HeroManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SPlayerMsg.GetFormations.class)
public class MReqGetFormationsHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) getGameData();
        HeroManager manager = player.getHeroManager();
        manager.reqGetFormations();
    }

}
