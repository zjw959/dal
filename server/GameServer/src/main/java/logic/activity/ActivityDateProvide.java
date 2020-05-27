package logic.activity;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import gm.db.global.bean.ActivityConfigure;
import logic.activity.script.IActivityDateProvideScript;
import logic.support.LogicScriptsUtils;

/**
 * 活动数据管理
 */
public class ActivityDateProvide{
    
    private static final ActivityDateProvide DEFAULT = new ActivityDateProvide();
    public static ActivityDateProvide getDefault(){
        return DEFAULT;
    }
    
    private static final Logger LOGGER = Logger.getLogger(ActivityDateProvide.class);
    
    private IActivityDateProvideScript getManagerScript() {
        return LogicScriptsUtils.getIActivityDateProvideScript();
    }
    
    private long lastCheckTime = 0;
    /**
     * 保存所有的活动信息  包括已经过期的活动
     */
    private ConcurrentHashMap<Integer, ActivityConfigure> allActivityMap = new ConcurrentHashMap<>();
    
    /**
     * 检测所有活动变更，游戏服务器更新活动信息
     */
    public void checkActivityChange(){
        getManagerScript().checkActivityChange(allActivityMap, LOGGER);
    }
    
    /**
     * 收到新活动执行操作
     * @param config
     */
    public void createActivity(ActivityConfigure config){
        getManagerScript().createActivity(config, allActivityMap, LOGGER);
    }
    
    /**
     * 活动被修改执行操作  活动id不能修改
     * @param oldActivity 老活动
     * @param newActivity 新活动
     */
    public void updateActivity(ActivityConfigure oldActivity,ActivityConfigure newActivity){
        getManagerScript().updateActivity(oldActivity, newActivity, allActivityMap, LOGGER);
    }
    
    /**
     * 根据id获取活动信息
     * @param id  活动id
     * @return
     */
    public ActivityConfigure getConfigById(Integer id){
        return allActivityMap.get(id);
    }

  
    /**
     * 加载单个活动信息
     */
    public void loadSingleActivityConfig(ActivityConfigure config){
        getManagerScript().loadSingleActivityConfig(config,allActivityMap,LOGGER);
    }

    /**
     * 获取所有活动信息
     * @return
     */
    public Collection<ActivityConfigure> value() {
        return allActivityMap.values();
    }

    /**
     * 判断活动是否正在进行  ----处于玩家可以进行活动阶段
     * @param config
     * @return 正在进行true
     */
    public boolean isEnable(ActivityConfigure config) {
        return getManagerScript().isEnable(config);
    }
    
    /**
     * 判断活动是否是处于显示阶段    处于显示阶段   返回true
     * @param config
     * @return
     */
    public boolean isShowTimeEnable(ActivityConfigure config){
        return getManagerScript().isShowTimeEnable(config);
    }
    
    /**
     * 判断活动是否过时
     * @param config
     * @return
     */
    public boolean isOutTime(ActivityConfigure config){
        return getManagerScript().isOutTime(config);
    }

    public long getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(long lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }
    

}
