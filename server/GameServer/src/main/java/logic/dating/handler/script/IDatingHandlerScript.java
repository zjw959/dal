package logic.dating.handler.script;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingDialogDTO;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.item.bean.Item;
import logic.role.bean.Role;
import script.IScript;

/**
 * 约会逻辑处理器
 * 
 * @author lihongji
 *
 */
public interface IDatingHandlerScript extends IScript {


    /** 获取匹配的约会剧本 */
    public List<DatingRuleCfgBean> getDatingRuleByRoleIdDatingType(int roleCid,int handlerType);

    /**
     * 创建剧本记录,这是流程的最后一步,可以处理日志
     */
    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto);

    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId);

    /**
     * 对话
     */
    public void dialog(Player player, DatingDialogDTO msg,Logger LOGGER);

    public boolean isFail(Player player, int roleCid, boolean isLastNode);

    /**
     * 约会结束数据记录
     */
    public void endDatingRecord(Player player, BaseDating cfg, int roleCid,
            CurrentDatingBean record);

    /**
     * 校验选择的节点id
     *
     * @param record 当前剧本记录
     * @param selectedNodeId 选择的节点id
     */
    public void checkSelectedNode(CurrentDatingBean record, int branchNodeId, int selectedNodeId,
            boolean isLastNode);

    /**
     * 处理中间节点
     *
     * @param record 当前约会记录
     * @param previousCfg 前一个节点
     * @param branchNodeCfg 分支节点
     * @param selectedNodeCfg 选择节点
     */
    public void dealAmongNode(Player player, CurrentDatingBean record, BaseDating previousCfg,
            BaseDating branchNodeCfg, BaseDating selectedNodeCfg);

    /**
     * 获取下一个节点
     */
    public List<BaseDating> getNextNodes(CurrentDatingBean record, BaseDating preNode);

    /**
     * 记录约会数据
     *
     * @param player 玩家对象
     * @param cfg 约会配置
     */
    public void recordCurrentDating(Player player, BaseDating cfg, CurrentDatingBean record);

    /**
     * 获取剧本对话奖励
     */
    public Map getReward(Player player, BaseDating cfg, CurrentDatingBean record);

    /**
     * 结算
     */
    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record,Logger LOGGER);

    /**
     * 发送结算消息
     */
    public void sendSettlementMsg(Player player, CurrentDatingBean record, int favor,
            BaseDating cfg, List<Item> itemInfos);

    /**
     * 计算心情值
     */
    public int calcMood(Map rewardMap, Role role);

    /** 计算无聊的加成值 **/
    public void calcState(int datingType, Map rewardMap, Player player, Role role);

    /**
     * 计算好感度
     * 
     * @param record 当前约会记录
     * @param rewardMap 约会奖励
     * @param role 精灵
     */
    public int calcFavor(CurrentDatingBean record, Map rewardMap, Role role);

    /**
     * 计算约会得分对好感度的加成
     * 
     * @param favor 好感度
     * @param score 约会得分
     * @return
     */
    public int calcFavor(int favor, int score);

    /**
     * 获取初始节点
     */
    public int getStartNodeId(DatingRuleCfgBean cfg);

    /**
     * 获取分支节点
     */
    public Map<Integer, List<Integer>> getScriptBranchNode(Player player, int scriptId,
            DatingScriptDTO dto);

    /**
     * 检查节点 检查节点是否达成条件，未达成条件则不给玩家
     *
     * @param player 玩家
     * @param datingCfg 配置
     */
    public boolean checkItem(Player player, BaseDating datingCfg, DatingScriptDTO dto);


}
