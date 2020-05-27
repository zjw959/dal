package logic.dating.handler.logic;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import logic.character.bean.Player;
import logic.constant.DatingConstant;
import logic.constant.DatingTypeConstant;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.GameErrorCode;
import logic.dating.DatingManager;
import logic.dating.DatingService;
import logic.dating.bean.CityDatingBean;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.script.ITriggerDatingHandlerScript;
import logic.dating.handler.script.ITripDatingHandlerScript;
import logic.functionSwitch.FunctionSwitchService;
import logic.msgBuilder.DatingMsgBuilder;
import logic.support.LogBeanFactory;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.ExceptionEx;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CShareMsg;
import data.GameDataManager;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;

/**
 * 出游约会逻辑处理器
 * 
 * @author Alan
 *
 */
public class TripDatingHandler extends DatingHandler {

    private ITripDatingHandlerScript getManagerScript() {
        return LogicScriptsUtils.getITripDatingHandlerScript();
    }
    
    @Override
    public int getHandlerIdentification() {
        return getManagerScript().getHandlerIdentification();
    }

    private static Logger LOGGER = Logger.getLogger(TripDatingHandler.class);

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
    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto) {
        getManagerScript().createCurrentScriptRecord(player, cfg, branchNodes, startNodeId, roleIds, dto, LOGGER);
    }

    @Override
    public void endDatingRecord(Player player, BaseDating cfg, int roleCid,
            CurrentDatingBean record) {
        getManagerScript().endDatingRecord(player, cfg, roleCid, record);
    }

    @Override
    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record) {
        getManagerScript().handleAfterCreatedScriptRecord(player, record, LOGGER);
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

    @Override
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId) {
        return getManagerScript().getByPlayerIdDatingTypeRoleId(datingType, roleId, player, datingId);
    }
}
