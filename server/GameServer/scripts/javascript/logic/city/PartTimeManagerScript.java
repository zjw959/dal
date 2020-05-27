package javascript.logic.city;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CNewBuildingMsg.RespPartTimeJobAward.Builder;
import com.google.common.collect.Maps;
import data.GameDataManager;
import data.bean.CityJobCfgBean;
import data.bean.NewBuildCfgBean;
import logic.character.bean.Player;
import logic.city.BuildingGeneral;
import logic.city.NewBuildingManager;
import logic.city.PartTimeManager;
import logic.city.build.BuildingConstant;
import logic.city.build.bean.JobRecord;
import logic.city.build.bean.PartTimeRecord;
import logic.city.script.IPartTimeManagerScript;
import logic.constant.EAcrossDayType;
import logic.constant.EEventType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.GameErrorCode;
import logic.item.ItemPackageHelper;
import logic.msgBuilder.NewBuildingMsgBuilder;
import logic.support.LogBeanFactory;
import logic.support.MessageUtils;
import thread.log.LogProcessor;
import utils.ExceptionEx;

/***
 * 兼职
 * 
 * @author lihongji
 *
 */
public class PartTimeManagerScript implements IPartTimeManagerScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.CITY_PART_JOB_SCRIPT.Value();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JobRecord createJobEvent(int buildingId, int jobId, Player player, Logger LOGGER) {
        PartTimeManager manager = player.getPartTimeManager();
        CityJobCfgBean jobBean = GameDataManager.getCityJobCfgBean(jobId);
        if (jobBean == null)
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_JOB_IS_NOT_EXIST, "兼职任务不存在");
        if (manager.getJobEvent().getEtime() != 0)
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_JOB_IS_NOT_COMPLETE, "兼职未完成");
        // 检测对应的建筑身上是否有相应的兼职Id
        JobRecord existJob = checkJobIsExist(manager.getPartRecord().get(buildingId), jobId);
        if (existJob == null)
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_JOB_IS_NOT_EXIST,
                    "兼职任务不存在:" + jobId);
        // if (player.getNewBuildingManager().getDayType() != existJob.getType())
        // MessageUtils.throwCondtionError(GameErrorCode.BUILDING_JOB_IS_NOT_RIGHT_TIME,
        // "兼职时间点不对");
        long etime = System.currentTimeMillis() + jobBean.getCostTime() * 1000;
        if (!player.getBagManager().enoughByTemplateId(jobBean.getCostVim()))
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_JOB_IS_NOT_ENOUGH_STRENGTH,
                    "体力不足");
        player.getBagManager().removeItemsByTemplateIdNoCheck(jobBean.getCostVim(), true,
                EReason.BUILDING_GIVEUP_JOB);
        manager.getJobEvent().init(buildingId, etime, jobId, existJob.getRefreshTime(),
                existJob.getType());
        // 设置当前兼职状态
        existJob.setJobType(BuildingConstant.PARTTIME_JOB_WORKING_ON);

        // 城市兼职开始时间
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createCityGameLog(player,
                    EReason.PARTTIME_JOB_STARTTIME.value(), jobId));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }

        return existJob;
    }

    /** 检测当前job是否存在列表中 **/
    @Override
    public JobRecord checkJobIsExist(PartTimeRecord ptRecord, int jobId) {
        if (ptRecord == null || ptRecord.getJobList() == null)
            return null;
        Iterator<JobRecord> iterator = ptRecord.getJobList().iterator();
        while (iterator.hasNext()) {
            JobRecord job = iterator.next();
            if (job.getJobId() == jobId)
                return job;
        }
        return null;
    }

    /** 刷新建筑中的兼职列表 **/
    @Override
    public void refreshPartTimeList(Player player) {
        NewBuildingManager manager = player.getNewBuildingManager();
        manager.getValidbuilding().forEach((buildingId) -> {
            // 建筑当前建筑是否能够兼职
            if (checkBuilingFunId(buildingId, player))
                refreshPartTimeBuilding(buildingId, player);
        });
        MessageUtils.send(player, NewBuildingMsgBuilder.getPartTimeJobList(player));
    }

    /** 刷新单个建筑的兼职 **/
    @SuppressWarnings("unchecked")
    @Override
    public void refreshPartTimeBuilding(int buildingId, Player player) {
        PartTimeManager manager = player.getPartTimeManager();
        PartTimeRecord ptRecord = manager.getPartRecord().get(buildingId);

        ArrayList<Integer> list = getJobList(buildingId, ptRecord.getJobLevel());
        ArrayList<JobRecord> jobList = new ArrayList<JobRecord>();
        // 获取当前兼职的长度
        int lenth = GameDataManager.getNewBuildCfgBean(buildingId).getJobNum();
        for (int i = 0; i < lenth; i++) {
            ArrayList<Integer> randomList = (ArrayList<Integer>) list.clone();
            BuildingGeneral.resetProbability(randomList, 2, 1);
            int index = BuildingGeneral.random(randomList, 2, 0, 1);
            JobRecord job = new JobRecord();
            job.setJobId(randomList.get(index));
            job.setJobType(BuildingConstant.PARTTIME_JOB_CAN_WORK);
            if (BuildingConstant.CUISINE_FORCE_DAY.length > i) {
                job.setType(BuildingConstant.CUISINE_FORCE_DAY[i]);
            } else {
                job.setType((int) (Math.random() * BuildingConstant.RANDOM_CUISINE));
            }
            jobList.add(job);
            // 移除当前元素
            list.remove(index);
            list.remove(index);
        }
        ptRecord.setJobList(jobList);
    }

    /**
     * 获取当前建筑的兼职列表
     * 
     * @param buildingId 建筑id
     * @param ptLevel 兼职等级
     * @return
     */
    @Override
    public ArrayList<Integer> getJobList(int buildingId, int ptLevel) {
        ArrayList<Integer> buildingList = new ArrayList<Integer>();
        List<CityJobCfgBean> beans = GameDataManager.getCityJobCfgBeans();
        for (CityJobCfgBean bean : beans) {
            int[] condition = bean.getCondition();
            if (condition[0] == buildingId && ptLevel >= condition[1]) {
                buildingList.add(bean.getId());
                buildingList.add(condition[2]);
            }
        }
        return buildingList;
    }

    /** 刷新 **/
    @Override
    public void refresh(Player player) {
        refreshPartTimeList(player);
    }

    /** 获取当前兼职列表中的 **/
    @Override
    public JobRecord getJobRecord(int buildingId, int jobId, Player player) {
        PartTimeRecord pRecord = player.getPartTimeManager().getPartRecord().get(buildingId);
        if (pRecord == null)
            return null;
        List<JobRecord> jobs = pRecord.getJobList();
        if (jobs == null)
            return null;
        for (JobRecord job : jobs) {
            if (job.getJobId() == jobId)
                return job;
        }
        return null;
    }

    /** 获取当前兼职列表中的 **/
    @SuppressWarnings("unchecked")
    @Override
    public Builder getAward(Player player, Logger LOGGER) {
        PartTimeManager manager = player.getPartTimeManager();
        if (manager.getJobEvent().getEtime() == 0)
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_JOB_IS_NOT_EXIST, "兼职任务不存在");
        if (manager.getJobEvent().getEtime() > System.currentTimeMillis())
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_JOB_IS_NOT_COMPLETE, "兼职没有完成");
        Map<Integer, Integer> gift = getGift(manager.getJobEvent().getJobId());
        player.getBagManager().addItems(gift, true, EReason.GIVE_BUILDING_PTJOB);
        CityJobCfgBean cityJobCfgBean =
                GameDataManager.getCityJobCfgBean(manager.getJobEvent().getJobId());
        int addExp = 0;
        if (cityJobCfgBean != null)
            addExp = cityJobCfgBean.getJobExp();
        addExp(addExp, manager.getJobEvent().getBuildingId(), player.getPartTimeManager());
        JobRecord jobRecord = getJobRecord(manager.getJobEvent().getBuildingId(),
                manager.getJobEvent().getJobId(), player);
        Map<Integer, Integer> extraGift = getExtraGift(gift, GameDataManager
                .getCityJobCfgBean(manager.getJobEvent().getJobId()).getResultShow1());
        if (jobRecord != null
                && jobRecord.getRefreshTime() == manager.getJobEvent().getRefreshTime()) {
            jobRecord.setExtraGift(extraGift);
            jobRecord.setGift(gift);
            jobRecord.setJobType(BuildingConstant.PARTTIME_JOB_SUCCESS);
        }

        int jobId = manager.getJobEvent().getJobId();
        int buildingId = manager.getJobEvent().getBuildingId();
        manager.getJobEvent().clear();

        // 城市兼职结束时间
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createCityGameLog(player,
                    EReason.PARTTIME_JOB_ENDTIME.value(), jobId));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
        trigger(buildingId, cityJobCfgBean, player);
        return NewBuildingMsgBuilder.packageIngJobAward(addExp, gift, extraGift, buildingId,
                manager.getPartRecord().get(buildingId));
    }

    /** 封装奖励 **/
    @SuppressWarnings("unchecked")
    @Override
    public Map<Integer, Integer> getGift(int jobId) {
        /** 奖励 **/
        Map<Integer, Integer> gift = new HashMap<Integer, Integer>();
        CityJobCfgBean cityJob = GameDataManager.getCityJobCfgBean(jobId);
        ItemPackageHelper.unpack(cityJob.getResult(), null, gift);
        return gift;
    }

    /** 获取额外的奖励 **/
    @Override
    public Map<Integer, Integer> getExtraGift(Map<Integer, Integer> gifts,
            Map<Integer, Integer> systemGift) {
        Map<Integer, Integer> extraGift = new HashMap<Integer, Integer>();
        Iterator<Entry<Integer, Integer>> iterator = gifts.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Integer, Integer> entry = iterator.next();
            if (entry != null) {
                if (systemGift.get(entry.getKey()) == null
                        || systemGift.get(entry.getKey()) != entry.getValue()) {
                    extraGift.put(entry.getKey(), entry.getValue());
                    iterator.remove();
                }
            }
        }
        return extraGift;
    }

    /** 增加经验 **/
    @Override
    public void addExp(int addExp, int buildingId, PartTimeManager manager) {
        if (addExp <= 0)
            return;
        NewBuildCfgBean newBuildCfgBean = GameDataManager.getNewBuildCfgBean(buildingId);
        int maxLevel = newBuildCfgBean.getJobLevels().length + 1;
        PartTimeRecord ptrecord = manager.getPartRecord().get(buildingId);
        if (ptrecord.getJobLevel() >= maxLevel)
            return;
        long nowExp = addExp + ptrecord.getExp();
        int levelUpExp = newBuildCfgBean.getJobLevels()[ptrecord.getJobLevel() - 1];
        if (levelUpExp <= nowExp) {
            ptrecord.setJobLevel(ptrecord.getJobLevel() + 1);
            ptrecord.setExp(nowExp - levelUpExp);
        } else {
            ptrecord.setExp(nowExp);
        }
        if (ptrecord.getJobLevel() >= maxLevel)
            ptrecord.setExp(0);

    }

    /** 检测 **/
    @Override
    public void checkUnlock(Player player) {
        Set<Integer> validbuilding = player.getNewBuildingManager().getValidbuilding();
        // 要刷新的建筑兼职
        Set<Integer> refreshBuildings = new HashSet<Integer>();
        validbuilding.forEach((buildingId) -> {
            NewBuildCfgBean newBuildCfgBean = GameDataManager.getNewBuildCfgBean(buildingId);
            if (player.getPartTimeManager().getPartRecord().get(buildingId) == null
                    && newBuildCfgBean.getJobNum() > 0) {
                PartTimeRecord ptrecord = new PartTimeRecord(buildingId, 1, 0);
                player.getPartTimeManager().getPartRecord().put(buildingId, ptrecord);
                refreshPartTimeBuilding(buildingId, player);
                refreshBuildings.add(buildingId);
            }
        });
        MessageUtils.send(player, NewBuildingMsgBuilder.getPartTimeJobList(player));
    }

    /** 解锁建筑 **/
    @SuppressWarnings("unchecked")
    @Override
    public void unlock(Object object, Player player) {
        if (object == null)
            return;
        Map<String, Object> param = (Map<String, Object>) object;
        if (param.get(BuildingConstant.EVENT_CONDITION_ID) == null)
            return;
        int id = (int) param.get(BuildingConstant.EVENT_CONDITION_ID);
        switch (id) {
            case BuildingConstant.REFRESH_ALL:
                Map<Integer, ArrayList<Integer>> map = (Map<Integer, ArrayList<Integer>>) param
                        .get(EventConditionKey.EVENT_RESULT_DATA);
                for (Map.Entry<Integer, ArrayList<Integer>> entry : map.entrySet()) {
                    ArrayList<Integer> list = entry.getValue();
                    if (list == null)
                        continue;
                    if (list.contains(BuildingConstant.UNLOCK_PT_JOB_TYPE)) {
                        checkUnlock(player);
                        break;
                    }
                }
                break;
            default:
                break;
        }

    }

    /** 检测当前建筑是否能够兼职 **/
    @Override
    public boolean checkBuilingFunId(int buildingId, Player player) {
        NewBuildCfgBean newBuildBean = GameDataManager.getNewBuildCfgBean(buildingId);
        if (newBuildBean == null)
            return false;
        Map<Integer, Set<Integer>> funIds = player.getNewBuildingManager().getValidFunIds();
        if (funIds == null || funIds.get(buildingId) == null)
            return false;
        Set<Integer> funs = funIds.get(buildingId);
        if (funs.contains(BuildingConstant.UNLOCK_PT_JOB_TYPE) && newBuildBean.getJobNum() > 0)
            return true;
        return false;
    }

    /** 放弃兼职 **/
    @SuppressWarnings("unchecked")
    @Override
    public void giveUpJob(Player player, Logger LOGGER) {
        PartTimeManager manager = player.getPartTimeManager();
        if (manager.getJobEvent().getEtime() <= 0)
            MessageUtils.throwCondtionError(GameErrorCode.BUILDING_JOB_IS_NOT_EXIST, "兼职任务不存在");
        JobRecord jobRecord = getJobRecord(manager.getJobEvent().getBuildingId(),
                manager.getJobEvent().getJobId(), player);
        if (jobRecord != null
                && jobRecord.getRefreshTime() == manager.getJobEvent().getRefreshTime()) {
            jobRecord.setJobType(BuildingConstant.PARTTIME_JOB_CAN_WORK);
        }
        CityJobCfgBean jobBean =
                GameDataManager.getCityJobCfgBean(manager.getJobEvent().getJobId());
        if (jobBean != null)
            player.getBagManager().addItems(jobBean.getCostVim(), true,
                    EReason.BUILDING_GIVEUP_JOB);

        int jobId = manager.getJobEvent().getJobId();
        manager.getJobEvent().clear();
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createCityGameLog(player,
                    EReason.PARTTIME_JOB_GIVETIME.value(), jobId));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
    }

    /** 跨天刷新 **/
    @Override
    public void acrossDay(EAcrossDayType type, boolean isNotify, Player player) {
        if (type == EAcrossDayType.GAME_ACROSS_DAY) {
            refresh(player);
        }

    }

    @Override
    public void trigger(int buildingId, CityJobCfgBean cityJobCfgBean, Player player) {
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.RECEIVE_WORK_REWARD_COUNT);
        in.put(EventConditionKey.BUILD_CID, buildingId);
        player._fireEvent(in, EEventType.BUILDING_EVENT.value());

        int second = cityJobCfgBean.getCostTime();
        in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.WORK_TIME);
        in.put(EventConditionKey.BUILD_CID, buildingId);
        in.put(EventConditionKey.WORK_TIME, second);
        player._fireEvent(in, EEventType.BUILDING_EVENT.value());
    }

}
