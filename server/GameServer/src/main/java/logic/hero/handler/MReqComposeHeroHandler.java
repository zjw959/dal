package logic.hero.handler;

import org.game.protobuf.c2s.C2SHeroMsg;

import exception.AbstractLogicModelException;
import logic.character.bean.Player;
import logic.hero.HeroManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SHeroMsg.HeroCompose.class)
public class MReqComposeHeroHandler extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        Player player = (Player) getGameData();
        C2SHeroMsg.HeroCompose msg = (C2SHeroMsg.HeroCompose) getMessage().getData();
        HeroManager manager = player.getHeroManager();
        manager.reqComposeHero(msg);
    }
}
