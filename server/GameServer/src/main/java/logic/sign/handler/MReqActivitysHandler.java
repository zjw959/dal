package logic.sign.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CActivityMsg.RespActivitys;
import exception.AbstractLogicModelException;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/**
 * 获取签到信息 code=5121
 * 
 * @author lihongji
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SActivityMsg.ReqActivitys.class)
public class MReqActivitysHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqActivitysHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        RespActivitys.Builder respBuilder = RespActivitys.newBuilder();
        // 次日奖励
        if (player.getTomorrowSignManager().ableSerialize())
            respBuilder.addActivitys(player.getTomorrowSignManager().getSignInfo());
        // 月签到
        respBuilder.addActivitys(player.getMonthSignManager().getSignInfo());
        // 体力活动
        respBuilder.addActivitys(player.getApSupplyManager().getSignInfo());
        // 7日签到
        if (player.getSevenDaySignManager().ableSerialize())
            respBuilder.addActivitys(player.getSevenDaySignManager().getSignInfo());
        MessageUtils.send(player, respBuilder);
    }

}
