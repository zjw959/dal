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
import logic.pay.bean.MonthCard;
import logic.task.bean.GameEventStack;
import logic.task.bean.GameEventVector;
import logic.task.bean.Task;
import utils.ToolMap;

public class OtherEventListenner implements IListener {

    @Override
    public void initEventCondition(Player player, GameEventStack eventStack) {

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onEvent(Event event, Player player) {
        GameEventStack eventStack = event.peek();
        switch (eventStack.getConditionType()) {
            case EventConditionType.COMPOUND: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    eventStack.changeProgress(1);
                }
                break;
            }
            case EventConditionType.SUMMON: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    eventStack.changeProgress(
                            ToolMap.getInt(EventConditionKey.CARD_COUNT, (Map) event.getParams()));
                }
                break;
            }
            case EventConditionType.BUY_RESOURCE: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.RESOURCE_CID,
                            (Map) event.getParams(), eventStack.getParams());
                    if (bool) {
                        eventStack.changeProgress(1);
                    }
                }
                break;
            }
            case EventConditionType.STORE_BUY: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.COMMODITY_CID,
                            (Map) event.getParams(), eventStack.getParams());
                    bool &= verify_eq(EventConditionKey.STORE_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    if (bool) {
                        eventStack.changeProgress(1);
                    }
                }
                break;
            }
            case EventConditionType.GAIN_MONTH_CARD: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    MonthCard mc = player.getPayManager().getMonthCard();
                    if (System.currentTimeMillis() < mc.getEndTime()) {
                        eventStack.changeProgress(1);
                    }
                }
                break;
            }
            case EventConditionType.SMALL_GAME: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.SUB_TYPE, (Map) event.getParams(),
                            eventStack.getParams());
                    if (bool) {
                        eventStack.changeProgress(1);
                    }
                }
                break;
            }
            case EventConditionType.EQUIP_UPGRADE: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = true;
                    if (bool) {
                        eventStack.changeProgress(1);
                    }
                }
                break;
            }
        }
    }

    /** 活动检测 **/
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
        if (eventConditionCfg.getEventId() != EventType.OTHER_EVENT)
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
