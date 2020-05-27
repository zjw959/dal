package logic.task;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CItemMsg.ItemList;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import data.GameDataManager;
import data.bean.TaskCfgBean;
import event.Event;
import event.IEventListener;
import event.IListener;
import logic.basecore.IAcrossDay;
import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.EAcrossDayType;
import logic.constant.EEventType;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.ItemConstantId;
import logic.constant.TaskConstant;
import logic.item.ItemUtils;
import logic.item.bean.Item;
import logic.msgBuilder.ItemMsgBuilder;
import logic.msgBuilder.TaskMsgBuilder;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import logic.task.bean.Task;
import utils.ExceptionEx;
import utils.MapUtil;

public class TaskManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter, ICreatePlayerInitialize, IEventListener, IAcrossDay {
    private static final Logger LOGGER = Logger.getLogger(TaskManager.class);

    private Map<Integer, Task> taskMap = new HashMap<Integer, Task>();
    private Map<Integer, Task> dailyMap = new HashMap<Integer, Task>();
    private Map<String, Object> FirstMap = new HashMap<String, Object>();
    private transient Map<Integer, Integer> finishMap = new HashMap<Integer, Integer>();
    private int actEngrty;// 活跃度
    private long lastLoginTime;

    public void reqTasks(Player player) {
        LogicScriptsUtils.getITaskScript().reqTasks(player, ChangeType.DEFAULT);
    }


    public void submitTasks(Player player, int taskCid) {
        LogicScriptsUtils.getITaskScript().submitTasks(player, taskCid);
    }

    /**
     * 接受任务
     */
    public Task acceptTask(Player player, TaskCfgBean taskCfg) {
        return LogicScriptsUtils.getITaskScript().acceptTask(player, taskCfg);
    }

    @Override
    public void eventPerformed(Event event) {
        LogicScriptsUtils.getITaskScript().eventPerformed(player, event);
    }

    public Map<Integer, Task> getAcceptTaskMap() {
        return LogicScriptsUtils.getITaskScript().getTask(player, true);
    }

    public Map<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public Map<Integer, Integer> getFinishMap() {
        return finishMap;
    }

    @Override
    public void registerPerformed(Player player) {
        for (EEventType type : EEventType.values()) {
            if (type.getListener() == null) {
                continue;
            }
            IListener listener = null;
            try {
                listener = type.getListener().newInstance();
                if (listener instanceof IListener) {
                    player.registerEventListener(type.value(), this);
                }
            } catch (Exception e) {
                LOGGER.error("task manager register error", e);
            }
        }
    }



    public Integer getFinishTask(Integer key) {
        return finishMap.get(key);
    }



    public boolean inFinishMap(int taskId) {
        return finishMap.containsKey(taskId);
    }

    public boolean checkFirst(String key, boolean value) {
        return MapUtil.getBoolean(FirstMap, key, value);
    }

    public void putFirstMap(String key, Object value) {
        FirstMap.put(key, value);
    }

    public void putFinishMap(Integer key, Integer value) {
        finishMap.put(key, value);
    }

    public void putTaskMap(Integer key, Task value) {
        TaskCfgBean cfgBean = GameDataManager.getTaskCfgBean(key);
        if (cfgBean.getResetType() == TaskConstant.TYPE_DAY_CONSTANT) {
            dailyMap.put(key, value);
        } else {
            taskMap.put(key, value);
        }
    }

    public Task getTask(Integer key) {
        TaskCfgBean cfgBean = GameDataManager.getTaskCfgBean(key);
        if (cfgBean.getResetType() == TaskConstant.TYPE_DAY_CONSTANT) {
            return dailyMap.get(key);
        } else {
            return taskMap.get(key);
        }
    }

    public Task removeTask(Integer key) {
        TaskCfgBean cfgBean = GameDataManager.getTaskCfgBean(key);
        if (cfgBean.getResetType() == TaskConstant.TYPE_DAY_CONSTANT) {
            return dailyMap.remove(key);
        } else {
            return taskMap.remove(key);
        }
    }


