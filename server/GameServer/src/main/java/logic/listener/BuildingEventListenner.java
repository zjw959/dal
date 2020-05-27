package logic.listener;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import com.google.common.collect.Maps;

import cn.hutool.core.date.DateUnit;
import data.GameDataManager;
import data.bean.EventConditionCfgBean;
import event.Event;
import event.IListener;
import gm.db.global.bean.ActivityTaskItem;
import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.task.bean.GameEventStack;
import logic.task.bean.GameEventVector;
import logic.task.bean.Task;
import utils.TimeUtil;
import utils.ToolMap;

public class BuildingEventListenner implements IListener {

    public static Map<Integer, DateUnit> dateUnitMap = Maps.newHashMap();

    public static int MS = 1;
    public static int SECOND = 2;
    public static int MINUTE = 3;
    public static int HOUR = 4;
    public static int DAY = 5;
    public static int WEEK = 6;

    static {
        dateUnitMap.put(MS, DateUnit.MS);
        dateUnitMap.put(SECOND, DateUnit.SECOND);
        dateUnitMap.put(MINUTE, DateUnit.MINUTE);
        dateUnitMap.put(HOUR, DateUnit.HOUR);
        dateUnitMap.put(DAY, DateUnit.DAY);
        dateUnitMap.put(WEEK, DateUnit.WEEK);
    }

    public static DateUnit getDateUnit(int type) {
        return dateUnitMap.get(type);
    }

    @Override
    public void initEventCondition(Player player, GameEventStack eventStack) {
        switch (eventStack.getConditionType()) {
            case EventConditionType.BUILDING_LVL_COUNT: {
                // int count = 0;
                // int buildingCid =
                // ToolMap.getInt(EventConditionKey.BUILD_CID, eventStack.getParams());
                // int limitLvl =
                // ToolMap.getInt(EventConditionKey.LIMIT_LEVEL, eventStack.getParams());
                // List<Building> buildings = player.getBuildingsFkPlayerId();
                // for (Building building : buildings) {
                // if (buildingCid != 0
                // && buildingCid != building.getBuildingProxy().getBuildId()) {
                // continue;
                // }
                // if (limitLvl != 0
                // && building.getBuildingProxy().getCfg().getLevel() < limitLvl) {
                // continue;
                // }
                // count++;
                // }
                // eventStack.setProgress(count);
                // break;
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onEvent(Event event, Player player) {
        GameEventStack eventStack = event.peek();
        switch (eventStack.getConditionType()) {
            case EventConditionType.WORK_STAGE: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.BUILD_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_eq(EventConditionKey.BUILD_LVL, (Map) event.getParams(),
                            eventStack.getParams());
                    if (bool) {
                        eventStack.changeProgress(1);
                    }
                }
                break;
            }
            case EventConditionType.BUILDING_LVL_COUNT: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.BUILD_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    int oldLv =
                            ToolMap.getInt(EventConditionKey.OLD_LEVEL, (Map) event.getParams());
                    int newLv =
                            ToolMap.getInt(EventConditionKey.NOW_LEVEL, (Map) event.getParams());
                    int limitLvl =
                            ToolMap.getInt(EventConditionKey.LIMIT_LEVEL, eventStack.getParams());
                    if (oldLv < limitLvl && newLv >= limitLvl) {
                        bool &= true;
                    } else {
                        bool &= false;
                    }

                    if (bool) {
                        eventStack.changeProgress(1);
                    }
                }
                break;
            }
            case EventConditionType.RECEIVE_WORK_REWARD_COUNT: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.BUILD_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    if (bool) {
                        eventStack.changeProgress(1);
                    }
                }
                break;
            }
            case EventConditionType.WORK_TIME: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.BUILD_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    int timeType = (int) eventStack.getParams().get(EventConditionKey.TIME_UNIT);
                    if (bool) {
                        int workTime = ToolMap.getInt(EventConditionKey.WORK_TIME,
                                (Map) event.getParams());
                        int betweenTime = TimeUtil.getBetweenTime(workTime, Calendar.SECOND,
                                getDateUnit(timeType));
                        eventStack.changeProgress(betweenTime);
                    }
                }
                break;
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void handler(Player player, Event event, GameEventVector vector, int conditionId,
            Map params, int maxProcess, List<TaskInfo> taskInfos) {
        EventConditionCfgBean eventConditionCfg =
                GameDataManager.getEventConditionCfgBean(conditionId);
        if (eventConditionCfg == null) {
            return;
        }
        int eventId = eventConditionCfg.getEventId();
        // 检查该条件监听的事件
        if (event.getEventId() != eventId) {
            return;
        }

        // 如果条件参数为空，则任务成功触发条件
        boolean trigger = (params == null || params.isEmpty());

        // 当前进度
        int nowProgress = vector.getProgress();
        if (nowProgress >= maxProcess) {
            trigger = true;
        } else {
            trigger = false;
        }

        event.push(
                new GameEventStack(eventId, conditionId, params, nowProgress, maxProcess, trigger));
        onEvent(event, player);
        GameEventStack eventStack = event.peek();

        // 条件触发成功
        if (eventStack.isTrigger(vector, eventStack.getProgress())) {
            vector.trigger(maxProcess);
            taskInfos.add(vector.notify(player));
            // 条件触发之后才真正接受任务
            Task task = vector.getTask();
            if (checkPut(player.getTaskManager(), task)) {
                player.getTaskManager().putTaskMap(task.getCid(), task);
            }
        }
    }

    @Override
    public void activityHandler(Player player, Event event, ActivityTaskItem taskItem,
            ActivityRecord record) {

    }

    @Override
    public void initActivityEventCondition(Player player, GameEventStack eventStack,
            ActivityRecord record) {

    }

}
