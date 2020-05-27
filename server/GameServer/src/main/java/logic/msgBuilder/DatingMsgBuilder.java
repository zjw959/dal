package logic.msgBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;

import logic.character.bean.Player;
import logic.constant.DatingConstant;
import logic.constant.DatingTypeConstant;
import logic.dating.DatingManager;
import logic.dating.bean.CityDatingBean;
import logic.dating.bean.CurrentDatingBean;

import org.game.protobuf.s2c.S2CDatingMsg;
import org.game.protobuf.s2c.S2CShareMsg;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

public class DatingMsgBuilder {


    /**
     * 登录获取约会信息 code = 1539
     * 
     * @param player
     * @return
     */
    public static S2CDatingMsg.GetDatingInfo.Builder getDatingInfoMsg(Player player) {
        DatingManager dating = player.getDatingManager();
        S2CDatingMsg.GetDatingInfo.Builder builder = S2CDatingMsg.GetDatingInfo.newBuilder();

        if (dating != null) {
            // 已经完成剧本
            if (dating.getCompleteDatings() != null && dating.getCompleteDatings().size() > 0) {
                builder.addAllDatingRuleCid(dating.getCompleteDatings());
            }
            // 结局
            if (dating.getCompleteEndings() != null && dating.getCompleteEndings().size() > 0) {
                builder.addAllEndings(dating.getCompleteEndings());
            }
        }

        // 未完成剧本
        dating.getCurrentDatings().values().forEach(record -> {
            if (record.getScriptId() == 0) {
                return;
            }
            if (record.getDatingType() != DatingTypeConstant.DATING_TYPE_OUT
                    && record.getDatingType() != DatingTypeConstant.DATING_TYPE_RESERVE)
                builder.addNotFinishDating(getNotFinishDating(record,
                        !dating.getCompleteDatings().contains(record.getScriptId())));
            // 为了兼容旧的协议
            if (record instanceof CityDatingBean)
                builder.addCityDatingInfoList(
                        getCityDatingInfo(S2CShareMsg.ChangeType.ADD, (CityDatingBean) record,false));
        });

        for (Map.Entry<Integer, Set<Integer>> entry : dating.getRoleTriggerScripts().entrySet()) {
            S2CDatingMsg.UpdateTriggerDating.Builder roleTriggerBuilder =
                    S2CDatingMsg.UpdateTriggerDating.newBuilder();
            roleTriggerBuilder.setRoleId(entry.getKey()).addAllDatingRuleCid(entry.getValue());
            builder.addTriggerDating(roleTriggerBuilder.build());
        }
        return builder;
    }

    /**
     * 获取约会剧本 code = 1542
     * 
     * @param branchNodes 分支节点
     */
    public static S2CDatingMsg.DatingScriptMsg.Builder getDatingScriptMsg(int datingRuleCid,
            Map<Integer, List<Integer>> branchNodes,boolean isFirst,String datingId) {
        S2CDatingMsg.DatingScriptMsg.Builder builder = S2CDatingMsg.DatingScriptMsg.newBuilder();
        builder.setDatingRuleCid(datingRuleCid);
        builder.setIsFirst(isFirst);
        builder.setDatingId(datingId);
        if (branchNodes != null) {
            branchNodes
                    .forEach((datingCfgId, nextNodeIds) -> builder.addBranchNodes(buildBranchNode(
                            (int) datingCfgId, (List<Integer>) nextNodeIds)));
        }
        return builder;
    }

    /**
     * 获取对话消息 code = 1538
     * 
     * @param score 积分
     */
    public static S2CDatingMsg.DialogueMsg.Builder getDialogueMsg(int score) {
        S2CDatingMsg.DialogueMsg.Builder builder = S2CDatingMsg.DialogueMsg.newBuilder();
        builder.setScore(score);
        return builder;
    }

    /**
     * 获取 约会结算消息 code = 1540
     * 
     * @param score 积分
     * @param favor 好感度
     * @param obsolete 剧本是否有效
     */
    public static S2CDatingMsg.DatingSettlementMsg.Builder getDatingSettlementMsg(int score,
            int favor, int scriptId, List<RewardsMsg> rewards,boolean obsolete) {
        S2CDatingMsg.DatingSettlementMsg.Builder builder =
                S2CDatingMsg.DatingSettlementMsg.newBuilder();
        builder.setScore(score).setFavor(favor);
        if (scriptId != 0) {
            builder.setScriptId(scriptId);
        }
        builder.setObsolete(obsolete);
        builder.addAllRewards(rewards);
        return builder;
    }

