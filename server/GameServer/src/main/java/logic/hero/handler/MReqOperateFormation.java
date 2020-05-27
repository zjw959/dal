package logic.hero.handler;

import org.game.protobuf.c2s.C2SPlayerMsg;

import logic.character.bean.Player;
import logic.hero.HeroManager;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SPlayerMsg.OperateFormation.class)
public class MReqOperateFormation extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) getGameData();
        C2SPlayerMsg.OperateFormation msg = (C2SPlayerMsg.OperateFormation) getMessage().getData();
        HeroManager manager = player.getHeroManager();
        manager.reqOperateFormation(msg);
    }

}
