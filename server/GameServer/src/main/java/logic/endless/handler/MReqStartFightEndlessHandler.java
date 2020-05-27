package logic.endless.handler;

import logic.constant.EFunctionType;
import logic.constant.GameErrorCode;
import logic.endless.bean.EndlessVO;
import logic.functionSwitch.FunctionSwitchService;
import logic.msgBuilder.EndlessCloisterMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CEndlessCloisterMsg.RspEndlessCloisterInfo;

import exception.AbstractLogicModelException;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SEndlessCloisterMsg.ReqStartFightEndless.class)
public class MReqStartFightEndlessHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqStartFightEndlessHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.ENDLESS)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:endless_dungeon");
        }
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        int startStageId = player.getEndlessCloisterManager().getStartStage();
        EndlessVO endlessVO = player.getEndlessCloisterManager().getEndlessVO();

        RspEndlessCloisterInfo.Builder info =
                EndlessCloisterMsgBuilder.createEndlessCloisterInfo(player);
        logic.support.MessageUtils.send(player, info);

        logic.support.MessageUtils.send(player,
                EndlessCloisterMsgBuilder.createRspStartFightEndless(endlessVO, startStageId));
    }
}
