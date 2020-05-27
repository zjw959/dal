package logic.activity;

import org.game.protobuf.s2c.S2CActivityMsg.ActivityConfigMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityProgressMsg;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import gm.db.global.bean.ActivityConfigure;
import logic.activity.bean.ActivityRecord;
import logic.activity.script.IActivityCmdUtilsScript;
import logic.character.bean.Player;
import logic.support.LogicScriptsUtils;

/**
 * 活动发送协议
 *{@link ActivityCmdUtils#sendAllActivityInfo(Player)} 用于玩家登陆的时候发送所有活动信息（活动配置，条目配置，条目进度）
 *
 *{@link ActivityCmdUtils#sendSingleActivityConfigToAll(ActivityConfigure, ChangeType)} 单个活动(配置和条目)新增修改删除的时候  通知所有在线玩家
 *{@link ActivityCmdUtils#sendSingleActivityRecordToPlayer(ActivityRecord, Player)}   玩家活动进度变更通知客户端
 *
 *{@link ActivityCmdUtils#sendAllActivityConfigToPlayer(Player)} 当客户端请求所有【活动配置信息】时  回给客户端 PS不常用
 *{@link ActivityCmdUtils#sendAllActivityItemToPlayer(Player)} 当客户端请求所有【活动条目信息】时  回给客户端 PS不常用
 *{@link ActivityCmdUtils#sendAllActivityRecordToPlayer(Player)} 当客户端请求所有【活动进度信息】时  回给客户端 PS不常用
 */
public class ActivityCmdUtils {
    
    private static final ActivityCmdUtils DEFAULT = new ActivityCmdUtils();
    public static ActivityCmdUtils getDefault(){
        return DEFAULT;
    }
    
    private IActivityCmdUtilsScript getManagerScript() {
        return LogicScriptsUtils.getIActivityCmdUtilsScript();
    }

    
    
    /**
     * 发送所有活动信息（活动配置，条目配置，条目进度）给玩家
     * 该方法用于玩家每次登陆时候调用
     * @param player
     */
    public void sendAllActivityInfo(Player player){
        getManagerScript().sendAllActivityInfo(player);
    }
    
    
    
    /** 玩家进度组装 **/
     private ActivityProgressMsg.Builder activityRecordToBuild(ActivityProgressMsg.Builder recordBuild, ActivityRecord record) {
        return getManagerScript().activityRecordToBuild(recordBuild, record);
    }

    /** 活动数据组装 **/
    private ActivityConfigMsg.Builder activityConfigureToBuild(ActivityConfigMsg.Builder build, ActivityConfigure config,Player player, ChangeType type) {
       return getManagerScript().activityConfigureToBuild(build, config, player, type);
    }
    
    /**
     * 通知单个活动信息(配置和条目新增修改删除)给-----某个玩家
     * @param player 
     * @param config 
     */
    public void sendSingleActivityConfigToPlayer(ActivityConfigure config, Player player,ChangeType type){
        getManagerScript().sendSingleActivityConfigToPlayer(config, player, type);
    }
    
    /**
     * 通知单个活动信息(配置和条目新增修改删除)给-----所有在线玩家
     * @param player 
     * @param config 
     */
    public void sendSingleActivityConfigToAll(ActivityConfigure config,ChangeType type){
        getManagerScript().sendSingleActivityConfigToAll(config, type);
    }
    
    /**
     * 通知单个活动进度给单个玩家
     * @param player 
     * @param config 
     */
    public void sendSingleActivityRecordToPlayer(ActivityRecord record,Player player){
        getManagerScript().sendSingleActivityRecordToPlayer(record, player);
    }
    
    /**
     * 发送所有活动配置信息给客户端
     * @param player
     */
    public void sendAllActivityConfigToPlayer(Player player){
        getManagerScript().sendAllActivityConfigToPlayer(player);
    }
    
    /**
     * 发送所有活动条目给客户端
     * @param player
     */
    public void sendAllActivityItemToPlayer(Player player){
        getManagerScript().sendAllActivityItemToPlayer(player);
    }
    
    /**
     * 发送所有活动记录给玩家
     * @param player
     */
    public void sendAllActivityRecordToPlayer(Player player){
        getManagerScript().sendAllActivityRecordToPlayer(player);
    }
}

