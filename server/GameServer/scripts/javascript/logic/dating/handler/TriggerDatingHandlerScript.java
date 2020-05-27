package javascript.logic.dating.handler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.constant.DatingTypeConstant;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.dating.DatingManager;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.logic.TriggerDatingHandler;
import logic.dating.handler.script.ITriggerDatingHandlerScript;
import logic.functionSwitch.FunctionSwitchService;
import logic.role.bean.Role;
import logic.support.LogBeanFactory;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.ExceptionEx;

public class TriggerDatingHandlerScript implements ITriggerDatingHandlerScript {

    @Override
    public int getHandlerIdentification() {
        return DatingTypeConstant.DATING_TYPE_TRIGGER;
    }

    private static Logger LOGGER = Logger.getLogger(TriggerDatingHandler.class);

    @Override
    public void checkDating(Player player, DatingScriptDTO dto) {

        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.DATE_EVENT)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:trigger_dating");
        }
        int scriptId = dto.getScriptId();
        int roleId = dto.getRoleId();
        // 看板娘模块检测
        Role role = player.getRoleManager().getRole(roleId);
        if (role == null)
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "触发约会，无效的roleId");
        DatingManager dm = player.getDatingManager();
        if (!dm.roleTriggerDatingExists(roleId, scriptId))
            MessageUtils.throwCondtionError(GameErrorCode.NOT_TRIGGER_SCRIPT,
                    "触发约会，无效的剧本id，改剧本未触发");
        if (dm.getCompleteDatings().contains(scriptId))
            MessageUtils.throwCondtionError(GameErrorCode.COMPLETED_DATING, "触发约会，已经完成的约会");
    }

    @Override
    public void handleDatingResource(Player player, DatingScriptDTO dto) {

    }

    @Override
    public DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto) {
        DatingRuleCfgBean datingRuleCfg = GameDataManager.getDatingRuleCfgBean(dto.getScriptId());
        return datingRuleCfg;
    }

    @Override
    public List<Integer> getDatingRoles(Player player, DatingScriptDTO dto) {
        return Arrays.asList(dto.getRoleId());
    }

    @Override
    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg) {

    }

    @Override
    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record) {
        try {
            LogProcessor.getInstance()
                    .sendLog(LogBeanFactory.createDatingLog(player,
                            record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                            record.getScriptId(), 0, EReason.TRIGGER_DATING_STIME.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    @Override
    public List<BaseDating> getDatingCfgBeansByTypeAndScript(int scriptCid) {
        // 分表获取
        List<BaseDating> allCfgList = GameDataManager.getDatingCfgBeans().stream()
                .filter(cfg -> cfg.getScriptId() == scriptCid).collect(Collectors.toList());
        return allCfgList;
    }

    @Override
    public void settlement(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record, Logger LOGGER) {
        LogicScriptsUtils.getIDatingHandlerScript().settlement(player, selectedNodeCid, datingType,
                roleCid, record, LOGGER);
        try {
            LogProcessor.getInstance()
                    .sendLog(LogBeanFactory.createDatingLog(player,
                            record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                            record.getScriptId(), 0, EReason.TRIGGER_DATING_ETIME.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    @Override
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId) {
        DatingManager dm = player.getDatingManager();
        // 此逻辑可以直接用id做索引查找,用组合条件的原因？——客户端设计当前约会对象唯一（玩家使用客户端只能同时进行一场约会）,未发送id，可以后续改写
        CurrentDatingBean record = dm.getByDatingTypeRoleId(datingType, roleId);
        return record;
    }

    @Override
    public BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId) {
        // 分表获取
        return GameDataManager.getDatingCfgBean(datingCid);
    }

    @Override
    public int getScriptId() {
        return EScriptIdDefine.TRIGGER_HANDLER_SCRIPT.Value();
    }

}
