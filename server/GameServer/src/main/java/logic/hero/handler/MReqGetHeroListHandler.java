package logic.hero.handler;

import org.game.protobuf.c2s.C2SHeroMsg;

import exception.AbstractLogicModelException;
import logic.character.bean.Player;
import logic.hero.HeroManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SHeroMsg.GetHeros.class)
public class MReqGetHeroListHandler extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        Player player = (Player) getGameData();
        HeroManager manager = player.getHeroManager();
        manager.reqGetHeroList();
    }

}
