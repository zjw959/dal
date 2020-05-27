package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import logic.city.PartTimeManager;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 
 * 获取兼职奖励 code=2078
 * 
 * @author lihongji
 *
 */
@MHandler(messageClazz = C2SNewBuildingMsg.ReqPartTimeJobAward.class)
public class MReqPartTimeJobAward extends MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MReqPartTimeJobAward.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        PartTimeManager manager = player.getPartTimeManager();
        MessageUtils.send(player, manager.getAward());
    }
}
