package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 
 * 获取兼职任务 code =2076
 * 
 * @author lihongji
 *
 */
@MHandler(messageClazz = C2SNewBuildingMsg.ReqPartTimeJobList.class)
public class MReqPartTimeJobList extends MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MReqDoPartTimeJob.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        MessageUtils.send(player, NewBuildingMsgBuilder.getPartTimeJobList(player));
    }

}
