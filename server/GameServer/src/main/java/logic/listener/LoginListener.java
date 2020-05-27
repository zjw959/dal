package logic.listener;

import java.util.Date;
import java.util.List;
import java.util.Map;
import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.EventType;
import logic.task.bean.GameEventStack;
import logic.task.bean.GameEventVector;
import logic.task.bean.Task;
import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;
import utils.DateEx;
import utils.TimeUtil;
import utils.ToolMap;
import cn.hutool.core.date.DateUtil;
import data.GameDataManager;
import data.bean.EventConditionCfgBean;
import event.Event;
import event.IListener;
import gm.db.global.bean.ActivityTaskItem;

public class LoginListener implements IListener {

    @Override
    public void initEventCondition(Player player, GameEventStack eventStack) {
        switch (eventStack.getConditionType()) {
            case EventConditionType.TOTAL_LOGIN_DAY: {
                boolean bool = true;
                Date lastLoginDate = new Date(player.getTaskManager().getLastLoginTime());
                Date nowLoginDate = new Date();
                bool &= checkPreEntryFinish(player, eventStack);
                bool &= TimeUtil.betweenDay6Clock(lastLoginDate, nowLoginDate) != 0;
                // 只有相差天数不等于0 才认为是新的一天登陆
                if (bool) {
                    eventStack.changeProgress(1);
                }
            }
                break;
            case EventConditionType.CONT_LOGIN_DAY: {
                Date lastLoginDate = new Date(player.getTaskManager().getLastLoginTime());
                Date nowLoginDate = new Date();
                long day = TimeUtil.betweenDay6Clock(lastLoginDate, nowLoginDate);
                // 只有相差天数等于1 才认为是连续登陆
                if (day == 1) {
                    eventStack.changeProgress(1);
                } else if (day >= 2) {
                    eventStack.setProgress(1);
                }
            }
                break;
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void onEvent(Event event, Player player) {
        GameEventStack eventStack = event.peek();
        switch (eventStack.getConditionType()) {
            case EventConditionType.TOTAL_LOGIN_DAY: {
                boolean bool = true;

                Date lastLoginDate =
                        ToolMap.getDate(EventConditionKey.LAST_LOGIN_DATE, (Map) event.getParams());
                Date nowLoginDate =
                        ToolMap.getDate(EventConditionKey.NOW_LOGIN_DATE, (Map) event.getParams());
                bool &= checkPreEntryFinish(player, eventStack);
                bool &= TimeUtil.betweenDay6Clock(lastLoginDate, nowLoginDate) != 0;
                // 只有相差天数不等于0 才认为是新的一天登陆
                if (bool) {
                    eventStack.changeProgress(1);
                }
            }
                break;
            case EventConditionType.CONT_LOGIN_DAY: {
                Date lastLoginDate =
                        ToolMap.getDate(EventConditionKey.LAST_LOGIN_DATE, (Map) event.getParams());
                Date nowLoginDate =
                        ToolMap.getDate(EventConditionKey.NOW_LOGIN_DATE, (Map) event.getParams());
                long day = TimeUtil.betweenDay6Clock(lastLoginDate, nowLoginDate);
                // 只有相差天数等于1 才认为是连续登陆
                if (day == 1) {
                    eventStack.changeProgress(1);
                } else if (day >= 2) {
                    eventStack.setProgress(1);
                }
            }
                break;
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

    private boolean checkPreEntryFinish(Player player, GameEventStack eventStack) {
        boolean bool = true;
        // 需要活动条目XX完结
        // int preEntryId =
        // MapUtils.getIntValue(eventStack.getParams(), EventConditionKey.PRE_ENTRY_ID);
        // if (preEntryId != 0) {
        // ActivityEntryInfo entry = ActivityInfoCache.getEntry(preEntryId);
        // ActivityInfo activityInfo = ActivityInfoCache.getById(entry.getActivityId());
        // BaseActivity baseActivity = null;
        // if (activityInfo != null) {
        // switch (activityInfo.getDataType()) {
        // case ActivityConstant.ACTIVITY_DATA_TYPE_PLAYER:
        // baseActivity = PlayerActivityCache.me()
        // .getByActivityIdPlayerId(activityInfo.getId(), player.getId());
        // break;
        // case ActivityConstant.ACTIVITY_DATA_TYPE_SERVER:
        // baseActivity = new ServerActivityProxy(activityInfo);
        // break;
        // }
        // }
        // Map<Integer, Integer> entryMap = baseActivity.getEntry(player.getId())
        // .computeIfAbsent(preEntryId, k -> new HashMap<>());
        // int status = MapUtils.getIntValue(entryMap, ActivitySmallKey.STATUS, 0);
        // bool = (status == ActivityConstant.ENTRY_STATUS_RECEIVE);
        // }
        return bool;
    }

    public static void main(String[] args) {
        System.err.println(DateEx.getDayHour(new Date(), -1, 23));
        System.err.println(DateUtil.betweenDay(DateEx.getDayHour(new Date(), -1, 23),
                DateEx.getDayHour(new Date(), 0, 6), true));;
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
        if (eventConditionCfg.getEventId() != EventType.LOGIN)
            return;
        // 当前进度
        // int nowProgress = record.getProgress();
        // if (nowProgress >= taskItem.getProgress()) {
        // return;
        // }
        if (record.getRefreshTime() == null) {
            record.setProgress((record.getProgress() + 1));
            long timeNow = System.currentTimeMillis();
            long timeStart = TimeUtil.getTheZeroClock(new Date());
            if (timeNow < (timeStart + (TimeUtil.ONE_HOUR * TimeUtil.SIX_TIME))) {
                record.setRefreshTime(new Date(timeStart - (23 * TimeUtil.ONE_HOUR)));
            } else
                record.setRefreshTime(new Date());
            return;
        }
        Map<String, Object> in = (Map<String, Object>) event.getParams();
        in.put(EventConditionKey.LAST_LOGIN_DATE, record.getRefreshTime().getTime());
        event.push(new GameEventStack(eventConditionCfg.getEventId(), taskItem.getFinishCondid(),
                null, record.getProgress(), taskItem.getProgress(), false));
        onEvent(event, player);

        GameEventStack eventStack = event.peek();
        if (eventStack.getProgress() != 0) {
            record.setProgress(eventStack.getProgress());
            record.setRefreshTime(new Date());
        }
    }

    @Override
    public void initActivityEventCondition(Player player, GameEventStack eventStack,
            ActivityRecord record) {
        switch (eventStack.getConditionType()) {
            case EventConditionType.TOTAL_LOGIN_DAY: {
                boolean bool = true;
                Date lastLoginDate = record.getRefreshTime();
                if (lastLoginDate == null) {
                    eventStack.changeProgress(1);
                    long timeNow = System.currentTimeMillis();
                    long timeStart = TimeUtil.getTheZeroClock(new Date());
                    if(record.getRefreshTime()==null)
                    {
                        if (timeNow < (timeStart + (TimeUtil.ONE_HOUR * TimeUtil.SIX_TIME))) {
                            record.setRefreshTime(
                                    new Date(timeStart - (23 * TimeUtil.ONE_HOUR)));
                        } else
                            record.setRefreshTime(new Date());
                    }else
                        record.setRefreshTime(new Date());
                } else {
                    Date nowLoginDate = new Date();
                    bool &= checkPreEntryFinish(player, eventStack);
                    bool &= TimeUtil.betweenDay6Clock(lastLoginDate, nowLoginDate) != 0;
                    // 只有相差天数不等于0 才认为是新的一天登陆
                    if (bool) {
                        eventStack.changeProgress(1);
                        long timeNow = System.currentTimeMillis();
                        long timeStart = TimeUtil.getTheZeroClock(new Date());
                        if(record.getRefreshTime()==null)
                        {
                            if (timeNow < (timeStart + (TimeUtil.ONE_HOUR * TimeUtil.SIX_TIME))) {
                                record.setRefreshTime(
                                        new Date(timeStart - (23 * TimeUtil.ONE_HOUR)));
                            } else
                                record.setRefreshTime(new Date());
                        }else
                            record.setRefreshTime(new Date());
                    }
                }
            }
                break;
            case EventConditionType.CONT_LOGIN_DAY: {
                Date lastLoginDate = record.getRefreshTime();
                if (lastLoginDate == null)
                    eventStack.changeProgress(1);
                else {
                    Date nowLoginDate = new Date();
                    long day = TimeUtil.betweenDay6Clock(lastLoginDate, nowLoginDate);
                    // 只有相差天数等于1 才认为是连续登陆
                    if (day == 1) {
                        eventStack.changeProgress(1);
                    } else if (day >= 2) {
                        eventStack.setProgress(1);
                    }
                }
                record.setRefreshTime(new Date());
            }
                break;
        }

    }

}
