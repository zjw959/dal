package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.c2s.C2SNewBuildingMsg.ReqGetFoodBaseAward;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 请求料理数据 code=2069
 * 
 * @author lihongji
 *
 */
@MHandler(messageClazz = C2SNewBuildingMsg.ReqGetFoodBaseAward.class)
public class MReqGetFoodBaseAwardHandler extends MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MReqGetFoodBaseAwardHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SNewBuildingMsg.ReqGetFoodBaseAward req = (ReqGetFoodBaseAward) getMessage().getData();
        MessageUtils.send(player, player.getCuisineManager().getAward(req.getFoodId()));
    }

}
