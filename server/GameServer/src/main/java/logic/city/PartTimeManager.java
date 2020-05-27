package logic.city;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CNewBuildingMsg.RespPartTimeJobAward;
import data.bean.CityJobCfgBean;
import logic.basecore.IAcrossDay;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.IUnlockBuilding;
import logic.basecore.PlayerBaseFunctionManager;
import logic.character.bean.Player;
import logic.city.build.bean.JobEvent;
import logic.city.build.bean.JobRecord;
import logic.city.build.bean.PartTimeRecord;
import logic.city.script.IPartTimeManagerScript;
import logic.constant.EAcrossDayType;
import logic.support.LogicScriptsUtils;

/**
 * 
 * 兼职管理器
 * 
 * @author lihongji
 *
 */
public class PartTimeManager extends PlayerBaseFunctionManager
        implements IUnlockBuilding, IRoleJsonConverter, IAcrossDay {

    /** 兼职记录 **/
    Map<Integer, PartTimeRecord> partRecord = new HashMap<Integer, PartTimeRecord>();
    
    /** 正在进行的兼职事件 **/
    JobEvent jobEvent = new JobEvent();

    private static final Logger LOGGER = Logger.getLogger(PartTimeManager.class);
    
    private IPartTimeManagerScript getManagerScript() {
        return LogicScriptsUtils.getIPartTimeManagerScript();
    }

    /** 检测当前是否能够兼职 **/
    public JobRecord createJobEvent(int buildingId, int jobId, Player player) {
        return getManagerScript().createJobEvent(buildingId, jobId, player,LOGGER);
    }

    /** 检测当前job是否存在列表中 **/
    public JobRecord checkJobIsExist(PartTimeRecord ptRecord, int jobId) {
        return getManagerScript().checkJobIsExist(ptRecord, jobId);
    }

    /** 刷新建筑中的兼职列表 **/
    public void refreshPartTimeList() {
        getManagerScript().refreshPartTimeList(player);
    }

    /** 刷新单个建筑的兼职 **/
    public void refreshPartTimeBuilding(int buildingId) {
        getManagerScript().refreshPartTimeBuilding(buildingId, player);
    }

    /**
     * 获取当前建筑的兼职列表
     * 
     * @param buildingId 建筑id
     * @param ptLevel 兼职等级
     * @return
     */
    public ArrayList<Integer> getJobList(int buildingId, int ptLevel) {
        return getManagerScript().getJobList(buildingId, ptLevel);
    }

    /** 刷新 **/
    public void refresh() {
        getManagerScript().refresh(player);
    }

    /** 获取当前兼职列表中的 **/
    public JobRecord getJobRecord(int buildingId, int jobId) {
        return getManagerScript().getJobRecord(buildingId, jobId, player);
    }

    /** 获取奖励 **/
    public RespPartTimeJobAward.Builder getAward() {
        return getManagerScript().getAward(player,LOGGER);
    }

    public void trigger(int buildingId, CityJobCfgBean cityJobCfgBean) {
        getManagerScript().trigger(buildingId, cityJobCfgBean, player);
    }

    /** 封装奖励 **/
    public Map<Integer, Integer> getGift(int jobId) {
        return getManagerScript().getGift(jobId);
    }


    /** 获取额外的奖励 **/
    public Map<Integer, Integer> getExtraGift(Map<Integer, Integer> gifts,
            Map<Integer, Integer> systemGift) {
        return getManagerScript().getExtraGift(gifts, systemGift);
    }

    /** 增加经验 **/
    public void addExp(int addExp, int buildingId) {
        getManagerScript().addExp(addExp, buildingId, this);
    }


    /** 检测 **/
    public void checkUnlock() {
        getManagerScript().checkUnlock(player);
    }

    /** 解锁建筑就直接 **/
    @Override
    public void unlock(Object object) {
        getManagerScript().unlock(object, player);
    }


    /** 检测当前建筑是否能够兼职 **/
    public boolean checkBuilingFunId(int buildingId) {
        return getManagerScript().checkBuilingFunId(buildingId, player);
    }


    /** 放弃兼职 **/
    public void giveUpJob() {
        getManagerScript().giveUpJob(player,LOGGER);
    }

    public Map<Integer, PartTimeRecord> getPartRecord() {
        return partRecord;
    }

    public void setPartRecord(Map<Integer, PartTimeRecord> partRecord) {
        this.partRecord = partRecord;
    }

    public JobEvent getJobEvent() {
        return jobEvent;
    }

    public void setJobEvent(JobEvent jobEvent) {
        this.jobEvent = jobEvent;
    }

//    public long getRefreshPartTime() {
//        return RefreshPartTime;
//    }
//
//    public void setRefreshPartTime(long refreshPartTime) {
//        RefreshPartTime = refreshPartTime;
//    }

    /** 跨天刷新 **/
    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify) {
        getManagerScript().acrossDay(type, isNotify, player);
    }



}
