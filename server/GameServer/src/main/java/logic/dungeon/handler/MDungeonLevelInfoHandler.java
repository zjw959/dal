package logic.dungeon.handler;

import logic.msgBuilder.DungeonMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CDungeonMsg;

import exception.AbstractLogicModelException;

/**
 * 请求获取关卡及章节信息 code = 1796
 * 
 * @author Alan
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SDungeonMsg.GetLevelInfo.class)
public class MDungeonLevelInfoHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MDungeonLevelInfoHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        S2CDungeonMsg.GetLevelInfo.Builder builder = DungeonMsgBuilder.getDungeonRecordMsg(player);
        MessageUtils.send(player, builder);
    }
}
