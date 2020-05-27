/**
 * 
 */
package logic.activity.constant;

import gm.db.global.bean.ActivityConfigure;

/**
 *  活动类型   {@link ActivityConfigure#getType()} 由该字段配置
 */
public interface ActivityType {
    /**
     * 未知
     */
    int UNKONW = 0;

    /**
     * 活动商店
     */
    int activity_store = 1;

    /**
     * 活动任务
     */
    int activity_task = 2;


}
