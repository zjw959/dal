package logic.constant;

/**
 * @author : DengYing
 * @CreateDate : 2017年8月23日 下午4:55:39
 * @Description ：精灵状态
 */
public class RoleConstant {
    /** 默认未使用的精灵 */
    public final static int STATUS_DEFAULT = 0;
    /** 当前使用的精灵 */
    public final static int STATUS_USE = 1;

    /** 效果类型：心情自然下降减缓X% */
    public final static int EFFECT_TYPE_1 = 1;
    /** 效果类型：每隔A小时，精灵心情增加X */
    public final static int EFFECT_TYPE_2 = 2;
    /** 效果类型：触发预定约会概率增加X% */
    public final static int EFFECT_TYPE_3 = 3;
    /** 效果类型：约会结束时，心情结算增加X% */
    public final static int EFFECT_TYPE_4 = 4;
    /** 效果类型：约会结束时，好感度结算增加X% */
    public final static int EFFECT_TYPE_5 = 5;


    /** 系统总概率 **/
    public final static int SYSTEM_PROBABILITY = 100;


    /** 条件 **/
    public final static String STATE_CONDITION1 = "condition1";
    public final static String STATE_CONDITION2 = "condition2";
    public final static String STATE_CONDITION3 = "condition3";
    public final static String STATE_CONDITION4 = "condition4";
    public final static String STATE_CONDITION5 = "condition5";
    public final static String STATE_CONDITION6 = "condition6";


    /** 刷新精灵的时间 **/
    public static int REFRESH_ROLE_STATE_TIME = 1 * 3600 * 1000;

    /**喂食**/
    public static int FEED_STATE=1;
    /**日常**/
    public static int DAILY_DATING=2;

    /** 饥饿 **/
    public final static int HUNGER = 6;

    /** 愤怒 **/
    public final static int ANGRY = 7;

    /** 无聊 **/
    public final static int BORING = 8;


}
