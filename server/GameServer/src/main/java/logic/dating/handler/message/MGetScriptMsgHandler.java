package logic.dating.handler.message;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SDatingMsg;
import data.bean.DatingRuleCfgBean;
import exception.AbstractLogicModelException;
import logic.character.bean.Player;
import logic.constant.GameErrorCode;
import logic.dating.DatingManager;
import logic.dating.DatingService;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.logic.DatingHandler;
import logic.msgBuilder.DatingMsgBuilder;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

/**
 * 获取剧本,进入约会场景
 * 
 * @author Alan
 *
 */
@MHandler(messageClazz = org.game.protobuf.c2s.C2SDatingMsg.GetScriptMsg.class)
public class MGetScriptMsgHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MGetScriptMsgHandler.class);

    @Override
    public void action() throws AbstractLogicModelException {
        Player player = (Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        C2SDatingMsg.GetScriptMsg msg = (C2SDatingMsg.GetScriptMsg) getMessage().getData();

        int buildingId = msg.getBuildId();
        int roleId = msg.getRoleId();
        int scriptType = msg.getScriptType().getNumber();
        int cityId = msg.getCityId();
        int scriptId = msg.getScriptId();
        String cityDatingId = msg.getCityDatingId();
        long datingId = 0;
        if (cityDatingId != null && cityDatingId.length() > 0)
            datingId = Long.parseLong(cityDatingId);

        DatingManager dm = player.getDatingManager();

        // 组装数据
        DatingScriptDTO dto =
                new DatingScriptDTO(buildingId, roleId, scriptType, cityId, scriptId, cityDatingId);
        // 获取业务逻辑对象
        DatingHandler dh = DatingService.getInstance().getLogicHandler(scriptType);
        CurrentDatingBean record =
                dh.getByPlayerIdDatingTypeRoleId(scriptType, roleId, player, datingId);
        // 对约会中断条件的检测
        dh.checkDating(player, dto);
        // 获取对应的看板娘
        List<Integer> roles = dh.getDatingRoles(player, dto);
        // 对玩家接口逻辑的判定拦截
        if (record != null && record.getScriptId() == 0)
            MessageUtils.throwCondtionError(GameErrorCode.HAVE_NOT_FINISH_SCRIPT);
        // 预处理一些资源,比如日常约会增加次数
        dh.handleDatingResource(player, dto);

        // 获取剧本
        DatingRuleCfgBean datingRule = dh.getDatingRuleCfg(player, dto);
        dh.checkDatingRule(player, datingRule);
        // 筛选分支节点
        Map<Integer, List<Integer>> branchNodes =
                dh.getScriptBranchNode(player, datingRule.getId(), dto);

        // 获取开始节点
        int startNodeId = dh.getStartNodeId(datingRule);

        dh.createCurrentScriptRecord(player, datingRule, branchNodes, startNodeId, roles, dto);
        MessageUtils.send(player, DatingMsgBuilder.getDatingScriptMsg(datingRule.getId(),
                branchNodes, !dm.getCompleteDatings().contains(datingRule.getId()), cityDatingId));
        // 原协议的适配
        MessageUtils.returnEmptyBody();
    }
}
