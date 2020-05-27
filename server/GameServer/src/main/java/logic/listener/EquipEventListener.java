package logic.listener;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

import data.GameDataManager;
import data.bean.EquipmentCfgBean;
import data.bean.EventConditionCfgBean;
import event.Event;
import event.IListener;
import gm.db.global.bean.ActivityTaskItem;
import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.EventType;
import logic.equip.EquipService;
import logic.item.bean.EquipItem;
import logic.item.bean.Item;
import logic.role.bean.Role;
import logic.task.bean.GameEventStack;
import logic.task.bean.GameEventVector;
import logic.task.bean.Task;
import utils.ToolMap;

public class EquipEventListener implements IListener {

    @Override
    public void initEventCondition(Player player, GameEventStack eventStack) {
        switch (eventStack.getConditionType()) {
            // 某看板娘第一次穿某时装
            case EventConditionType.DRESS: {
                break;
            }
            // 灵装最大等级
            case EventConditionType.EQUIP_MAX_LVL: {
                int equipId =
                        ToolMap.getInt(EventConditionKey.EQUIPMENT_CID, eventStack.getParams());
                int maxLvl = player.getBagManager().getMaxEquipLvl(equipId);
                eventStack.setProgress(maxLvl);
                break;
            }
            case EventConditionType.EQUIP_COVER_COUNT: {
                int coverId = ToolMap.getInt(EventConditionKey.COVER_ID, eventStack.getParams());
                Collection<Item> AllItem = player.getBagManager().getAllItems();
                int maxLvl = 0;
                Set<Integer> set = new HashSet<>();
                for (Item item : AllItem) {
                    if (!(item instanceof EquipItem)) {
                        continue;
                    }
                    EquipItem equip = (EquipItem) item;
                    EquipmentCfgBean cfgBean =
                            GameDataManager.getEquipmentCfgBean(equip.getTemplateId());
                    if (cfgBean.getSuit() != coverId || set.contains(coverId)) {
                        continue;
                    }
                    if (EquipService.getInstance().checkHaveCover(player, coverId)) {
                        maxLvl++;
                        set.add(coverId);
                    }
                }
                eventStack.setProgress(maxLvl);
                break;
            }
            default:
                break;
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onEvent(Event event, Player player) {
        GameEventStack eventStack = event.peek();
        switch (eventStack.getConditionType()) {
            // 某看板娘第一次穿某时装
            case EventConditionType.DRESS: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = true;
                    bool &= verify_eq(EventConditionKey.ROLE_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_eq(EventConditionKey.DRESS_ID, (Map) event.getParams(),
                            eventStack.getParams());

                    boolean checkCount = false;
                    if (ToolMap.getBoolean(EventConditionKey.FIRST_TIME, eventStack.getParams(),
                            false)) {
                        checkCount = isFirstTimeDress(player, event, eventStack);
                        bool &= checkCount;
                    }

                    if (bool) {
                        eventStack.setProgress(1);
                        if (checkCount) {
                            updateRole(player, event);
                        }
                    }
                }
                break;
            }
            // 灵装最大等级
            case EventConditionType.EQUIP_MAX_LVL: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.EQUIPMENT_CID,
                            (Map) event.getParams(), eventStack.getParams());
                    int equipLvl =
                            ToolMap.getInt(EventConditionKey.NOW_LEVEL, (Map) event.getParams());
                    int progress = eventStack.getMaxProgress();
                    if (bool && equipLvl <= progress) {
                        eventStack.setProgress(equipLvl);
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
            case EventConditionType.EQUIP_CHANGER: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = verify_eq(EventConditionKey.EQUIP_STAR, (Map) event.getParams(),
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

    /**
     * 是否第一次穿次时装
     */
    @SuppressWarnings("rawtypes")
    private boolean isFirstTimeDress(Player player, Event event, GameEventStack eventStack) {
        boolean bool = player.getTaskManager().checkFirst("isFirstTimeDress", false);
        if (bool) {
            return bool;
        }

        Role role = player.getRoleManager()
                .getRole((Integer) ((Map) event.getParams()).get(EventConditionKey.ROLE_CID));
        int dressCid = (int) ((Map) event.getParams()).get(EventConditionKey.DRESS_ID);
        bool = !role.getDressDating().contains(dressCid);
        if (bool) {
            player.getTaskManager().putFirstMap("isFirstTimeDress", true);
        }
        return bool;
    }

    @SuppressWarnings("rawtypes")
    private void updateRole(Player player, Event event) {
        boolean bool = player.getTaskManager().checkFirst("updateRole", false);
        if (bool) {
            return;
        }
        int dressCid = (int) ((Map) event.getParams()).get(EventConditionKey.ROLE_CID);
        Role role = player.getRoleManager().getRole(dressCid);
        // role.getDressDating().add(dressCid);
        // role.update();

        player.getTaskManager().putFirstMap("updateRole", true);
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
        if (eventConditionCfg.getEventId() != EventType.PASS_DUP)
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
