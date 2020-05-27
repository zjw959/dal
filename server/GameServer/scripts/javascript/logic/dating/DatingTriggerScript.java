package javascript.logic.dating;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CShareMsg;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import data.GameDataManager;
import data.bean.DatingRuleCfgBean;
import data.bean.DiscreteDataCfgBean;
import logic.character.bean.Player;
import logic.constant.DatingConstant;
import logic.constant.DiscreteDataID;
import logic.constant.DiscreteDataKey;
import logic.constant.EFunctionType;
import logic.constant.EScriptIdDefine;
import logic.dating.DatingManager;
import logic.dating.DatingService;
import logic.dating.bean.CityDatingBean;
import logic.dating.script.IBaseDatingScript;
import logic.dating.script.ICityDatingTimeOutCheckScript;
import logic.dating.script.IDatingTriggerScript;
import logic.functionSwitch.FunctionSwitchService;
import logic.msgBuilder.DatingMsgBuilder;
import logic.role.RoleManager;
import logic.role.bean.Role;
import logic.support.MessageUtils;
import utils.RandomEx;
import utils.TimeUtil;

/***
 * 
 * 约会触发脚本
 * 
 * @author lihongji
 *
 */

public class DatingTriggerScript implements IDatingTriggerScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.DATING_TRIGGER_SCRIPT.Value();
    }

    /** 刷新城市约会状态 */
    @Override
    public void refreshCityDating(Player player, Date now, IBaseDatingScript baseScipt,Logger LOGGER) {

        DatingManager dm = player.getDatingManager();
        List<CityDatingBean> allCityDatings = dm.getCurrentCityDatings(baseScipt.getDatingType());
        ICityDatingTimeOutCheckScript timeoutChecker = baseScipt.getTimeoutChecker(player,LOGGER);
        List<CityDatingBean> outTimeRecordList = allCityDatings.stream().filter(
                record -> timeoutChecker != null && timeoutChecker.timeout(record, player, now))
                .collect(Collectors.toList());
        // 通知客户端更新
        if (outTimeRecordList != null && outTimeRecordList.size() > 0) {
            MessageUtils.send(player, DatingMsgBuilder
                    .buildCityDatingInfoList(S2CShareMsg.ChangeType.DELETE, outTimeRecordList));
        }
        refreshCity(player, outTimeRecordList);
    }

    @Override
    public void refreshCity(Player player, List<CityDatingBean> outTimeRecordList) {
        outTimeRecordList.forEach((bean) -> {
            player.getCityRoleManager().removeOccupyBuilding(bean.getRoleIds().get(0));
        });

    }

    @Override
    public void triggerDating(Player player, Date now, IBaseDatingScript baseScipt) {
        DatingManager dm = player.getDatingManager();
        // 该时段是否已经触发过
        if (alreadyTrigger(player, now, baseScipt)) {
            return;
        }
        // 获取当前时段
        Map<String, List<Integer>> timeFrame = getTimeFrame(now);
        // 当前未到触发时段
        if (timeFrame == null || timeFrame.isEmpty())
            return;
        // 设置当前触发时间
        List<Integer> triggerFrame = timeFrame.get(DatingConstant.TRIGGER_TIME);
        List<Long> newTriggerFrame = Arrays.asList(
                TimeUtil.getDateWithHourMinute(now, triggerFrame.get(0), triggerFrame.get(1))
                        .getTime(),
                TimeUtil.getDateWithHourMinute(now, triggerFrame.get(2), triggerFrame.get(3))
                        .getTime(),
                0l);
        dm.setLastTriggerFrame(baseScipt.getDatingType(), newTriggerFrame);
        // 剧本检测
        Set<Integer> freeBuildingList = getFreeBuildingList(player);
        // 只要不成功则设置下次检测时间
        if (freeBuildingList.size() == 0) {
            newTriggerFrame.set(2, (TimeUtil.getTodayOfhonur() + TimeUtil.ONE_HOUR));
            return;
        }
        List<Integer> freeRoleList = getFreeRole(player, baseScipt);
        if (freeRoleList.size() == 0) {
            newTriggerFrame.set(2, (TimeUtil.getTodayOfhonur() + TimeUtil.ONE_HOUR));
            return;
        }
        // 打乱精灵顺序
        Collections.shuffle(freeRoleList);
        List<DatingRuleCfgBean> datingRuleCfgList =
                getUsableOutDatingRuleCfg(freeRoleList, freeBuildingList, player, baseScipt);
        boolean trigger = filtrateDating(player, datingRuleCfgList, timeFrame, now, baseScipt);
        if (!trigger) {
            newTriggerFrame.set(2, (TimeUtil.getTodayOfhonur() + TimeUtil.ONE_HOUR));
        }
    }

    /**
     * 获取空闲的精灵列表
     */
    @Override
    public List<Integer> getFreeRole(Player player, IBaseDatingScript baseScipt) {
        RoleManager rm = player.getRoleManager();
        return rm.getRoles().values().stream().filter(role -> baseScipt.isRoleValid(role, player))
                .map(role -> role.getCid()).collect(Collectors.toList());
    }

    /**
     * 获取可用的剧本
     * 
     * @param roleCidList 可用的精灵
     * @param buildingCidList 可用的建筑
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<DatingRuleCfgBean> getUsableOutDatingRuleCfg(List<Integer> roleCidList,
            Set<Integer> buildingCidList, Player player, IBaseDatingScript baseScipt) {
        // 似乎没有更好的办法进行剧本遍历
        List<DatingRuleCfgBean> all = GameDataManager.getDatingRuleCfgBeans();
        return all.stream()
                // 类型过滤
                .filter(cfg -> cfg.getType() == baseScipt.getDatingType())
                // 可用建筑过滤
                .filter(cfg -> buildingCidList
                        .contains(DatingService.getInstance().getDatingRuleEnterCondtionByType(cfg,
                                DatingConstant.ENTER_CDTION_KEY_BUILD_CID)))
                // 可用精灵过滤
                .filter(cfg -> cfg.getEnterCondition()
                        .get(DatingConstant.ENTER_CDTION_KEY_ROLE_CIDS) != null
                        && roleCidList.containsAll((Collection) cfg.getEnterCondition()
                                .get(DatingConstant.ENTER_CDTION_KEY_ROLE_CIDS)))
                // 额外条件过滤
                .filter(cfg -> isRuleValid(cfg, player)).collect(Collectors.toList());
    }

    /** 当前类型约会中,剧本可用的额外条件 */
    @Override
    public boolean isRuleValid(DatingRuleCfgBean rule, Player player) {
        return true;
    }

    /** 获取当前类型约会中可用的建筑 */
    @Override
    public Set<Integer> getFreeBuildingList(Player player) {
        Set<Integer> buildingCfgList = getValidBuildings(player);
        Set<Integer> buildingCidList = Sets.newHashSet();
        for (Integer building : buildingCfgList) {
            if (!isBuildingValid(building, player))
                buildingCidList.add(building);
        }
        return buildingCidList;
    }

    @Override
    public Set<Integer> getValidBuildings(Player player) {
        return player.getNewBuildingManager().getValidbuilding();
    }

    /** 当前类型约会中,建筑是否可用 */
    @Override
    public boolean isBuildingValid(int building, Player player) {
        return player.getCityRoleManager().getOccupyBuilding().get(building) != null;
    }

    /**
     * 获取触发时段 触发时段被所有城市约会共享，在同一触发时段只能触发一次任意类型的城市约会
     * P.S.目前打工约会、预定约会、出游约会的触发时间做成了统一的，如果策划需要改成每种时间不同，就把这个写到它们各自的触发类中去
     * 
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Map<String, List<Integer>> getTimeFrame(Date now) {
        Map<String, List<Integer>> timeFrameMap = Maps.newHashMap();
        int index = 0;
        // 从离散配置获取触发与约会时间段
        Map discreteData =
                GameDataManager.getDiscreteDataCfgBean((DiscreteDataID.DATING)).getData();
        // 触发时段
        List<List<Integer>> triggerTimeFrameList =
                (List<List<Integer>>) discreteData.get(DiscreteDataKey.TRIGGER_FRAME);
        for (List<Integer> timeFrame : triggerTimeFrameList) {
            Date beginTime =
                    TimeUtil.getDateWithHourMinute(now, timeFrame.get(0), timeFrame.get(1));
            Date endTime = TimeUtil.getDateWithHourMinute(now, timeFrame.get(2), timeFrame.get(3));
            // 是否属于当前时段
            if (beginTime.getTime() <= now.getTime() && endTime.getTime() >= now.getTime()) {
                timeFrameMap.put(DatingConstant.TRIGGER_TIME, timeFrame);
                break;
            }
            index++;
        }
        // 触发与约会配置是独立配置的,这里获取相应的约会时间段
        if (timeFrameMap.size() > 0) {
            List<List<Integer>> datingTimeFrameList =
                    (List<List<Integer>>) discreteData.get(DiscreteDataKey.DATING_TIME_FRAME);
            timeFrameMap.put(DatingConstant.DATING_TIME, datingTimeFrameList.get(index));
        }
        return timeFrameMap;
    }

    /** 检查今日当前时段是否已经触发约会 **/
    @Override
    public boolean alreadyTrigger(Player player, Date now, IBaseDatingScript baseScipt) {
        DatingManager dm = player.getDatingManager();
        List<Long> timeFrame = dm.getLastTriggerFrame(baseScipt.getDatingType());
        // 没有记录则没有触发过
        if (timeFrame == null)
            return false;

        Date beginTime = new Date(timeFrame.get(0));
        Date endTime = new Date(timeFrame.get(1));
        long resetTime = timeFrame.get(2);
        if (resetTime != 0 && now.getTime() > resetTime && now.getTime() < timeFrame.get(1)) {
            return false;
        }
        // 这里不支持跨天时段的配置
        // 如果当前时间与上次检测时间是同一天并且与凌晨到此时的秒数在上次时间段内则已经检测过
        // 使用经过秒数可以规避夏令时冬令时问题
        int nowPast = TimeUtil.getSecondsWithTodayHourMinute(now);
        if (TimeUtil.isSameDay(now, endTime)
                && TimeUtil.getSecondsWithTodayHourMinute(beginTime) <= nowPast
                && nowPast <= TimeUtil.getSecondsWithTodayHourMinute(endTime))
            return true;
        return false;
    }

    /** 筛选约会并进行触发 **/
    @Override
    public boolean filtrateDating(Player player, List<DatingRuleCfgBean> datingRuleCfgList,
            Map<String, List<Integer>> timeFrame, Date now, IBaseDatingScript baseScipt) {
        DatingManager dm = player.getDatingManager();
        // 相对约会时间
        List<Integer> datingFrame = timeFrame.get(DatingConstant.DATING_TIME);

        for (DatingRuleCfgBean datingRuleCfg : datingRuleCfgList) {
            if (checkDatingTriggerRate(player, datingRuleCfg)) {
                CityDatingBean record = DatingService.getInstance()
                        .getDatingTrigger(baseScipt.getDatingType())
                        .createCityDatingBean(dm, datingRuleCfg, datingFrame, now, baseScipt);
                DatingService.getInstance().getDatingTrigger(baseScipt.getDatingType())
                        .addDatingLog(player, record);
                player.getDatingManager().putDatingBean(record);
                updateCity(player, datingRuleCfg);
                updateRole(player, datingRuleCfg);
                sendDating(player, record);
                return true;
            }
        }
        return false;
    }

    /** 生成约会记录 **/
    @SuppressWarnings("unchecked")
    @Override
    public CityDatingBean createCityDatingBean(DatingManager dm, DatingRuleCfgBean datingRuleCfg,
            List<Integer> datingFrame, Date now, IBaseDatingScript baseScipt) {
        // 绝对约会时间段
        long datingBegin = TimeUtil
                .getDateWithHourMinute(now, datingFrame.get(0), datingFrame.get(1)).getTime();
        long datingEnd = TimeUtil.getDateWithHourMinute(now, datingFrame.get(2), datingFrame.get(3))
                .getTime();
        return new CityDatingBean(dm.newDatingId(), baseScipt.getDatingType(),
                // 起始得分60
                60, 0, datingRuleCfg.getStartNodeId(), datingRuleCfg.getId(),
                // 从剧本配置获取精灵
                (List<Integer>) datingRuleCfg.getEnterCondition()
                        .get(DatingConstant.ENTER_CDTION_KEY_ROLE_CIDS),
                // 建筑id
                datingRuleCfg.getBuildingId(),
                // 绝对约会时间段
                datingBegin, datingEnd, now.getTime(),
                // 相对约会时间段
                datingFrame, DatingConstant.RESERVE_DATING_STATE_NO_DATING, now.getTime());
    }

    /**
     * 校验触发几率 1.乱入约会只校验约会几率 2.单人约会不仅校验约会几率，还要校验看板娘的几率(逐次递减的几率)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean checkDatingTriggerRate(Player player, DatingRuleCfgBean datingRuleCfg) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.DATE_EVENT)) {
            return false;
        }
        // 概率极限值
        int limit = 100;
        int random;
        if (datingRuleCfg.getEnterCondition()
                .get(DatingConstant.ENTER_CDTION_KEY_ROLE_CIDS) == null) {
            return false;
        }
        // 加入固定触发概率，固定触发概率通过以后再进行衰减率随机校验
        random = RandomEx.nextInt(limit);
        DiscreteDataCfgBean ddcb = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.DATING);
        if (random > (Integer) ddcb.getData().get(DiscreteDataKey.CITY_DATING_TRIGGER_RATE)) {
            return false;
        }
        int roleCid = (int) ((List) datingRuleCfg.getEnterCondition()
                .get(DatingConstant.ENTER_CDTION_KEY_ROLE_CIDS)).get(0);
        // 对精灵的概率检测
        RoleManager rm = player.getRoleManager();
        Role role = rm.getRole(roleCid);
        random = RandomEx.nextInt(limit);
        if (random < getTriggerRate(role, datingRuleCfg, player)) {
            return true;
        }
        return false;
    }

    /**
     * 获得衰减几率
     */
    @SuppressWarnings("unchecked")
    @Override
    public int getDampingRate(Role role, Player player) {
        DiscreteDataCfgBean ddcb = GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.DATING);
        List<Integer> rateList = (List<Integer>) ddcb.getData().get(DiscreteDataKey.DECREASE_RATE);
        DatingManager dm = player.getDatingManager();
        return rateList.get(dm.getRoleCityDatingCount(role.getCid()));
    }

    @Override
    public int getTriggerRate(Role role, DatingRuleCfgBean datingRuleCfg, Player player) {
        return getDampingRate(role, player);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void updateCity(Player player, DatingRuleCfgBean cfg) {
        int buildingId = DatingService.getInstance().getDatingRuleEnterCondtionByType(cfg,
                DatingConstant.ENTER_CDTION_KEY_BUILD_CID);

        int roleCid = (Integer) ((List) cfg.getEnterCondition()
                .get(DatingConstant.ENTER_CDTION_KEY_ROLE_CIDS)).get(0);
        // 在城建系统记录当前占用
        player.getCityRoleManager().addOccupyBuilding(roleCid, buildingId, cfg.getCityLines());

    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateRole(Player player, DatingRuleCfgBean cfg) {
        List<Integer> roleList = (List<Integer>) cfg.getEnterCondition()
                .get(DatingConstant.ENTER_CDTION_KEY_ROLE_CIDS);
        addCityDatingCount(player, roleList);

    }

    @Override
    public void sendDating(Player player, CityDatingBean record) {
        MessageUtils.send(player,
                DatingMsgBuilder.getCityDatingInfo(S2CShareMsg.ChangeType.ADD, record,false));

    }

    @Override
    public void addCityDatingCount(Player player, List<Integer> roleIdList) {
        DatingManager dm = player.getDatingManager();
        for (Integer roleCid : roleIdList) {
            dm.addRoleCityDatingCount(roleCid);
        }

    }

    @Override
    public void acceptDating(boolean accept, Player player) {

    }

    @Override
    public boolean checkReserveDating(Player player) {
        return false;
    }

}
