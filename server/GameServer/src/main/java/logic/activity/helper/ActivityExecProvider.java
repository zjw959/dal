package logic.activity.helper;

import java.util.HashMap;
import java.util.Map;

import logic.activity.constant.ActivityType;
import logic.activity.helper.list.ActivityStoreHelper;
import logic.activity.helper.list.ActivityTaskHelper;

/***
 * 
 * 活动帮助
 * 
 * @author
 *
 */
public class ActivityExecProvider {

    private static final ActivityExecProvider DEFAULT = new ActivityExecProvider();

    public static ActivityExecProvider getDefault() {
        return DEFAULT;
    }

    private boolean isInit=false;
    /** 活动帮助集合-任务类型 商品类型 **/
    private Map<Integer, IActivityExecutor> execMap = new HashMap<Integer, IActivityExecutor>();

    /** 获取helper **/
    public IActivityExecutor get(int key) {
    	if(!isInit)init();
        return execMap.get(key);
    }

    public void init() {
        execMap.put(ActivityType.activity_store, new ActivityStoreHelper());
        execMap.put(ActivityType.activity_task, new ActivityTaskHelper());
        isInit=true;
    }
}
