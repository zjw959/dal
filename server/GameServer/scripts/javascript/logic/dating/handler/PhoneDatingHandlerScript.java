package javascript.logic.dating.handler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import data.GameDataManager;
import data.bean.BaseDating;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.constant.DatingConstant;
import logic.constant.DatingTypeConstant;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.dating.DatingManager;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.script.IPhoneDatingHandlerScript;
import logic.functionSwitch.FunctionSwitchService;
import logic.support.LogBeanFactory;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.ExceptionEx;

public class PhoneDatingHandlerScript implements IPhoneDatingHandlerScript {

    @Override
    public int getHandlerIdentification() {
        return DatingTypeConstant.DATING_TYPE_PHONE;
    }

    @Override
    public void checkDating(Player player, DatingScriptDTO dto) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.DATE_EVENT)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:phone_dating");
        }
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
    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record,
            Logger LOGGER) {
        try {
            LogProcessor.getInstance()
                    .sendLog(LogBeanFactory.createDatingLog(player,
                            record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                            record.getScriptId(), 0, EReason.PHONE_DATING_STIME.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    @Override
    public BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId) {
        return GameDataManager.getDatingCfgBean(datingCid);
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
        dealMessage(player, selectedNodeCid, datingType, roleCid, record);
        LogicScriptsUtils.getIDatingHandlerScript().settlement(player, selectedNodeCid, datingType,
                roleCid, record, LOGGER);
        try {
            LogProcessor.getInstance()
                    .sendLog(LogBeanFactory.createDatingLog(player,
                            record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                            record.getScriptId(), 0, EReason.PHONE_DATING_ETIME.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    /** 处理节点奖励 **/
    @SuppressWarnings("unchecked")
    public void settleNode(Player player, int datingType, int roleCid, CurrentDatingBean record,
            int handlerType) {
        DatingRuleCfgBean bean = GameDataManager.getDatingRuleCfgBean(record.getScriptId());
        if (bean.getPhoneEnd() != null && bean.getPhoneEnd().size() > 0) {
            bean.getPhoneEnd().forEach((datingId, Object) -> {
                if (Object != null) {
                    Map<Integer, Object> awardMap = (Map<Integer, Object>) Object;
                    if (awardMap != null) {
                        awardMap.forEach((id, gift) -> {
                            if (record.getSelectedNode().contains(id)) {
                                if (gift != null) {
                                    Map<Integer, Integer> award = (Map<Integer, Integer>) gift;
                                    player.getBagManager().addItems(award, true,
                                            EReason.DATING_NODE);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    /** 处理信息 **/
    @SuppressWarnings("unchecked")
    public void dealMessage(Player player, int selectedNodeCid, int datingType, int roleCid,
            CurrentDatingBean record) {
        DatingRuleCfgBean bean = GameDataManager.getDatingRuleCfgBean(record.getScriptId());
        if (bean.getPhoneType() == null)
            return;
        bean.getPhoneType().forEach((id, nodeId) -> {
            hanlder((int) id, player, record, (int) nodeId, datingType, roleCid);
        });
    }


    /** 处理 **/
    public void hanlder(int id, Player player, CurrentDatingBean record, int nodeId, int datingType,
            int roleCid) {
        switch (id) {
            case DatingConstant.PHONE_RESERVEDAING:
                if (check(id, player, record)) {
                    player.getDatingManager()
                            .acceptDating(record.getSelectedNode().contains(nodeId), player);
                    settleNode(player, datingType, roleCid, record, id);
                }
                break;
            default:
                break;
        }
    }

    /** 检测是否能够接受 **/
    public boolean check(int id, Player player, CurrentDatingBean record) {
        if (id == DatingConstant.PHONE_RESERVEDAING) {
            return player.getDatingManager().checkReserveDating();
        }
        return false;
    }


    /**
     * 创建剧本记录,这是流程的最后一步,可以处理日志
     */
    @Override
    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto) {
        DatingManager dm = player.getDatingManager();
        CurrentDatingBean bean = dm.getByPlayerIdDatingTypeRoleId(cfg.getType(), roleIds);
        if (bean == null)
            LogicScriptsUtils.getIDatingHandlerScript().createCurrentScriptRecord(player, cfg,
                    branchNodes, startNodeId, roleIds, dto);
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
    public int getScriptId() {
        return EScriptIdDefine.PHONE_HANDLERE_SCRIPT.Value();
    }



}
