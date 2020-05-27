package logic.constant;

/**
 * 副本类型 Created by fxf on 2017/11/22.
 */
public interface DungeonTypeConstant {

    /**
     * 主线剧情关卡
     */
    int DUNGEON_TYPE_MAIN_LINE = 1;
    /**
     * 常规关卡
     */
    int DUNGEON_TYPE_GENERAL = 2;
    /**
     * 活动关卡
     */
    int DUNGEON_TYPE_ACTIVITY = 3;

    /**
     * 组队关卡
     */
    int DUNGEON_TYPE_TEAM = 4;
    
    // 常规关卡子类
    /**
     * 默认关卡
     */
    int DUNGEON_TYPE_GENERAL_DEFAULT= 1;
    /**
     * 约会关卡
     */
    int DUNGEON_TYPE_GENERAL_DATING = 2;
    /**
     * 城市约会关卡
     */
    int DUNGEON_TYPE_GENERAL_CITY_DATING = 3;
}
