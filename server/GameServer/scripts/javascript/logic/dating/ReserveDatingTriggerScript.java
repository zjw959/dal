package javascript.logic.dating;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CShareMsg;

import data.GameDataManager;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.constant.DatingConstant;
import logic.constant.DatingTypeConstant;
import logic.constant.DiscreteDataID;
import logic.constant.DiscreteDataKey;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.constant.RoleConstant;
import logic.dating.DatingManager;
import logic.dating.DatingService;
import logic.dating.bean.CityDatingBean;
import logic.dating.script.IBaseDatingScript;
import logic.dating.script.IReserveDatingTriggerScript;
import logic.msgBuilder.DatingMsgBuilder;
import logic.msgBuilder.RoleMsgBuilder;
import logic.role.bean.Role;
import logic.support.LogBeanFactory;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.ExceptionEx;
import utils.TimeUtil;

/***
 * 
 * 预定约会剧本
 * 
 * @author lihongji
 *
 */

public class ReserveDatingTriggerScript implements IReserveDatingTriggerScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.RESERVEDATINGTRIGGER_SCRIPT.Value();
    }

    /** 获取可用建筑 **/
    @Override
    public Set<Integer> getValidBuildings(Player player) {
        return LogicScriptsUtils.getIDatingTriggerScript().getValidBuildings(player);
    }

    /** 生成约会记录 **/
    @Override
    public CityDatingBean createCityDatingBean(DatingManager dm, DatingRuleCfgBean datingRuleCfg,
            List<Integer> datingFrame, Date now, IBaseDatingScript baseScipt) {
        CityDatingBean dating =
                LogicScriptsUtils.getIDatingTriggerScript().createCityDatingBean(dm, datingRuleCfg,
                        datingFrame, now, LogicScriptsUtils.getIBaseReserveDatingTriggerScript());
        dating.setState(DatingConstant.RESERVE_DATING_STATE_INVITATION);
        return dating;
    }

    /** 衰减 **/
    @Override
    public int getTriggerRate(Role role, DatingRuleCfgBean datingRuleCfg, Player player) {
        int mood = role.getMood();
        int cfgMood = (Integer) GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.DATING)
                .getData().get(DiscreteDataKey.MOOD);
        if (mood <= cfgMood)
            return 0;
        int percent =
                (mood - 60) * 10 / 100 + 10 + role.getEffectByType(RoleConstant.EFFECT_TYPE_3);
        return percent * getDampingRate(role, player) / 100;
    }

    /**
     * 更新约会状态,返回是否过期
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public boolean datingTimeUpdate(CityDatingBean record, Date date, Player player,Logger LOGGER) {
        long beginTime = record.getDatingBeginTime();
        long endTime = record.getDatingEndTime();
        boolean timeout = false;
        Map data = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.DATING).getData();
        Map<Integer, Integer> datingTimeout =
                (Map<Integer, Integer>) data.get(DiscreteDataKey.DATING_OUT_TIME);
        if (isBeginningDating(date, beginTime, endTime, record)) {
            // 约会时间开始
            record.setState(DatingConstant.RESERVE_DATING_STATE_DATING_TIME_START);
            List<CityDatingBean> refresh = new ArrayList<>();
            refresh.add(record);
            MessageUtils.send(player, DatingMsgBuilder
                    .buildCityDatingInfoList(S2CShareMsg.ChangeType.UPDATE, refresh));
        } else if (isLateDating(date, endTime, record)) {
            // 约会时间已过
            record.setState(DatingConstant.RESERVE_DATING_STATE_DATING_TIME_END);
            sendFailAward(player, record);
            addFailLog(player, record, LOGGER,EReason.RESERVER_DATING_SYSTEM_LATE.value());
            player.getDatingManager().deleteDating(record);
            player.getDatingManager().removePhoneDating(player, record);
            timeout = true;
        } else if (record.getState() == DatingConstant.RESERVE_DATING_STATE_INVITATION) {
            int outDatingTime = datingTimeout.get(DatingTypeConstant.DATING_TYPE_RESERVE);
            // 未接受邀请且超时
            if ((int) ((date.getTime() - record.getCreateTime())
                    / TimeUtil.MINUTE) >= outDatingTime) {
                record.setState(DatingConstant.RESERVE_DATING_STATE_NO_DATING);
                player.getDatingManager().deleteDating(record);
                player.getDatingManager().removePhoneDating(player, record);
                timeout = true;
            }
        }
        // 邀请且超时
        else if (record.getState() == DatingConstant.RESERVE_DATING_STATE_DATING_TIME_START) {
            if (date.getTime() > record.getDatingEndTime()) {
                // 移除约会
                sendFailAward(player, record);
                addFailLog(player, record, LOGGER,EReason.RESERVER_DATING_LATE.value());
                player.getDatingManager().deleteDating(record);
                player.getDatingManager().removePhoneDating(player, record);
                timeout = true;
            }
        }
        // 只要超时就移除记录
        if (date.getTime() > record.getDatingEndTime()) {
            player.getDatingManager().deleteDating(record);
            player.getDatingManager().removePhoneDating(player, record);
            timeout = true;
        }
        return timeout;
    }

    /** 迟到 */
    @Override
    public boolean isLateDating(Date date, long endTime, CityDatingBean record) {
        return record.getState() == DatingConstant.RESERVE_DATING_STATE_ACCEPT_INVITATION
                && endTime <= date.getTime();
    }

    /** 进行 */
    @Override
    public boolean isBeginningDating(Date date, long beginTime, long endTime,
            CityDatingBean record) {
        return record.getState() == DatingConstant.RESERVE_DATING_STATE_ACCEPT_INVITATION
                && beginTime <= date.getTime() && endTime >= date.getTime();
    }

    /** 发送失败奖励 **/
    @Override
    public void sendFailAward(Player player, CityDatingBean record) {
        DatingRuleCfgBean datingRuleCfg =
                GameDataManager.getDatingRuleCfgBean(record.getScriptId());
        Role role = player.getRoleManager().getRole(record.getRoleIds().get(0));
        @SuppressWarnings("rawtypes")
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
    }

    /** 接受邀请 **/
    @Override
    public void acceptDating(boolean accept, Player player, Logger LOGGER) {
        List<CityDatingBean> beans = player.getDatingManager()
                .getCurrentCityDatings(DatingTypeConstant.DATING_TYPE_RESERVE);
        if (beans == null || beans.size() <= 0)
            return;
        CityDatingBean record = beans.get(0);
        if (record.getState() != DatingConstant.RESERVE_DATING_STATE_INVITATION)
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "并非受邀请状态");
        int type = EReason.RESERVER_DATING_ACCPET.value();
        if (accept) {
            record.setState(DatingConstant.RESERVE_DATING_STATE_ACCEPT_INVITATION);
        } else {
            type = EReason.RESERVER_DATING_NOT_ACCPET.value();
            record.setState(DatingConstant.RESERVE_DATING_STATE_NO_DATING);
            player.getDatingManager().deleteDating(record);
            DatingRuleCfgBean cfg = GameDataManager.getDatingRuleCfgBean(record.getScriptId());
            DatingService.getInstance().clearCity(player, cfg);
        }
        Role role = player.getRoleManager().getRole(record.getRoleIds().get(0));
        MessageUtils.send(player,
                RoleMsgBuilder.createRoleInfo(S2CShareMsg.ChangeType.UPDATE, player, role));

        MessageUtils.send(player,
                DatingMsgBuilder.getCityDatingInfo(
                        accept ? S2CShareMsg.ChangeType.UPDATE : S2CShareMsg.ChangeType.DELETE,
                        record, true));

        MessageUtils.send(player, DatingMsgBuilder.createDatingAcceptMsg(accept));

        try {
            LogProcessor.getInstance()
                    .sendLog(LogBeanFactory.createDatingLog(player,
                            record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                            record.getScriptId(), 0, type, null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    /** 检查是否超时 **/
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public boolean checkReserveDating(Player player) {
        List<CityDatingBean> beans = player.getDatingManager()
                .getCurrentCityDatings(DatingTypeConstant.DATING_TYPE_RESERVE);
        if (beans == null || beans.size() <= 0)
            return false;
        CityDatingBean record = beans.get(0);
        Map data = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.DATING).getData();
        Map<Integer, Integer> datingTimeout =
                (Map<Integer, Integer>) data.get(DiscreteDataKey.DATING_OUT_TIME);
        Date date = new Date();
        int outDatingTime = datingTimeout.get(DatingTypeConstant.DATING_TYPE_RESERVE);
        if (record.getState() != DatingConstant.RESERVE_DATING_STATE_INVITATION)
            return false;
        if ((int) ((date.getTime() - record.getCreateTime()) / TimeUtil.MINUTE) >= outDatingTime) {
            return false;
        }
        return true;
    }

    /**
     * 获得衰减几率
     */
    @Override
    public int getDampingRate(Role role, Player player) {
        return LogicScriptsUtils.getIDatingTriggerScript().getDampingRate(role, player);
    }

    /** 刷新城市 **/
    @Override
    public void refreshCityDating(Player player, Date now, IBaseDatingScript baseScipt,Logger LOGGER) {
        LogicScriptsUtils.getIDatingTriggerScript().refreshCityDating(player, now,
                LogicScriptsUtils.getIBaseReserveDatingTriggerScript(),LOGGER);
    }

    @Override
    public void triggerDating(Player player, Date now, IBaseDatingScript baseScipt) {
        LogicScriptsUtils.getIDatingTriggerScript().triggerDating(player, now,
                LogicScriptsUtils.getIBaseReserveDatingTriggerScript());
    }

    /** 获取空闲精灵 **/
    @Override
    public List<Integer> getFreeRole(Player player, IBaseDatingScript baseScipt) {
        return LogicScriptsUtils.getIDatingTriggerScript().getFreeRole(player,
                LogicScriptsUtils.getIBaseReserveDatingTriggerScript());
    }

    /** 获取可用剧本 **/
    @Override
    public List<DatingRuleCfgBean> getUsableOutDatingRuleCfg(List<Integer> roleCidList,
            Set<Integer> buildingCidList, Player player, IBaseDatingScript baseScipt) {
        return LogicScriptsUtils.getIDatingTriggerScript().getUsableOutDatingRuleCfg(roleCidList,
                buildingCidList, player, LogicScriptsUtils.getIBaseReserveDatingTriggerScript());
    }

    /** 获取约会类型 **/
    @Override
    public int getDatingType() {
        return LogicScriptsUtils.getIBaseReserveDatingTriggerScript().getDatingType();
    }

    /** 增加约会日志 **/
    @Override
    public void addDatingLog(Player player, CityDatingBean record, Logger LOGGER) {
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createDatingLog(player,
                    record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                    record.getScriptId(), 0, EReason.RESERVER_DATING_SYSTEM_STIME.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    @Override
    public void addFailLog(Player player, CityDatingBean record, Logger LOGGER,int reasonType) {
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createDatingLog(player,
                    record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                    record.getScriptId(), 0, EReason.RESERVER_DATING_LATE.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }
}
