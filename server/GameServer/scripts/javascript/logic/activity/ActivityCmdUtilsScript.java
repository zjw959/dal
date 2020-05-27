package javascript.logic.activity;

import java.util.Collection;
import java.util.List;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityConfigMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityItemMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityProgressMsg;
import org.game.protobuf.s2c.S2CActivityMsg.NewPushActivitys;
import org.game.protobuf.s2c.S2CActivityMsg.NewRespActivityItems;
import org.game.protobuf.s2c.S2CActivityMsg.NewRespActivityProgress;
import org.game.protobuf.s2c.S2CActivityMsg.NewRespActivitys;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import gm.db.global.bean.ActivityConfigure;
import logic.activity.ActivityDateProvide;
import logic.activity.ActivityManager;
import logic.activity.LActivityPush;
import logic.activity.bean.ActivityRecord;
import logic.activity.constant.ActivityType;
import logic.activity.helper.ActivityExecProvider;
import logic.activity.helper.DefaultActivityExecutor;
import logic.activity.helper.IActivityExecutor;
import logic.activity.script.IActivityCmdUtilsScript;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.constant.EScriptIdDefine;
import logic.support.MessageUtils;
import thread.player.PlayerProcessor;
import thread.player.PlayerProcessorManager;
import utils.TimeUtil;

/***
 * 
 * 活动协议
 * 
 * @author lihongji
 *
 */

