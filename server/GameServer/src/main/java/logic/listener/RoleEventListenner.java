package logic.listener;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.EventType;
import logic.role.bean.Role;
import logic.task.bean.GameEventStack;
import logic.task.bean.GameEventVector;
import logic.task.bean.Task;

import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import utils.ToolMap;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

import data.GameDataManager;
import data.bean.EventConditionCfgBean;
import event.Event;
import event.IListener;
import gm.db.global.bean.ActivityTaskItem;

public class RoleEventListenner implements IListener {

    @Override
    public void initEventCondition(Player player, GameEventStack eventStack) {
        switch (eventStack.getConditionType()) {
            case EventConditionType.ROLE_FAVOR: {
                Collection<Role> roles = player.getRoleManager().getRoles().values();
                int maxFavor = 0;
                for (Role role : roles) {
                    int roleId = ToolMap.getInt(EventConditionKey.ROLE_CID, eventStack.getParams());
                    if (roleId != 0 && roleId != role.getCid()) {
                        continue;
                    }
                    maxFavor = Math.max(maxFavor, role.getFavor());
                }
                eventStack.setProgress(maxFavor);
                break;
            }
            case EventConditionType.ROLE_FAVOR_COUNT: {
                Collection<Role> roles = player.getRoleManager().getRoles().values();
                int count = 0;
                for (Role role : roles) {
                    int roleId = ToolMap.getInt(EventConditionKey.ROLE_CID, eventStack.getParams());
                    int limit = ToolMap.getInt(EventConditionKey.LIMIT, eventStack.getParams());
                    if (roleId != 0 && roleId != role.getCid()) {
                        continue;
                    }
                    if (limit != 0 && role.getFavor() < limit) {
                        continue;
                    }
                    count++;
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
            case EventConditionType.FINISH_DATING_COUNT: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.ROLE_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_eq(EventConditionKey.DATING_TYPE, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_eq(EventConditionKey.SCRIPT_ID, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_eq(EventConditionKey.END_TYPE, (Map) event.getParams(),
                            eventStack.getParams());
                    if (bool) {
                        eventStack.changeProgress(1);
                    }
                }

                break;
            }
            case EventConditionType.DONATE_GIFT: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.ROLE_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_eq(EventConditionKey.GIFT_CID, (Map) event.getParams(),
                            eventStack.getParams());

                    boolean checkCount = false;
                    if (ToolMap.getInt(EventConditionKey.MAX_COUNT, eventStack.getParams(),
                            0) > 0) {
                        checkCount = checkTriggerCount(player, event, eventStack);
                        bool &= checkCount;
                    }

                    if (bool) {
                        eventStack.changeProgress(1);
                        if (checkCount) {
                            updateDonate(player, event);
                        }
                    }
                }
                break;
            }
            case EventConditionType.ROLE_FAVOR: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.ROLE_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    int oldFavor =
                            ToolMap.getInt(EventConditionKey.OLD_FAVOR, (Map) event.getParams());
                    int nowFavor =
                            ToolMap.getInt(EventConditionKey.NOW_FAVOR, (Map) event.getParams());
                    int targetFavor = eventStack.getMaxProgress();
                    if (bool && oldFavor < targetFavor && nowFavor > eventStack.getProgress()) {
                        eventStack.setProgress(nowFavor);
                    }
                }
                break;
            }
            case EventConditionType.ROLE_FAVOR_COUNT: {
                if (checkTriggerType((Map) event.getParams(), EventConditionType.ROLE_FAVOR)) {
                    int oldFavor =
                            ToolMap.getInt(EventConditionKey.OLD_FAVOR, (Map) event.getParams());
                    int nowFavor =
                            ToolMap.getInt(EventConditionKey.NOW_FAVOR, (Map) event.getParams());
                    boolean bool = verify_eq(EventConditionKey.ROLE_CID, (Map) event.getParams(),
                            eventStack.getParams());

                    int limit = ToolMap.getInt(EventConditionKey.LIMIT, eventStack.getParams());
                    if (bool && oldFavor < limit && nowFavor >= limit) {
                        eventStack.changeProgress(1);
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean checkTriggerCount(Player player, Event event, GameEventStack eventStack) {
        int maxCount = ToolMap.getInt(EventConditionKey.MAX_COUNT, eventStack.getParams(), 0);

        boolean bool = player.getTaskManager().checkFirst("checkTriggerCount", false);
        if (bool) {
            return bool;
        }

        Role role = player.getRoleManager()
                .getRole(ToolMap.getInt(EventConditionKey.ROLE_CID, (Map) event.getParams(), 0));
        int giftId = ToolMap.getInt(EventConditionKey.GIFT_CID, (Map) event.getParams(), 0);
        int count = (int) role.getDonateDating().computeIfAbsent(giftId, k -> 0);


        bool = count < maxCount;
        if (bool) {
            player.getTaskManager().putFirstMap("checkTriggerCount", true);
        }
        return bool;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void updateDonate(Player player, Event event) {
        boolean bool = player.getTaskManager().checkFirst("updateDonate", false);
        if (bool) {
            return;
        }

        // 修改触发记录
        Role role = player.getRoleManager()
                .getRole((Integer) ((Map) event.getParams()).get(EventConditionKey.ROLE_CID));
        int giftId = (Integer) ((Map) event.getParams()).get(EventConditionKey.GIFT_CID);
        int count = (int) role.getDonateDating().computeIfAbsent(giftId, k -> 0);
        role.getDonateDating().put(giftId, count + 1);

        player.getTaskManager().putFirstMap("updateDonate", true);
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
        if (eventConditionCfg.getEventId() != EventType.ROLE_CHANGE)
            return;

        // 当前进度
        int nowProgress = record.getProgress();
        if (nowProgress >= taskItem.getProgress())
            return;
        Map<String, Integer> params = null;
        if (taskItem.getFinishParams() != null && taskItem.getFinishParams().length() > 0) {
            params = Maps.newHashMap();
            params = new Gson().fromJson(taskItem.getFinishParams(), params.getClass());
        }
        event.push(new GameEventStack(eventConditionCfg.getEventId(), taskItem.getFinishCondid(),
                params, nowProgress, taskItem.getProgress(), false));
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
