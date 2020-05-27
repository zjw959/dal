package logic.endless.handler;

import java.util.ArrayList;
import java.util.List;

import logic.constant.EFunctionType;
import logic.constant.GameErrorCode;
import logic.endless.bean.PassStageTO;
import logic.functionSwitch.FunctionSwitchService;
import logic.msgBuilder.EndlessCloisterMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SEndlessCloisterMsg;
import org.game.protobuf.s2c.S2CEndlessCloisterMsg.RspEndlessCloisterInfo;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import exception.AbstractLogicModelException;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SEndlessCloisterMsg.ReqPassStageEndless.class)
public class MReqPassStageEndlessHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqPassStageEndlessHandler.class);

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
        C2SEndlessCloisterMsg.ReqPassStageEndless msg =
                (C2SEndlessCloisterMsg.ReqPassStageEndless) getMessage().getData();
        // 组装数据
        PassStageTO passTo = new PassStageTO(msg.getLevelCid(), msg.getCostTime());
        // 逻辑模块调用
        int nextStageId = player.getEndlessCloisterManager().passStageAndGetNext(passTo);
        List<RewardsMsg> rewards = new ArrayList<RewardsMsg>();
        if (passTo.getPassRewards() != null)
            passTo.getPassRewards().forEach((k, v) -> {
                rewards.add(RewardsMsg.newBuilder().setId(k).setNum(v).build());
            });
        RspEndlessCloisterInfo.Builder info =
                EndlessCloisterMsgBuilder.createEndlessCloisterInfo(player);
        logic.support.MessageUtils.send(player, info);

        logic.support.MessageUtils.send(player, EndlessCloisterMsgBuilder
                .createRspPassStageEndless(player.getEndlessCloisterManager().getEndlessVO(),
                        rewards, nextStageId));
    }
}
