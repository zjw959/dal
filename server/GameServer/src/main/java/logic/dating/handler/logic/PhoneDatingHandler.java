package logic.dating.handler.logic;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.constant.DatingTypeConstant;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.script.IPhoneDatingHandlerScript;
import logic.support.LogicScriptsUtils;

/***
 * 
 * 手机约会
 * 
 * @author lihongji
 *
 */

public class PhoneDatingHandler extends DatingHandler {

    @Override
    public int getHandlerIdentification() {
        return DatingTypeConstant.DATING_TYPE_PHONE;
    }

    private IPhoneDatingHandlerScript getManagerScript() {
        return LogicScriptsUtils.getIPhoneDatingHandlerScript();
    }
    
    
    private static Logger LOGGER = Logger.getLogger(PhoneDatingHandler.class);

    @Override
    public void checkDating(Player player, DatingScriptDTO dto) {
        getManagerScript().checkDating(player, dto);
    }

    @Override
    public void handleDatingResource(Player player, DatingScriptDTO dto) {
        getManagerScript().handleDatingResource(player, dto);
    }

    @Override
    public DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto) {
        return getManagerScript().getDatingRuleCfg(player, dto);
    }

    @Override
    public List<Integer> getDatingRoles(Player player, DatingScriptDTO dto) {
        return getManagerScript().getDatingRoles(player, dto);
    }

    @Override
    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg) {
         getManagerScript().checkDatingRule(player, ruleCfg);

    }

    @Override
    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record) {
        getManagerScript().handleAfterCreatedScriptRecord(player, record,LOGGER);
    }

    @Override
    public BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId) {
        return getManagerScript().getDatingCfgBeanByType(datingCid, datingRuleId);
    }

    @Override
    public List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid) {
       return getManagerScript().getDatingCfgBeansByTypeAndScript(scriptCid);
    }


    @Override
    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record) {
       getManagerScript().settlement(player, selectedNodeCid, datingType, roleCid, record, LOGGER);
    }

    /** 处理节点奖励 **/
    public void settleNode(Player player, int datingType, int roleCid, CurrentDatingBean record,
            int handlerType) {
        getManagerScript().settleNode(player, datingType, roleCid, record, handlerType);
    }

    /** 处理信息 **/
    public void dealMessage(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record) {
        getManagerScript().dealMessage(player, selectedNodeCid, datingType, roleCid, record);
    }


    /** 处理 **/
    public void hanlder(int id, Player player, CurrentDatingBean record, int nodeId, int datingType,
            int roleCid) {
        getManagerScript().hanlder(id, player, record, nodeId, datingType, roleCid);
    }

    /** 检测是否能够接受 **/
    public boolean check(int id, Player player, CurrentDatingBean record) {
        return getManagerScript().check(id, player, record);
    }


    /**
     * 创建剧本记录,这是流程的最后一步,可以处理日志
     */
    @Override
    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto) {
         getManagerScript().createCurrentScriptRecord(player, cfg, branchNodes, startNodeId, roleIds, dto);
     
    }
    
    
    @Override
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId) {
        return getManagerScript().getByPlayerIdDatingTypeRoleId(datingType, roleId, player, datingId);
    }
    
}
