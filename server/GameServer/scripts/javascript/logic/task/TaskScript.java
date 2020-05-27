package javascript.logic.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import logic.character.bean.Player;
import logic.constant.EEventType;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.EventType;
import logic.constant.GameErrorCode;
import logic.constant.ItemConstantId;
import logic.constant.TaskConstant;
import logic.functionSwitch.FunctionSwitchService;
import logic.item.ItemUtils;
import logic.mail.MailService;
import logic.msgBuilder.TaskMsgBuilder;
import logic.support.MessageUtils;
import logic.task.ITaskScript;
import logic.task.TaskManager;
import logic.task.bean.GameEventStack;
import logic.task.bean.GameEventVector;
import logic.task.bean.Task;
import logic.task.bean.TaskVector;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;
import org.game.protobuf.s2c.S2CTaskMsg.ResultSubmitTask;
import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import utils.ExceptionEx;
import utils.ToolMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import data.GameDataManager;
import data.bean.EventConditionCfgBean;
import data.bean.TaskCfgBean;
import event.Event;
import event.IListener;

public class TaskScript extends ITaskScript {
    private static final Logger LOGGER = Logger.getLogger(TaskScript.class);

    @Override
    public int getScriptId() {
        return EScriptIdDefine.TASK_SCRIPT.Value();
    }

