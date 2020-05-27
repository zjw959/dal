package logic.msgBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.game.protobuf.s2c.S2CNewBuildingMsg;
import org.game.protobuf.s2c.S2CShareMsg;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import data.GameDataManager;
import data.bean.FoodbaseCfgBean;
import logic.character.bean.Player;
import logic.city.CityRoleManager;
import logic.city.NewBuildingManager;
import logic.city.build.BuildingConstant;
import logic.city.build.bean.BuildEvent;
import logic.city.build.bean.CityRoleRecord;
import logic.city.build.bean.CuisineEvent;
import logic.city.build.bean.JobEvent;
import logic.city.build.bean.JobRecord;
import logic.city.build.bean.ManualEvent;
import logic.city.build.bean.PartTimeRecord;
import logic.city.build.bean.PrizeClawRecord;
import utils.TimeUtil;

/***
 * 新城市建设
 * 
 * @author lihongji
 *
 */
public class NewBuildingMsgBuilder {

    /** 获取新城市建设的信息 **/
    public static S2CNewBuildingMsg.RespGetAllBuildingInfo.Builder getAllBuildingInfo(
            Player player) {
        NewBuildingManager newBuildingManager = player.getNewBuildingManager();
        CityRoleManager cityRoleManager = player.getCityRoleManager();
        S2CNewBuildingMsg.RespGetAllBuildingInfo.Builder builder =
                S2CNewBuildingMsg.RespGetAllBuildingInfo.newBuilder();
        for (Integer buildingId : newBuildingManager.getValidbuilding()) {
            S2CNewBuildingMsg.NewBuildingInfo.Builder info =
                    S2CNewBuildingMsg.NewBuildingInfo.newBuilder();
            info.setBuildingId(buildingId);
            packageBuildingFun(info, buildingId, newBuildingManager);
            builder.addBuildinginfos(info);
        }
        packageIngCityRole(cityRoleManager, builder);
        packingBuildingEvent(builder, newBuildingManager);
        builder.setDayType(newBuildingManager.getDayType());
        return builder;
    }

    /** 当前建筑解封的功能 **/
    public static void packageBuildingFun(S2CNewBuildingMsg.NewBuildingInfo.Builder info,
            int buildingId, NewBuildingManager manager) {
        Map<Integer, Set<Integer>> funIds = manager.getValidFunIds();
        if (funIds == null || funIds.get(buildingId) == null)
            return;
        for (Integer funId : funIds.get(buildingId)) {
            info.addBuildingFuns(funId);
        }
    }

    /** 精灵系统 **/
    public static void packageIngCityRole(CityRoleManager cityRoleManager,
            S2CNewBuildingMsg.RespGetAllBuildingInfo.Builder builder) {
        for (Map.Entry<Integer, ArrayList<CityRoleRecord>> entry : cityRoleManager.getCityRoles()
                .entrySet()) {
            for (CityRoleRecord room : entry.getValue()) {
                S2CNewBuildingMsg.RoleInRoom.Builder roleInRoom =
                        S2CNewBuildingMsg.RoleInRoom.newBuilder();
                roleInRoom.setBuildingId(room.getBuildingId());
                roleInRoom.setDressId(room.getDressId());
                roleInRoom.setLineId(room.getLineId());
                builder.addRoleInRooms(roleInRoom);
            }
        }
    }

    /** 提醒事件 **/
    public static void packingBuildingEvent(
            S2CNewBuildingMsg.RespGetAllBuildingInfo.Builder builder,
            NewBuildingManager newBuildingManager) {
        newBuildingManager.getBuildingEvent().forEach((Integer, buildEvents) -> {
            buildEvents.forEach(buildEvent -> {
                builder.addRemindEvents(packingEvent(buildEvent));
            });
        });
    }

    /** 封装buildevent **/
    public static S2CNewBuildingMsg.RemindEvent.Builder packingEvent(BuildEvent buildEvent) {
        S2CNewBuildingMsg.RemindEvent.Builder eventBuilder =
                S2CNewBuildingMsg.RemindEvent.newBuilder();
        eventBuilder.setBuildingId(buildEvent.getBuildingId());
        eventBuilder.setEventType(buildEvent.getEventType());
        eventBuilder.setExeId(buildEvent.getExeId());
        return eventBuilder.setFunId(buildEvent.getFunId());
    }


