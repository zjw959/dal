package logic.listener;

import java.util.List;
import java.util.Map;

import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.msgBuilder.TaskMsgBuilder;
import logic.task.bean.GameEventStack;
import logic.task.bean.GameEventVector;
import logic.task.bean.Task;

import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import utils.ToolMap;

import com.google.common.collect.Lists;

import event.Event;
import event.IListener;
import gm.db.global.bean.ActivityTaskItem;

public class TaskAcceptListener implements IListener {

    @Override
    public void initEventCondition(Player player, GameEventStack eventStack) {

    }

    @Override
    public void onEvent(Event event, Player player) {
        GameEventStack eventStack = event.peek();
        switch (eventStack.getConditionType()) {
            case EventConditionType.TASK_ACCEPT: {
                boolean bool = true;
                // 等级检查
                int plvl = ToolMap.getInt(EventConditionKey.PLAYER_LVL, eventStack.getParams());
                if (plvl > 0) {
                    bool &= player.getLevel() >= plvl;
                }
                // 前置任务检查
                int preTaskId =
                        ToolMap.getInt(EventConditionKey.PRE_TASK_ID, eventStack.getParams());
                if (preTaskId > 0) {
                    bool &= (player.getTaskManager().inFinishMap(preTaskId));
                }

                if (bool) {
                    eventStack.setProgress(1);
                }
            }
                break;
            default:
                break;
        }
    }

    // @Override
    @SuppressWarnings("rawtypes")
    // public void handler(Player player, Event event, GameEventVector vector, int conditionId,
    // Map params, int maxProcess) {
    // // 取得全部开启的任务
    // List<TaskCfgBean> cfgs = GameDataManager.getTaskCfgBeans();
    //
    // // 取得全部接取的任务
    // Collection<Task> tasks = player.getTaskManager().getTaskMap().values();
    //
    // Set<Integer> taskIds = Sets.newHashSet();
    // Map<Integer, Integer> finishTasks = player.getTaskManager().getFinishMap();
    // taskIds.addAll(finishTasks.keySet());
    // for (Task task : tasks) {
    // taskIds.add(task.getCid());
    // }
    //
    // List<TaskInfo> addTasks = Lists.newArrayList();
    //
    // for (TaskCfgBean taskCfg : cfgs) {
    // if (!taskCfg.getOpen()) {
    // continue;
    // }
    // // 任务已存在
    // if (taskIds.contains(taskCfg.getId())) {
    // continue;
    // }
    //
    // int _conditionId = taskCfg.getAcceptCondId();
    // // 取得事件条件
    // EventConditionCfgBean eventConditionCfg =
    // GameDataManager.getEventConditionCfgBean(_conditionId);
    // if (eventConditionCfg == null) {
    // return;
    // }
    //
    // // 检查与事件源是否匹配
    // int eventId = eventConditionCfg.getEvent_id();
    // if (event.getEventId() != eventId) {
    // continue;
    // }
    //
    // // 接任务：用的是接任务的条件参数
    // Map _params = taskCfg.getAcceptParams();
    // boolean trigger = (_params == null || _params.isEmpty());
    //
    // Task task = null;
    // if (trigger) {
    // task = player.getTaskManager().acceptTask(player, taskCfg);
    // } else {
    // event.push(new GameEventStack(eventId, _conditionId, _params, 0, 0, trigger));
    // onEvent(event, player);
    // GameEventStack eventStack = event.peek();
    // // 条件触发成功
    // if (eventStack.isTrigger()) {
    // task = player.getTaskManager().acceptTask(player, taskCfg);
    // }
    // }
    //
    // if (task != null) {
    // addTasks.add(TaskMsgBuilder.createTaskInfo(player, ChangeType.ADD, task));
    // }
    // }
    // // 加载玩家触发不推送
    // if (((Map) event.getParams()).containsKey(EventConditionKey.LOAD_PLAYER)) {
    // return;
    // }
    // TaskMsgBuilder.notifyTaskInfos(player, addTasks);
    // }
    @Override
    public void handler(Player player, Event event, GameEventVector vector, int conditionId,
            Map params, int maxProcess, List<TaskInfo> taskInfos) {
        // 原本的
        Map beforeMap = (Map) ((Map) event.getParams()).get(EventConditionKey.ACCEPT);
        // 重新接取任务后的
        Map<Integer, Task> afterMap = player.getTaskManager().getAcceptTaskMap();
        List<TaskInfo> addTasks = Lists.newArrayList();
        for (Task task : afterMap.values()) {
            // 对比
            if (beforeMap.containsKey(task.getCid())) {
                continue;
            }
            addTasks.add(TaskMsgBuilder.createTaskInfo(player, ChangeType.ADD, task));
        }
        // 加载玩家触发不推送
        if (((Map) event.getParams()).containsKey(EventConditionKey.LOAD_PLAYER)) {
            return;
        }
        taskInfos.addAll(addTasks);
    }

    /** 活动检测 **/
    @Override
    public void activityHandler(Player player, Event event, ActivityTaskItem taskItem,
            ActivityRecord record) {
        return;
    }

    /** 任务活动初始化 **/
    @Override
    public void initActivityEventCondition(Player player, GameEventStack eventStack,
            ActivityRecord record) {
        initEventCondition(player, eventStack);
    }
}
