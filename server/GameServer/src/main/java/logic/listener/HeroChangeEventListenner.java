package logic.listener;

import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

import data.GameDataManager;
import data.bean.EventConditionCfgBean;
import event.Event;
import event.IListener;
import gm.db.global.bean.ActivityTaskItem;
import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.EventType;
import logic.task.bean.GameEventStack;
import logic.task.bean.GameEventVector;
import logic.task.bean.Task;
import utils.ToolMap;

public class HeroChangeEventListenner implements IListener {

    @Override
    public void initEventCondition(Player player, GameEventStack eventStack) {
        switch (eventStack.getConditionType()) {
            case EventConditionType.HERO_MAX_LVL: {
                int heroCid = ToolMap.getInt(EventConditionKey.HERO_CID, eventStack.getParams());
                int maxLvl = player.getHeroManager().getMaxHeroLvl(heroCid);
                eventStack.setProgress(maxLvl);
            }
                break;
            // case EventConditionType.ADVANCED_LVL: {
            // int heroCid = ToolMap.getInt(EventConditionKey.HERO_CID, eventStack.getParams());
            // int maxLvl = player.getHeroManager().getMaxHeroAdvancedLvl(heroCid);
            // eventStack.setProgress(maxLvl);
            // }
            // break;
            case EventConditionType.ADVANCED_LVL_COUNT: {
                int limitLvl =
                        ToolMap.getInt(EventConditionKey.LIMIT_LEVEL, eventStack.getParams());
                int count = player.getHeroManager().getHeroCountByLevel(limitLvl);
                eventStack.setProgress(count);
            }
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onEvent(Event event, Player player) {
        GameEventStack eventStack = event.peek();
        switch (eventStack.getConditionType()) {
            case EventConditionType.HERO_MAX_LVL:
            case EventConditionType.ADVANCED_LVL: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    int oldLv =
                            ToolMap.getInt(EventConditionKey.OLD_LEVEL, (Map) event.getParams());
                    int nowLv =
                            ToolMap.getInt(EventConditionKey.NOW_LEVEL, (Map) event.getParams());
                    int progress = eventStack.getMaxProgress();
                    if (verify_eq(EventConditionKey.HERO_CID, (Map) event.getParams(),
                            eventStack.getParams()) && oldLv < progress
                            && nowLv > eventStack.getProgress()) {
                        eventStack.setProgress(nowLv);
                    }
                }

            }
                break;
            case EventConditionType.ADVANCED_LVL_COUNT: {
                if (checkTriggerType((Map) event.getParams(),
                        EventConditionType.ADVANCED_LVL_COUNT)) {
                    int oldLevel =
                            ToolMap.getInt(EventConditionKey.OLD_LEVEL, (Map) event.getParams());
                    int nowLevel =
                            ToolMap.getInt(EventConditionKey.NOW_LEVEL, (Map) event.getParams());
                    boolean bool = verify_eq(EventConditionKey.ROLE_CID, (Map) event.getParams(),
                            eventStack.getParams());

                    int limit =
                            ToolMap.getInt(EventConditionKey.LIMIT_LEVEL, eventStack.getParams());
                    if (bool && oldLevel < limit && nowLevel >= limit) {
                        eventStack.changeProgress(1);
                    }
                }
                break;
            }
            case EventConditionType.HERO_UP_LVL_COUNT: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.HERO_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    if (bool) {
                        eventStack.changeProgress(1);
                    }
                }
                break;
            }
            default:
                break;
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

    /** 活动检测 **/
    @SuppressWarnings("unchecked")
    @Override
    public void activityHandler(Player player, Event event, ActivityTaskItem taskItem,
            ActivityRecord record) {
        EventConditionCfgBean eventConditionCfg =
                GameDataManager.getEventConditionCfgBean(taskItem.getFinishCondid());
        if (eventConditionCfg == null) {
            return;
        }
        // 检查该条件监听的事件
        if (eventConditionCfg.getEventId() != EventType.HERO_CHANGE)
            return;

        // 当前进度
        // int nowProgress = record.getProgress();
        // if (nowProgress >= taskItem.getProgress())
        // return;
        Map<String, Integer> params = null;
        if (taskItem.getFinishParams() != null && taskItem.getFinishParams().length() > 0) {
            params = Maps.newHashMap();
            params = new Gson().fromJson(taskItem.getFinishParams(), params.getClass());
        }
        event.push(new GameEventStack(eventConditionCfg.getEventId(), taskItem.getFinishCondid(),
                params, record.getProgress(), taskItem.getProgress(), false));
        onEvent(event, player);
        GameEventStack eventStack = event.peek();
        record.setProgress(eventStack.getProgress());

    }

    /** 任务活动初始化 **/
    @Override
    public void initActivityEventCondition(Player player, GameEventStack eventStack,
            ActivityRecord record) {
        initEventCondition(player, eventStack);
    }

}