    /** 刷新 **/
    public static S2CNewBuildingMsg.RespUpdateBuildingInfo.Builder refreshUnlockBuilding(
            List<BuildEvent> buildingEvent, Set<Integer> buildings, Player player) {
        NewBuildingManager newBuildingManager = player.getNewBuildingManager();
        S2CNewBuildingMsg.RespUpdateBuildingInfo.Builder builder =
                S2CNewBuildingMsg.RespUpdateBuildingInfo.newBuilder();
        if (buildings != null)
            buildings.forEach((buildingId) -> {
                S2CNewBuildingMsg.NewBuildingInfo.Builder info =
                        S2CNewBuildingMsg.NewBuildingInfo.newBuilder();
                info.setBuildingId(buildingId);
                packageBuildingFun(info, buildingId, newBuildingManager);
                builder.addBuildinginfos(info);
            });
        if (buildingEvent != null)
            buildingEvent.forEach((buildEvent) -> {
                builder.addRemindEvents(packingEvent(buildEvent));
            });
        builder.setDayType(newBuildingManager.getDayType());
        return builder;
    }


    /** 刷新精灵系统 **/
    public static S2CNewBuildingMsg.RespUpdateBuildingInfo.Builder refreshCityRole(
            CityRoleManager cityRoleManager,List<BuildEvent> buildingEvent) {
        S2CNewBuildingMsg.RespUpdateBuildingInfo.Builder builder =
                S2CNewBuildingMsg.RespUpdateBuildingInfo.newBuilder();
        for (Map.Entry<Integer, ArrayList<CityRoleRecord>> entry : cityRoleManager.getCityRoles()
                .entrySet()) {
            for (CityRoleRecord room : entry.getValue()) {
                S2CNewBuildingMsg.RoleInRoom.Builder roleInRoom =
                        S2CNewBuildingMsg.RoleInRoom.newBuilder();
                roleInRoom.setBuildingId(room.getBuildingId());
                roleInRoom.setDressId(room.getDressId());
                roleInRoom.setLineId(room.getLineId());
                builder.addRoleInRooms(roleInRoom);
            }
        }
        if (buildingEvent != null)
            buildingEvent.forEach((buildEvent) -> {
                builder.addRemindEvents(packingEvent(buildEvent));
            });
        return builder;
    }


    /** 移除事件 **/
    public static S2CNewBuildingMsg.RespRemindSuccess.Builder removeEvent(Player player,
            int eventType) {
        S2CNewBuildingMsg.RespRemindSuccess.Builder builder =
                S2CNewBuildingMsg.RespRemindSuccess.newBuilder();
        player.getNewBuildingManager().removeEvent(eventType);
        builder.setIsSuccess(true);
        builder.setEventType(eventType);
        return builder;
    }


    /**刷新提醒事件**/
    public static S2CNewBuildingMsg.RespUpdateBuildingInfo.Builder addEvent(List<BuildEvent> buildingEvent){
        S2CNewBuildingMsg.RespUpdateBuildingInfo.Builder builder =
                S2CNewBuildingMsg.RespUpdateBuildingInfo.newBuilder();
        if (buildingEvent != null)
            buildingEvent.forEach((buildEvent) -> {
                builder.addRemindEvents(packingEvent(buildEvent));
            });
        return builder;
    }
    
    

