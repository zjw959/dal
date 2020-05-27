/**
 * 
 */
package logic.activity.constant;

import gm.db.global.bean.ActivityConfigure;

/**
 *  活动状态    {@link ActivityConfigure#getStatus()} 由该字段配置
 */
public interface ActivityStatus {
    /**
     * 自动检测，根据开启和结束时间自行检测
     */
    byte AUTO = 2;

    /**
     * 活动有效，忽略其他条件，强制设置为有效
     */
    byte ENABLE = 1;

    /**
     * 活动无效，忽略任意条件，强制为无效
     */
    byte DISABLE = 0;
}
