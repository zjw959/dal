package logic.dating.handler.logic;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.constant.DatingTypeConstant;
import logic.constant.ItemConstantId;
import logic.constant.RoleConstant;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingDialogDTO;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.script.IDatingHandlerScript;
import logic.item.bean.Item;
import logic.role.bean.Role;
import logic.support.LogicScriptsUtils;

/**
 * 约会逻辑处理器
 * 
 * @author Alan
 *
 */
public abstract class DatingHandler {

    private static Logger LOGGER = Logger.getLogger(DatingHandler.class);

    /**
     * 获取处理器识别号
     */
    public abstract int getHandlerIdentification();

    /**
     * 约会前置条件中断检测
     */
    public abstract void checkDating(Player player, DatingScriptDTO dto);

    /**
     * 处理约会所需资源
     */
    public abstract void handleDatingResource(Player player, DatingScriptDTO dto);

    /**
     * 获取剧本配置
     */
    public abstract DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto);

    /**
     * 获取约会看板娘列表
     */
    public abstract List<Integer> getDatingRoles(Player player, DatingScriptDTO dto);

    /**
     * 对剧本的中断检测
     */
    public abstract void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg);

    /**
     * 剧本创建后的处理,比如日志
     */
    public abstract void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record);

    /** 根据不同类型约会获取相应的约会配置 */
    public abstract BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId);

    /** 根据不同类型约会获取相应的约会配置 */
    public abstract List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid);

    
    private IDatingHandlerScript getManagerScript() {
        return LogicScriptsUtils.getIDatingHandlerScript();
    }

    
    
    
    /** 获取匹配的约会剧本 */
    protected List<DatingRuleCfgBean> getDatingRuleByRoleIdDatingType(int roleCid,int handlerType) {
        return getManagerScript().getDatingRuleByRoleIdDatingType(roleCid, handlerType);
    }

    /**
     * 创建剧本记录,这是流程的最后一步,可以处理日志
     */
    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto) {
         getManagerScript().createCurrentScriptRecord(player, cfg, branchNodes, startNodeId, roleIds, dto);
    }

    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player,long datingId) {
        return getManagerScript().getByPlayerIdDatingTypeRoleId(datingType, roleId, player, datingId);
    }

    /**
     * 对话
     */
    public void dialog(Player player, DatingDialogDTO msg) {
        getManagerScript().dialog(player, msg,LOGGER);
    }

    public boolean isFail(Player player, int roleCid, boolean isLastNode) {
        return getManagerScript().isFail(player, roleCid, isLastNode);
    }

    /**
     * 约会结束数据记录
     */
    public void endDatingRecord(Player player, BaseDating cfg, int roleCid,
            CurrentDatingBean record) {
         getManagerScript().endDatingRecord(player, cfg, roleCid, record);
    }

    /**
     * 校验选择的节点id
     *
     * @param record 当前剧本记录
     * @param selectedNodeId 选择的节点id
     */
    private void checkSelectedNode(CurrentDatingBean record, int branchNodeId, int selectedNodeId,
            boolean isLastNode) {
         getManagerScript().checkSelectedNode(record, branchNodeId, selectedNodeId, isLastNode);
    }

    /**
     * 处理中间节点
     *
     * @param record 当前约会记录
     * @param previousCfg 前一个节点
     * @param branchNodeCfg 分支节点
     * @param selectedNodeCfg 选择节点
     */
    private void dealAmongNode(Player player, CurrentDatingBean record, BaseDating previousCfg,
            BaseDating branchNodeCfg, BaseDating selectedNodeCfg) {
       getManagerScript().dealAmongNode(player, record, previousCfg, branchNodeCfg, selectedNodeCfg);
    }

    /**
     * 获取下一个节点
     */
    private List<BaseDating> getNextNodes(CurrentDatingBean record, BaseDating preNode) {
        return getManagerScript().getNextNodes(record, preNode);
    }

    /**
     * 记录约会数据
     *
     * @param player 玩家对象
     * @param cfg 约会配置
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void recordCurrentDating(Player player, BaseDating cfg, CurrentDatingBean record) {
         getManagerScript().recordCurrentDating(player, cfg, record);
        
    }

    /**
     * 获取剧本对话奖励
     */
    @SuppressWarnings("rawtypes")
    private Map getReward(Player player, BaseDating cfg, CurrentDatingBean record) {
        return getReward(player, cfg, record);
    }

    /**
     * 结算
     */
    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record) {
       getManagerScript().settlement(player, selectedNodeCid, datingType, roleCid, record,LOGGER);
    }

    /**
     * 发送结算消息
     */
    public void sendSettlementMsg(Player player, CurrentDatingBean record, int favor,
            BaseDating cfg, List<Item> itemInfos) {
        getManagerScript().sendSettlementMsg(player, record, favor, cfg, itemInfos);
    }

    /**
     * 计算心情值
     */
    private int calcMood(Map<Integer, Integer> rewardMap, Role role) {
        if (role == null)
            return 0;
        int mood = (int) rewardMap.computeIfAbsent(ItemConstantId.ROLE_MOOD, k -> 0);
        // 获取看板娘增益
        if (mood != 0) {
            mood *= (100 + role.getEffectByType(RoleConstant.EFFECT_TYPE_4)) / 100;
        }
        rewardMap.put(ItemConstantId.ROLE_MOOD, mood);
        return mood;
    }

    /** 计算无聊的加成值 **/
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void calcState(int datingType, Map rewardMap, Player player, Role role) {
        if (datingType == DatingTypeConstant.DATING_TYPE_DAILY)
            player.getRoleManager().addPercentByRoleState(rewardMap, role, RoleConstant.BORING,
                    RoleConstant.DAILY_DATING);
    }
    /**
     * 获取初始节点
     */
    public int getStartNodeId(DatingRuleCfgBean cfg) {
       return getManagerScript().getStartNodeId(cfg);
    }

    /**
     * 获取分支节点
     */
    public Map<Integer, List<Integer>> getScriptBranchNode(Player player, int scriptId,
            DatingScriptDTO dto) {
       return getManagerScript().getScriptBranchNode(player, scriptId, dto);
    }

    /**
     * 检查节点 检查节点是否达成条件，未达成条件则不给玩家
     *
     * @param player 玩家
     * @param datingCfg 配置
     */
}
