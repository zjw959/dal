package logic.hero.handler;

import org.game.protobuf.c2s.C2SPlayerMsg;

import logic.character.bean.Player;
import logic.hero.HeroManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SPlayerMsg.ChangeHelpFightHero.class)
public class MReqChangeHelpFightHeroHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) getGameData();
        C2SPlayerMsg.ChangeHelpFightHero msg = (C2SPlayerMsg.ChangeHelpFightHero) getMessage().getData();
        HeroManager heroManager = player.getHeroManager();
        heroManager.reqChangeHelpFightHero(msg);
    }

}
