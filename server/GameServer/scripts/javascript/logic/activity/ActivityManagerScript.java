package javascript.logic.activity;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;
import com.google.common.collect.Maps;
import event.Event;
import gm.db.global.bean.ActivityConfigure;
import gm.db.global.dao.ActivityConfigureDao;
import logic.activity.ActivityCmdUtils;
import logic.activity.ActivityDateProvide;
import logic.activity.ActivityManager;
import logic.activity.bean.ActivityRecord;
import logic.activity.constant.ActivityStatus;
import logic.activity.constant.ActivityType;
import logic.activity.helper.ActivityExecProvider;
import logic.activity.helper.DefaultActivityExecutor;
import logic.activity.helper.IActivityExecutor;
import logic.activity.helper.list.ActivityTaskHelper;
import logic.activity.script.IActivityManagerScript;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import logic.constant.EFunctionType;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.functionSwitch.FunctionSwitchService;
import logic.support.MessageUtils;
import utils.DateEx;

/***
 * 
 * 玩家活动记录
 * 
 * @author lihongji
 *
 */
public class ActivityManagerScript implements IActivityManagerScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.ACTIVITYMANAGER_SCRIPT.Value();
    }

    /** 通过活动id 条目id 获取记录 **/
    @Override
    public ActivityRecord getRecord(Integer activitId, Integer itemId,
            Map<Integer, Map<Integer, ActivityRecord>> activityRecord) {
        //特殊处理  活动类型为商店的全服限购活动的剩余商品数量
        ActivityConfigure config = ActivityDateProvide.getDefault().getConfigById(activitId);
        Map<Integer, ActivityRecord> recordMap = activityRecord.get(activitId);
        ActivityRecord record = null;
        if (recordMap == null || recordMap.isEmpty()) {
            if(config!=null&&config.getType()==ActivityType.activity_store){
                IActivityExecutor exec = ActivityExecProvider.getDefault().get(config.getType());
                record = exec.checkRecord(record,config,itemId);
            }
            return record;
        }
        record = recordMap.get(itemId);
        if(config!=null&&config.getType()==ActivityType.activity_store){
        	IActivityExecutor exec = ActivityExecProvider.getDefault().get(config.getType());
        	record = exec.checkRecord(record,config,itemId);
        }
        return record;
    }

    /** 重新添加记录 **/
    @Override
    public void addRcord(ActivityRecord record,
            Map<Integer, Map<Integer, ActivityRecord>> activityRecord) {
        Map<Integer, ActivityRecord> itemRecord = activityRecord.get(record.getActivityId());
        if (itemRecord == null) {
            itemRecord = Maps.newHashMap();
        }
        itemRecord.put(record.getItermId(), record);
        activityRecord.put(record.getActivityId(), itemRecord);

    }

    // 判断条目是否完成领取过
    // 判断条目使能能够完成和领取
    // 领取奖励
    // 修改记录
    // 返回客户端新的数据
    @Override
    public void getReward(Player player, int activityId, int activitEntryId, String extendData,
            Map<Integer, Map<Integer, ActivityRecord>> activityRecord, Logger LOGGER) {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.BACK_ACTIVITY)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:back_activity");
        }
        // 判断活动是否过时
        LOGGER.info("玩家领取奖励,玩家id=" + player.getPlayerId() + ",活动id=" + activityId + ",条目id="
                + activitEntryId + ",附加信息=" + extendData);
        ActivityConfigure config = ActivityDateProvide.getDefault().getConfigById(activityId);
        if (!ActivityDateProvide.getDefault().isShowTimeEnable(config)) {
            LOGGER.error("客户端发送了过时的活动领奖信息,活动id=" + activityId + "玩家id=" + player.getPlayerId());
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_ACTIVITY, activityId + "活动不存在");
            return;
        }
        // 判断该条目是否有效
        if (!config.getItemsList().contains(activitEntryId)) {
            LOGGER.error("错误的领奖条目信息,活动id=" + activityId + ",条目id=" + activitEntryId + "玩家id="
                    + player.getPlayerId());
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_ACTIVITY_ENTRY,
                    activitEntryId + "条目不存在");
            return;
        }
        IActivityExecutor exec = ActivityExecProvider.getDefault().get(config.getType());
        ActivityRecord record = getRecord(activityId, activitEntryId, activityRecord);
        // 判断该活动条目是否领取过奖励
        boolean gotReward = false;
        try {
            gotReward = exec.isGotReward(config, record, activitEntryId, extendData);
        } catch (Exception e) {
          MessageUtils.throwLogicError(GameErrorCode.PLAYER_DATA_ERROR, e, "player [",
          String.valueOf(player.getPlayerId()), "], ", String.valueOf(config.getId()), ", ",
          String.valueOf(activitEntryId), ", ");
        }
        if (gotReward) {
            if (config.getType() == ActivityType.activity_store) {
                ActivityCmdUtils.getDefault().sendSingleActivityRecordToPlayer(record, player);
                MessageUtils.throwCondtionError(GameErrorCode.ACTIVITY_ITEM_SOLD_OUT,
                        activitEntryId + "商品已售空");
            }
            LOGGER.error("领取过的条目信息,活动id=" + activityId + ",条目id=" + activitEntryId + "玩家id="
                    + player.getPlayerId());
            MessageUtils.throwCondtionError(GameErrorCode.ACTIVITY_ITEM_GOT_REWARD,
                    activitEntryId + "条目已经领取过了");
            return;
        }
        boolean isCan = exec.isCanReward(player, config, activitEntryId, extendData);
        if (!isCan) {
            LOGGER.error("玩家无法领取奖励,活动id=" + activityId + ",条目id=" + activitEntryId + "玩家id="
                    + player.getPlayerId());
            MessageUtils.throwCondtionError(GameErrorCode.ACTIVITY_ITEM_GOT_REWARD,
                    activitEntryId + "条目未完成");
            return;
        }
        try {
            gotReward = exec.getReward(player, config, activitEntryId, extendData, record);
        } catch (Exception e) {
            MessageUtils.throwLogicError(GameErrorCode.PLAYER_DATA_ERROR, e, "player [",
                    String.valueOf(player.getPlayerId()), "], ", String.valueOf(config.getId()), ", ",
                    String.valueOf(activitEntryId), ", ");
        }
        if (!gotReward) {
            MessageUtils.throwCondtionError(GameErrorCode.ACTIVITY_ITEM_GET_REWARD_FAIL,
                    activitEntryId + "条目领取失败");
            return;
        }
        // 策划说活动进度变更的话 进度信息要在领奖协议之前就返回
