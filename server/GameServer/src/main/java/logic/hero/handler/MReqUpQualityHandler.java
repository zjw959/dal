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

@MHandler(messageClazz = C2SHeroMsg.ReqUpQuality.class)
public class MReqUpQualityHandler extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.HERO_UP_QUALITY)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:HERO_UP_QUALITY");
        }
        Player player = (Player) getGameData();
        C2SHeroMsg.ReqUpQuality msg = (C2SHeroMsg.ReqUpQuality) getMessage().getData();
        HeroManager manager = player.getHeroManager();
        manager.reqUpQuality(msg);
    }

}
