package logic.dating.handler.logic;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.script.IDungeonDatingHandlerScript;
import logic.item.bean.Item;
import logic.support.LogicScriptsUtils;

/**
 * 关卡约会逻辑处理器
 * 
 * @author Alan
 *
 */
public class DungeonDatingHandler extends DatingHandler {

    private static Logger LOGGER = Logger.getLogger(DungeonDatingHandler.class);
    
    private IDungeonDatingHandlerScript getManagerScript() {
        return LogicScriptsUtils.getIDungeonDatingHandlerScript();
    }
    
    
    @Override
    public int getHandlerIdentification() {
        return getManagerScript().getHandlerIdentification();
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
    public void checkDating(Player player, DatingScriptDTO dto) {
        getManagerScript().checkDating(player, dto);
    }

    @Override
    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg) {
        getManagerScript().checkDatingRule(player, ruleCfg);
    }

    @Override
    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId,
            List<Integer> roleIds, DatingScriptDTO dto) {
         getManagerScript().createCurrentScriptRecord(player, cfg, branchNodes, startNodeId, roleIds, dto);
    }

    @Override
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType,
            int roleId, Player player,long datingId) {
        return getManagerScript().getByPlayerIdDatingTypeRoleId(datingType, roleId, player, datingId);
    }

    @Override
    public void handleAfterCreatedScriptRecord(Player player,
            CurrentDatingBean record) {
        getManagerScript().handleAfterCreatedScriptRecord(player, record);

    }

    @Override
    public BaseDating getDatingCfgBeanByType(int datingCid,int datingRuleId) {
        return getManagerScript().getDatingCfgBeanByType(datingCid, datingRuleId);
    }

    @Override
    public List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid) {
        return getManagerScript().getDatingCfgBeansByTypeAndScript(scriptCid);
    }

    @Override
    public void endDatingRecord(Player player, BaseDating cfg, int roleCid,
            CurrentDatingBean record) {
        getManagerScript().endDatingRecord(player, cfg, roleCid, record);
    }

    @Override
    public void sendSettlementMsg(Player player, CurrentDatingBean record,
            int favor, BaseDating cfg, List<Item> itemInfos) {
        getManagerScript().sendSettlementMsg(player, record, favor, cfg, itemInfos);
    }

    @Override
    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record) {
         getManagerScript().settlement(player, selectedNodeCid, datingType, roleCid, record, LOGGER);
    }
}
