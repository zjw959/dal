package logic.sign.handler;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CActivityMsg.RespActivitys;
import exception.AbstractLogicModelException;
import logic.constant.GameErrorCode;
import logic.sign.ApSupplyManager;
import logic.sign.MonthSignManager;
import logic.sign.SevenDaySignManager;
import logic.sign.TomorrowSignManager;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/***
 * 获取sign奖励 code=5122
 * 
 * @author lihongji
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SActivityMsg.SubmitActivity.class)
public class MSubmitActivityHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MSubmitActivityHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        org.game.protobuf.c2s.C2SActivityMsg.SubmitActivity msg =
                (org.game.protobuf.c2s.C2SActivityMsg.SubmitActivity) getMessage().getData();
        int activityId = msg.getActivitId();
        org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity.Builder builder =
                org.game.protobuf.s2c.S2CActivityMsg.ResultSubmitActivity.newBuilder();
        RespActivitys.Builder respBuilder = RespActivitys.newBuilder();
        // 月签到
        if (activityId == MonthSignManager.MONTHSIGN_ID) {
            builder = player.getMonthSignManager().getAward();
            MessageUtils.send(player, builder);
            // 刷新月签到记录
            respBuilder.addActivitys(player.getMonthSignManager().getSignInfo());
            MessageUtils.send(player, respBuilder);
        }
        // 次日登录奖励
        else if (activityId == TomorrowSignManager.TOMORROWSIGN_ID) {
            builder = player.getTomorrowSignManager().getAward();
            logic.support.MessageUtils.send(player, builder);
            // 刷新次日奖励记录
            if (player.getTomorrowSignManager().ableSerialize()) {
                respBuilder.addActivitys(player.getTomorrowSignManager().getSignInfo());
                MessageUtils.send(player, respBuilder);
            }
        } else if (activityId == ApSupplyManager.APSUPPLY_ID) {
            builder = player.getApSupplyManager().getAward();
            logic.support.MessageUtils.send(player, builder);
            // 刷新客户端体力补给记录
            respBuilder.addActivitys(player.getApSupplyManager().getSignInfo());
            MessageUtils.send(player, respBuilder);
        } else if (activityId == SevenDaySignManager.SEVENDAYSIN_ID) {
            builder = player.getSevenDaySignManager().getAward();
            logic.support.MessageUtils.send(player, builder);
            respBuilder.addActivitys(player.getSevenDaySignManager().getSignInfo());
            MessageUtils.send(player, respBuilder);
        }
        // 没有该类型抛错
        else {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR);
        }

    }
}
