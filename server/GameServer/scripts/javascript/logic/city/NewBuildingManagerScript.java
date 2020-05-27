package javascript.logic.city;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import data.GameDataManager;
import data.bean.NewBuildCfgBean;
import logic.character.bean.Player;
import logic.city.BuildingGeneral;
import logic.city.NewBuildingManager;
import logic.city.build.BuildingConstant;
import logic.city.build.bean.BuildEvent;
import logic.city.script.INewBuildingManagerScript;
import logic.constant.EEventType;
import logic.constant.EScriptIdDefine;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.MessageUtils;
import utils.TimeUtil;

/***
 * 城市建筑脚本
 * @author lihongji
 *
 */
public class NewBuildingManagerScript implements INewBuildingManagerScript {

	/**返回脚本的Id**/
    @Override
    public int getScriptId() {
        return EScriptIdDefine.CITY_NEWBUILDING_SCRIPT.Value();
    }

    /** 建筑解锁 **/
    @Override
    public Map<Integer, ArrayList<Integer>> checkUnlock(boolean notice, Player player,
            Set<Integer> validbuilding, Map<Integer, Set<Integer>> validFunIds) {
        ArrayList<BuildEvent> events = new ArrayList<BuildEvent>();
        Map<Integer, ArrayList<Integer>> buildingsFundIds =
                new HashMap<Integer, ArrayList<Integer>>();
        checkUnlockBuilding(events, buildingsFundIds, validbuilding, player);
        checkUnlockFunIds(events, buildingsFundIds, validbuilding, player, validFunIds);
        // 刷新建筑功能
        MessageUtils.send(player, NewBuildingMsgBuilder.refreshUnlockBuilding(events,
                buildingsFundIds.keySet(), player));
        return buildingsFundIds;
    }

    /** 建筑检测 **/
    @SuppressWarnings("unchecked")
    @Override
    public void checkUnlockBuilding(ArrayList<BuildEvent> events,
            Map<Integer, ArrayList<Integer>> buildingsFundIds, Set<Integer> validbuilding,
            Player player) {
        List<NewBuildCfgBean> list = GameDataManager.getNewBuildCfgBeans();
        for (int i = 0; i < list.size(); i++) {
            NewBuildCfgBean newBuild = list.get(i);
            if (validbuilding.contains(newBuild.getId()))
                continue;
            if (!BuildingGeneral.checkBuildingCondition(newBuild.getRoomOpenType(), player))
                continue;
            validbuilding.add(newBuild.getId());
            // 创建一个建筑事件
            BuildEvent event = BuildingGeneral.createBuildingEvent(newBuild.getId(),
                    BuildingConstant.EVENT_TYPE_BUILD_UNSEAL, 0, 0, player);
            if (events != null)
                events.add(event);
            if (buildingsFundIds != null)
                buildingsFundIds.put(newBuild.getId(), new ArrayList<Integer>());
        }

    }
    /** 检测功能行开放 **/
    @Override
    public void checkUnlockFunIds(ArrayList<BuildEvent> events,
            Map<Integer, ArrayList<Integer>> buildingsFundIds, Set<Integer> validbuilding,
            Player player, Map<Integer, Set<Integer>> validFunIds) {
        validbuilding.forEach((buildingId) -> {
            NewBuildCfgBean bean = GameDataManager.getNewBuildCfgBean(buildingId);
            if (bean.getBuildEffect() == null || bean.getBuildEffect().length == 0)
                return;
            Set<Integer> funIds = validFunIds.get(buildingId);
            for (Integer fun : bean.getBuildEffect()) {
                if (funIds != null && funIds.contains(fun))
                    continue;
                if (checkCondition(bean, fun, player)) {
                    if (funIds == null) {
                        funIds = new HashSet<Integer>();
                        validFunIds.put(buildingId, funIds);
                    }
                    funIds.add(fun);
                    BuildEvent event = BuildingGeneral.createBuildingEvent(buildingId,
                            BuildingConstant.EVENT_TYPE_FUNID_UNSEAL, 0, fun, player);
                    if (events != null)
                        events.add(event);
                    if (buildingsFundIds != null) {
                        ArrayList<Integer> list = buildingsFundIds.get(buildingId);
                        if (list == null) {
                            list = new ArrayList<Integer>();
                            buildingsFundIds.put(buildingId, list);
                        }
                        list.add(fun);
                    }
                }
            }
        });

    }
    
