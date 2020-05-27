package logic.constant;

/**
 * 约会常量
 * Created by fxf on 2017/8/21.
 */
public interface DatingConstant {

    /** 回应类型：1=接受约会邀请 2=拒绝约会邀请*/
    String ANSWER_TYPE = "type";
    /** 回应影响心情值 */
    String ANSWER_MOOD = "mood";
    /** 回应影响好感度 */
    String ANSWER_FAVOR = "favor";

    /**预约约会时间状态：准时赴约*/
    int RESERVE_DATING_TIME_STATE_ON_TIME = 0;
    /**预约约会时间状态：迟到*/
    int RESERVE_DATING_TIME_STATE_LATE_IN_DAY = 1;
    /**预约约会时间状态：迟到且跨天*/
    int RESERVE_DATING_TIME_STATE_LATE_OUT_DAY = 2;

    /**对话最大条数*/
    int MAX_DIALOGUE_NUM = 3;

    /** 状态：无约会 */
    int RESERVE_DATING_STATE_NO_DATING = 0;
    /**状态：约会邀请状态**/
    int RESERVE_DATING_STATE_INVITATION=1;
    /** 状态：接受邀请 */
    int RESERVE_DATING_STATE_ACCEPT_INVITATION = 2;
    /** 状态：约会时间开始 */
    int RESERVE_DATING_STATE_DATING_TIME_START = 3;
    /** 状态：约会时间结束 */
    int RESERVE_DATING_STATE_DATING_TIME_END = 4;
   
    
    
    /**约会时间段：白天*/
    int DATING_FRAME_DAY = 0;
    /**约会时间段：晚上*/
    int DATING_FRAME_NIGHT = 1;
    /**约会时间段：全天*/
    int DATING_FRAME_ALL_DAY = 2;
    
    /**剧本筛选条件：建筑等级*/
    String DATING_CONDITION_BUILD_LEVEL = "buildLevel";
    /**剧本筛选条件：心情值*/
    String DATING_CONDITION_MOOD = "mood";
    /**剧本筛选条件：时间*/
    String DATING_CONDITION_TIME = "time";
    /**剧本筛选条件：拥有指定看板娘*/
    String DATING_CONDITION_OWN_ROLE = "ownRole";
    /**剧本选择奖励 */
    String ANSWER_REWARD = "reward";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~进入条件 key~~~~~~~~~~~~~~~~~~~~~~~~~~

    /** 进入条件：区域id */
    String ENTER_CDTION_KEY_CITY_CID = "cityCid";
    /** 进入条件：建筑id */
    String ENTER_CDTION_KEY_BUILD_CID = "buildingCid";
    /** 进入条件：权重 */
    String ENTER_CDTION_KEY_WEIGHT  = "weight";
    /** 进入条件：好感度 */
    String ENTER_CDTION_KEY_FAVOR  = "favor";
    /** 进入条件：时间段 */
    String ENTER_CDTION_KEY_TIME_FRAME  = "timeFrame";
    /** 进入条件：心情值 */
    String ENTER_CDTION_KEY_MOOD  = "mood";
    /** 进入条件：看板娘id列表 */
    String ENTER_CDTION_KEY_ROLE_CIDS = "roleCids";
    /** 进入条件：随机率 */
    String ENTER_CDTION_KEY_RATE = "rate";
    /** 进入条件：是否单人约会 */
    String ENTER_CDTION_KEY_SINGLE = "single";
    /** 进入条件：白天还是黑夜 */
    String ENTER_CDTION_KEY_DAYTYPE  = "time";
    /** 进入条件：关卡进度 */
    String ENTER_CDTION_KEY_PASS  = "pass";


    //~~~~~~~~~~~~~~~~~~~~~~~~~~otherInfo key~~~~~~~~~~~~~~~~~~~~~~~~~~
    /** datingRule.otherInfo: 预定约会：回应选项*/
    String OTHER_INFO_KEY_ANSWER_MAP = "answer";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~失败条件 key~~~~~~~~~~~~~~~~~~~~~~~~~~
    String FAIL_CDTION_KEY_SCORE = "score";

    //~~~~~~~~~~~~~~~~~~~~~~~~~~城市约会 key~~~~~~~~~~~~~~~~~~~~~~~~~~
    /** 触发时间 */
    String TRIGGER_TIME = "triggerTime";
    /** 预定约会时间段 */
    String DATING_TIME = "datingTime";
    /**失败奖励*/
    String FAIL_REWARD = "failReward";
    /**邀请剧本id*/
    int INVITE_SCRIPT_ID = 0;
    /**失败剧本id*/
    int FAIL_SCRIPT_ID = 1;



    //~~~~~~~~~~~~~~~~ CityDatingRecord.info.key ~~~~~~~~~~~~~~~~
    /**预定约会状态*/
    String RESERVE_DATING_STATE = "re_da_st";
    /**预定约会开始时间*/
    String DATING_BEGIN_TIME = "da_be_ti";
    /**预定约会结束时间*/
    String DATING_END_TIME = "da_en_ti";
    /**时间段*/
    String TIME_FRAME = "ti_fr";

    /**失败条件：分数*/
    String FAIL_CDTION_SCORE = "score";

    //~~~~~~~~~~~~~~~~ 手机约会 ~~~~~~~~~~~~~~~~
    int PHONE_RESERVEDAING=1;//手机关联的预定约会
    
    
    
    
    public static final String EVENT_RESULT_DATA = "data";
    
    public static final String EVENT_CONDITION_ID="id";
    
    /**
     * ANSWERDATING=1 手机接受与未接受预定回复
     */
    public static final int  ANSWERDATING=1;
    
    

}
