package javascript.logic.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityItemMsg;
import org.game.protobuf.s2c.S2CActivityMsg.NewResultSubmitActivity;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import data.GameDataManager;
import data.bean.EventConditionCfgBean;
import event.Event;
import event.IListener;
import gm.db.global.bean.ActivityConfigure;
import gm.db.global.bean.ActivityTaskItem;
import gm.utils.ActivityUtils;
import logic.activity.ActivityCmdUtils;
import logic.activity.ActivityDateProvide;
import logic.activity.LActivityTask;
import logic.activity.bean.ActivityRecord;
import logic.activity.constant.ActivityRecordStatus;
import logic.activity.constant.ActivityStatus;
import logic.activity.helper.DefaultActivityExecutor;
import logic.activity.helper.list.ActivityTaskHelper;
import logic.activity.script.IActivityTaskHelperScript;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.EEventType;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.EventConditionKey;
import logic.functionSwitch.FunctionSwitchService;
import logic.support.MessageUtils;
import logic.task.bean.GameEventStack;
import thread.player.PlayerProcessor;
import thread.player.PlayerProcessorManager;
import utils.ExceptionEx;
import utils.TimeUtil;

public class ActivityTaskHelperScript implements IActivityTaskHelperScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.ACTIVITY_TASK_HELPER_SCRIPT.Value();
    }


    /** 开启活动 **/
    @Override
    public void openActivity(ActivityConfigure config,
            Map<Integer, Map<Integer, ActivityTaskItem>> map, Logger LOGGER) {
        // 初始化任务条目信息
        Map<Integer, ActivityTaskItem> itemMap = map.get(config.getId());
        // if(itemMap){不管当前有没有都强行初始化
        itemMap = new HashMap<Integer, ActivityTaskItem>();
        map.put(config.getId(), itemMap);
        List<ActivityTaskItem> list = queryItemList(config);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (ActivityTaskItem item : list) {
            itemMap.put(item.getId(), item);
        }

        for (Player player : PlayerManager.getAllPlayers()) {
            if (player.isOnline()) {
                PlayerProcessor processor = PlayerProcessorManager.getInstance()
                        .getProcessorByUserName(player.getUserName());
                if (processor != null) {
                    processor.executeInnerHandler(new LActivityTask(player));
                }
            }
        }
    }

    /** 获取条目信息 **/
    @Override
    public List<ActivityTaskItem> queryItemList(ActivityConfigure config) {
        List<Integer> list = config.getItemsList();
        List<ActivityTaskItem> itemList = new ArrayList<>();
        for (Integer itemId : list) {
            ActivityTaskItem item = ActivityUtils.queryTaskItemById(itemId);
            itemList.add(item);
        }
        return itemList;
    }

    /** 移除活动 **/
    @Override
    public void closeActivity(ActivityConfigure config,
            Map<Integer, Map<Integer, ActivityTaskItem>> map) {
        map.remove(config.getId());
        config.setStatus(ActivityStatus.DISABLE);
    }

    /** 修改活动 **/
    @Override
    public void changeActivity(ActivityConfigure config,
            Map<Integer, Map<Integer, ActivityTaskItem>> map, Logger LOGGER) {
        Map<Integer, ActivityTaskItem> itemMap = map.get(config.getId());
        if (itemMap == null) {
            openActivity(config, map, LOGGER);
        } else {
            List<ActivityTaskItem> list = queryItemList(config);
            for (ActivityTaskItem item : list) {
                ActivityTaskItem exist = itemMap.get(item.getId());
                if (exist == null) {
                    itemMap.put(item.getId(), item);
                } else {
                    exist.setRank(item.getRank());
                    exist.setDes(item.getDes());
                    exist.setDes2(item.getDes2());
                    exist.setOpen(item.getOpen());
                    exist.setFinishCondid(item.getFinishCondid());
                    exist.setResetType(item.getResetType());
                    exist.setFinishParams(item.getFinishParams());
                    exist.setIcon(item.getIcon());
                    exist.setReward(item.getReward());
                    exist.setJumpInterface(item.getJumpInterface());
                    exist.setProgress(item.getProgress());
                }
            }
        }
    }

    @Override
    public void activityTaskCheck(Event event, Player player,
            Map<Integer, Map<Integer, ActivityTaskItem>> map, Logger LOGGER) {
        // 活动指定等级才能初始化
        if (player.getLevel() < DefaultActivityExecutor.OPEN_ACTIVITY_LEVEL)
            return;

        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.BACK_ACTIVITY)) {
            return;
        }

        map.forEach((id, items) -> {
            // 验证活动是否处理open 状态
            ActivityConfigure config = ActivityDateProvide.getDefault().getConfigById(id);
            if (config != null && ActivityDateProvide.getDefault().isEnable(config)) {
                items.forEach((itemId, taskItem) -> {
                    // 完成任务：用的是完成任务的条件参数
                    int eventId = getReallyEventId(event.getParams());
                    if (eventId == GameDataManager
                            .getEventConditionCfgBean(taskItem.getFinishCondid()).getEventId()) {
                        EEventType type = EEventType.gerEEventType(eventId);
                        if (type.getListener() != null) {
                            try {
                                IListener listener = type.getListener().newInstance();
                                ActivityRecord record =
                                        player.getActivityManager().getRecord(id, itemId);
                                if (record == null) {
                                    record = new ActivityRecord(id, itemId);
                                    player.getActivityManager().addRcord(record);
                                }
                                if (record.getStatus() == ActivityRecordStatus.unfinished) {
                                    // 验证是的达到领奖条件（达到以后不处理）
                                    if (taskItem.getProgress() > record.getProgress()) {
                                        listener.activityHandler(player, event, taskItem, record);
                                        if (taskItem.getProgress() <= record.getProgress()) {
                                            record.setStatus(
                                                    ActivityRecordStatus.finished_not_getreward);
                                        }
                                        ActivityCmdUtils.getDefault()
                                                .sendSingleActivityRecordToPlayer(record, player);

                                    } else {
                                        record.setStatus(
                                                ActivityRecordStatus.finished_not_getreward);
                                        ActivityCmdUtils.getDefault()
                                                .sendSingleActivityRecordToPlayer(record, player);
                                    }
                                }
                            } catch (Exception e) {
                                LOGGER.error(ExceptionEx.e2s(e));
                            }
                        }
                    }
                });
            }
        });
    }



    @SuppressWarnings("unchecked")
    @Override
    public int getReallyEventId(Object object) {
        if (object != null) {
            Map<String, Object> in = (Map<String, Object>) object;
            return (int) in.get(EventConditionKey.EVENT_ID);
        }
        return 0;
    }

    /** 初始化活动 **/
    @SuppressWarnings("unchecked")
    @Override
    public void init(Player player, Map<Integer, Map<Integer, ActivityTaskItem>> map,
            Logger LOGGER) {
        // 活动指定等级才能初始化
        if (player.getLevel() < DefaultActivityExecutor.OPEN_ACTIVITY_LEVEL)
            return;
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.BACK_ACTIVITY)) {
            return;
        }
        boolean push=player.getActivityManager().isOpen();
        map.forEach((id, taskItems) -> {
            ActivityConfigure config=ActivityDateProvide.getDefault().getConfigById(id);
            if (config != null && ActivityDateProvide.getDefault().isEnable(config)) {
                // 验证活动是否处理open 状态
                taskItems.forEach((itemId, taskItem) -> {
                    // 完成任务：用的是完成任务的条件参数
                    EventConditionCfgBean eventConditionCfg =
                            GameDataManager.getEventConditionCfgBean(taskItem.getFinishCondid());
                    if (eventConditionCfg != null) {
                        int eventId = eventConditionCfg.getEventId();
                        ActivityRecord record = player.getActivityManager().getRecord(id, itemId);
                        if (record == null) {
                            record = new ActivityRecord(id, itemId);
                            player.getActivityManager().addRcord(record);
                        }
                        if (record.getStatus() == ActivityRecordStatus.unfinished) {
                            if (taskItem.getProgress() > record.getProgress()) {
                                Map<String, Integer> params = null;
                                if (taskItem.getFinishParams() != null
                                        && taskItem.getFinishParams().length() > 0) {
                                    params = Maps.newHashMap();
                                    params = new Gson().fromJson(taskItem.getFinishParams(),
                                            params.getClass());
                                }
                                GameEventStack eventStack = new GameEventStack(eventId,
                                        taskItem.getFinishCondid(), params, record.getProgress(),
                                        taskItem.getProgress(), false);
                                EEventType type = EEventType.gerEEventType(eventStack.getEventId());
                                IListener listener = null;
                                if (type != null && type.getListener() != null) {
                                    try {
                                        listener = type.getListener().newInstance();
                                        // 执行初始化
                                        listener.initActivityEventCondition(player, eventStack,
                                                record);
                                        record.setProgress(eventStack.getProgress());
                                        if (taskItem.getProgress() <= record.getProgress())
                                            record.setStatus(
                                                    ActivityRecordStatus.finished_not_getreward);
                                        if(push)
                                        ActivityCmdUtils.getDefault()
                                                .sendSingleActivityRecordToPlayer(record, player);
                                    } catch (InstantiationException e) {
                                        LOGGER.error(ExceptionEx.e2s(e));
                                    } catch (IllegalAccessException e) {
                                        LOGGER.error(ExceptionEx.e2s(e));
                                    }
                                }
                            } else {
                                record.setStatus(ActivityRecordStatus.finished_not_getreward);
                                if(push)
                                ActivityCmdUtils.getDefault()
                                        .sendSingleActivityRecordToPlayer(record, player);
                            }
                        }
                    }

                });
            }
        });
    }


    /** 判断奖励是否领取 **/
    @Override
    public boolean isGotReward(ActivityConfigure config, ActivityRecord record, Integer itemId,
            String extendData, Map<Integer, Map<Integer, ActivityTaskItem>> map, Logger LOGGER) {
        Map<Integer, ActivityTaskItem> itemMap = map.get(config.getId());
        if (itemMap == null || itemMap.isEmpty()) {
            LOGGER.error("activity item map is empty , actId = " + config.getId());
            return true;
        }
        ActivityTaskItem taskItem = itemMap.get(itemId);
        if (taskItem == null) {
            LOGGER.error("activity item is empty , itemId = " + itemId);
            return true;
        }
        if (record == null) {
            LOGGER.error("activity record is null , itemId = " + itemId);
            return true;
        }
        if (record.getStatus() == ActivityRecordStatus.unfinished) {
            LOGGER.error("activity item is unfinished , itemId = " + itemId);
            return true;
        }
        return false;
    }

    // 判断玩家是否能够领取奖励
    @Override
    public boolean isCanReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData, Map<Integer, Map<Integer, ActivityTaskItem>> map, Logger LOGGER) {
        if (config == null) {
            LOGGER.error("activity item is not record , itemId = " + itemId);
            return false;
        }
        Map<Integer, ActivityTaskItem> itemMap = map.get(config.getId());
        ActivityRecord record = player.getActivityManager().getRecord(config.getId(), itemId);
        if (record.getGotCount() > 0) {
            LOGGER.error("activity item is get , itemId = " + itemId);
            return false;
        }
        if (record.getStatus() == ActivityRecordStatus.finished_gotreward)
            return false;
        ActivityTaskItem taskItem = itemMap.get(itemId);
        return record.getProgress() >= taskItem.getProgress();
    }


    /** 领取活动奖励 成功领取ture **/
    @Override
    public boolean getReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData, ActivityRecord record,
            Map<Integer, Map<Integer, ActivityTaskItem>> mapHelper, Logger LOGGER) {
        Map<Integer, ActivityTaskItem> itemMap = mapHelper.get(config.getId());
        if (itemMap == null || itemMap.isEmpty()) {
            LOGGER.error("activity item map is empty , actId = " + config.getId());
            return false;
        }
        ActivityTaskItem item = itemMap.get(itemId);
        if (item == null) {
            LOGGER.error("activity item is empty , itemId = " + itemId);
            return false;
        }
        
        /**先改变状态再发送奖励**/
        updateReward(config, record, itemId, extendData);
        
        Map<Integer, Integer> map = getAwardMap(item.getReward());
        player.getBagManager().addItems(map, true, EReason.ACTIVITY_TASK);
        NewResultSubmitActivity.Builder build = NewResultSubmitActivity.newBuilder();
        build.setActivityid(config.getId());
        build.setActivitEntryId(itemId);
        RewardsMsg.Builder rewardBuild = RewardsMsg.newBuilder();
        for (Integer key : map.keySet()) {
            rewardBuild.setId(key);
            rewardBuild.setNum(map.get(key));
            build.addRewards(rewardBuild);
        }
        MessageUtils.send(player, build);
        return true;
    }

    /** 组装数据 **/
    @Override
    public ActivityItemMsg.Builder getActivityEntryToBuild(ActivityItemMsg.Builder taskBuild,
            Integer itemId, ActivityConfigure config, Player player,
            Map<Integer, Map<Integer, ActivityTaskItem>> map) {
        Map<Integer, ActivityTaskItem> itemMap = map.get(config.getId());
        if (itemMap == null || itemMap.isEmpty()) {
            return null;
        }
        ActivityTaskItem item = itemMap.get(itemId);
        if (item == null) {
            return null;
        }
        // int playerProgress = 0;
        // ActivityManager manager = player.getActivityManager();
        // ActivityRecord record = manager.getRecord(config.getId(), itemId);
        // if (record != null) {
        // playerProgress = record.getProgress();
        // }

        // 条目id
        taskBuild.setId(item.getId());
        // 类型
        taskBuild.setType(config.getType());
        // 任务描述
        taskBuild.setDetails(item.getDes());
        // 需要进度
        taskBuild.setTarget(item.getProgress() + "");

        JSONObject obj = new JSONObject();
        // icon
        obj.put("icon", item.getIcon());
        // 任务描述2
        obj.put("des2", item.getDes2());
        // 跳转界面
        if (item.getJumpInterface() != 0)
            obj.put("jumpInterface", item.getJumpInterface());
        // 重置类型
        obj.put("resetType", item.getResetType());

        String extenDate = JSON.toJSONString(obj, true);

        // 额外信息
        taskBuild.setExtendData(extenDate);
        // 奖励信息
        taskBuild.setReward(item.getReward());
        // 顺序
        taskBuild.setRank(item.getRank());

        return taskBuild;
    }

    /** 重置条目 **/
    @Override
    public void resetInItem(Player player, ActivityConfigure config, Integer itemId,
            Map<Integer, Map<Integer, ActivityTaskItem>> map) {
        ActivityRecord record = player.getActivityManager().getRecord(config.getId(), itemId);
        if (record == null) {
            return;
        }
        Map<Integer, ActivityTaskItem> itemMap = map.get(config.getId());
        if (itemMap == null || itemMap.isEmpty()) {
            return;
        }
        ActivityTaskItem item = itemMap.get(itemId);
        if (item == null) {
            return;
        }
        record.setProgress(0);
        record.setStatus(ActivityRecordStatus.unfinished);
        record.setRefreshTime(new Date());

    }

    /** 领奖 **/
    @Override
    public ActivityRecord updateReward(ActivityConfigure config, ActivityRecord record,
            int activitEntryId, String extendData) {
        if (record == null) {
            record = new ActivityRecord(config.getId(), activitEntryId);
        }
        record.addGotCount();
        record.setStatus(ActivityRecordStatus.finished_gotreward);
        return record;
    }


    /** 获取当前Map奖励 **/
    @SuppressWarnings("unchecked")
    @Override
    public Map<Integer, Integer> getAwardMap(String awardInfo) {
        return (Map<Integer, Integer>) JSON.parseObject(awardInfo, Map.class);
    }


    @Override
    public boolean isNeedResetInItem(Player player, ActivityConfigure config, Integer itemId,
            Map<Integer, Map<Integer, ActivityTaskItem>> map) {
        Map<Integer, ActivityTaskItem> itemMap = map.get(config.getId());
        if (itemMap == null || itemMap.isEmpty()) {
            return false;
        }
        ActivityTaskItem item = itemMap.get(itemId);
        if (item == null || item.getResetType() != ActivityTaskHelper.RESET) {
            return false;
        }
        ActivityRecord record = player.getActivityManager().getRecord(config.getId(), itemId);
        if (record == null) {
            return false;
        }
        if (TimeUtil.isSameDay(record.getRefreshTime(), new Date())) {
            return false;
        }
        return false;
    }


}
