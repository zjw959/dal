package logic.dating.handler.message;

import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SDatingMsg;
import data.GameDataManager;
import data.bean.BaseDating;
import exception.AbstractLogicModelException;
import logic.character.bean.Player;
import logic.constant.DatingTypeConstant;
import logic.constant.GameErrorCode;
import logic.dating.DatingManager;
import logic.dating.DatingService;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingDialogDTO;
import logic.dating.handler.logic.DatingHandler;
import logic.item.bean.Item;
import logic.msgBuilder.DatingMsgBuilder;
import logic.msgBuilder.ShareMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/**
 * 约会对话
 * <p>
 * <b>仅部分交互</b>
 * 
 * @author Alan
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SDatingMsg.DialogueMsg.class)
public class MDialogueMsgHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MDialogueMsgHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        Player player = (Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SDatingMsg.DialogueMsg msg = (C2SDatingMsg.DialogueMsg) getMessage().getData();
        int branchNodeId = msg.getBranchNodeId();
        int selectedNodeId = msg.getSelectedNodeId();
        int datingType = msg.getDatingType();
        int roleId = msg.getRoleId();
        String id = msg.getDatingId();
        long datingId = 0;
        if (id != null && id.length() > 0)
            datingId = Long.parseLong(id);

        boolean isLastNode = msg.getIsLastNode();
        BaseDating datingCfg = GameDataManager.getDatingCfgBean(selectedNodeId);
        // 组装传输对象
        DatingDialogDTO dto = new DatingDialogDTO(branchNodeId, selectedNodeId, datingType, roleId,
                isLastNode, datingId);
        DatingManager dm = player.getDatingManager();
        DatingHandler dh = DatingService.getInstance().getLogicHandler(datingType);
        // 取得约会数据对象
        CurrentDatingBean record =
                dh.getByPlayerIdDatingTypeRoleId(datingType, roleId, player, datingId);
        if (record == null) {
            if (datingType == DatingTypeConstant.DATING_TYPE_RESERVE
                    || datingType == DatingTypeConstant.DATING_TYPE_OUT) {
                if (isLastNode) {
                    MessageUtils.send(player, DatingMsgBuilder.getDatingSettlementMsg(0, 0, 0,
                            ShareMsgBuilder.createReward(new ArrayList<Item>()), true));
                }
                MessageUtils.returnEmptyBody();
            } else {
                MessageUtils.throwCondtionError(GameErrorCode.SCRIPT_IS_NULL,
                        "CurrentDatingBean does not exist");
            }
        } else {
            // 内部逻辑处理
            dh.dialog(player, dto);
            // 判定逻辑对象状态
            if (dh.isFail(player, roleId, isLastNode)) {
                dm.deleteDating(record);
                MessageUtils.send(player,
                        DatingMsgBuilder.createDatingFailMsg(datingCfg.getScriptId()));
            } else {
                if (isLastNode) {
                    dh.settlement(player, selectedNodeId, datingType, roleId, record);
                } else {
                    MessageUtils.send(player, DatingMsgBuilder.getDialogueMsg(record.getScore()));
                }
            }
            // 客户端同步请求兼容
            MessageUtils.returnEmptyBody();
        }
    }
}