    public static S2CDatingMsg.CityDatingInfo.Builder getCityDatingInfo(S2CShareMsg.ChangeType ct,
            CityDatingBean cityDatingRecord,boolean inDating) {
        S2CDatingMsg.CityDatingInfo.Builder builder = S2CDatingMsg.CityDatingInfo.newBuilder();
        builder.setCt(ct).setCityDatingId(cityDatingRecord.getId() + "")
                .setState(cityDatingRecord.getState())
                .setDate((int) (cityDatingRecord.getDatingBeginTime() / 1000))
                .setDatingRuleCid(cityDatingRecord.getScriptId());
        if (cityDatingRecord.getDatingTimeFrame() != null)
            builder.addAllDatingTimeFrame(cityDatingRecord.getDatingTimeFrame());
        builder.setInDating(inDating);
        return builder;
    }

    public static S2CDatingMsg.CityDatingInfoList.Builder buildCityDatingInfoList(
            S2CShareMsg.ChangeType ct, List<CityDatingBean> recordList) {
        S2CDatingMsg.CityDatingInfoList.Builder builder =
                S2CDatingMsg.CityDatingInfoList.newBuilder();
        recordList.forEach(record -> builder.addCityDatingInfo(getCityDatingInfo(ct, record,false)));
        return builder;
    }

    /**
     * 城市约会信息列表
     */
    public static S2CDatingMsg.CityDatingInfoList buildCityDatingInfoList(
            List<CityDatingBean> recordList) {
        S2CDatingMsg.CityDatingInfoList.Builder builder =
                S2CDatingMsg.CityDatingInfoList.newBuilder();
        S2CShareMsg.ChangeType ct;
        for (CityDatingBean record : recordList) {
            if (record.getState() == DatingConstant.RESERVE_DATING_STATE_NO_DATING) {
                ct = S2CShareMsg.ChangeType.DELETE;
            } else {
                ct = S2CShareMsg.ChangeType.UPDATE;
            }
            builder.addCityDatingInfo(getCityDatingInfo(ct, record,false));
        }
        return builder.build();
    }

    /**
     * 获取未完成约会信息
     *
     * @param record 当前约会记录
     */
    private static S2CDatingMsg.NotFinishDating getNotFinishDating(CurrentDatingBean record,boolean isFirst) {
        if (record != null && record.getScriptId() != 0) {
            S2CDatingMsg.NotFinishDating.Builder notFinishBuilder =
                    S2CDatingMsg.NotFinishDating.newBuilder();
            notFinishBuilder.setScore(record.getScore()).setDatingRuleCid(record.getScriptId())
                    .setDatingType(record.getDatingType()).addAllRoleCid(record.getRoleIds())
                    .setCurrentNodeId(record.getCurrentCid());

            if (record.getDatingType() != DatingTypeConstant.DATING_TYPE_MAIN) {
                record.getCurrentScript().forEach(
                        (datingCfgId, nextNodeIds) -> notFinishBuilder
                                .addBranchNodes(buildBranchNode((int) datingCfgId,
                                        (List<Integer>) nextNodeIds)));
            }
            for (Object nodeId : record.getSelectedNode()) {
                notFinishBuilder.setSelectedNode((Integer) nodeId);
            }
            notFinishBuilder.setIsFirst(isFirst);
            return notFinishBuilder.build();
        }
        return null;
    }

    /**
     * 获取分支节点消息
     *
     * @param branchNodes 分支节点
     */
    private static S2CDatingMsg.BranchNodes getBranchNodes(Map<Integer, List<Integer>> branchNodes) {
        S2CDatingMsg.BranchNodes.Builder builder = S2CDatingMsg.BranchNodes.newBuilder();
        branchNodes.forEach((datingCfgId, branchNodeIds) -> {
            S2CDatingMsg.BranchNode.Builder branchNodeBuilder =
                    S2CDatingMsg.BranchNode.newBuilder();
            branchNodeBuilder.setDatingId(datingCfgId);
            branchNodeBuilder.addAllNextNodeIds(branchNodeIds);
            builder.addBranchNodes(branchNodeBuilder);
        });
        return builder.build();
    }

    private static S2CDatingMsg.BranchNode buildBranchNode(int datingCfgId,
            List<Integer> nextNodeIds) {
        S2CDatingMsg.BranchNode.Builder branchNodeBuilder = S2CDatingMsg.BranchNode.newBuilder();
        return branchNodeBuilder.setDatingId(datingCfgId).addAllNextNodeIds(nextNodeIds).build();
    }

    public static S2CDatingMsg.DatingFail.Builder createDatingFailMsg(int datingRuleCid) {
        S2CDatingMsg.DatingFail.Builder builder = S2CDatingMsg.DatingFail.newBuilder();
        return builder.setDatingRuleCid(datingRuleCid);
    }
    
    /**返回手机是否接受预定约会**/
    public static S2CDatingMsg.PhoneDatingAccept.Builder createDatingAcceptMsg(boolean accept){
        S2CDatingMsg.PhoneDatingAccept.Builder builder =S2CDatingMsg.PhoneDatingAccept.newBuilder();
        return builder.setAccept(accept);
    }
}
