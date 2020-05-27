package logic.hero.handler;

import org.game.protobuf.c2s.C2SHeroMsg;

import exception.AbstractLogicModelException;
import logic.character.bean.Player;
import logic.constant.EFunctionType;
import logic.constant.GameErrorCode;
import logic.functionSwitch.FunctionSwitchService;
import logic.hero.HeroManager;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SHeroMsg.HeroUpgrade.class)
public class MReqUpgradeHeroHandler extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.HERO_LEVEL_UP)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:HERO_LEVEL_UP");
        }
        Player player = (Player) getGameData();
        C2SHeroMsg.HeroUpgrade msg = (C2SHeroMsg.HeroUpgrade) getMessage().getData();
        HeroManager manager = player.getHeroManager();
        manager.reqUpgradeHero(msg);
    }

}
