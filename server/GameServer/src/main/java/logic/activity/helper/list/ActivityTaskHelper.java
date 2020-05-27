package logic.activity.helper.list;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityItemMsg;
import event.Event;
import gm.db.global.bean.ActivityConfigure;
import gm.db.global.bean.ActivityTaskItem;
import logic.activity.bean.ActivityRecord;
import logic.activity.helper.DefaultActivityExecutor;
import logic.activity.script.IActivityTaskHelperScript;
import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;

/***
 * 
 * @author 任务活动helper
 *
 */
public class ActivityTaskHelper extends DefaultActivityExecutor {

    private static final Logger LOGGER = Logger.getLogger(ActivityStoreHelper.class);

    /** 重置为2 不重置为1 **/
    public static int RESET = 2, NO_RESET = 1;

    /**
     * <活动id，<任务条目id，任务信息>>
     */
    private Map<Integer, Map<Integer, ActivityTaskItem>> map =
            new HashMap<Integer, Map<Integer, ActivityTaskItem>>();

    private IActivityTaskHelperScript getManagerScript() {
        return LogicScriptsUtils.getIActivityTaskHelperScript();
    }


    /** 开启活动 **/
    @Override
    public void openActivity(ActivityConfigure config) {
        getManagerScript().openActivity(config, map, LOGGER);
    }

    // /** 获取条目信息 **/
    // private List<ActivityTaskItem> queryItemList(ActivityConfigure config) {
    // return getManagerScript().queryItemList(config);
    // }

    /** 移除活动 **/
    @Override
    public void closeActivity(ActivityConfigure config) {
        getManagerScript().closeActivity(config, map);
    }

    /** 修改活动 **/
    @Override
    public void changeActivity(ActivityConfigure config) {
        getManagerScript().changeActivity(config, map, LOGGER);
    }

    public void activityTaskCheck(Event event, Player player) {
        getManagerScript().activityTaskCheck(event, player, map,LOGGER);
    }



    public int getReallyEventId(Object object) {
        return getManagerScript().getReallyEventId(object);
    }

    /** 初始化活动 **/
    public void init(Player player) {
        getManagerScript().init(player, map,LOGGER);
    }


    /** 判断奖励是否领取 **/
    @Override
    public boolean isGotReward(ActivityConfigure config, ActivityRecord record, Integer itemId,
            String extendData) {
        return getManagerScript().isGotReward(config, record, itemId, extendData, map, LOGGER);
    }

    // 判断玩家是否能够领取奖励
    @Override
    public boolean isCanReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData) {
        return getManagerScript().isCanReward(player, config, itemId, extendData, map, LOGGER);
    }


    /** 领取活动奖励 成功领取ture **/
    @Override
    public boolean getReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData, ActivityRecord record) {
        return getManagerScript().getReward(player, config, itemId, extendData, record, map,
                LOGGER);
    }

    /** 组装数据 **/
    @Override
    public ActivityItemMsg.Builder getActivityEntryToBuild(ActivityItemMsg.Builder taskBuild,
            Integer itemId, ActivityConfigure config, Player player) {
        return getManagerScript().getActivityEntryToBuild(taskBuild, itemId, config, player, map);
    }

    /** 重置条目 **/
    @Override
    public void resetInItem(Player player, ActivityConfigure config, Integer itemId) {
        getManagerScript().resetInItem(player, config, itemId, map);
    }

    /** 领奖 **/
    @Override
    public ActivityRecord updateReward(ActivityConfigure config, ActivityRecord record,
            int activitEntryId, String extendData) {
        return getManagerScript().updateReward(config, record, activitEntryId, extendData);
    }


    /** 获取当前Map奖励 **/
    public Map<Integer, Integer> getAwardMap(String awardInfo) {
        return getManagerScript().getAwardMap(awardInfo);
    }

    /** 判断是否需要重置 **/
    @Override
    public boolean isNeedResetInItem(Player player, ActivityConfigure config, Integer itemId) {
        return getManagerScript().isNeedResetInItem(player, config, itemId, map);
    }
}


