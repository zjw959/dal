/**
 * 
 */
package logic.activity.helper;

import gm.db.global.bean.ActivityConfigure;
import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;

import org.game.protobuf.s2c.S2CActivityMsg.ActivityItemMsg;

/**
 * 活动接口
 * @author
 */
public interface IActivityExecutor {

    /**
     * 判断活动是否已经领取过奖励 领取过true
     * 
     * @return
     */
    boolean isGotReward(ActivityConfigure config, ActivityRecord record, Integer itemId,
            String extendData) throws Exception;

    /**
     * 判断活动是否能完成 有资格完成true
     * @return
     */
    boolean isCanReward(Player player, ActivityConfigure config, Integer itemId, String extendData);

    /**
     * 领取活动奖励 成功领取ture
     * 
     * @param player
     * @param config
     * @param itemId
     * @param extendData
     * @param record 
     * @return
     */
    boolean getReward(Player player, ActivityConfigure config, Integer itemId, String extendData, ActivityRecord record) throws Exception ;

    /**
     * 活动创建时 启动活动开始定时器
     * 
     * @param config
     * @return
     */
    void loadOpenTrigger(ActivityConfigure config);

    /**
     * 活动创建时 启动活动结束定时器
     * 
     * @param config
     * @return
     */
    void loadCloseTrigger(ActivityConfigure config);

    /**
     * 通知活动开启之前 初始化活动数据
     * 
     * @param config
     */
    void openActivity(ActivityConfigure config);

    /**
     * 通知所有在线玩家 活动开启
     * 
     * @param config
     */
    void notifyActivityOpen(ActivityConfigure config);

    /**
     * 通知所有在线玩家 活动结束
     * 
     * @param config
     */
    void notifyActivityClose(ActivityConfigure config);

    /**
     * 通知活动结束后 清空活动数据
     * 
     * @param config
     */
    void closeActivity(ActivityConfigure config);

    /**
     * 通知活动修改前 修改活动数据
     * 
     * @param config
     */
    void changeActivity(ActivityConfigure config);

    /**
     * 通知所有在线玩家 活动变更
     * 
     * @param config
     */
    void notifyActivityChange(ActivityConfigure config);

    /**
     * 重置进度
     * @param player 
     * 
     * @param config
     */
    void reset(Player player, ActivityConfigure config) throws Exception ;

    /**
     * 去掉所有定时器
     * 
     * @param config
     */
    void removeAllTrigger(ActivityConfigure config);

    /**
     * 获得玩家条目信息
     * 
     * @param entryBuild
     * @param itemId
     * @param config
     * @return entryBuild
     */
    ActivityItemMsg.Builder getActivityEntryToBuild(ActivityItemMsg.Builder entryBuild, Integer itemId, ActivityConfigure config,
            Player player);

    String getActivityRemark(ActivityConfigure config);

    String getActivityExtendData(ActivityConfigure config);

    /**
     * 判断某个活动条目是否需要重置
     * @param player
     * @param config
     * @param itemId
     * @return
     */
    boolean isNeedResetInItem(Player player, ActivityConfigure config, Integer itemId);

    /**
     * 重置该条目
     * @param player
     * @param config
     * @param itemId
     */
    void resetInItem(Player player, ActivityConfigure config, Integer itemId) throws Exception ;

    /**
     * 领取奖励成功之后修改活动记录
     * @param config
     * @param record
     * @param activitEntryId
     * @param extendData
     * @return
     */
    ActivityRecord updateReward(ActivityConfigure config, ActivityRecord record, int activitEntryId, String extendData);

	/**
	 * 获取玩家活动记录前对玩家记录进行检测
	 * @param record
	 * @param config 
	 * @param itemId 
	 * @return
	 */
	ActivityRecord checkRecord(ActivityRecord record, ActivityConfigure config, Integer itemId);

}
