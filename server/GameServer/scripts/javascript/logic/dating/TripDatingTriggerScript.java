package javascript.logic.dating;

import java.util.Date;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import data.bean.DatingRuleCfgBean;
import logic.character.bean.Player;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.dating.DatingManager;
import logic.dating.bean.CityDatingBean;
import logic.dating.script.IBaseDatingScript;
import logic.dating.script.ITripDatingTriggerScript;
import logic.role.bean.Role;
import logic.support.LogBeanFactory;
import logic.support.LogicScriptsUtils;
import thread.log.LogProcessor;
import utils.ExceptionEx;

/***
 * 
 * 出游约会脚本
 * 
 * @author lihongji
 *
 */

public class TripDatingTriggerScript implements ITripDatingTriggerScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.TRIPDATING_SCRIPT.Value();
    }

    /** 获取可用建筑 **/
    @Override
    public Set<Integer> getValidBuildings(Player player) {
        return LogicScriptsUtils.getIDatingTriggerScript().getValidBuildings(player);
    }

    /** 刷新城市约会 **/
    @Override
    public void refreshCityDating(Player player, Date now, IBaseDatingScript baseScipt,
            Logger LOGGER) {
        LogicScriptsUtils.getIDatingTriggerScript().refreshCityDating(player, now,
                LogicScriptsUtils.getIBaseTripDatingTriggerScript(), LOGGER);
    }

    @Override
    public void triggerDating(Player player, Date now, IBaseDatingScript baseScipt) {
        LogicScriptsUtils.getIDatingTriggerScript().triggerDating(player, now,
                LogicScriptsUtils.getIBaseTripDatingTriggerScript());
    }

    /** 获取可用建筑 **/
    @Override
    public List<Integer> getFreeRole(Player player, IBaseDatingScript baseScipt) {
        return LogicScriptsUtils.getIDatingTriggerScript().getFreeRole(player,
                LogicScriptsUtils.getIBaseTripDatingTriggerScript());
    }

    /** 获取可用剧本 **/
    @Override
    public List<DatingRuleCfgBean> getUsableOutDatingRuleCfg(List<Integer> roleCidList,
            Set<Integer> buildingCidList, Player player, IBaseDatingScript baseScipt) {
        return LogicScriptsUtils.getIDatingTriggerScript().getUsableOutDatingRuleCfg(roleCidList,
                buildingCidList, player, LogicScriptsUtils.getIBaseTripDatingTriggerScript());
    }

    /** 获取几率 **/
    @Override
    public int getDampingRate(Role role, Player player) {
        return LogicScriptsUtils.getIDatingTriggerScript().getDampingRate(role, player);
    }

    /** 生成城市约会 **/
    @Override
    public CityDatingBean createCityDatingBean(DatingManager dm, DatingRuleCfgBean datingRuleCfg,
            List<Integer> datingFrame, Date now, IBaseDatingScript baseScipt) {
        return LogicScriptsUtils.getIDatingTriggerScript().createCityDatingBean(dm, datingRuleCfg,
                datingFrame, now, LogicScriptsUtils.getIBaseTripDatingTriggerScript());
    }

    /** 获取约会类型 **/
    @Override
    public int getDatingType() {
        return LogicScriptsUtils.getIBaseTripDatingTriggerScript().getDatingType();
    }

    /** 增加日志 **/
    @Override
    public void addDatingLog(Player player, CityDatingBean record, Logger LOGGER) {
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createDatingLog(player,
                    record.getRoleIds() == null ? 0 : record.getRoleIds().get(0),
                    record.getScriptId(), 0, EReason.TRIP_DATING_SYSTEM_STIME.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

}
