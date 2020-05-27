package javascript.logic.dating.handler;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import logic.dating.handler.logic.ReserveDatingHandler;
import logic.dating.handler.script.IReserveDatingHandlerScript;
import logic.functionSwitch.FunctionSwitchService;
import logic.msgBuilder.DatingMsgBuilder;
import logic.role.bean.Role;
import logic.support.LogBeanFactory;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.ExceptionEx;

public class ReserveDatingHandlerScript implements IReserveDatingHandlerScript {

    @Override
    public int getHandlerIdentification() {
        return DatingTypeConstant.DATING_TYPE_RESERVE;
    }

    private static Logger LOGGER = Logger.getLogger(ReserveDatingHandler.class);

    @Override
    public void checkDating(Player player, DatingScriptDTO dto) {

        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.DATE_EVENT)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:city_dating");
        }

        long cityDatingRecordId = Long.valueOf(dto.getCityDatingId());
        DatingManager dm = player.getDatingManager();
        CityDatingBean record = dm.getCityDating(cityDatingRecordId);
        if (record == null)
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "获取预约约会剧本，记录id错误");
        // 在邀请状态或者是无约会的状态或者是未接受邀请状态不能进行约会
        if (record.getState() == DatingConstant.RESERVE_DATING_STATE_NO_DATING
                || record.getState() == DatingConstant.RESERVE_DATING_STATE_INVITATION)
            MessageUtils.throwCondtionError(GameErrorCode.DATING_IS_NO_ACCEPT_STATE,
                    "非接受邀请状态，不能进行约会");
        if (System.currentTimeMillis() < record.getDatingBeginTime())
            MessageUtils.throwCondtionError(GameErrorCode.IS_NOT_DATING_TIME_YET, "预定约会时间未到");
    }

    @Override
    public void handleDatingResource(Player player, DatingScriptDTO dto) {
        long cityDatingRecordId = Long.valueOf(dto.getCityDatingId());
        DatingManager dm = player.getDatingManager();
        CityDatingBean record = dm.getCityDating(cityDatingRecordId);
        DatingRuleCfgBean datingRuleCfg =
                GameDataManager.getDatingRuleCfgBean(record.getScriptId());

        // 判断时间
        int timeState = getReverseDatingTimeState(record);
        if (timeState == DatingConstant.RESERVE_DATING_TIME_STATE_LATE_IN_DAY) {
            // 约会迟到处理：
            // 1.迟到惩罚
            // 2.发送失败剧本id
            // 3.删除城市约会记录，并发送到客户端
            // 4.更新城市状态为非约会占用状态
            // 需要看板娘模块支持
            Role role = player.getRoleManager().getRole(record.getRoleIds().get(0));
            // RoleCache.me().getByCidPlayerId(record.getCityDatingRecordProxy().getRoleCids().get(0),player.getId());

            // LogDsp logDsp = LogDsp.newBuilder(GoodsDsp.RESERVE_FAIL);
            // Optional对null对象的友好处理
            Optional<Map> optMap = Optional
                    .ofNullable((Map) datingRuleCfg.getOtherInfo().get(DatingConstant.FAIL_REWARD));
            // 好感度处理
            Optional<Integer> optFavor =
                    optMap.map(map -> (Integer) map.get(DatingConstant.ANSWER_FAVOR));
            if (optFavor.isPresent()) {
                // 需要看板娘模块支持
                player.getRoleManager().changeFavor(role, optFavor.get(), EReason.DATING_BEGIN);
            }
            // 心情值处理
            Optional<Integer> optMood =
                    optMap.map(map -> (Integer) map.get(DatingConstant.ANSWER_MOOD));
            if (optMood.isPresent()) {
                // 需要看板娘模块支持
                player.getRoleManager().changeMood(role, optMood.get(), EReason.DATING_BEGIN);
            }
            // 这里删除了约会记录,后续的操作都需要对记录判定以给出相应的返回
            dm.deleteDating(record);
            // 缓存被删除的剧本id
            dto.setScriptId(datingRuleCfg.getId());
            MessageUtils.send(player, DatingMsgBuilder
                    .getCityDatingInfo(S2CShareMsg.ChangeType.DELETE, record, false));
            DatingService.getInstance().clearCity(player, datingRuleCfg);
        }
    }

    @Override
    public DatingRuleCfgBean getDatingRuleCfg(Player player, DatingScriptDTO dto) {
        int scriptId = getCurrentDatingRuleCfgId(player, dto);
        DatingRuleCfgBean datingRuleCfg = GameDataManager.getDatingRuleCfgBean(scriptId);
        return datingRuleCfg;
    }

    @Override
    public List<Integer> getDatingRoles(Player player, DatingScriptDTO dto) {
        // 触发约会时创建,不再需要玩家请求时创建
        return null;
    }

    @Override
    public void checkDatingRule(Player player, DatingRuleCfgBean ruleCfg) {

    }

    @Override
    public void createCurrentScriptRecord(Player player, DatingRuleCfgBean cfg,
            Map<Integer, List<Integer>> branchNodes, int startNodeId, List<Integer> roleIds,
            DatingScriptDTO dto) {
        // 触发约会时创建,不再需要玩家请求时创建
        CityDatingBean record = getCityDatingRecord(player, dto);
        // 使用此时检测的节点信息,设置节点
        record.setCurrentScript(branchNodes);
        handleAfterCreatedScriptRecord(player, record);
    }

    @Override
    public Map<Integer, List<Integer>> getScriptBranchNode(Player player, int scriptId,
            DatingScriptDTO dto) {
        // 记录删除后返回空数据
        if (!CityDatingBeanExists(player, dto))
            return null;
        return LogicScriptsUtils.getIDatingHandlerScript().getScriptBranchNode(player, scriptId,
                dto);
    }

    @Override
    public boolean CityDatingBeanExists(Player player, DatingScriptDTO dto) {
        return getCityDatingRecord(player, dto) != null;
    }

    @Override
    public CityDatingBean getCityDatingRecord(Player player, DatingScriptDTO dto) {
        long cityDatingRecordId = Long.valueOf(dto.getCityDatingId());
        DatingManager dm = player.getDatingManager();
        CityDatingBean record = dm.getCityDating(cityDatingRecordId);
        return record;
    }

    /** 可能已经删除了约会记录,后续的操作都需要对记录判定以从缓存获取被删除的剧本id */
    @Override
    public int getCurrentDatingRuleCfgId(Player player, DatingScriptDTO dto) {
        CityDatingBean record = getCityDatingRecord(player, dto);
        if (record == null) {
            DatingRuleCfgBean tempDatingRuleCfg =
                    GameDataManager.getDatingRuleCfgBean(dto.getScriptId());
            return (int) tempDatingRuleCfg.getOtherScriptIds().get(DatingConstant.FAIL_SCRIPT_ID);
        }
        if (record.getState() == DatingConstant.RESERVE_DATING_STATE_INVITATION) {
            DatingRuleCfgBean tempDatingRuleCfg =
                    GameDataManager.getDatingRuleCfgBean(dto.getScriptId());
            return (int) tempDatingRuleCfg.getOtherScriptIds().get(DatingConstant.INVITE_SCRIPT_ID);
        }
        return record.getScriptId();
    }

    /**
     * 获取预约约会的时间状态
     *
     * @return 0：正常约会时间 1：迟到，但未跨天 2：迟到且跨天
     */
    @Override
    public int getReverseDatingTimeState(CityDatingBean record) {
        Calendar beginTime = Calendar.getInstance();
        beginTime.setTimeInMillis(record.getDatingBeginTime());

        Calendar endTime = Calendar.getInstance();
        endTime.setTimeInMillis(record.getDatingEndTime());

        Calendar now = Calendar.getInstance();
        if (beginTime.getTimeInMillis() <= now.getTimeInMillis()
                && endTime.getTimeInMillis() >= now.getTimeInMillis()) {
            return DatingConstant.RESERVE_DATING_TIME_STATE_ON_TIME;
        } else {
            return DatingConstant.RESERVE_DATING_TIME_STATE_LATE_IN_DAY;
        }
    }

    @Override
    public void handleAfterCreatedScriptRecord(Player player, CurrentDatingBean record) {
        // 玩家操作状态设置
        CityDatingBean cityRecord = (CityDatingBean) record;
        cityRecord.setPlayerBeginTime(System.currentTimeMillis());
        try {
            LogProcessor.getInstance()
                    .sendLog(LogBeanFactory.createDatingLog(player,
                            record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                            record.getScriptId(), 0, EReason.RESERVER_DATING_STIME.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }

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
        player.getDatingManager().removePhoneDating(player, record);
        LogicScriptsUtils.getIDatingHandlerScript().endDatingRecord(player, cfg, roleCid, record);
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
                            record.getScriptId(), 0, EReason.RESERVER_DATING_ETIME.value(), null));
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
    public int getScriptId() {
        return EScriptIdDefine.RESERVE_HANDLER_SCRIPT.Value();
    }

}
