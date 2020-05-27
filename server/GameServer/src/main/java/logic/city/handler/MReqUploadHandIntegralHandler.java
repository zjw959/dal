package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.c2s.C2SNewBuildingMsg.ReqUploadHandIntegral;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 手工上传积分
 * 
 * @author lihongji
 *
 */

@MHandler(messageClazz = org.game.protobuf.c2s.C2SNewBuildingMsg.ReqUploadHandIntegral.class)
public class MReqUploadHandIntegralHandler extends MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MReqUploadHandIntegralHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SNewBuildingMsg.ReqUploadHandIntegral req =
                (ReqUploadHandIntegral) getMessage().getData();
        int manualId = req.getManualId();
        int integral = req.getIntegral();
        int stime = req.getStime();
        int etime = req.getEtime();

        MessageUtils.send(player,
                player.getManualManager().checkOperateIntegral(manualId, integral));
        // 客户端同步请求兼容
        MessageUtils.returnEmptyBody();
    }

}
