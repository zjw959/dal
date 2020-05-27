package javascript.logic.activity;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import gm.db.global.bean.ActivityConfigure;
import gm.db.global.dao.ActivityConfigureDao;
import logic.activity.ActivityDateProvide;
import logic.activity.constant.ActivityStatus;
import logic.activity.helper.ActivityExecProvider;
import logic.activity.helper.IActivityExecutor;
import logic.activity.script.IActivityDateProvideScript;
import logic.constant.EScriptIdDefine;

/**
 * 活动数据管理
 * 
 * @author lihongji
 *
 */

public class ActivityDateProvideScript implements IActivityDateProvideScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.ACTIVITYDATEPROVIDE_SCRIPT.Value();
    }

    /** 检测活动变化 **/
    @Override
    public void checkActivityChange(ConcurrentHashMap<Integer, ActivityConfigure> allActivityMap,
            Logger LOGGER) {
        long lastCheckTime = ActivityDateProvide.getDefault().getLastCheckTime();
        if (System.currentTimeMillis() - lastCheckTime < 10000) {
            return;
        }
        List<ActivityConfigure> list = ActivityConfigureDao.selectActivityConfigureList();
        if (list == null || list.isEmpty()) {
            return;
        }
        for (ActivityConfigure config : list) {
            if (config.getUpdateStatus() == 1) {
                continue;
            }
            loadSingleActivityConfig(config, allActivityMap, LOGGER);
        }
        ActivityDateProvide.getDefault().setLastCheckTime(System.currentTimeMillis());
    }

    /**
     * 收到新活动执行操作
     * 
     * @param config
     */
    @Override
    public void createActivity(ActivityConfigure config,
            ConcurrentHashMap<Integer, ActivityConfigure> allActivityMap, Logger LOGGER) {
        allActivityMap.put(config.getId(), config);
        LOGGER.info("检测到新活动-- new activity:" + config.toString());
        if (config.getStatus() == ActivityStatus.DISABLE) {
            return;
        }
        if (config.getStatus() == ActivityStatus.AUTO && isOutTime(config)) {// 判断活动是否已经过时
            return;
        }
        IActivityExecutor exec = ActivityExecProvider.getDefault().get(config.getType());
        if (isShowTimeEnable(config)) {// 表示活动已经开启 直接调用活动开启和通知玩家的方法
            exec.openActivity(config);
            exec.notifyActivityOpen(config);
        }
        exec.loadOpenTrigger(config);
        exec.loadCloseTrigger(config);

    }

    /**
     * 活动被修改执行操作 活动id不能修改
     * 
     * @param oldActivity 老活动
     * @param newActivity 新活动
     */
    @Override
    public void updateActivity(ActivityConfigure oldActivity, ActivityConfigure newActivity,
            ConcurrentHashMap<Integer, ActivityConfigure> allActivityMap, Logger LOGGER) {

        // 根据更新时间判断活动是否被修改
        if (newActivity.getChangeTime() <= oldActivity.getChangeTime()) {
            return;
        }
        // 判断新老活动状态变更
        boolean oldIsEnable = isShowTimeEnable(oldActivity);// 老活动的状态
        boolean newIsEnable = isShowTimeEnable(newActivity);// 新活动的状态
        IActivityExecutor exec = ActivityExecProvider.getDefault().get(newActivity.getType());
        if (newIsEnable && !oldIsEnable) {// 老活动不开启-->新活动开启
            LOGGER.info("活动" + newActivity.getId() + "被修改开启");
            exec.openActivity(newActivity);
            exec.notifyActivityOpen(newActivity);
        }
        if (oldIsEnable && !newIsEnable) {// 老活动开启-->新活动不开启
            LOGGER.info("活动" + newActivity.getId() + "被修改关闭");
            exec.closeActivity(oldActivity);// --这里有一点要注意的 当活动关闭的时候会调用老活动的关闭方法 如果有什么结束发奖 那么奖励还是会照常发送
            exec.notifyActivityClose(newActivity);
        }
        if (newIsEnable && oldIsEnable) {// 老活动开启-->新活动开启-->活动内容发生变化
            LOGGER.info("活动" + newActivity.getId() + "内容发生变动");
            exec.changeActivity(newActivity);
            exec.notifyActivityChange(newActivity);
        }
        allActivityMap.put(newActivity.getId(), newActivity);
        exec.removeAllTrigger(newActivity);// 这里清空该活动老的定时器,重新初始化一下所有定时器
        exec.loadOpenTrigger(newActivity);
        exec.loadCloseTrigger(newActivity);
    }

    /**
     * 加载单个活动信息
     */
    @Override
    public void loadSingleActivityConfig(ActivityConfigure config,
            ConcurrentHashMap<Integer, ActivityConfigure> allActivityMap, Logger LOGGER) {
        ActivityConfigure exist = allActivityMap.get(config.getId());
        if (exist == null) {
            createActivity(config, allActivityMap, LOGGER);
        } else {
            updateActivity(exist, config, allActivityMap, LOGGER);
        }
    }

    /**
     * 判断活动是否正在进行 ----处于玩家可以进行活动阶段
     * 
     * @param config
     * @return 正在进行true
     */
    @Override
    public boolean isEnable(ActivityConfigure config) {
        if (config.getStatus() == ActivityStatus.ENABLE) {
            return true;
        }
        if (config.getStatus() == ActivityStatus.DISABLE) {
            return false;
        }
        Long now = System.currentTimeMillis();
        if (now > config.getBeginTime() && now < config.getEndTime()) {
            return true;
        }
        return false;
    }

    /**
     * 判断活动是否是处于显示阶段 处于显示阶段 返回true
     * 
     * @param config
     * @return
     */
    @Override
    public boolean isShowTimeEnable(ActivityConfigure config) {
        if (config.getStatus() == ActivityStatus.ENABLE) {
            return true;
        }
        if (config.getStatus() == ActivityStatus.DISABLE) {
            return false;
        }
        Long now = System.currentTimeMillis();
        if (now > config.getShowBeginTime() && now < config.getEndTime()) {
            return true;
        }
        return false;
    }

    /**
     * 判断活动是否过时
     * 
     * @param config
     * @return
     */
    @Override
    public boolean isOutTime(ActivityConfigure config) {
        Long now = System.currentTimeMillis();
        if (config.getShowEndTime() < now) {
            return true;
        }
        return false;
    }

}