//        record = exec.updateReward(config, record, activitEntryId, extendData);
        addRcord(record, activityRecord);
        ActivityCmdUtils.getDefault().sendSingleActivityRecordToPlayer(record, player);

    }

    @Override
    public void execute(Event event, Player player) {
        Collection<ActivityConfigure> allActivity = ActivityDateProvide.getDefault().value();
        for (ActivityConfigure config : allActivity) {
            if (!ActivityDateProvide.getDefault().isEnable(config)) {
                continue;
            }
            IActivityExecutor exec = ActivityExecProvider.getDefault().get(config.getType());
            if (exec instanceof ActivityTaskHelper) {
                ActivityTaskHelper helper = (ActivityTaskHelper) exec;
                helper.activityTaskCheck(event, player);
            }
        }
        IActivityExecutor exec = ActivityExecProvider.getDefault().get(2);
        if (exec instanceof ActivityTaskHelper) {
            ActivityTaskHelper helper = (ActivityTaskHelper) exec;
            helper.activityTaskCheck(event, player);
        }
    }

    @Override
    public void createPlayerInitialize(Player player) {
//        activityInit(player);
    }

    /** 初始化 **/
    @Override
    public void activityInit(Player player) {
        ActivityTaskHelper exec = (ActivityTaskHelper) ActivityExecProvider.getDefault()
                .get(ActivityType.activity_task);
        exec.init(player);
    }

    /** 跨天 
     * @throws Exception **/
    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify,
            Map<Integer, Map<Integer, ActivityRecord>> activityRecord, Player player, Logger LOGGER)
            throws Exception {
        if (type == EAcrossDayType.SYS_ACROSS_DAY) {
//            activityInit(player);
            for (Integer activityId : activityRecord.keySet()) {
                ActivityConfigure config =
                        ActivityDateProvide.getDefault().getConfigById(activityId);
                if (config == null) {
                    LOGGER.error("_Activity is error id:" + activityId + "_playerId:"
                            + player.getPlayerId());
                    continue;
                }
                if (ActivityDateProvide.getDefault().isEnable(config)) {// 只有处于正在进行的活动才需要检测
                                                                        // 预览和展示阶段不需要再重置了
                    IActivityExecutor exec =
                            ActivityExecProvider.getDefault().get(config.getType());
                    if (exec == null)
                        LOGGER.error("_Activity is error id:" + activityId + "_playerId:"
                                + player.getPlayerId() + "_activityType:" + config.getType());
                    else
                        exec.reset(player, config);
                }
            }
        } else if (type == EAcrossDayType.GAME_ACROSS_DAY) {
            activityInit(player);
        }
    }

    /** 验证是否开放活动 **/
    @Override
    public void open(Player player) {
        if (!player.getActivityManager().isOpen()
                && player.getLevel() >= DefaultActivityExecutor.OPEN_ACTIVITY_LEVEL) {
            activityInit(player);
            player.getActivityManager().setOpen(true);
            ActivityCmdUtils.getDefault().sendAllActivityInfo(player);
        }
    }

    /**移除玩家无效的记录**/
    @Override
    public void removeRecord(Map<Integer, Map<Integer, ActivityRecord>> activityRecord,
            Player player, Logger LOGGER) {
        if (activityRecord == null)
            return;
        long timeNow = System.currentTimeMillis();
        Iterator<Entry<Integer, Map<Integer, ActivityRecord>>> iterator =
                activityRecord.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer, Map<Integer, ActivityRecord>> entry = iterator.next();
            ActivityConfigure config =
                    ActivityDateProvide.getDefault().getConfigById(entry.getKey());
            if (config == null) {
                config = ActivityConfigureDao.selectActivityConfigureById(entry.getKey());
            }
            if (config != null && (config.getShowEndTime()
                    + (ActivityManager.REMOVE_DAY * DateEx.TIME_DAY)) > timeNow) {
                continue;
            }
            if (config != null && config.getStatus() == ActivityStatus.ENABLE)
                return;
            // 日志记录
            logRecord();
            iterator.remove();
        }
    }

    @Override
    public void logRecord() {
        
    }

}