    @Override
    protected void reqTasks(Player player, ChangeType type) {
        MessageUtils.send(player,
                TaskMsgBuilder.createRespTasks(player, getTask(player, true), type));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void submitTasks(Player player, int taskCid) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.TASK)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:submitTasks");
        }
        TaskManager taskManager = player.getTaskManager();
        Task task = taskManager.getTask(taskCid);
        if (task == null || taskManager.inFinishMap(taskCid)
                || task.getStatus() != TaskConstant.STATUS_FINISH) {
            MessageUtils.throwCondtionError(GameErrorCode.TASK_STATUS_ERROR,
                    "任务重重复提交 id:" + taskCid);
            return;
        }

        TaskCfgBean cfg = GameDataManager.getTaskCfgBean(taskCid);
        player.getBagManager().addItems(cfg.getReward(), true, EReason.TASK);
        task.setStatus(TaskConstant.STATUS_RECEIVE);

        Map<Integer, Task> beforMap = Maps.newHashMap(getTask(player, true));
        // 不重置的任务提交后就删除
        if (cfg.getResetType() == TaskConstant.RESET_NO) {
            task.setFinish(1);
            taskManager.putFinishMap(task.getCid(), task.getProgress());
        }

        List<TaskInfo> taskInfos = Lists.newArrayList();
        taskInfos.add(TaskMsgBuilder.createTaskInfo(player, ChangeType.UPDATE, task));
        TaskMsgBuilder.notifyTaskInfos(player, taskInfos);
        // 触发任务激活事件
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.ACCEPT, beforMap);
        player._fireEvent(in, EventType.TASK_ACCEPT);

        ResultSubmitTask.Builder builder = ResultSubmitTask.newBuilder();
        builder.setTaskCid(task.getCid());
        builder.setTaskDbId(String.valueOf(0L));

        Map<Integer, Integer> rewardMap = cfg.getReward();
        for (Entry<Integer, Integer> entry : rewardMap.entrySet()) {
            builder.addRewards(
                    RewardsMsg.newBuilder().setId(entry.getKey()).setNum(entry.getValue()));
        }
        MessageUtils.send(player, builder);
    }


    @SuppressWarnings("rawtypes")
    @Override
    protected Map<Integer, Task> getTask(Player player, boolean isInit) {
        TaskManager taskManager = player.getTaskManager();
        Map<Integer, Task> allActiMap = Maps.newHashMap();
        List<TaskCfgBean> taskList = GameDataManager.getTaskCfgBeans();
        for (TaskCfgBean cfg : taskList) {
            if (taskManager.inTaskMap(cfg.getId())) {
                allActiMap.put(cfg.getId(), taskManager.getTask(cfg.getId()));
                continue;
            }
            if (allActiMap.containsKey(cfg.getId())) {
                continue;
            }
            if (cfg.getResetType() == TaskConstant.TYPE_DAY_CONSTANT) {
                continue;
            }
            // 检测领取条件是否满足
            Map map = cfg.getAcceptParams();
            if (isTrigger(player, taskManager, map) && cfg.getOpen()) {
                // 进度继承
                int initProgress = cfg.getExtendsTaskId() != 0
                        ? getFinishTaskProgress(taskManager, cfg.getExtendsTaskId()) : 0;
                Task task = new Task(cfg.getId(), initProgress, TaskConstant.STATUS_RUNING,
                        new Date().getTime(), 0);
                if (isInit) {
                    // 初始化事件条件
                    eventConditionInit(player, new TaskVector(task), cfg.getFinishCondId(),
                            cfg.getFinishParams(), task.getProgress(), cfg.getProgress());
                }

                allActiMap.put(cfg.getId(), task);
            }
        }
        if (isInit && taskManager.getLastLoginTime() == 0) {
            taskManager.setLastLoginTime(new Date().getTime());
        }
        return allActiMap;
    }



    @SuppressWarnings("rawtypes")
    public boolean isTrigger(Player player, TaskManager manager, Map map) {
        if (map == null || map.isEmpty()) {
            return true;
        } else {
            boolean code = onEvent(player, manager, EventConditionType.TASK_ACCEPT, map);
            if (code) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("rawtypes")
    public boolean onEvent(Player player, TaskManager manager, int type, Map map) {
        switch (type) {
            case EventConditionType.TASK_ACCEPT: {
                boolean bool = true;
                // 等级检查
                int plvl = ToolMap.getInt(EventConditionKey.PLAYER_LVL, map);
                if (plvl > 0) {
                    bool &= player.getLevel() >= plvl;
                }
                // 前置任务检查
                int preTaskId = ToolMap.getInt(EventConditionKey.PRE_TASK_ID, map);
                if (preTaskId > 0) {
                    bool &= (manager.inFinishMap((preTaskId)));
                }
                return bool;
            }
            default:
                return false;
        }
    }

    /**
     * 取得已经完成的任务进度
     * 
     * @param taskId
     * @return
     */
    public int getFinishTaskProgress(TaskManager taskManager, int taskId) {
        int finishProgress = 0;
        TaskCfgBean cfg = GameDataManager.getTaskCfgBean(taskId);
        if (cfg.getResetType() == TaskConstant.RESET_NO) {
            Integer progress = taskManager.getFinishTask(taskId);
            finishProgress = progress == null ? 0 : progress;
        } else {
            Task task = taskManager.getTask(taskId);
            finishProgress = task == null ? 0 : task.getProgress();
        }
        return finishProgress;
    }

    /**
     * 初始化事件条件
     * 
     * @param source
     * @return
     */
    @SuppressWarnings("rawtypes")
    public boolean eventConditionInit(Player player, GameEventVector vector, int conditionId,
            Map params, int process, int maxProcess) {

        EventConditionCfgBean eventConditionCfg =
                GameDataManager.getEventConditionCfgBean(conditionId);
        if (eventConditionCfg == null) {
            LOGGER.error("not fond eventConditionId :" + conditionId);
            return false;
        }
        // 继承进度
        if (process >= maxProcess) {
            vector.trigger(maxProcess);
            Task task = vector.getTask();
            if (checkPut(player.getTaskManager(), task)) {
                player.getTaskManager().putTaskMap(task.getCid(), task);
                return true;
            }
            return false;
        }

        // 如该条件不需要检查历史数据,则不进行条件初始化
        if (!eventConditionCfg.getHistory()) {
            Task task = vector.getTask();
            if (checkPut(player.getTaskManager(), task)) {
                player.getTaskManager().putTaskMap(task.getCid(), task);
                return true;
            }
            return false;
        }
        int eventId = eventConditionCfg.getEventId();
        GameEventStack eventStack =
                new GameEventStack(eventId, conditionId, params, process, maxProcess, false);
        EEventType type = EEventType.gerEEventType(eventStack.getEventId());
        if (type == null || type.getListener() == null) {
            return false;
        }
        IListener listener = null;
        try {
            listener = type.getListener().newInstance();
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        // 执行初始化
        listener.initEventCondition(player, eventStack);

        // 如果有条件进度被修改了
        if (eventStack.isTrigger(vector, eventStack.getProgress())) {
            vector.trigger(maxProcess);
            Task task = vector.getTask();
            if (checkPut(player.getTaskManager(), task)) {
                player.getTaskManager().putTaskMap(task.getCid(), task);
                return true;
            }

        }
        return false;
    }

    public boolean checkPut(TaskManager manager, Task task) {
        if (manager.inTaskMap(task.getCid()) || manager.inFinishMap(task.getCid())) {
            return false;
        }
        if (task.getProgress() == 0 && task.getStatus() == 0) {
            return false;
        }
        return true;
    }

    @Override
    protected Task acceptTask(Player player, TaskCfgBean taskCfg) {
        TaskManager taskManager = player.getTaskManager();
        // 进度继承
        int initProgress = taskCfg.getExtendsTaskId() != 0
                ? getFinishTaskProgress(taskManager, taskCfg.getExtendsTaskId()) : 0;

        Task task = new Task(taskCfg.getId(), initProgress, TaskConstant.STATUS_RUNING,
                new Date().getTime(), 0);
        // 初始化事件条件
        boolean code = eventConditionInit(player, new TaskVector(task), taskCfg.getFinishCondId(),
                taskCfg.getFinishParams(), task.getProgress(), taskCfg.getProgress());
        if (!code) {
            return null;
        }
        return task;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void eventPerformed(Player player, Event event) {
        // 取得玩家还在进行中的任务
        List<Task> list = Lists.newArrayList();
        Map<Integer, Task> allActiMap = getTask(player, false);
        list.addAll(allActiMap.values());
        List<TaskInfo> taskInfos = Lists.newArrayList();
        for (Task task : list) {
            // 因为有了进度继承所以没有完成的活动和可以领奖的活动都要检查
            if (task.getStatus() == TaskConstant.STATUS_RECEIVE
                    || task.getStatus() == TaskConstant.STATUS_TIME_OUT) {
                continue;
            }
            TaskCfgBean taskCfg = GameDataManager.getTaskCfgBean(task.getCid());
            if (taskCfg == null) {
                continue;
            }
            // 完成任务：用的是完成任务的条件参数
            Map params = taskCfg.getFinishParams();
            EEventType type = EEventType.gerEEventType(event.getEventId());
            if (type.getListener() == null) {
                break;
            }
            IListener listener = null;
            try {
                listener = type.getListener().newInstance();
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
            listener.handler(player, event, new TaskVector(task), taskCfg.getFinishCondId(), params,
                    taskCfg.getProgress(), taskInfos);
            if (event.getEventId() == EEventType.TASK_ACCEPT.value())
                break;
        }
        if (!taskInfos.isEmpty()) {
            TaskMsgBuilder.notifyTaskInfos(player, taskInfos);
        }

        if (event.getParams() != null) {
            Map<String, Object> in = (Map<String, Object>) event.getParams();
            in.put(EventConditionKey.EVENT_ID, event.getEventId());
        }
        player._fireEvent(event.getParams(), EEventType.ACTIVITY_EVENT.value());
    }

    @Override
    protected List<Task> checkTimeOutTasks(TaskManager manager) {
        List<Task> timeOutTasks = Lists.newArrayList();
        List<TaskCfgBean> cfgs = GameDataManager.getTaskCfgBeans();
        for (TaskCfgBean taskCfg : cfgs) {
            // 有效
            Task task = manager.getTask(taskCfg.getId());
            if (task == null) {
                continue;
            }
            // 开启判断
            if (!taskCfg.getOpen()) {
                timeOutTasks.add(task);
                manager.removeTask(task.getCid());
                continue;
            }
        }
        return timeOutTasks;
    }

    /**
     * 重置任务/日常任务重置
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<ChangeType, List<Task>> resetTasks(Player player) {
        TaskManager manager = player.getTaskManager();
        Map<ChangeType, List<Task>> resetTasks = Maps.newHashMap();
        Map<Integer, Integer> mailStrategy = Maps.newHashMap();
        Date nowDate = new Date();
        List<TaskCfgBean> cfgs = GameDataManager.getTaskCfgBeans();
        int nowLevel = player.getLevel();
        ChangeType type = ChangeType.UPDATE;
        for (TaskCfgBean taskCfg : cfgs) {
            int resetType = taskCfg.getResetType();
            int[] stage = taskCfg.getPlayerLevel();
            if (resetType != TaskConstant.TYPE_DAY_CONSTANT) {
                continue;
            }
            Task task = manager.getTask(taskCfg.getId());
            if (task == null) {
                if (!taskCfg.getOpen() || !isInRange(stage, nowLevel)) {
                    continue;
                }
                task = new Task(taskCfg.getId(), 0, TaskConstant.STATUS_RUNING,
                        new Date().getTime(), 0);
                type = ChangeType.ADD;
            } else if (!isInRange(stage, nowLevel)) {
                type = ChangeType.DELETE;
            }
            // (新增)如果当天的日常任务奖励没领取,通过邮件发送
            if (task.getStatus() == TaskConstant.STATUS_FINISH) {
                TaskCfgBean cfg = GameDataManager.getTaskCfgBean(task.getCid());
                Map<Integer, Integer> reward = cfg.getReward();
                // 合并奖励
                ItemUtils.mergeItemMap(mailStrategy, reward);
            }
            task.setProgress(0);
            task.setStatus(TaskConstant.STATUS_RUNING);
            task.setDate(nowDate.getTime());
            List<Task> list = resetTasks.get(type);
            if (list == null) {
                list = new ArrayList<>();
                resetTasks.put(type, list);
            }
            list.add(task);
        }
        // 再整合一次
        type = ChangeType.UPDATE;
        if (mailStrategy.containsKey(ItemConstantId.PLAYER_ACTIVE)) {
            player.getBagManager().addItem(ItemConstantId.PLAYER_ACTIVE,
                    mailStrategy.get(ItemConstantId.PLAYER_ACTIVE), true, EReason.TASK);
            for (TaskCfgBean taskCfg : cfgs) {
                int resetType = taskCfg.getResetType();
                int[] stage = taskCfg.getPlayerLevel();
                if (resetType != TaskConstant.TYPE_DAY_CONSTANT) {
                    continue;
                }
                Task task = manager.getTask(taskCfg.getId());
                if (task == null) {
                    if (!taskCfg.getOpen() || !isInRange(stage, nowLevel)) {
                        continue;
                    }
                    task = new Task(taskCfg.getId(), 0, TaskConstant.STATUS_RUNING,
                            new Date().getTime(), 0);
                    type = ChangeType.ADD;
                } else if (!isInRange(stage, nowLevel)) {
                    type = ChangeType.DELETE;
                }
                // (新增)如果当天的日常任务奖励没领取,通过邮件发送
                if (task.getStatus() == TaskConstant.STATUS_FINISH) {
                    TaskCfgBean cfg = GameDataManager.getTaskCfgBean(task.getCid());
                    Map<Integer, Integer> reward = cfg.getReward();
                    // 合并奖励
                    ItemUtils.mergeItemMap(mailStrategy, reward);
                }
                // 自检测已完成
                task.setProgress(0);
                task.setStatus(TaskConstant.STATUS_RUNING);
                task.setDate(nowDate.getTime());

                List<Task> list = resetTasks.get(type);
                if (!isExist(task.getCid(), list)) {
                    if (list == null) {
                        list = new ArrayList<>();
                        resetTasks.put(type, list);
                    }
                    list.add(task);
                }
            }
        }
        // 未领取邮件
        // 处理走邮件的道具
        if (!mailStrategy.isEmpty()) {
            if (mailStrategy.containsKey(ItemConstantId.PLAYER_ACTIVE)) {
                // 去除活跃度
                mailStrategy.remove(ItemConstantId.PLAYER_ACTIVE);
            }
            if (!mailStrategy.isEmpty()) {
                String title = GameDataManager.getStringCfgBean(212009).getText();
                String body = GameDataManager.getStringCfgBean(212010).getText();
                ((MailService) (MailService.getInstance())).sendPlayerMail(false,
                        player.getPlayerId(), title, body, mailStrategy, EReason.DAILY_REWARD);
            }
        }
        // 移除任务
        for (Map.Entry<ChangeType, List<Task>> entry : resetTasks.entrySet()) {
            if (entry.getKey() == ChangeType.ADD) {
                for (Task task : entry.getValue()) {
                    manager.putTaskMap(task.getCid(), task);
                }
            } else if (entry.getKey() == ChangeType.DELETE) {
                for (Task task : entry.getValue()) {
                    manager.removeTask(task.getCid());
                }
            }
        }
        return resetTasks;
    }

    public boolean isInRange(int[] range, int level) {
        if (range == null || range.length <= 0) {
            return false;
        }
        int lenght = range.length;
        // 如果配置表只配了一个
        if (lenght < 2) {
            return level >= range[0];
        } else {
            return level >= range[0] && level <= range[range.length - 1];
        }
    }

    public boolean isExist(int cid, List<Task> list) {
        if (list == null) {
            return false;
        }
        for (Task task : list) {
            if (cid == task.getCid()) {
                return true;
            }
        }
        return false;
    }
}
