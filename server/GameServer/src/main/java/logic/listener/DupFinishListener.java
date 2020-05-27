package logic.listener;

import java.util.Collection;
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
import logic.constant.DungeonConstant;
import logic.constant.EEventType;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.dungeon.bean.DungeonBean;
import logic.task.bean.GameEventStack;
import logic.task.bean.GameEventVector;
import logic.task.bean.Task;
import utils.ToolMap;

public class DupFinishListener implements IListener {

    @Override
    public void initEventCondition(Player player, GameEventStack eventStack) {
        switch (eventStack.getConditionType()) {
            case EventConditionType.FIGHT_EVENT: {
                break;
            }
            case EventConditionType.FIGHT_TOTAL_STAR: {
                Collection<DungeonBean> list = player.getDungeonManager().getDungeons().values();
                int count = 0;
                for (DungeonBean bean : list) {
                    count += bean.getStar();
                }
                eventStack.setProgress(count);
                break;
            }
            case EventConditionType.PERFECT_DUN: {
                Collection<DungeonBean> list = player.getDungeonManager().getDungeons().values();
                int count = 0;
                for (DungeonBean bean : list) {
                    if (bean.getStar() >= DungeonConstant.CHAPTER_MAX_STAR) {
                        count++;
                    }
                }
                eventStack.setProgress(count);
                break;
            }
            case EventConditionType.FIGHT_BATTER: {
                break;
            }
            case EventConditionType.FIGHT_ASSIGN_DUP: {
                int dunId =
                        ToolMap.getInt(EventConditionKey.DUNGEON_CID, eventStack.getParams(), 0);
                Collection<DungeonBean> list = player.getDungeonManager().getDungeons().values();
                int count = 0;
                for (DungeonBean bean : list) {
                    if (bean.getCid() == dunId && bean.isWin()) {
                        count = 1;
                        break;
                    }
                }
                eventStack.setProgress(count);
                break;
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onEvent(Event event, Player player) {
        GameEventStack eventStack = event.peek();
        switch (eventStack.getConditionType()) {
            case EventConditionType.FIGHT_EVENT: {
                boolean bool = true;
                bool = checkParams(event, eventStack, bool);
                if (bool) {
                    eventStack.changeProgress(1);
                }
                break;
            }
            case EventConditionType.FIGHT_TOTAL_STAR: {
                int addStar = ToolMap.getInt(EventConditionKey.ADD_STAR, (Map) event.getParams());
                boolean bool = addStar > 0;
                bool = checkParams(event, eventStack, bool);
                if (bool) {
                    eventStack.changeProgress(addStar);
                }
                break;
            }
            case EventConditionType.PERFECT_DUN: {
                boolean bool =
                        ToolMap.getBoolean(EventConditionKey.FIRST_3_STAR, (Map) event.getParams());
                bool = checkParams(event, eventStack, bool);
                if (bool) {
                    eventStack.changeProgress(1);
                }
                break;
            }
            case EventConditionType.FIGHT_BATTER: {
                int batter = ToolMap.getInt(EventConditionKey.BATTER, (Map) event.getParams(), 0);
                boolean bool = batter > 0;
                bool = checkParams(event, eventStack, bool);
                if (bool && batter > eventStack.getMaxProgress()) {
                    // 设置最大连击进度
                    eventStack.setProgress(batter);
                }
                break;
            }
            case EventConditionType.FIGHT_PICKUP_COUNT: {
                int pickUpCount =
                        ToolMap.getInt(EventConditionKey.PICK_UP_COUNT, (Map) event.getParams(), 0);
                boolean bool = pickUpCount > 0;
                bool = checkParams(event, eventStack, bool);
                if (bool && pickUpCount > eventStack.getMaxProgress()) {
                    // 设置最大连击进度
                    eventStack.changeProgress(pickUpCount);
                }
                break;
            }
            case EventConditionType.FIGHT_PICKUP_TYPE_COUNT: {
                int pickUpTypeCount = ToolMap.getInt(EventConditionKey.PICK_UP_TYPE_COUNT,
                        (Map) event.getParams(), 0);
                boolean bool = pickUpTypeCount > 0;
                bool = checkParams(event, eventStack, bool);
                if (bool && pickUpTypeCount >= eventStack.getProgress()) {
                    // 设置最大连击进度
                    eventStack.changeProgress(pickUpTypeCount);
                }
                break;
            }
            case EventConditionType.FIGHT_ASSIGN_DUP: {
                boolean bool = true;
                bool = checkParams(event, eventStack, bool);
                if (bool) {
                    eventStack.changeProgress(1);
                }
            }
        }
    }

    /**
     * 条件参数检查
     * 
     * @param event
     * @param eventStack
     * @param bool
     * @return
     */
    @SuppressWarnings("rawtypes")
    private boolean checkParams(Event event, GameEventStack eventStack, boolean bool) {
        bool &= verify_eq(EventConditionKey.DUNGEON_CID, (Map) event.getParams(),
                eventStack.getParams());
        bool &= verify_ge(EventConditionKey.STAR, (Map) event.getParams(), eventStack.getParams());
        bool &= verify_le(EventConditionKey.FIGHT_HERO_COUNT, (Map) event.getParams(),
                eventStack.getParams());
        bool &= verify_le(EventConditionKey.FIGHT_TIME, (Map) event.getParams(),
                eventStack.getParams());
        bool &= verify_eq(EventConditionKey.DIFFICULTY, (Map) event.getParams(),
                eventStack.getParams());
        bool &= verify_eq(EventConditionKey.DUNGOEN_TYPE, (Map) event.getParams(),
                eventStack.getParams());
        return bool;
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
        if (eventConditionCfg.getEventId() != EEventType.PASS_DUP.value())
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
