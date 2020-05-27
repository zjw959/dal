package logic.city.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SNewBuildingMsg.ReqGetHandWorkInfo;

import data.GameDataManager;
import data.bean.HandworkbaseCfgBean;
import logic.city.build.bean.ManualEvent;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 
 * 请求获取手工数据 code = 2080
 * 
 * @author lihongji
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SNewBuildingMsg.ReqGetHandWorkInfo.class)
public class MreqGetHandWorkInfoHandler extends MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(MreqGetHandWorkInfoHandler.class);

    @Override
    public void action() throws Exception {
        logic.character.bean.Player player = (logic.character.bean.Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        ReqGetHandWorkInfo info = (ReqGetHandWorkInfo) getMessage().getData();
        boolean clear = !info.getNeedSave();
        ManualEvent event = player.getManualManager().getEvent();
        HandworkbaseCfgBean handworkBean =
                GameDataManager.getHandworkbaseCfgBean(event.getManualId());
        if (handworkBean != null && handworkBean.getDatingId() > 0) {
            clear = true;
        }
        MessageUtils.send(player,
                NewBuildingMsgBuilder.packageIngHandWorkInfo(player, clear));
    }

}
