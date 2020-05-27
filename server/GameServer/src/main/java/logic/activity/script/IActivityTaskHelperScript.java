package logic.activity.script;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityItemMsg;
import event.Event;
import gm.db.global.bean.ActivityConfigure;
import gm.db.global.bean.ActivityTaskItem;
import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;
import script.IScript;

public interface IActivityTaskHelperScript extends IScript {

    /** 开启活动 **/
    public void openActivity(ActivityConfigure config,
            Map<Integer, Map<Integer, ActivityTaskItem>> map, Logger LOGGER);

    /** 获取条目信息 **/
    public List<ActivityTaskItem> queryItemList(ActivityConfigure config);

    /** 移除活动 **/
    public void closeActivity(ActivityConfigure config,Map<Integer, Map<Integer, ActivityTaskItem>> map);

    /** 修改活动 **/
    public void changeActivity(ActivityConfigure config,Map<Integer, Map<Integer, ActivityTaskItem>> map, Logger LOGGER);

    public void activityTaskCheck(Event event, Player player,
            Map<Integer, Map<Integer, ActivityTaskItem>> map,Logger LOGGER);

    public int getReallyEventId(Object object);

    /** 初始化活动 **/
    public void init(Player player,Map<Integer, Map<Integer, ActivityTaskItem>> map,Logger LOGGER);

    /** 判断奖励是否领取 **/
    public boolean isGotReward(ActivityConfigure config, ActivityRecord record, Integer itemId,
            String extendData,Map<Integer, Map<Integer, ActivityTaskItem>> map, Logger LOGGER);

    // 判断玩家是否能够领取奖励
    public boolean isCanReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData,Map<Integer, Map<Integer, ActivityTaskItem>> map, Logger LOGGER);

    /** 领取活动奖励 成功领取ture **/
    public boolean getReward(Player player, ActivityConfigure config, Integer itemId,
            String extendData, ActivityRecord record,Map<Integer, Map<Integer, ActivityTaskItem>> map, Logger LOGGER);

    /** 组装数据 **/
    public ActivityItemMsg.Builder getActivityEntryToBuild(ActivityItemMsg.Builder taskBuild,
            Integer itemId, ActivityConfigure config, Player player,Map<Integer, Map<Integer, ActivityTaskItem>> map);

    /** 重置条目 **/
    public void resetInItem(Player player, ActivityConfigure config, Integer itemId,Map<Integer, Map<Integer, ActivityTaskItem>> map);

    /** 领奖 **/
    public ActivityRecord updateReward(ActivityConfigure config, ActivityRecord record,
            int activitEntryId, String extendData);

    /** 获取当前Map奖励 **/
    public Map<Integer, Integer> getAwardMap(String awardInfo);
    
    public boolean isNeedResetInItem(Player player, ActivityConfigure config, Integer itemId,Map<Integer, Map<Integer, ActivityTaskItem>> map);

}
