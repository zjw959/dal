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
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.EventType;
import logic.hero.bean.Hero;
import logic.task.bean.GameEventStack;
import logic.task.bean.GameEventVector;
import logic.task.bean.Task;
import utils.ToolMap;

public class AngelChangeEventListenner implements IListener {

    @Override
    public void initEventCondition(Player player, GameEventStack eventStack) {
        switch (eventStack.getConditionType()) {
            case EventConditionType.ADVANCED_LVL: {
                Collection<Hero> heros = player.getHeroManager().getHeroList();
                int maxLvl = 0;
                int heroCid = ToolMap.getInt(EventConditionKey.HERO_CID, eventStack.getParams());
                for (Hero hero : heros) {
                    if (heroCid != 0 && heroCid != hero.getCid()) {
                        continue;
                    }
                    // 后面加天使
                    // maxLvl = Math.max(maxLvl, hero.getAngelLvl());
                }
                eventStack.setProgress(maxLvl);
            }
                break;
            case EventConditionType.ADVANCED_LVL_COUNT: {
                Collection<Hero> heros = player.getHeroManager().getHeroList();
                int limitLvl =
                        ToolMap.getInt(EventConditionKey.LIMIT_LEVEL, eventStack.getParams());
                int heroCid = ToolMap.getInt(EventConditionKey.HERO_CID, eventStack.getParams());
                int count = 0;
                for (Hero hero : heros) {
                    if (heroCid != 0 && heroCid != hero.getCid()) {
                        continue;
                    }
                    // if (hero.getStageLevel() < limitLvl) {
                    // continue;
                    // }
                    count++;
                }
                eventStack.setProgress(count);
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
            case EventConditionType.ANGEL_STAR_LVL: {
                if (checkTriggerType((Map) event.getParams(), EventConditionType.ANGEL_STAR_LVL)) {
                    int oldLv =
                            ToolMap.getInt(EventConditionKey.OLD_LEVEL, (Map) event.getParams());
                    int nowLv =
                            ToolMap.getInt(EventConditionKey.NOW_LEVEL, (Map) event.getParams());
                    int progress = eventStack.getMaxProgress();
                    if (verify_eq(EventConditionKey.HERO_CID, (Map) event.getParams(),
                            eventStack.getParams()) && oldLv < progress) {
                        eventStack.setProgress(nowLv);
                    }
                }
                break;
            }
            case EventConditionType.ANGEL_STAR_LVL_COUNT: {
                // 天使升星事件触发拥有某星数天使数量事件
                if (checkTriggerType((Map) event.getParams(), EventConditionType.ANGEL_STAR_LVL)) {
                    int oldLv =
                            ToolMap.getInt(EventConditionKey.OLD_LEVEL, (Map) event.getParams());
                    int nowLv =
                            ToolMap.getInt(EventConditionKey.NOW_LEVEL, (Map) event.getParams());
                    int limitLvl =
                            ToolMap.getInt(EventConditionKey.LIMIT_LEVEL, eventStack.getParams());
                    if (oldLv < limitLvl && nowLv >= limitLvl) {
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
        if (eventConditionCfg.getEventId() != EventType.ANGEL_CHANGE)
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