    public boolean inTaskMap(int taskId) {
        TaskCfgBean cfgBean = GameDataManager.getTaskCfgBean(taskId);
        if (cfgBean.getResetType() == TaskConstant.TYPE_DAY_CONSTANT) {
            return dailyMap.containsKey(taskId);
        } else {
            return taskMap.containsKey(taskId);
        }
    }

    public Map<Integer, Task> getDailyMap() {
        return dailyMap;
    }

    public boolean inDailyMap(int taskId) {
        return dailyMap.containsKey(taskId);
    }


    /**
     * 任务检查
     * 
     * @param players
     */
    private void checkTasks() {
        List<TaskInfo> taskInfos = Lists.newArrayList();
        List<Task> list =
                LogicScriptsUtils.getITaskScript().checkTimeOutTasks(player.getTaskManager());
        // 检测开启任务
        if (!list.isEmpty()) {
            taskInfos.addAll(TaskMsgBuilder.createTaskInfoList(player, ChangeType.DELETE, list));
            TaskMsgBuilder.notifyTaskInfos(player, taskInfos);
        }
    }

    /**
     * 任务检查
     * 
     * @param players
     */
    private void checkAcrossDay() {
        try {
            List<TaskInfo> taskInfos = Lists.newArrayList();
            Map<ChangeType, List<Task>> map = LogicScriptsUtils.getITaskScript().resetTasks(player);
            // 重置任务
            for (Entry<ChangeType, List<Task>> entry : map.entrySet()) {
                taskInfos.addAll(TaskMsgBuilder.createTaskInfoList(player, entry.getKey(),
                        entry.getValue()));
            }
            if (!taskInfos.isEmpty()) {
                TaskMsgBuilder.notifyTaskInfos(player, taskInfos);
            }
            // 每日重置活跃度
            resetActEngrtyAndNotify();
            // 每日检测完成
            acrossDayCommit();
        } catch (Exception e) {
            LOGGER.error(ConstDefine.LOG_ERROR_LOGIC_PREFIX + ExceptionEx.e2s(e));
        }

    }

    /**
     * 
     */
    public void acrossDayCommit() {
        login();
        MonthCard();
        this.lastLoginTime = new Date().getTime();
    }

    public void login() {
        // 这里触发下登录事件
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.LAST_LOGIN_DATE, new Date(this.lastLoginTime));
        in.put(EventConditionKey.NOW_LOGIN_DATE, new Date());
        player._fireEvent(in, EEventType.LOGIN.value());
    }

    public void MonthCard() {
        // 触发月卡任务事件
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.GAIN_MONTH_CARD);
        player._fireEvent(in, EEventType.OTHER_EVENT.value());
    }

    public void init() {
        for (Task task : taskMap.values()) {
            if (task.getFinish() == 0) {
                continue;
            }
            finishMap.put(task.getCid(), task.getProgress());
        }
    }

    @Override
    public void tick() {
        try {
            if (dailyMap.isEmpty()) {
                checkAcrossDay();
            }
            checkTasks();
        } catch (Exception e) {
            LOGGER.error(ConstDefine.LOG_ERROR_LOGIC_PREFIX + ExceptionEx.e2s(e));
        }

    }

    @Override
    public void loginInit() {
        // acrossDayCommit();
    }

    @Override
    public void createPlayerInitialize() {
        init();
    }


    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        if (type == EAcrossDayType.GAME_ACROSS_DAY) {
            checkAcrossDay();
        }
    }

    public int getActEngrty() {
        return actEngrty;
    }

    public void setActEngrty(int actEngrty) {
        this.actEngrty = actEngrty;
    }

    public int addActEngrty(int number) {
        this.actEngrty += number;
        return this.actEngrty;
    }

    public void resetActEngrtyAndNotify() {
        this.actEngrty = 0;
        ItemList.Builder itemChange = ItemList.newBuilder();
        List<Item> items = ItemUtils.createItems(ItemConstantId.PLAYER_ACTIVE, this.actEngrty);
        ItemMsgBuilder.addItemMsg(itemChange, ChangeType.UPDATE, items.get(0));
        // 发送
        MessageUtils.send(player, itemChange);

    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

}
