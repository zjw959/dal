package logic.summon.handler;

import org.game.protobuf.c2s.C2SSummonMsg;

import exception.AbstractLogicModelException;
import logic.character.bean.Player;
import logic.constant.EFunctionType;
import logic.constant.GameErrorCode;
import logic.functionSwitch.FunctionSwitchService;
import logic.summon.SummonManager;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2SSummonMsg.ComposeFinish.class)
public class MReqComposeFinishHandler extends MessageHandler {

    @Override
    public void action() throws AbstractLogicModelException {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.SUMMON)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:SUMMON");
        }
        Player player = (Player) getGameData();
        C2SSummonMsg.ComposeFinish msg = (C2SSummonMsg.ComposeFinish) getMessage().getData();
        SummonManager manager = player.getSummonManager();
        manager.reqComposeFinish(msg);
    }
}
