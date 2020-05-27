/**
 * 
 */
package logic.activity.constant;

import logic.activity.bean.ActivityRecord;

/**
 *	活动状态    {@link ActivityRecord#getStatus()} 
 */
public interface ActivityRecordStatus {
	/**
     * 未完成
     */
    byte unfinished = 0;
    
    /**
     * 完成未领奖
     */
    byte finished_not_getreward = 1;

    /**
     * 已经领奖
     */
    byte finished_gotreward = 2;
}