public class ActivityCmdUtilsScript implements IActivityCmdUtilsScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.ACTIVITYCMDUTILS_SCRIPT.Value();
    }


    /**
     * 发送所有活动信息（活动配置，条目配置，条目进度）给玩家 该方法用于玩家每次登陆时候调用
     * 
     * @param player
     */
    @Override
    public void sendAllActivityInfo(Player player) {
        Collection<ActivityConfigure> allActivity = ActivityDateProvide.getDefault().value();
        if (allActivity == null || allActivity.isEmpty()) {
            return;
        }
        // 活动指定等级才能初始化
        if (player.getLevel() < DefaultActivityExecutor.OPEN_ACTIVITY_LEVEL) {
            return;
        }
        NewRespActivitys.Builder allConfigBuilder = NewRespActivitys.newBuilder();
        NewRespActivityItems.Builder allItemBuilder = NewRespActivityItems.newBuilder();
        NewRespActivityProgress.Builder allRecordBuilder = NewRespActivityProgress.newBuilder();
        ActivityManager manager = player.getActivityManager();
        for (ActivityConfigure config : allActivity) {
            if (!ActivityDateProvide.getDefault().isShowTimeEnable(config)) {
                continue;
            }
            ActivityConfigMsg.Builder configBuild = ActivityConfigMsg.newBuilder();
            configBuild = activityConfigureToBuild(configBuild, config, player, ChangeType.DEFAULT);
            allConfigBuilder.addActivitys(configBuild);
            List<Integer> itemList = config.getItemsList();
            if (itemList == null || itemList.isEmpty()) {
                continue;
            }
            IActivityExecutor exec = ActivityExecProvider.getDefault().get(config.getType());
            for (Integer itemId : itemList) {
                ActivityItemMsg.Builder itemBuild = ActivityItemMsg.newBuilder();
                itemBuild = exec.getActivityEntryToBuild(itemBuild, itemId, config, player);
                if (itemBuild == null) {
                    continue;
                }
                allItemBuilder.addItems(itemBuild);
                ActivityRecord record = manager.getRecord(config.getId(), itemId);
                if (record == null) {
                    continue;
                }
                ActivityProgressMsg.Builder recordBuild = ActivityProgressMsg.newBuilder();
                recordBuild = activityRecordToBuild(recordBuild, record);
                allRecordBuilder.addItems(recordBuild);
            }
        }
        // 这里顺序按照 条目配置-条目进度-活动配置发送给客户端
        MessageUtils.send(player, allConfigBuilder);
        MessageUtils.send(player, allItemBuilder);
        MessageUtils.send(player, allRecordBuilder);
    }



    /** 玩家进度组装 **/
    @Override
    public ActivityProgressMsg.Builder activityRecordToBuild(
            ActivityProgressMsg.Builder recordBuild, ActivityRecord record) {
        recordBuild.setId(record.getActivityId());
        recordBuild.setItemId(record.getItermId());
        recordBuild.setProgress(record.getProgress());
        recordBuild.setExtend(record.getExtra() == null ? "" : record.getExtra());
        recordBuild.setStatus(record.getStatus());
        return recordBuild;
    }

    /** 活动数据组装 **/
    @Override
    public ActivityConfigMsg.Builder activityConfigureToBuild(ActivityConfigMsg.Builder build,
            ActivityConfigure config, Player player, ChangeType type) {
        build.setCt(type);
        build.setId(config.getId());
        build.setActivityType(config.getType());
        build.setActivityTitle(config.getTitle());
        IActivityExecutor exec = ActivityExecProvider.getDefault().get(config.getType());
        if (config.getBeginTime() > 0) {
            build.setStartTime((int) (config.getBeginTime() / TimeUtil.SECOND));
        }
        if (config.getEndTime() > 0) {
            build.setEndTime((int) (config.getEndTime() / TimeUtil.SECOND));
        }
        if (config.getShowBeginTime() > 0) {
            build.setShowStartTime((int) (config.getShowBeginTime() / TimeUtil.SECOND));
        }
        if (config.getShowEndTime() > 0) {
            build.setShowEndTime((int) (config.getShowEndTime() / TimeUtil.SECOND));
        }
        build.setRank(config.getWeight());
        String remark = exec.getActivityRemark(config);
        if (remark != null) {
            build.setRemark(remark);
        }
        String extendData = exec.getActivityExtendData(config);
        if (extendData != null) {
            build.setExtendData(extendData);
        }
        List<Integer> itemsList = config.getItemsList();
        for (Integer itemId : itemsList) {
            build.addItems(itemId);
        }
        if (config.getShowIcon() != null && !config.getShowIcon().isEmpty()) {
            build.setShowIcon(config.getShowIcon().trim());
        }
        return build;
    }

    /**
     * 通知单个活动信息(配置和条目新增修改删除)给-----某个玩家
     * 
     * @param player
     * @param config
     */
    @Override
    public void sendSingleActivityConfigToPlayer(ActivityConfigure config, Player player,
            ChangeType type) {
        NewPushActivitys.Builder builder = NewPushActivitys.newBuilder();
        NewRespActivityItems.Builder itemBuilder = NewRespActivityItems.newBuilder();
        NewRespActivityProgress.Builder allRecordBuilder = NewRespActivityProgress.newBuilder();
        ActivityConfigMsg.Builder build = ActivityConfigMsg.newBuilder();

        build = activityConfigureToBuild(build, config, player, type);
        builder.addActivitys(build);

        List<Integer> itemList = config.getItemsList();
        if (itemList != null && !itemList.isEmpty()) {
            IActivityExecutor exec = ActivityExecProvider.getDefault().get(config.getType());
            if (config.getType() == ActivityType.activity_task)
                player.getActivityManager().activityInit();
            for (Integer itemId : itemList) {
                ActivityItemMsg.Builder itemBuild = ActivityItemMsg.newBuilder();
                itemBuild = exec.getActivityEntryToBuild(itemBuild, itemId, config, player);
                if (itemBuild == null) {
                    continue;
                }
                itemBuilder.addItems(itemBuild);
            }
                MessageUtils.send(player, itemBuilder);
            for (Integer itemId : itemList) {
                ActivityRecord record =
                        player.getActivityManager().getRecord(config.getId(), itemId);
                if (record == null) {
                    continue;
                }
                ActivityProgressMsg.Builder recordBuild = ActivityProgressMsg.newBuilder();
                recordBuild = activityRecordToBuild(recordBuild, record);
                allRecordBuilder.addItems(recordBuild);
            }

            MessageUtils.send(player, allRecordBuilder);

        }
        MessageUtils.send(player, builder);
    }

    /**
     * 通知单个活动信息(配置和条目新增修改删除)给-----所有在线玩家
     * 
     * @param player
     * @param config
     */
    @Override
    public void sendSingleActivityConfigToAll(ActivityConfigure config, ChangeType type) {
        for (Player player : PlayerManager.getAllPlayers()) {
            if (player.isOnline()) {
                // sendSingleActivityConfigToPlayer(config, player, type);
                PlayerProcessor processor = PlayerProcessorManager.getInstance()
                        .getProcessorByUserName(player.getUserName());
                if (processor != null) {
                    processor.executeInnerHandler(new LActivityPush(player, config, type));
                }

            }
        }
    }

    /**
     * 通知单个活动进度给单个玩家
     * 
     * @param player
     * @param config
     */
    @Override
    public void sendSingleActivityRecordToPlayer(ActivityRecord record, Player player) {
        NewRespActivityProgress.Builder allRecordBuilder = NewRespActivityProgress.newBuilder();
        ActivityProgressMsg.Builder recordBuild = ActivityProgressMsg.newBuilder();
        recordBuild = activityRecordToBuild(recordBuild, record);
        allRecordBuilder.addItems(recordBuild);
        MessageUtils.send(player, allRecordBuilder);
    }

    /**
     * 发送所有活动配置信息给客户端
     * 
     * @param player
     */
    @Override
    public void sendAllActivityConfigToPlayer(Player player) {
        Collection<ActivityConfigure> allActivity = ActivityDateProvide.getDefault().value();
        NewRespActivitys.Builder allConfigBuilder = NewRespActivitys.newBuilder();
        if (allActivity == null || allActivity.isEmpty()) {
            MessageUtils.send(player, allConfigBuilder);
            return;
        }
        // 活动指定等级才能初始化
        if (player.getLevel() < DefaultActivityExecutor.OPEN_ACTIVITY_LEVEL) {
            MessageUtils.send(player, allConfigBuilder);
            return;
        }
        for (ActivityConfigure config : allActivity) {
            if (!ActivityDateProvide.getDefault().isShowTimeEnable(config)) {
                continue;
            }
            ActivityConfigMsg.Builder configBuild = ActivityConfigMsg.newBuilder();
            configBuild = activityConfigureToBuild(configBuild, config, player, ChangeType.DEFAULT);
            allConfigBuilder.addActivitys(configBuild);
        }
        MessageUtils.send(player, allConfigBuilder);
    }

    /**
     * 发送所有活动条目给客户端
     * 
     * @param player
     */
    @Override
    public void sendAllActivityItemToPlayer(Player player) {
        Collection<ActivityConfigure> allActivity = ActivityDateProvide.getDefault().value();
        NewRespActivityItems.Builder allItemBuilder = NewRespActivityItems.newBuilder();
        if (allActivity == null || allActivity.isEmpty()) {
            MessageUtils.send(player, allItemBuilder);
            return;
        }
        // 活动指定等级才能初始化
        if (player.getLevel() < DefaultActivityExecutor.OPEN_ACTIVITY_LEVEL) {
            MessageUtils.send(player, allItemBuilder);
            return;
        }
        for (ActivityConfigure config : allActivity) {
            if (!ActivityDateProvide.getDefault().isShowTimeEnable(config)) {
                continue;
            }
            List<Integer> itemList = config.getItemsList();
            if (itemList == null || itemList.isEmpty()) {
                continue;
            }
            IActivityExecutor exec = ActivityExecProvider.getDefault().get(config.getType());
            for (Integer itemId : itemList) {
                ActivityItemMsg.Builder itemBuild = ActivityItemMsg.newBuilder();
                itemBuild = exec.getActivityEntryToBuild(itemBuild, itemId, config, player);
                if (itemBuild == null) {
                    continue;
                }
                allItemBuilder.addItems(itemBuild);
            }
        }
        MessageUtils.send(player, allItemBuilder);
    }

    /**
     * 发送所有活动记录给玩家
     * 
     * @param player
     */
    @Override
    public void sendAllActivityRecordToPlayer(Player player) {
        Collection<ActivityConfigure> allActivity = ActivityDateProvide.getDefault().value();
        NewRespActivityProgress.Builder allRecordBuilder = NewRespActivityProgress.newBuilder();
        if (allActivity == null || allActivity.isEmpty()) {
            MessageUtils.send(player, allRecordBuilder);
            return;
        }
        // 活动指定等级才能初始化
        if (player.getLevel() < DefaultActivityExecutor.OPEN_ACTIVITY_LEVEL) {
            MessageUtils.send(player, allRecordBuilder);
            return;
        }

        ActivityManager manager = player.getActivityManager();
        for (ActivityConfigure config : allActivity) {
            if (!ActivityDateProvide.getDefault().isShowTimeEnable(config)) {
                continue;
            }
            List<Integer> itemList = config.getItemsList();
            if (itemList == null || itemList.isEmpty()) {
                continue;
            }
            for (Integer itemId : itemList) {
                ActivityRecord record = manager.getRecord(config.getId(), itemId);
                if (record == null) {
                    continue;
                }
                ActivityProgressMsg.Builder recordBuild = ActivityProgressMsg.newBuilder();
                recordBuild = activityRecordToBuild(recordBuild, record);
                allRecordBuilder.addItems(recordBuild);
            }
        }
        MessageUtils.send(player, allRecordBuilder);
    }

}

