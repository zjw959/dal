package logic.dungeon.handler;

import logic.msgBuilder.DungeonMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CDungeonMsg;

import exception.AbstractLogicModelException;

/**
 * 获取关卡组多倍收益    code = 1809
 * 
 * @author Alan
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SDungeonMsg.GroupMultipleRewardMsg.class)
public class MGroupMultipleRewardHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MGroupMultipleRewardHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        S2CDungeonMsg.GroupMultipleRewardMsg.Builder builder = DungeonMsgBuilder.getGroupMultipleRewardMsg(player);
        MessageUtils.send(player, builder);
    }
}
