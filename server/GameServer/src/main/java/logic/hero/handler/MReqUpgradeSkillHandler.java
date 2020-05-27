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

@MHandler(messageClazz = C2SHeroMsg.ReqUpgradeSkill.class)
public class MReqUpgradeSkillHandler extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.ANGEL)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:ANGEL");
        }
        Player player = (Player) getGameData();
        C2SHeroMsg.ReqUpgradeSkill msg = (C2SHeroMsg.ReqUpgradeSkill) getMessage().getData();
        HeroManager manager = player.getHeroManager();
        manager.reqUpgradeSkill(msg);
    }

}