    // ==========================================料理活动开始(有新增请在固定地方增加)==============================================================
    /**
     * 请求料理活动信息
     * 
     * @param player
     */
    public static S2CNewBuildingMsg.RespgetFoodbaseInfo.Builder packageIngCuisineInfo(
            Player player, boolean isClear) {
        S2CNewBuildingMsg.RespgetFoodbaseInfo.Builder builder =
                S2CNewBuildingMsg.RespgetFoodbaseInfo.newBuilder();
        CuisineEvent event = player.getCuisineManager().getEvent();
        FoodbaseCfgBean foodBasebean = GameDataManager.getFoodbaseCfgBean(event.getCuisineId());
        if (foodBasebean != null && foodBasebean.getDatingId() > 0) {
            isClear = true;
        }
        if (isClear) {
            event.clear();
        }
        if (event.getCuisineId() != 0) {
            S2CNewBuildingMsg.FoodbaseInfo.Builder foodBaseInfo =
                    S2CNewBuildingMsg.FoodbaseInfo.newBuilder();
            foodBaseInfo.setFoodId(event.getCuisineId());
            foodBaseInfo.setEndTime((int) (event.getEtime() / TimeUtil.SECOND));
            foodBaseInfo.setIntegral(event.getIntegral() + player.getCuisineManager().getNatureTime(
                    GameDataManager.getFoodbaseCfgBean(event.getCuisineId()), player));
            builder.setFoodbaseInfo(foodBaseInfo);
        }
        return builder;
    }

    /** 创建料理事件 **/
    public static S2CNewBuildingMsg.RespCookFoodbase.Builder getCuisineEvent(Player player) {
        S2CNewBuildingMsg.RespCookFoodbase.Builder builder =
                S2CNewBuildingMsg.RespCookFoodbase.newBuilder();
        CuisineEvent event = player.getCuisineManager().getEvent();
        builder.setEndTime((int) (event.getEtime() / TimeUtil.SECOND));
        return builder.setFoodId(event.getCuisineId());
    }

    /** 料理奖励封装 **/
    public static S2CNewBuildingMsg.RespGetFoodBaseAward.Builder packingCuiSineReward(int cuisineId,
            Map<Integer, Integer> award) {
        S2CNewBuildingMsg.RespGetFoodBaseAward.Builder builder =
                S2CNewBuildingMsg.RespGetFoodBaseAward.newBuilder();
        builder.setFoodId(cuisineId);
        award.forEach((gid, num) -> {
            builder.addRewards(RewardsMsg.newBuilder().setId(gid).setNum(num));
        });
        return builder;
    }

    /** Qte上传 **/
    public static S2CNewBuildingMsg.RespUploadQteIntegral.Builder packingQTEInfo(CuisineEvent event,
            int qteId) {
        S2CNewBuildingMsg.RespUploadQteIntegral.Builder builder =
                S2CNewBuildingMsg.RespUploadQteIntegral.newBuilder();
        builder.setFoodId(event.getCuisineId());
        builder.setIntegral(0);
        builder.setQteId(qteId);
        builder.setQteIntegral(event.getIntegral());
        return builder;
    }

    // ==========================================料理活动结束(有新增请在固定地方增加)==============================================================

    // ==========================================兼职任务开始(有新增请在固定地方增加)==============================================================


    /** 获取兼职任务列表 **/
    public static S2CNewBuildingMsg.RespPartTimeJobList.Builder getPartTimeJobList(Player player) {
        S2CNewBuildingMsg.RespPartTimeJobList.Builder builder =
                S2CNewBuildingMsg.RespPartTimeJobList.newBuilder();
        Map<Integer, PartTimeRecord> ptRecord = player.getPartTimeManager().getPartRecord();
        if (ptRecord == null)
            return builder;
        ptRecord.forEach((buildingId, record) -> {
            builder.addJobLists(packageIngJobList(record));
        });
        JobEvent event = player.getPartTimeManager().getJobEvent();
        if (event.getEtime() > 0) {
            S2CNewBuildingMsg.JobInfo.Builder eventJob = S2CNewBuildingMsg.JobInfo.newBuilder();
            eventJob.setBuildingId(event.getBuildingId());
            eventJob.setJobId(event.getJobId());
            eventJob.setEtime((int) (event.getEtime() / 1000));
            eventJob.setType(event.getType());
            eventJob.setJobType(BuildingConstant.PARTTIME_JOB_WORKING_ON);
            builder.setJobEvent(eventJob);
        }
        return builder;
    }

