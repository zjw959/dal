package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.c2s.C2SNewBuildingMsg.ReqRemindSuccess;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 
 * 建筑移除事件
 * 
 * @author lihongji
 *
 */
@MHandler(messageClazz = C2SNewBuildingMsg.ReqRemindSuccess.class)
public class MReqRemindSuccess extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqRemindSuccess.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        ReqRemindSuccess remove = (ReqRemindSuccess) getMessage().getData();
        int eventType = remove.getEventType();
        MessageUtils.send(player, NewBuildingMsgBuilder.removeEvent(player, eventType));
    }

}
