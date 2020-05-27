package logic.dating.handler.logic;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.script.IReserveDatingHandlerScript;
import logic.support.LogicScriptsUtils;

/**
 * 预定约会逻辑处理器
 * 
 * @author Alan
 *
 */
public class ReserveDatingHandler extends DatingHandler {

    
    private static Logger LOGGER = Logger.getLogger(ReserveDatingHandler.class);
   
    
    private IReserveDatingHandlerScript getManagerScript() {
        return LogicScriptsUtils.getIReserveDatingHandlerScript();
    }
    
    @Override
    public int getHandlerIdentification() {
        return getManagerScript().getHandlerIdentification();
    }
   

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
        // 触发约会时创建,不再需要玩家请求时创建
        return getManagerScript().getDatingRoles(player, dto);
    }

    @Override
    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg) {
        getManagerScript().checkDatingRule(player, ruleCfg);
    }

    @Override
    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto) {
        getManagerScript().createCurrentScriptRecord(player, cfg, branchNodes, startNodeId, roleIds, dto);
    }

    @Override
    public Map<Integer, List<Integer>> getScriptBranchNode(Player player, int scriptId,
            DatingScriptDTO dto) {
            return getManagerScript().getScriptBranchNode(player, scriptId, dto);
    }

//    private boolean CityDatingBeanExists(Player player, DatingScriptDTO dto) {
//        return getManagerScript().CityDatingBeanExists(player, dto);
//    }
//
//    private CityDatingBean getCityDatingRecord(Player player, DatingScriptDTO dto) {
//        return getManagerScript().getCityDatingRecord(player, dto);
//    }
//
//    /** 可能已经删除了约会记录,后续的操作都需要对记录判定以从缓存获取被删除的剧本id */
//    private int getCurrentDatingRuleCfgId(Player player, DatingScriptDTO dto) {
//        return getManagerScript().getCurrentDatingRuleCfgId(player, dto);
//    }

//    /**
//     * 获取预约约会的时间状态
//     *
//     * @return 0：正常约会时间 1：迟到，但未跨天 2：迟到且跨天
//     */
//    private int getReverseDatingTimeState(CityDatingBean record) {
//        return getManagerScript().getReverseDatingTimeState(record);
//    }

    @Override
    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record) {
        getManagerScript().handleAfterCreatedScriptRecord(player, record);

    }

    @Override
    public void endDatingRecord(Player player, BaseDating cfg, int roleCid,
            CurrentDatingBean record) {
        getManagerScript().endDatingRecord(player, cfg, roleCid, record);
    }

    @Override
    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record) {
        getManagerScript().settlement(player, selectedNodeCid, datingType, roleCid, record, LOGGER);
    }
    
    @Override
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId) {
        return getManagerScript().getByPlayerIdDatingTypeRoleId(datingType, roleId, player, datingId);
    }
	@Override
    public BaseDating getDatingCfgBeanByType(int datingCid,int datingRuleId) {
	    return getManagerScript().getDatingCfgBeanByType(datingCid, datingRuleId);
	}

	@Override
    public List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid) {
		return getManagerScript().getDatingCfgBeansByTypeAndScript(scriptCid);
	}
}
