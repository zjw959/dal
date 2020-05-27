package logic.city.build;

public interface BuildingConstant {

    /** 城市背包的刷新时间(下一天的零点+Building_CITY_PACKAGE_TIME) **/
//    public static int BUILDING_CITY_PACKAGE_TIME = 6 * 3600 * 1000;

    /** 早上 **/
    public static int DAY_TIME = 6 * 3600 * 1000;
    /** 晚上 **/
    public static int DAYNIGHT_TIME = 18 * 3600 * 1000;

    /** 刷新精灵的时间 **/
    public static int REFRESH_ROLE_ROOM_TIME = 1 * 3600 * 1000;

    /** 升级要求： */
    int BUILDING_UP_CONDITION_1 = 1;
    /** 升级要求： */
    int BUILDING_UP_CONDITION_2 = 2;
    /** 升级要求：X */
    int BUILDING_UP_CONDITION_3 = 3;
    /** 升级要求： */
    int BUILDING_UP_CONDITION_4 = 4;

    // ==============================================兼职开始=======================================================================
    /** 白天 **/
    public static final int DAYTIME = 0;
    /** 黑夜 **/
    public static final int NIGHTTIME = 1;
    /** 兼职时间点 **/
    public static final int[] CUISINE_FORCE_DAY = {0, 1};
    /** 兼职随机点 **/
    public static final int RANDOM_CUISINE = 2;

    // ==============================================兼职结束=======================================================================

    // ==============================================料理开始=======================================================================
    /** 料理（空闲状态） **/
    public static final int CUISINE_FREE = 0;
    /** 料理（工作状态） **/
    public static final int CUISINE_WORK = 1;


    // ==============================================料理结束=======================================================================



    // ==============================================建筑事件开始===================================================================
    /**
     * EVENT_TYPE_BUILD_UNSEAL=1 建筑解封 EVENT_TYPE_FUNID_UNSEAL=2 功能解封 EVENT_TYPE_ROLE_UNSEAL=3 精灵增加
     * EVENT_TYPE_CUISINE_ADD=5 料理增加
     */
    public static final int EVENT_TYPE_BUILD_UNSEAL = 1, EVENT_TYPE_FUNID_UNSEAL = 2,
            EVENT_TYPE_ROLE_UNSEAL = 3,EVENT_TYPE_CUISINE_ADD=5;
    

    /***
     * 
     * EVENT_TYPE_PART_TIME_JOB=101 兼职任务
     */
    public static final int EVENT_TYPE_PART_TIME_JOB = 1001;



    /**
     * UNLOCK_PT_JOB_TYPE=1 兼职功能 UNLOCK_CUISINE_TYPE=4 料理功能
     * UNLOCK_HAND_WORK_TYPE=3 手工
     */
    public static final int UNLOCK_PT_JOB_TYPE = 1,UNLOCK_HAND_WORK_TYPE=3, UNLOCK_CUISINE_TYPE = 4;

    // ==============================================建筑事件结束===================================================================


    // ==============================================城市精灵开始===================================================================

    /**
     * ROLE_TYPE=1 精灵 NPC_TYPE=2 npc
     * 
     */
    public static final int ROLE_TYPE = 1, NPC_TYPE = 2;
    
    public static final int ROLE_BUILDING[]= {2,3};
    /**刷新精灵的总概率**/
    public static final int ROLE_ALL_PROB=100;

    // ==============================================城市精灵结束===================================================================

    
    public static final String EVENT_RESULT_DATA = "data";
    
    public static final String EVENT_CONDITION_ID="id";
    /**LEVEL_UP=1 玩家升级   PASS_DUP=2 通过关卡**/
    public static final int LEVEL_UP=1,PASS_DUP=2;
    
    
    /**
     * REFRESH_ALL=1000 刷新所有 ADD_ROLE=1001  增加精灵  ATTRIBUTE_CHANGE=1002 角色属性变化
     */
    public static final int  REFRESH_ALL=1000,ADD_ROLE=1001,ATTRIBUTE_CHANGE=1002,CHANGE_MODEL=1003;
    


    // ==============================================兼职任务开始===================================================================
    /***
     * PARTTIME_JOB_SUCCESS=3 兼职已完成 PARTTIME_JOB_WOREING_ON=2 兼职任务中 PARTTIME_JOB_CAN_WORK=1 可兼职
     */
    public static final int PARTTIME_JOB_CAN_WORK = 1, PARTTIME_JOB_WORKING_ON = 2,
            PARTTIME_JOB_SUCCESS = 3;

    // ==============================================兼职任务结束===================================================================
}