    /** 封装兼职列表条目 **/
    public static S2CNewBuildingMsg.JobInfoList.Builder packageIngJobList(PartTimeRecord record) {
        S2CNewBuildingMsg.JobInfoList.Builder builder = S2CNewBuildingMsg.JobInfoList.newBuilder();
        builder.setBuildingId(record.getBuildingId());
        builder.setExp(record.getExp());
        builder.setLevel(record.getJobLevel());
        record.getJobList().forEach((jobRecord) -> {
            builder.addJobInfos(packageIngJobInfo(jobRecord, record.getBuildingId()));
        });
        return builder;
    }

    /** 封装兼职列表信息 **/
    public static S2CNewBuildingMsg.JobInfo.Builder packageIngJobInfo(JobRecord record,
            int buildingId) {
        S2CNewBuildingMsg.JobInfo.Builder builder = S2CNewBuildingMsg.JobInfo.newBuilder();
        builder.setBuildingId(buildingId);
        builder.setType(record.getType());
        rewardInfo(record.getGift(), builder);
        rewardInfo(record.getExtraGift(), builder);
        builder.setJobId(record.getJobId());
        builder.setJobType(record.getJobType());
        return builder;
    }

    /** 领奖回调 **/
    public static S2CNewBuildingMsg.RespPartTimeJobAward.Builder packageIngJobAward(int addExp,
            Map<Integer, Integer> gift, Map<Integer, Integer> extraGift, int buildingId,
            PartTimeRecord record) {
        S2CNewBuildingMsg.RespPartTimeJobAward.Builder builder =
                S2CNewBuildingMsg.RespPartTimeJobAward.newBuilder();
        builder.setAddExp(addExp);
        gift.forEach((itemId, num) -> {
            builder.addRewards(packageIngAwardInfo(itemId, num));
        });
        extraGift.forEach((itemId, num) -> {
            builder.addExtraRewards(packageIngAwardInfo(itemId, num));
        });
        builder.setJobList(packageIngJobList(record));
        return builder;
    }


    /** 奖励信息 **/
    public static void rewardInfo(Map<Integer, Integer> award,
            S2CNewBuildingMsg.JobInfo.Builder jobInfo) {
        if (award == null)
            return;
        award.forEach((itemId, num) -> {
            jobInfo.addRewards(packageIngAwardInfo(itemId, num));
        });
    }

    public static S2CShareMsg.RewardsMsg.Builder packageIngAwardInfo(int itemId, int num) {
        S2CShareMsg.RewardsMsg.Builder builder = S2CShareMsg.RewardsMsg.newBuilder();
        builder.setId(itemId);
        builder.setNum(num);
        return builder;
    }



    /**放弃奖励**/
    public static S2CNewBuildingMsg.RespGiveUpJob.Builder giveUpJob(int buildingId, Player player) {
        S2CNewBuildingMsg.RespGiveUpJob.Builder builder =
                S2CNewBuildingMsg.RespGiveUpJob.newBuilder();
        PartTimeRecord ptRecord = player.getPartTimeManager().getPartRecord().get(buildingId);
        builder.setJobList(packageIngJobList(ptRecord));
        JobEvent event = player.getPartTimeManager().getJobEvent();
        if (event.getEtime() > 0) {
            S2CNewBuildingMsg.JobInfo.Builder eventJob = S2CNewBuildingMsg.JobInfo.newBuilder();
            eventJob.setBuildingId(eventJob.getBuildingId());
            eventJob.setJobId(eventJob.getJobId());
            eventJob.setJobType(eventJob.getJobType());
            eventJob.setEtime(eventJob.getEtime());
            builder.setJobEvent(eventJob);
        }
        return builder;
    }

    // ==========================================兼职任务结束(有新增请在固定地方增加)==============================================================



    /**
     * 构建抓娃娃信息
     *
     * @param PrizeClawRecord
     */
    public static S2CNewBuildingMsg.GetGashaponInfo buildGashaponInfoMsg(
            PrizeClawRecord prizeClawRecord) {

        S2CNewBuildingMsg.GetGashaponInfo.Builder builder =
                S2CNewBuildingMsg.GetGashaponInfo.newBuilder();
        builder.setCatchEndTime(prizeClawRecord.getEndTime());
        builder.setPollRefreshCdEndTime(prizeClawRecord.getRefreshCD());
        builder.setEggPool(prizeClawRecord.getPool());
        builder.setEggPoolId(prizeClawRecord.getPoolId());
        return builder.build();
    }



