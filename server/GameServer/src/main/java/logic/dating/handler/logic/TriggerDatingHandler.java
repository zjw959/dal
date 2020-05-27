package logic.dating.handler.logic;

import java.util.List;
import org.apache.log4j.Logger;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.script.ITriggerDatingHandlerScript;
import logic.support.LogicScriptsUtils;

/**
 * 触发约会
 * 
 * @author Alan
 *
 */
public class TriggerDatingHandler extends DatingHandler {

    private ITriggerDatingHandlerScript getManagerScript() {
        return LogicScriptsUtils.getITriggerDatingHandlerScript();
    }
    
    @Override
    public int getHandlerIdentification() {
        return getManagerScript().getHandlerIdentification();
    }

    private static Logger LOGGER = Logger.getLogger(TriggerDatingHandler.class);

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
        getManagerScript().handleAfterCreatedScriptRecord(player, record);
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

    @Override
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId) {
       return getManagerScript().getByPlayerIdDatingTypeRoleId(datingType, roleId, player, datingId);
    }

    @Override
    public BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId) {
        return getManagerScript().getDatingCfgBeanByType(datingCid, datingRuleId);
    }
}
