package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.c2s.C2SNewBuildingMsg.ReqUploadQteIntegral;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 请求验证QTE完成上传积分 code=2068
 * 
 * @author lihongji
 *
 */
@MHandler(messageClazz = C2SNewBuildingMsg.ReqUploadQteIntegral.class)
public class MReqUploadQteIntegralHandler extends MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MReqUploadQteIntegralHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SNewBuildingMsg.ReqUploadQteIntegral req = (ReqUploadQteIntegral) getMessage().getData();

        MessageUtils.send(player, player.getCuisineManager().checkQteIntegral(req.getFoodId(),
                req.getQteId(), req.getIntegral()));
    }

}
