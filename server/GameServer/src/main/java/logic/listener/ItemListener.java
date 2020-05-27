package logic.listener;

import java.util.List;
import java.util.Map;

import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.EventType;
import logic.item.bean.Item;
import logic.task.bean.GameEventStack;
import logic.task.bean.GameEventVector;
import logic.task.bean.Task;

import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import utils.ToolMap;

import com.google.common.collect.Maps;
import com.google.gson.Gson;

import data.GameDataManager;
import data.bean.BaseGoods;
import data.bean.EquipmentCfgBean;
import data.bean.EventConditionCfgBean;
import data.bean.ItemTimeCfgBean;
import event.Event;
import event.IListener;
import gm.db.global.bean.ActivityTaskItem;

public class ItemListener implements IListener {

    @Override
    public void initEventCondition(Player player, GameEventStack eventStack) {
        switch (eventStack.getConditionType()) {
            case EventConditionType.HOLD_HERO: {
                int heroId = ToolMap.getInt(EventConditionKey.HERO_CID, eventStack.getParams());
                int quality = ToolMap.getInt(EventConditionKey.QUALITY, eventStack.getParams());
                int count = player.getHeroManager().getHeroCountByHeroIdAndQuality(heroId, quality);
                eventStack.setProgress(count);
            }
                break;
            case EventConditionType.HOLD_ITEM: {
                List<Item> items = player.getBagManager().getAllItems();
                BaseGoods goods = null;
                int count = 0;
                for (Item item : items) {
                    goods = GameDataManager.getBaseGoods(item.getTemplateId());
                    if (goods != null && goods instanceof ItemTimeCfgBean) {
                        ItemTimeCfgBean cfg = (ItemTimeCfgBean) goods;
                        goods = GameDataManager.getBaseGoods(cfg.getItemId());
                    }
                    if (goods == null) {
                        continue;
                    }
                    int itemId =
                            ToolMap.getInt(EventConditionKey.ITEM_CID, eventStack.getParams(), 0);
                    int superType =
                            ToolMap.getInt(EventConditionKey.SUPER_TYPE, eventStack.getParams(), 0);
                    int subType =
                            ToolMap.getInt(EventConditionKey.SUB_TYPE, eventStack.getParams(), 0);
                    int smallType =
                            ToolMap.getInt(EventConditionKey.SMALL_TYPE, eventStack.getParams(), 0);
                    int quality =
                            ToolMap.getInt(EventConditionKey.QUALITY, eventStack.getParams(), 0);
                    if (itemId != 0 && goods.getId() != itemId) {
                        continue;
                    }
                    if (superType != 0 && goods.getSuperType() != superType) {
                        continue;
                    }
                    if (subType != 0 && goods.getSubType() != subType) {
                        continue;
                    }
                    // if (smallType !=0 && goods.getSmallType() != smallType) {
                    // continue;
                    // }
                    if (quality != 0 && goods.getQuality() != quality) {
                        continue;
                    }
                    count++;
                }
                eventStack.setProgress(count);
            }
                break;
        }
    }


    // @Override
    // public void eventConditionInit(Player player, GameEventVector vector, int conditionId,
    // Map params, int process, int maxProcess) {
    // EventConditionCfgBean eventConditionCfg =
    // GameDataManager.getEventConditionCfgBean(conditionId);
    // if (eventConditionCfg == null) {
    // return;
    // }
    // // 继承进度
    // if (process >= maxProcess) {
    // vector.trigger(maxProcess);
    // return;
    // }
    //
    // // 如该条件不需要检查历史数据,则不进行条件初始化
    // if (!eventConditionCfg.getHistory()) {
    // return;
    // }
    //
    // int eventId = eventConditionCfg.getEvent_id();
    // GameEventStack eventStack =
    // new GameEventStack(eventId, conditionId, params, process, maxProcess, false);
    //
    // // 执行初始化
    // initEventCondition(player, eventStack);
    //
    // // 如果有条件进度被修改了
    // if (eventStack.isTrigger(vector, eventStack.getProgress())) {
    // vector.trigger(maxProcess);
    // }
    // }

