package utils;

import java.util.Map;

import com.google.common.collect.Maps;

import event.Event;
import logic.constant.EventConditionKey;

public class EventUtil {
    /**
     * 检查触发类型
     * 
     */
    @SuppressWarnings("rawtypes")
    public static boolean checkTriggerType(Map in, int triggerCode) {
        return ToolMap.getInt(EventConditionKey.CONDITION_TYPE, in) == triggerCode;
    }

    /**
     * 小于
     * 
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static boolean verify_lt(String key, Map in, Map Params) {
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
    public static boolean verify_le(String key, Map in, Map Params) {
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
    public static boolean verify_gt(String key, Map in, Map Params) {
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
    public static boolean verify_ge(String key, Map in, Map Params) {
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
    public static boolean verify_eq(String key, Map in, Map Params) {
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
    public static boolean verify_not_eq(String key, Map in, Map Params) {
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

    /**
     * 条件参数检查
     */
    @SuppressWarnings("rawtypes")
    public static boolean checkParams(Event event, Map params, String condition, boolean bool) {
        switch (condition) {
            case EventConditionKey.DUNGEON_CID:
                bool &= EventUtil.verify_eq(EventConditionKey.DUNGEON_CID, (Map) event.getParams(),
                        params);
                break;
            default:
                bool &= false;
                break;
        }

        // bool &= EventUtil.verify_ge(EventConditionKey.STAR, (Map) event.getParams(), params);
        // bool &= EventUtil.verify_le(EventConditionKey.FIGHT_HERO_COUNT, (Map) event.getParams(),
        // params);
        // bool &= EventUtil.verify_le(EventConditionKey.FIGHT_TIME, (Map) event.getParams(),
        // params);
        // bool &= EventUtil.verify_eq(EventConditionKey.DIFFICULTY, (Map) event.getParams(),
        // params);
        // bool &= EventUtil.verify_eq(EventConditionKey.DUNGOEN_TYPE, (Map) event.getParams(),
        // params);
        return bool;
    }

    public static void main(String[] args) {
        boolean bool = true;
        System.err.println(bool &= false);
    }
}
