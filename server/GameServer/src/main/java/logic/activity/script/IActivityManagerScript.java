package logic.activity.script;

import java.util.Map;

import org.apache.log4j.Logger;

import event.Event;
import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;
import logic.constant.EAcrossDayType;
import script.IScript;


/***
 * 
 * 玩家活动记录
 * 
 * @author lihongji
 *
 */
public interface IActivityManagerScript extends IScript {

    /** 通过活动id 条目id 获取记录 **/
    public ActivityRecord getRecord(Integer activitId, Integer itemId,
            Map<Integer, Map<Integer, ActivityRecord>> activityRecord);

    /** 重新添加记录 **/
    public void addRcord(ActivityRecord record,
            Map<Integer, Map<Integer, ActivityRecord>> activityRecord);

    // 判断条目是否完成领取过
    // 判断条目使能能够完成和领取
    // 领取奖励
    // 修改记录
    // 返回客户端新的数据
    public void getReward(Player player, int activityId, int activitEntryId, String extendData,
            Map<Integer, Map<Integer, ActivityRecord>> activityRecord, Logger LOGGER);

    public void execute(Event event, Player player);

    public void createPlayerInitialize(Player player);

    /** 初始化 **/
    public void activityInit(Player player);

    public void acrossDay(EAcrossDayType type, boolean isNotify,
            Map<Integer, Map<Integer, ActivityRecord>> activityRecord, Player player,Logger LOGGER) throws Exception ;

    public void open(Player player);
    
    /**移除记录**/
    public void removeRecord(Map<Integer, Map<Integer, ActivityRecord>> activityRecord, Player player,Logger LOGGER);

    /**日志记录**/
    public void logRecord();
}
