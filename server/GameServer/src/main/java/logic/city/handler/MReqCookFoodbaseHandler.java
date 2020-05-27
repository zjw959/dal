package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.c2s.C2SNewBuildingMsg.ReqCookFoodbase;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 请求料理数据 code=2067
 * 
 * @author lihongji
 *
 */
@MHandler(messageClazz = C2SNewBuildingMsg.ReqCookFoodbase.class)
public class MReqCookFoodbaseHandler extends MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MReqCookFoodbaseHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SNewBuildingMsg.ReqCookFoodbase req = (ReqCookFoodbase) getMessage().getData();
        player.getCuisineManager().createCuisineEvent(req.getFoodId());
        MessageUtils.send(player, NewBuildingMsgBuilder.getCuisineEvent(player));

    }

}