    /**检测条件**/
    @SuppressWarnings("unchecked")
    @Override
    public boolean checkCondition(NewBuildCfgBean bean,int fun,Player player)
    {
        if (bean.getEffectOpenType() == null)
            return true;
        Map<Integer, Object> map = (Map<Integer, Object>) bean.getEffectOpenType().get(fun);
        if (BuildingGeneral.checkBuildingCondition(map, player))
            return true;
        return false;

    }

    /** 创建事件 **/
    @Override
    public void createfireEvent(Player player, Set<Integer> validbuilding,
            Map<Integer, Set<Integer>> validFunIds) {
        Map<Integer, ArrayList<Integer>> addList =
                checkUnlock(true, player, validbuilding, validFunIds);
        if (addList == null)
            return;
        // 建筑解锁
        Map<String, Object> param = Maps.newConcurrentMap();
        param.put(BuildingConstant.EVENT_RESULT_DATA, addList);
        param.put(BuildingConstant.EVENT_CONDITION_ID, BuildingConstant.REFRESH_ALL);
        player.getEventDispatcher().fireEvent(param, EEventType.UNLOCK_BUILDING.value());

    }

    /** 移除事件 **/
    @Override
    public void removeEvent(int eventType, Player player,
            Map<Integer, ArrayList<BuildEvent>> buildingEvent) {
        Iterator<Entry<Integer, ArrayList<BuildEvent>>> entrys =
                buildingEvent.entrySet().iterator();
        while (entrys.hasNext()) {
            Entry<Integer, ArrayList<BuildEvent>> entry = entrys.next();
            if (entry.getValue() != null) {
                Iterator<BuildEvent> iterator = entry.getValue().iterator();
                while (iterator.hasNext()) {
                    BuildEvent event = iterator.next();
                    if (event.getEventType() == eventType) {
                        iterator.remove();
                    }
                }
                if (entry.getValue().size() == 0)
                    entrys.remove();
            }
        }

    }

    /** 检测时间点 **/
    @Override
    public void checkDayType(NewBuildingManager manager) {
        if (System.currentTimeMillis() < manager.getNextChangeTime())
            return;
        long timeNow = System.currentTimeMillis();
        long time = TimeUtil.getNextZeroClock() - TimeUtil.ONE_DAY;
        if (timeNow >= (time + BuildingConstant.DAY_TIME)
                && timeNow < (time + BuildingConstant.DAYNIGHT_TIME)) {
            manager.setDayType(BuildingConstant.DAYTIME);
            manager.setNextChangeTime(time + BuildingConstant.DAYNIGHT_TIME);

        } else {
            manager.setDayType(BuildingConstant.NIGHTTIME);
            manager.setNextChangeTime(TimeUtil.getNextZeroClock() + BuildingConstant.DAY_TIME);
        }

    }

    /** 检测 **/
    @Override
    public void check(Object object, Player player, Set<Integer> validbuilding,
            Map<Integer, Set<Integer>> validFunIds) {
        createfireEvent(player, validbuilding, validFunIds);

    }

    @Override
    public void createPlayerInitialize(Player player, Set<Integer> validbuilding,
            Map<Integer, Set<Integer>> validFunIds) {
        createfireEvent(player, validbuilding, validFunIds);

    }

    @Override
    public void tick(NewBuildingManager manager) {
        checkDayType(manager);
    }


}
