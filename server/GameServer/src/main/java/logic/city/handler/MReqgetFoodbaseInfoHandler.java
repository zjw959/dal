package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.c2s.C2SNewBuildingMsg.ReqgetFoodbaseInfo;

import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 请求料理数据 code=2066
 * 
 * @author lihongji
 *
 */
@MHandler(messageClazz = C2SNewBuildingMsg.ReqgetFoodbaseInfo.class)
public class MReqgetFoodbaseInfoHandler extends MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MReqgetFoodbaseInfoHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        ReqgetFoodbaseInfo info = (ReqgetFoodbaseInfo) getMessage().getData();
        MessageUtils.send(player,
                NewBuildingMsgBuilder.packageIngCuisineInfo(player, !info.getNeedSave()));
    }

}
