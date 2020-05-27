package logic.activity.script;

import org.game.protobuf.s2c.S2CActivityMsg.ActivityConfigMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityProgressMsg;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import gm.db.global.bean.ActivityConfigure;
import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;
import script.IScript;

/***
 * 
 * 活动协议
 * 
 * @author lihongji
 *
 */
public interface IActivityCmdUtilsScript extends IScript {


    /**
     * 发送所有活动信息（活动配置，条目配置，条目进度）给玩家 该方法用于玩家每次登陆时候调用
     * 
     * @param player
     */
    public void sendAllActivityInfo(Player player);



    /** 玩家进度组装 **/
    public ActivityProgressMsg.Builder activityRecordToBuild(
            ActivityProgressMsg.Builder recordBuild, ActivityRecord record);

    /** 活动数据组装 **/
    public ActivityConfigMsg.Builder activityConfigureToBuild(ActivityConfigMsg.Builder build,
            ActivityConfigure config, Player player, ChangeType type);

    /**
     * 通知单个活动信息(配置和条目新增修改删除)给-----某个玩家
     * 
     * @param player
     * @param config
     */
    public void sendSingleActivityConfigToPlayer(ActivityConfigure config, Player player,
            ChangeType type);

    /**
     * 通知单个活动信息(配置和条目新增修改删除)给-----所有在线玩家
     * 
     * @param player
     * @param config
     */
    public void sendSingleActivityConfigToAll(ActivityConfigure config, ChangeType type);

    /**
     * 通知单个活动进度给单个玩家
     * 
     * @param player
     * @param config
     */
    public void sendSingleActivityRecordToPlayer(ActivityRecord record, Player player);

    /**
     * 发送所有活动配置信息给客户端
     * 
     * @param player
     */
    public void sendAllActivityConfigToPlayer(Player player);

    /**
     * 发送所有活动条目给客户端
     * 
     * @param player
     */
    public void sendAllActivityItemToPlayer(Player player);

    /**
     * 发送所有活动记录给玩家
     * 
     * @param player
     */
    public void sendAllActivityRecordToPlayer(Player player);

}
