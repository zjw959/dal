package logic.constant;

public class TaskConstant {
    /** 任务进行中 */
    public static final int STATUS_RUNING = 0;
    /** 任务已完成 */
    public static final int STATUS_FINISH = 1;
    /** 任务已完结(领取) */
    public static final int STATUS_RECEIVE = 2;
    /** 任务过期 */
    public static final int STATUS_TIME_OUT = 4;


    /** 主线任务 */
    public static final int TYPE_MAIN_LINE = 1;
    /** 日常任务 */
    public static final int TYPE_DAY_CONSTANT = 2;
    /** 成就任务 */
    public static final int TYPE_GLORY = 3;


    /** 不重置 */
    public static final int RESET_NO = 1;
    /** 每天重置 */
    public static final int RESET_DAY = 2;


    /** 任务类型：月卡任务 */
    public static final int TASK_TYPE_MONTH_CARD = 8;

    public static final int RUN = 1;
    public static final int NOT_RUN = 0;
}
