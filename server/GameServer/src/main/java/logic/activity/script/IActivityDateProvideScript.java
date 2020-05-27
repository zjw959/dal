package logic.activity.script;

import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import gm.db.global.bean.ActivityConfigure;
import script.IScript;

public interface IActivityDateProvideScript extends IScript {

    public void checkActivityChange(ConcurrentHashMap<Integer, ActivityConfigure> allActivityMap,
            Logger LOGGER);

    /**
     * 收到新活动执行操作
     * 
     * @param config
     */
    public void createActivity(ActivityConfigure config,
            ConcurrentHashMap<Integer, ActivityConfigure> allActivityMap, Logger LOGGER);

    /**
     * 活动被修改执行操作 活动id不能修改
     * 
     * @param oldActivity 老活动
     * @param newActivity 新活动
     */
    public void updateActivity(ActivityConfigure oldActivity, ActivityConfigure newActivity,
            ConcurrentHashMap<Integer, ActivityConfigure> allActivityMap, Logger LOGGER);


    /**
     * 加载单个活动信息
     */
    public void loadSingleActivityConfig(ActivityConfigure config,
            ConcurrentHashMap<Integer, ActivityConfigure> allActivityMap, Logger LOGGER);


    /**
     * 判断活动是否正在进行 ----处于玩家可以进行活动阶段
     * 
     * @param config
     * @return 正在进行true
     */
    public boolean isEnable(ActivityConfigure config);

    /**
     * 判断活动是否是处于显示阶段 处于显示阶段 返回true
     * 
     * @param config
     * @return
     */
    public boolean isShowTimeEnable(ActivityConfigure config);

    /**
     * 判断活动是否过时
     * 
     * @param config
     * @return
     */
    public boolean isOutTime(ActivityConfigure config);

}