    @SuppressWarnings("rawtypes")
    @Override
    public void onEvent(Event event, Player player) {
        GameEventStack eventStack = event.peek();
        switch (eventStack.getConditionType()) {
            case EventConditionType.HOLD_HERO: {
                if (checkTriggerType((Map) event.getParams(), EventConditionType.HOLD_HERO)) {
                    boolean bool = true;
                    bool &= verify_eq(EventConditionKey.HERO_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_ge(EventConditionKey.QUALITY, (Map) event.getParams(),
                            eventStack.getParams());
                    if (bool) {
                        eventStack.changeProgress(1);
                    }
                }
                break;
            }
            case EventConditionType.HOLD_ITEM:
            case EventConditionType.ADD_ITEM: {
                if (checkTriggerType((Map) event.getParams(), EventConditionType.ADD_ITEM)) {
                    boolean bool = true;
                    bool &= verify_eq(EventConditionKey.ITEM_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_eq(EventConditionKey.SUPER_TYPE, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_eq(EventConditionKey.SUB_TYPE, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_eq(EventConditionKey.SMALL_TYPE, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_eq(EventConditionKey.QUALITY, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_not_eq(EventConditionKey.EXCLUDE, (Map) event.getParams(),
                            eventStack.getParams());
                    int count = ToolMap.getInt(EventConditionKey.COUNT, (Map) event.getParams());
                    if (bool) {
                        eventStack.changeProgress(count);
                    }
                }
                break;
            }
            case EventConditionType.DEL_ITEM: {
                if (checkTriggerType((Map) event.getParams(), eventStack.getConditionType())) {
                    boolean bool = true;
                    bool &= verify_eq(EventConditionKey.ITEM_CID, (Map) event.getParams(),
                            eventStack.getParams());
                    bool &= verify_not_eq(EventConditionKey.EXCLUDE, (Map) event.getParams(),
                            eventStack.getParams());
                    int count = ToolMap.getInt(EventConditionKey.COUNT, (Map) event.getParams());
                    if (bool) {
                        eventStack.changeProgress(count);
                    }
                }
                break;
            }
            case EventConditionType.EQUIP_COVER_COUNT: {
                if (checkTriggerType((Map) event.getParams(), EventConditionType.ADD_ITEM)) {
                    boolean bool = true;
                    int goodsId =
                            ToolMap.getInt(EventConditionKey.ITEM_CID, (Map) event.getParams());
                    int coverId =
                            ToolMap.getInt(EventConditionKey.COVER_ID, eventStack.getParams());
                    BaseGoods goods = GameDataManager.getBaseGoods(goodsId);
                    // BaseGoods goods = GameDataManager.getItemCfgBean(goodsId);
                    if (goods instanceof EquipmentCfgBean) {
                        EquipmentCfgBean equip = (EquipmentCfgBean) goods;
                        if (equip.getSuit() == coverId) {
                            // bool &= EquipmentManager.me().checkHaveCover(player, coverId);
                        } else if (coverId == 0) {
                            // int suitCount =
                            // checkEquipmentSuitCount(player, eventStack.getMaxProgress());
                            // bool &= suitCount > eventStack.getProgress();
                        } else {
                            bool &= false;
                        }
                    } else {
                        bool &= false;
                    }

                    if (bool) {
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
        // 检查该条件监听的事件
        if (eventConditionCfg.getEventId() != EventType.ITEM_EVENT)
            return;

        // 如果条件参数为空，则任务成功触发条件
        boolean trigger = (params == null || params.isEmpty());

        // 当前进度
        int nowProgress = vector.getProgress();
        if (nowProgress >= maxProcess) {
            trigger = true;
        } else {
            trigger = false;
        }

        event.push(new GameEventStack(eventConditionCfg.getEventId(), conditionId, params,
                nowProgress, maxProcess, trigger));
        onEvent(event, player);
        GameEventStack eventStack = event.peek();
        // 条件触发成功
        if (eventStack.isTrigger(vector, eventStack.getProgress())) {
            if (eventStack.getProgress() == nowProgress) {
                return;
            }
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
        int eventId = eventConditionCfg.getEventId();
        // 检查该条件监听的事件
        if (event.getEventId() != eventId) {
            return;
        }

        // 当前进度
        // int nowProgress = record.getProgress();
        // if (nowProgress >= taskItem.getProgress())
        // return;
        Map<String, Integer> params = null;
        if (taskItem.getFinishParams() != null && taskItem.getFinishParams().length() > 0) {
            params = Maps.newHashMap();
            params = new Gson().fromJson(taskItem.getFinishParams(), params.getClass());
        }
        event.push(new GameEventStack(eventId, taskItem.getFinishCondid(), params,
                record.getProgress(), taskItem.getProgress(), false));
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