    /**
     * 构建抓娃娃结果验证响应信息
     */
    public static S2CNewBuildingMsg.RespCheckGashaponResult.Builder buildRespCheckGashaponResultMsg(
            String eggPool, List<Integer> eggIds, List<RewardsMsg> rewards) {
        S2CNewBuildingMsg.RespCheckGashaponResult.Builder builder =
                S2CNewBuildingMsg.RespCheckGashaponResult.newBuilder();
        builder.setEggPool(eggPool);
        builder.addAllEggIds(eggIds);
        builder.addAllRewards(rewards);
        return builder;
    }


    /**
     * 构建抓娃娃响应信息
     *
     * @param endTime
     */
    public static S2CNewBuildingMsg.RespStartGashapon.Builder buildRespStartGashaponMsg(long endTime) {
        S2CNewBuildingMsg.RespStartGashapon.Builder builder =
                S2CNewBuildingMsg.RespStartGashapon.newBuilder();
        builder.setCatchEndTime(endTime);
        return builder;
    }



    /**
     * 构建刷新抓娃娃蛋池响应信息
     */
    public static S2CNewBuildingMsg.RespRefreshGashaponPool.Builder buildRespRefreshGashaponPoolMsg(
            PrizeClawRecord record) {
        S2CNewBuildingMsg.RespRefreshGashaponPool.Builder builder =
                S2CNewBuildingMsg.RespRefreshGashaponPool.newBuilder();
        builder.setEggPoolId(record.getPoolId());
        builder.setEggPool(record.getPool());
        builder.setPollRefreshCdEndTime(record.getRefreshCD());
        return builder;
    }


    // ==========================================手工系统(有新增请在固定地方增加)==============================================================

    /**
     * 请求手工系统信息
     * 
     * @param player
     */
    public static S2CNewBuildingMsg.RespGetHandWorkInfo.Builder packageIngHandWorkInfo(
            Player player, boolean isClear) {
        S2CNewBuildingMsg.RespGetHandWorkInfo.Builder builder =
                S2CNewBuildingMsg.RespGetHandWorkInfo.newBuilder();
        ManualEvent event = player.getManualManager().getEvent();
        if (isClear) {
            event.clear();
        }
        if (event.getManualId() != 0) {
            S2CNewBuildingMsg.HandWorkInfo.Builder handWorkInfo =
                    S2CNewBuildingMsg.HandWorkInfo.newBuilder();
            handWorkInfo.setManualId(event.getManualId());
            handWorkInfo.setEndTime((int) (event.getEtime() / TimeUtil.SECOND));
            handWorkInfo.setIntegral(event.getIntegral() + player.getManualManager()
                    .getNatureTime(GameDataManager.getHandworkbaseCfgBean(event.getManualId())));
            builder.setHandWorkInfo(handWorkInfo);
        }
        return builder;
    }
    
    
    /** 手工奖励封装 **/
    public static S2CNewBuildingMsg.RespGetHandWorkAward.Builder packingHandWorkReward(int manualId,
            Map<Integer, Integer> award) {
        S2CNewBuildingMsg.RespGetHandWorkAward.Builder builder =
                S2CNewBuildingMsg.RespGetHandWorkAward.newBuilder();
        builder.setManualId(manualId);
        award.forEach((gid, num) -> {
            builder.addRewards(RewardsMsg.newBuilder().setId(gid).setNum(num));
        });
        return builder;
    }
    
    /** 创建手工事件 **/
    public static S2CNewBuildingMsg.RespDoHandWork.Builder getHandWorkEvent(Player player) {
        S2CNewBuildingMsg.RespDoHandWork.Builder builder =
                S2CNewBuildingMsg.RespDoHandWork.newBuilder();
        ManualEvent event = player.getManualManager().getEvent();
        builder.setManualId(event.getManualId());
        builder.setEndTime((int) (event.getEtime() / TimeUtil.SECOND));
        return builder;
    }
    
    
}
