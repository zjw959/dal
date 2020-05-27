package logic.activity.helper;

import gm.db.global.bean.ActivityConfigure;

import java.util.Date;
import java.util.List;

import logic.activity.ActivityCmdUtils;
import logic.activity.ActivityDateProvide;
import logic.activity.bean.ActivityRecord;
import logic.activity.constant.ActivityStatus;
import logic.activity.quartz.QuartzManager;
import logic.character.bean.Player;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityItemMsg;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DefaultActivityExecutor implements IActivityExecutor, Job {

	private static final Logger LOGGER = Logger.getLogger(DefaultActivityExecutor.class);
	
    private static final String activityId = "activityId";
    private static final String functionName = "functionName";
    private static final String openTigger = "openTrigger";
    private static final String closeTigger = "closeTrigger";

    /** 开启活动的等级 **/
    public static final int OPEN_ACTIVITY_LEVEL = 5;

    @Override
    public void notifyActivityOpen(ActivityConfigure config) {
        ActivityCmdUtils.getDefault().sendSingleActivityConfigToAll(config, ChangeType.ADD);
    }

    @Override
    public void notifyActivityClose(ActivityConfigure config) {
        ActivityCmdUtils.getDefault().sendSingleActivityConfigToAll(config, ChangeType.DELETE);
    }

    @Override
    public void notifyActivityChange(ActivityConfigure config) {
    	ActivityCmdUtils.getDefault().sendSingleActivityConfigToAll(config, ChangeType.UPDATE);
    }

    @Override
    public void reset(Player player,ActivityConfigure config) throws Exception {
        List<Integer> items = config.getItemsList();
        if(items==null||items.isEmpty()){
            return;
        }
        IActivityExecutor exec = ActivityExecProvider.getDefault().get(config.getType());
        for(Integer itemId:items){
            if(exec.isNeedResetInItem(player,config,itemId)){
                exec.resetInItem(player,config,itemId);
            }
        }
    }

    @Override
    public void loadOpenTrigger(ActivityConfigure config) {
        if (config.getStatus() == ActivityStatus.DISABLE) {
            return;
        }
        if (config.getStatus() == ActivityStatus.ENABLE) {
            return;
        }
        Long now = System.currentTimeMillis();
        if (config.getShowBeginTime() < now) {
            return;
        }
        String startCron = QuartzManager.getCron(new Date(config.getShowBeginTime()));
        QuartzManager.addActivityJob(activityId + "-" + config.getId()+"-"+openTigger,
                DefaultActivityExecutor.class  , startCron, activityId, config.getId(), functionName,
                openTigger);
    }
  
    @Override
    public void loadCloseTrigger(ActivityConfigure config) {
        if (config.getStatus() == ActivityStatus.DISABLE) {
            return;
        }
        if (config.getStatus() == ActivityStatus.ENABLE) {
            return;
        }
        if (ActivityDateProvide.getDefault().isOutTime(config)) {
            return;
        }
        String endCron = QuartzManager.getCron(new Date(config.getShowEndTime()));
        QuartzManager.addActivityJob(activityId + "-" + config.getId()+"-"+closeTigger,
                DefaultActivityExecutor.class, endCron, activityId, config.getId(), functionName,
                closeTigger);
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jdMap = context.getJobDetail().getJobDataMap();
        Integer actId = (Integer) jdMap.get(activityId);
        String funName = (String) jdMap.get(functionName);
        if (actId == null || funName == null || funName.isEmpty()) {
            return;
        }
        ActivityConfigure config = ActivityDateProvide.getDefault().getConfigById(actId);
        if (config == null) {
            return;
        }
        LOGGER.info("执行定时任务,活动id="+actId+"函数名："+funName);
        IActivityExecutor exec = ActivityExecProvider.getDefault().get(config.getType());
        if (funName.equals(openTigger)) {
            exec.openActivity(config);
            exec.notifyActivityOpen(config);
        } else if (funName.equals(closeTigger)) {
             exec.notifyActivityClose(config);
             exec.closeActivity(config);
        }
    }

    @Override
    public void removeAllTrigger(ActivityConfigure config) {
        QuartzManager.removeJob(activityId + "-" + config.getId()+"-"+openTigger);
        QuartzManager.removeJob(activityId + "-" + config.getId()+"-"+closeTigger);
        LOGGER.info("清除定时任务："+activityId + "-" + config.getId());
    }

    @Override
    public ActivityItemMsg.Builder getActivityEntryToBuild(ActivityItemMsg.Builder entryBuild, Integer itemId,
            ActivityConfigure config, Player player) {
        // 这个方法没有普遍性 各自实现活动的数据发送
        return null;
    }

    @Override
    public void openActivity(ActivityConfigure config) {
        // 这个方法没有普遍性 各自实现活动自身的初始化
        // PS：一般初始化条目信息
    }

    @Override
    public void closeActivity(ActivityConfigure config) {
        // 这个方法没有普遍性 各自实现活动自身的结束
        // PS：一般清空条目信息+发奖之类的结束操作
    }

    @Override
    public void changeActivity(ActivityConfigure config) {
        // 这个方法没有普遍性 各自实现活动自身的修改
        // PS：一般重载条目信息
    }

    @Override
    public boolean isGotReward(ActivityConfigure config, ActivityRecord record, Integer itemId,
            String extendData) throws Exception{
        return true;
    }

    @Override
    public boolean isCanReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData) {
        return false;
    }

    @Override
    public boolean getReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData, ActivityRecord record) throws Exception {
        return true;
    }

    @Override
    public String getActivityRemark(ActivityConfigure config) {
        return null;
    }

    @Override
    public String getActivityExtendData(ActivityConfigure config) {
        return null;
    }

    @Override
    public boolean isNeedResetInItem(Player player, ActivityConfigure config, Integer itemId) {
        return false;
    }

    @Override
    public void resetInItem(Player player, ActivityConfigure config, Integer itemId) throws Exception  {
    }


    @Override
    public ActivityRecord updateReward(ActivityConfigure config, ActivityRecord record, int activitEntryId,
            String extendData) {
        if(record==null){
            record = new ActivityRecord(config.getId(),activitEntryId);
        }
        record.addGotCount();
        return record;
    }

	@Override
	public ActivityRecord checkRecord(ActivityRecord record, ActivityConfigure config, Integer itemId) {
		return null;
	}
}
