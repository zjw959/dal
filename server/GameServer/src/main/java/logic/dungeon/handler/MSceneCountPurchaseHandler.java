package logic.dungeon.handler;

import logic.constant.EFunctionType;
import logic.constant.GameErrorCode;
import logic.dungeon.bean.DungeonGroupBean;
import logic.functionSwitch.FunctionSwitchService;
import logic.msgBuilder.DungeonMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SDungeonMsg;

import exception.AbstractLogicModelException;

/**
 * 购买副本组战斗次数 code = 1800
 * 
 * @author Alan
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SDungeonMsg.BuyFightCount.class)
public class MSceneCountPurchaseHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MSceneCountPurchaseHandler.class);
    @Override
    public void action() throws AbstractLogicModelException {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.DAILY_DUNGEON)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:daily_dungeon");
        }
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SDungeonMsg.BuyFightCount msg = (C2SDungeonMsg.BuyFightCount) getMessage().getData();
        int levelGroupCid = msg.getCid();

        DungeonGroupBean levelGroup = player.getDungeonManager().buyGroupCount(levelGroupCid);

        MessageUtils.send(player, DungeonMsgBuilder.getUpdateLevelGroupInfo(levelGroup));
        MessageUtils.send(player, DungeonMsgBuilder.buildBuySceneCountMsg(levelGroupCid));
    }
}
