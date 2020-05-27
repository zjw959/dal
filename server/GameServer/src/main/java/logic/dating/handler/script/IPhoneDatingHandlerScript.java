package logic.dating.handler.script;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import script.IScript;

public interface IPhoneDatingHandlerScript extends IScript {

    public int getHandlerIdentification();

    public void checkDating(Player player, DatingScriptDTO dto);

    public void handleDatingResource(Player player, DatingScriptDTO dto);

    public DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto);

    public List<Integer> getDatingRoles(Player player, DatingScriptDTO dto);

    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg);

    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record,Logger LOGGER);

    public BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId);

    public List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid);


    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record,Logger LOGGER);

    /** 处理节点奖励 **/
    public void settleNode(Player player, int datingType, int roleCid, CurrentDatingBean record,
            int handlerType);

    /** 处理信息 **/
    public void dealMessage(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record);


    /** 处理 **/
    public void hanlder(int id, Player player, CurrentDatingBean record, int nodeId, int datingType,
            int roleCid);

    /** 检测是否能够接受 **/
    public boolean check(int id, Player player, CurrentDatingBean record);

    /**
     * 创建剧本记录,这是流程的最后一步,可以处理日志
     */
    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto);

    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId);


}
