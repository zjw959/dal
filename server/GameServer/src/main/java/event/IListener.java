package event;

import gm.db.global.bean.ActivityTaskItem;

import java.util.List;
import java.util.Map;

import logic.activity.bean.ActivityRecord;
import logic.character.bean.Player;
import logic.constant.EventConditionKey;
import logic.task.TaskManager;
import logic.task.bean.GameEventStack;
import logic.task.bean.GameEventVector;
import logic.task.bean.Task;

import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import utils.ToolMap;

import com.google.common.collect.Maps;

public interface IListener {

    public void initEventCondition(Player player, GameEventStack eventStack);

    public void onEvent(Event event, Player player);

    @SuppressWarnings("rawtypes")
    public void handler(Player player, Event event, GameEventVector vector, int conditionId,
            Map params, int maxProcess, List<TaskInfo> taskInfos);

    /** 活动检测 **/
    public void activityHandler(Player player, Event event, ActivityTaskItem taskItem,
            ActivityRecord record);

    /** 任务活动初始化 **/
    public void initActivityEventCondition(Player player, GameEventStack eventStack,
            ActivityRecord record);


    /**
     * 检查触发类型
     * 
     * @param in
     * @param triggerCode
     * @return
     */
    @SuppressWarnings("rawtypes")
    default boolean checkTriggerType(Map in, int triggerCode) {
        return ToolMap.getInt(EventConditionKey.CONDITION_TYPE, in) == triggerCode;
    }

    /**
     * 小于
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    default boolean verify_lt(String key, Map in, Map Params) {
        if (Params == null) {
            Params = Maps.newHashMap();
        }
        int condition = ToolMap.getInt(key, Params, 0);
        if (condition != 0) {
            int number = ToolMap.getInt(key, in, 0);
            return number < condition;
        }
        return true;
    }

    /**
     * 小于等于
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    default boolean verify_le(String key, Map in, Map Params) {
        if (Params == null) {
            Params = Maps.newHashMap();
        }
        int condition = ToolMap.getInt(key, Params, 0);
        if (condition != 0) {
            int number = ToolMap.getInt(key, in, 0);
            return number <= condition;
        }
        return true;
    }

    /**
     * 大于
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    default boolean verify_gt(String key, Map in, Map Params) {
        if (Params == null) {
            Params = Maps.newHashMap();
        }
        int condition = ToolMap.getInt(key, Params, 0);
        if (condition != 0) {
            int number = ToolMap.getInt(key, in, 0);
            return number > condition;
        }
        return true;
    }

    /**
     * 大于等于
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    default boolean verify_ge(String key, Map in, Map Params) {
        if (Params == null) {
            Params = Maps.newHashMap();
        }
        int condition = ToolMap.getInt(key, Params, 0);
        if (condition != 0) {
            int number = ToolMap.getInt(key, in, 0);
            return number >= condition;
        }
        return true;
    }

    /**
     * 等于
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    default boolean verify_eq(String key, Map in, Map Params) {
        if (Params == null) {
            Params = Maps.newHashMap();
        }
        int condition = ToolMap.getInt(key, Params, 0);
        if (condition != 0) {
            int number = ToolMap.getInt(key, in, 0);
            return number == condition;
        }
        return true;
    }

    /**
     * 不等于
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    default boolean verify_not_eq(String key, Map in, Map Params) {
        if (Params == null) {
            Params = Maps.newHashMap();
        }
        int condition = ToolMap.getInt(key, Params, 0);
        if (condition != 0) {
            int number = ToolMap.getInt(key, in, 0);
            return number != condition;
        }
        return true;
    }

    default boolean checkPut(TaskManager manager, Task task) {
        if (manager.inTaskMap(task.getCid())) {
            return false;
        }
        if (task.getProgress() == 0 && task.getStatus() == 0) {
            return false;
        }
        return true;
    }
}
