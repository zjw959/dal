package logic.dungeon.handler;

import logic.dungeon.DungeonManager;
import message.MHandler;
import message.MessageHandler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SDungeonMsg;

import exception.AbstractLogicModelException;

/**
 * 副本：通知服务器记录主线进度 code = 1795
 * 
 * @author Alan
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SDungeonMsg.ProgressMsg.class)
public class MMainLineProgressHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MMainLineProgressHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SDungeonMsg.ProgressMsg msg = (C2SDungeonMsg.ProgressMsg) getMessage().getData();
        int mlid = msg.getMainLineCid();
        DungeonManager dm = player.getDungeonManager();
        dm.promoteStory(mlid);
    }
}
