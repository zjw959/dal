package javascript.logic.dating.handler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CShareMsg;
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
import logic.dating.DatingService;
import logic.dating.bean.CityDatingBean;
import logic.dating.bean.CurrentDatingBean;
import logic.dating.bean.dto.DatingScriptDTO;
import logic.dating.handler.logic.TripDatingHandler;
import logic.dating.handler.script.ITriggerDatingHandlerScript;
import logic.dating.handler.script.ITripDatingHandlerScript;
import logic.functionSwitch.FunctionSwitchService;
import logic.msgBuilder.DatingMsgBuilder;
import logic.support.LogBeanFactory;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.ExceptionEx;

public class TripDatingHandlerScript implements ITripDatingHandlerScript {

    @Override
    public int getHandlerIdentification() {
        return DatingTypeConstant.DATING_TYPE_OUT;
    }

    @Override
    public void checkDating(Player player, DatingScriptDTO dto) {

        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.DATE_EVENT)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:city_dating");
        }

        DatingManager dm = player.getDatingManager();
        CityDatingBean record = dm.getCityDating(Long.valueOf(dto.getCityDatingId()));
        if (record == null)
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "没有找到CityDatingRecord，record==null");
        if (record.getDatingType() != getHandlerIdentification())
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "记录类型错误,record.getDatingType()!=DatingTypeConstant.DATING_TYPE_WORK");
    }

    @Override
    public void handleDatingResource(Player player, DatingScriptDTO dto) {

    }

    @Override
    public DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto) {
        DatingManager dm = player.getDatingManager();
        CityDatingBean record = dm.getCityDating(Long.valueOf(dto.getCityDatingId()));
        DatingRuleCfgBean datingRuleCfg =
                GameDataManager.getDatingRuleCfgBean(record.getScriptId());
        return datingRuleCfg;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Integer> getDatingRoles(Player player, DatingScriptDTO dto) {

        CityDatingBean bean =
                player.getDatingManager().getCityDating(Integer.parseInt(dto.getCityDatingId()));

        DatingRuleCfgBean datingRuleCfg = GameDataManager.getDatingRuleCfgBean(bean.getScriptId());
        List<Integer> roleCidList = (List<Integer>) datingRuleCfg.getEnterCondition()
                .get(DatingConstant.ENTER_CDTION_KEY_ROLE_CIDS);
        return roleCidList;
    }

    @Override
    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg) {

    }

    @Override
    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto, Logger LOGGER) {
        // 触发约会时创建,不再需要玩家请求时创建
        DatingManager dm = player.getDatingManager();
        CityDatingBean record = dm.getCityDating(Long.valueOf(dto.getCityDatingId()));
        // 使用此时检测的节点信息,设置节点
        record.setCurrentScript(branchNodes);
        handleAfterCreatedScriptRecord(player, record, LOGGER);
    }

    @Override
    public void endDatingRecord(Player player, BaseDating cfg, int roleCid,
            CurrentDatingBean record) {
        DatingManager dm = player.getDatingManager();
        CityDatingBean cityDatingRecord =
                (CityDatingBean) getByPlayerIdDatingTypeRoleId(getHandlerIdentification(), roleCid,
                        player, record.getId());
        // 城市约会记录有可能在约会完成之前就被定时器删除掉，删除以后不会影响当前约会
        if (cityDatingRecord != null) {
            dm.deleteDating(cityDatingRecord);
            MessageUtils.send(player, DatingMsgBuilder
                    .getCityDatingInfo(S2CShareMsg.ChangeType.DELETE, cityDatingRecord, true));
            DatingRuleCfgBean ruleCfg =
                    GameDataManager.getDatingRuleCfgBean(cityDatingRecord.getScriptId());
            DatingService.getInstance().clearCity(player, ruleCfg);
        }
        LogicScriptsUtils.getIDatingHandlerScript().endDatingRecord(player, cfg, roleCid, record);
    }

    @Override
    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record,
            Logger LOGGER) {
        // 玩家操作状态设置
        CityDatingBean cityRecord = (CityDatingBean) record;
        cityRecord.setPlayerBeginTime(System.currentTimeMillis());
        try {
            LogProcessor.getInstance()
                    .sendLog(LogBeanFactory.createDatingLog(player,
                            record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                            record.getScriptId(), 0, EReason.TRIP_DATING_STIME.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    @Override
    public BaseDating getDatingCfgBeanByType(int datingCid, int datingRuleId) {
        // 分表获取
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
        LogicScriptsUtils.getIDatingHandlerScript().settlement(player, selectedNodeCid, datingType,
                roleCid, record, LOGGER);
        try {
            LogProcessor.getInstance()
                    .sendLog(LogBeanFactory.createDatingLog(player,
                            record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                            record.getScriptId(), 0, EReason.TRIP_DATING_ETIME.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    @Override
    public CurrentDatingBean getByPlayerIdDatingTypeRoleId(int datingType, int roleId,
            Player player, long datingId) {
        DatingManager dm = player.getDatingManager();
        // 此逻辑可以直接用id做索引查找,用组合条件的原因？——客户端设计当前约会对象唯一（玩家使用客户端只能同时进行一场约会）,未发送id，可以后续改写
        CurrentDatingBean record = dm.getByDatingTypeRoleId(datingType, roleId, datingId);
        return record;
    }

    @Override
    public int getScriptId() {
        return EScriptIdDefine.TTRIP_HANDLER_SCRIPT.Value();
    }

}
