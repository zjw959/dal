package logic.hero.handler;

import org.game.protobuf.c2s.C2SHeroMsg;

import logic.character.bean.Player;
import logic.constant.EFunctionType;
import logic.constant.GameErrorCode;
import logic.functionSwitch.FunctionSwitchService;
import logic.hero.HeroManager;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SHeroMsg.ReqResetSkill.class)
public class MReqResetSkillHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.ANGEL)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:ANGEL");
        }
        Player player = (Player) getGameData();
        C2SHeroMsg.ReqResetSkill msg = (C2SHeroMsg.ReqResetSkill) getMessage().getData();
        HeroManager manager = player.getHeroManager();
        manager.reqResetSkill(msg);
    }

}
