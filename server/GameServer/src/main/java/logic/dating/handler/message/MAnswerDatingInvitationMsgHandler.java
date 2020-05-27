package logic.dating.handler.message;

import java.util.Map;
import logic.character.bean.Player;
import logic.constant.DatingConstant;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.GameErrorCode;
import logic.dating.DatingManager;
import logic.dating.DatingService;
import logic.dating.bean.CityDatingBean;
import logic.functionSwitch.FunctionSwitchService;
import logic.msgBuilder.DatingMsgBuilder;
import logic.msgBuilder.RoleMsgBuilder;
import logic.role.bean.Role;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;
import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SDatingMsg;
import data.GameDataManager;
import data.bean.DatingRuleCfgBean;
import exception.AbstractLogicModelException;

/**
 * 处理约会邀请
 * 
 * @author Alan
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SDatingMsg.AnswerDatingInvitationMsg.class)
public class MAnswerDatingInvitationMsgHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MAnswerDatingInvitationMsgHandler.class);

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void action() throws AbstractLogicModelException {
        Player player = (Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.DATE_DAILY)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:date_daily");
        }

        C2SDatingMsg.AnswerDatingInvitationMsg msg =
                (C2SDatingMsg.AnswerDatingInvitationMsg) getMessage().getData();
        long datingId = Long.valueOf(msg.getDatingId());
        int answer = msg.getAnswer();

        DatingManager dm = player.getDatingManager();
        CityDatingBean record = dm.getCityDating(datingId);
        // 前置检测
        if (record == null)
//            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "没有找到约会记录");
            MessageUtils.throwCondtionError(GameErrorCode.DATING_IS_OBSOLETE,
                    "预定约会接受邀请超时  datingId:" + datingId + ",answer:" + answer);
        if (record.getState() != DatingConstant.RESERVE_DATING_STATE_INVITATION)
            // if (record.getState() != DatingConstant.RESERVE_DATING_STATE_HAVE_INVITATION)
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "并非受邀请状态");

        DatingRuleCfgBean cfg = GameDataManager.getDatingRuleCfgBean(record.getScriptId());
        // 获取回复状态
        Map answerInfo =
                (Map) ((Map) cfg.getOtherInfo().get(DatingConstant.OTHER_INFO_KEY_ANSWER_MAP))
                        .get(answer);
        boolean isAccept = ((int) answerInfo.get(DatingConstant.ANSWER_TYPE)) == 1;
        // 设置回复状态
        answerInvitation(player, dm, record, isAccept, cfg);
        // 需要看板娘模块的支持
        Role role = player.getRoleManager().getRole(record.getRoleIds().get(0));
        int favor = (Integer) answerInfo.getOrDefault(DatingConstant.ANSWER_FAVOR, 0);// ToolMap.getInt(DatingConstant.ANSWER_FAVOR,answerInfo,0);
        if (favor != 0) {
            player.getRoleManager().changeFavor(role, favor, EReason.DATING_INVITATION);
        }
        int mood = (Integer) answerInfo.getOrDefault(DatingConstant.ANSWER_MOOD, 0);// ToolMap.getInt(DatingConstant.ANSWER_MOOD,answerInfo,0);
        if (mood != 0) {
            player.getRoleManager().changeMood(role, mood, EReason.DATING_INVITATION);
        }
        // 对原协议的适配
        MessageUtils.returnEmptyBody();
    }

    /**
     * 回应邀请
     */
    public void answerInvitation(Player player, DatingManager dm, CityDatingBean record,
            boolean accept, DatingRuleCfgBean cfg) {
        DatingService.getInstance().getDatingTrigger(record.getDatingType()).acceptDating(accept,
                player);

    }
}
