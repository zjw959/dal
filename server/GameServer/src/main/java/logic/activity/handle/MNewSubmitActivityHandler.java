package logic.activity.handle;

import logic.constant.GameErrorCode;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;
import exception.AbstractLogicModelException;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SActivityMsg.NewSubmitActivity;

/***
 * 奖励获取
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SActivityMsg.NewSubmitActivity.class)
public class MNewSubmitActivityHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MNewSubmitActivityHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        logic.character.bean.Player player = logic.character.PlayerManager.getPlayerByCtx(ctx);
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        org.game.protobuf.c2s.C2SActivityMsg.NewSubmitActivity msg =
                (NewSubmitActivity) getMessage().getData();
//        try {
            player.getActivityManager().getReward(player, msg.getActivitId(), msg.getActivitEntryId(),
                    msg.getExtendData());
//        } catch (Exception e) {
//            LOGGER.error("player get activity reward error", e);
//            MessageUtils.throwLogicError(GameErrorCode.PLAYER_DATA_ERROR, e, "player [",
//                    String.valueOf(player.getPlayerId()), "], ", String.valueOf(msg.getActivitId()), ", ",
//                    String.valueOf(msg.getActivitEntryId()), ", ", msg.getExtendData());
//        }
    }
}
