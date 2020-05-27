package logic.robot.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityConfigMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityItemMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityProgressMsg;
import org.game.protobuf.s2c.S2CChasmMsg;
import org.game.protobuf.s2c.S2CCommentMsg.CommentInfo;
import org.game.protobuf.s2c.S2CDatingMsg.CityDatingInfo;
import org.game.protobuf.s2c.S2CDatingMsg.NotFinishDating;
import org.game.protobuf.s2c.S2CDatingMsg.UpdateTriggerDating;
import org.game.protobuf.s2c.S2CDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg.DungeonLevelGroupInfo;
import org.game.protobuf.s2c.S2CDungeonMsg.LevelInfo;
import org.game.protobuf.s2c.S2CEndlessCloisterMsg.EndlessCloisterInfo;
import org.game.protobuf.s2c.S2CFriendMsg.FriendInfo;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;
import org.game.protobuf.s2c.S2CItemMsg;
import org.game.protobuf.s2c.S2CItemMsg.EquipmentInfo;
import org.game.protobuf.s2c.S2CItemMsg.ItemList;
import org.game.protobuf.s2c.S2CMailMsg.MailInfo;
import org.game.protobuf.s2c.S2CNewBuildingMsg.HandWorkInfo;
import org.game.protobuf.s2c.S2CNewBuildingMsg.JobInfo;
import org.game.protobuf.s2c.S2CNewBuildingMsg.JobInfoList;
import org.game.protobuf.s2c.S2CNewBuildingMsg.NewBuildingInfo;
import org.game.protobuf.s2c.S2CNewBuildingMsg.RemindEvent;
import org.game.protobuf.s2c.S2CPlayerMsg;
import org.game.protobuf.s2c.S2CRoleMsg;
import org.game.protobuf.s2c.S2CRoleMsg.RoleInfo;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CStoreMsg.StoreCommodityBuyInfo;
import org.game.protobuf.s2c.S2CStoreMsg.StoreInfo;
import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;
import org.game.protobuf.s2c.S2CTeamMsg;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.DungeonLevelCfgBean;

/**
 * @function 机器人玩家实例(数据)
 */
public class RobotPlayer {
    private RobotThread robot;
    /** 玩家信息 */
    private S2CPlayerMsg.PlayerInfo playerInfo;
    /** 服务器时间 */
    private int serverTime;
    /** 道具 */
    private Map<Integer, Map<String, S2CItemMsg.ItemInfo>> itemKV = new HashMap<>();
    /** 时装 */
    private Map<Integer, Map<String, S2CItemMsg.DressInfo>> dressKV = new HashMap<>();
    /** 灵装 */
    private Map<String, S2CItemMsg.EquipmentInfo> equipmentKV = new HashMap<>();
    /** 看板娘 */
    private Map<Integer, S2CRoleMsg.RoleInfo> roleMap = new HashMap<>();

    private int prayScore;
    private Map<Integer, Integer> composeInfos = new HashMap<>();

    private Map<Integer, HeroInfo> herosInfoKV = new HashMap<>();
    /** 任务列表 */
    private Map<Integer, Map<Integer, TaskInfo>> taskInfoKV = new HashMap<>();
    /** 商店信息 */
    private Map<Integer, StoreInfo> storeInfoKV = new HashMap<>();
    /** 购买记录 */
    private Map<Integer, StoreCommodityBuyInfo> buyHistoryKV = new HashMap<>();

    private Map<Integer, FriendInfo> friends = new HashMap<>();

    private Map<Integer, FriendInfo> recommendFriends = new HashMap<>();
    /** 邮件 */
    private Map<String, MailInfo> mails = new HashMap<>();
    /** 关卡信息 */
    private Map<Integer, LevelInfo> dungeons = new HashMap<>();
    /** 关卡组信息 */
    private Map<Integer, DungeonLevelGroupInfo> dungeonGroups = new HashMap<>();
    /** 建筑-功能 **/
    private Map<Integer, NewBuildingInfo> newBuildingInfos =
            new HashMap<Integer, NewBuildingInfo>();
    /** 提醒信息 **/
    private List<RemindEvent> reminds = new ArrayList<RemindEvent>();
    /** 兼职列表 **/
    private List<JobInfoList> jobInfoList = new ArrayList<>();
    /** 正在兼职中的任务 **/
    private JobInfo jobEvent;
    /** 白天还是黑夜 **/
    private int dayType;
    /** 料理数据 **/
    // 制作的料理的id
    private int foodId;
    // 料理结束时间
    private int endTime;
    // 料理积分
    private int integral;
    /** 签到活动 **/
    private List<ActivityInfoMsg> signList = new ArrayList<ActivityInfoMsg>();
    /** 未完成的剧本 **/
    private List<NotFinishDating> notFinishDating = new ArrayList<NotFinishDating>();
    // 已经通过的剧本id
    private List<Integer> ruleCidList = new ArrayList<Integer>();
    // 剧本结束节点
    private List<Integer> endings = new ArrayList<Integer>();
    // 城市约会信息列表（原预定约会列表）
    private List<CityDatingInfo> cityDatingInfoList = new ArrayList<CityDatingInfo>();
    // 触发约会
    private List<UpdateTriggerDating> triggerDating = new ArrayList<UpdateTriggerDating>();
    // //正在约会的节点
    // private List<org.game.protobuf.s2c.S2CDatingMsg.BranchNode> branchNodeList;
    // 正在约会的datingRuleId
    private int datingRuleCid;
    // 约会的节点
    private int datingNodeId;
    /** 约会唯一Id **/
    private long datingId;
    /** 手工信息 **/
    private HandWorkInfo handWorkInfo;
    /** 精灵列表 **/
    private List<RoleInfo> roles = new ArrayList<RoleInfo>();

    /** 无尽回廊 */
    private EndlessCloisterInfo endlessInfo;
    private int endlessNowStage;

    /** 深渊(组队副本) */
    private Map<Integer, S2CChasmMsg.ChasmInfo> chasmInfos = new HashMap<>();
    /** 队伍信息 */
    private S2CTeamMsg.TeamInfo teamInfo;
    /** 战斗房间id */
    public String fightId;
    /** 随机数 */
    public int randomSeed;

    // 深渊(组队副本)临时变量
    public int composeHeroOne = 110201;
    public boolean isComposeHeroOne = false;
    public int composeHeroTwo = 110301;
    public boolean isComposeHeroTwo = false;
    // 是否创建队伍
    public boolean isCreateTeam = false;
    // 深渊关卡
    public int chasmDungeonId;

    // 精灵功能的临时变量
    // 合成精灵id
    public int composeHeroId;
    // 升阶精灵id
    public int upQualityHeroId;
    // 升级精灵id
    public int upgradeHeroId;
    // 升级道具
    public int heroExpItem;
    // 换皮服精灵id
    public int changeSkinHeroId;
    // 换皮服id
    public int changeSkinId;

    // 召唤功能临时变量
    // 哪种召唤
    public int summonId;
    // 召唤消耗
    public int summonCost;
    // 合成id
    public int composeId;
    // 召唤得到的奖励
    public List<ItemList> summonAward = null;
    // 是否是召唤得到的奖励
    public boolean isSummonAward = false;

    // 天使树临时变量
    public int operateHeroId;
    // 觉醒天使树
    public int awakeHeroId;
    // 天使页
    public int skillPageId;
    // 技能类型
    public int type;
    // 技能位置
    public int pos;
    // 被动技能id
    public int passiveSkill;
    // 结晶稀有度
    public int crystalRarity = 1;
    // 结晶格子
    public int crystalCell;
    // 关卡缓存id
    public int dungeonLevelId;

    // 主线约会
    public List<Integer> entrances = new ArrayList<Integer>();
    public int chooseEntranceId = 0;
    public List<Integer> choices = new ArrayList<Integer>();
    public int choiceId = 0;

    // 礼包码临时变量
    public long giftSendTime;

    private boolean isLogin;

    private Map<Integer, List<CommentInfo>> hotMap = new HashMap<>();
    private Map<Integer, List<CommentInfo>> newMap = new HashMap<>();

    /** 活动信息 **/
    public List<ActivityConfigMsg> activitys = new ArrayList<>();
    /** 条目信息 **/
    public List<ActivityItemMsg> activityItems = new ArrayList<>();
    /** 玩家记录信息 **/
    public List<ActivityProgressMsg> activityRecords = new ArrayList<>();

    public RobotPlayer(RobotThread robotThread) {
        this.robot = robotThread;
    }

    public RobotThread getRobot() {
        return robot;
    }

    public void updateItems(S2CItemMsg.ItemList resItems) {
        List<S2CItemMsg.ItemInfo> itemInfos = resItems.getItemsList();
        List<S2CItemMsg.EquipmentInfo> equipmentInfos = resItems.getEquipmentsList();
        List<S2CItemMsg.DressInfo> dressInfos = resItems.getDresssList();
        for (S2CItemMsg.ItemInfo itemInfo : itemInfos) {
            if (itemInfo.getCt() == ChangeType.DELETE) {
                if (itemKV.containsKey(itemInfo.getCid())) {
                    Map<String, S2CItemMsg.ItemInfo> items = itemKV.get(itemInfo.getCid());
                    String entityId = itemInfo.getId();
                    items.remove(entityId);
                }
            } else if (itemInfo.getCt() == ChangeType.ADD) {
                if (itemKV.containsKey(itemInfo.getCid())) {
                    Map<String, S2CItemMsg.ItemInfo> items = itemKV.get(itemInfo.getCid());
                    String entityId = itemInfo.getId();
                    if (Long.parseLong(entityId) > Integer.MAX_VALUE) {
                        items.put(entityId, itemInfo);
                    } else {
                        S2CItemMsg.ItemInfo item = items.get(entityId);
                        S2CItemMsg.ItemInfo.Builder itemInfoBuilder =
                                S2CItemMsg.ItemInfo.newBuilder(item);
                        itemInfoBuilder.setNum(itemInfoBuilder.getNum() + itemInfo.getNum());
                        items.put(entityId, itemInfoBuilder.build());
                    }
                } else {
                    Map<String, S2CItemMsg.ItemInfo> iteminfoList = new HashMap<>();
                    iteminfoList.put(itemInfo.getId(), itemInfo);
                    itemKV.put(itemInfo.getCid(), iteminfoList);
                }
            } else if (itemInfo.getCt() == ChangeType.UPDATE) {
                if (itemKV.containsKey(itemInfo.getCid())) {
                    Map<String, S2CItemMsg.ItemInfo> items = itemKV.get(itemInfo.getCid());
                    String entityId = itemInfo.getId();
                    if (Long.parseLong(entityId) > Integer.MAX_VALUE) {
                        items.put(itemInfo.getId(), itemInfo);
                    } else {
                        S2CItemMsg.ItemInfo item = items.get(entityId);
                        S2CItemMsg.ItemInfo.Builder itemInfoBuilder =
                                S2CItemMsg.ItemInfo.newBuilder(item);
                        itemInfoBuilder.setNum(itemInfo.getNum());
                        items.put(entityId, itemInfoBuilder.build());
                    }
                } else {
                    Map<String, S2CItemMsg.ItemInfo> iteminfoList = new HashMap<>();
                    iteminfoList.put(itemInfo.getId(), itemInfo);
                    itemKV.put(itemInfo.getCid(), iteminfoList);
                }
            } else if (itemInfo.getCt() == ChangeType.DEFAULT) {
                if (itemKV.containsKey(itemInfo.getCid())) {
                    Map<String, S2CItemMsg.ItemInfo> iteminfoList = itemKV.get(itemInfo.getCid());
                    iteminfoList.put(itemInfo.getId(), itemInfo);
                } else {
                    Map<String, S2CItemMsg.ItemInfo> iteminfoList = new HashMap<>();
                    iteminfoList.put(itemInfo.getId(), itemInfo);
                    itemKV.put(itemInfo.getCid(), iteminfoList);
                }
            }
        }

        for (S2CItemMsg.DressInfo dressInfo : dressInfos) {
            if (dressInfo.getCt() == ChangeType.DELETE) {
                if (dressKV.containsKey(dressInfo.getCid())) {
                    dressKV.remove(dressInfo.getCid());
                }
            } else {
                if (dressKV.containsKey(dressInfo.getCid())) {
                    Map<String, S2CItemMsg.DressInfo> dressInfoList =
                            dressKV.get(dressInfo.getCid());
                    dressInfoList.put(dressInfo.getId(), dressInfo);
                } else {
                    Map<String, S2CItemMsg.DressInfo> dressInfoList = new HashMap<>();
                    dressInfoList.put(dressInfo.getId(), dressInfo);
                    dressKV.put(dressInfo.getCid(), dressInfoList);
                }
            }
        }

        for (S2CItemMsg.EquipmentInfo equipmentInfo : equipmentInfos) {
            if (equipmentInfo.getCt() == ChangeType.DELETE) {
                if (equipmentKV.containsKey(equipmentInfo.getId())) {
                    equipmentKV.remove(equipmentInfo.getId());
                }
            } else if (equipmentInfo.getCt() == ChangeType.UPDATE) {
                EquipmentInfo info = equipmentKV.get(equipmentInfo.getId());
                if (info == null) {
                    return;
                }
                EquipmentInfo.Builder builder = EquipmentInfo.newBuilder(info);
                builder.setHeroId(equipmentInfo.getHeroId());
                builder.setPosition(equipmentInfo.getPosition());
                builder.setExp(equipmentInfo.getExp());
                builder.setLevel(equipmentInfo.getLevel());
                equipmentKV.put(equipmentInfo.getId(), builder.build());
            } else {
                equipmentKV.put(equipmentInfo.getId(), equipmentInfo);
            }
        }
    }

    public String getEntityId(int cid) {
        Map<String, S2CItemMsg.ItemInfo> itemInfoMap = itemKV.get(cid);
        if (itemInfoMap != null) {
            for (Map.Entry<String, S2CItemMsg.ItemInfo> entry : itemInfoMap.entrySet()) {
                S2CItemMsg.ItemInfo dressInfo = entry.getValue();
                return dressInfo.getId();
            }
        }
        return null;
    }

    /**
     * 是否有足够的道具
     * 
     * @return
     */
    public boolean isEnoughItem(Map<Integer, Integer> items) {
        for (Map.Entry<Integer, Integer> entry1 : items.entrySet()) {
            int key = entry1.getKey();
            int value = entry1.getValue();
            long num = 0;
            if (itemKV.containsKey(key)) {
                Map<String, S2CItemMsg.ItemInfo> itemInfoKV = itemKV.get(key);
                for (Map.Entry<String, S2CItemMsg.ItemInfo> entry2 : itemInfoKV.entrySet()) {
                    num = num + entry2.getValue().getNum();
                }
            } else if (dressKV.containsKey(key)) {
                // TODO
            } else if (equipmentKV.containsKey(key)) {
                // TODO
            }
            if (num < value) {
                return false;
            }
        }
        return true;
    }

    public long getItemCount(int key) {
        long num = 0;
        if (itemKV.containsKey(key)) {
            Map<String, S2CItemMsg.ItemInfo> itemInfoKV = itemKV.get(key);
            for (Map.Entry<String, S2CItemMsg.ItemInfo> entry2 : itemInfoKV.entrySet()) {
                num = num + entry2.getValue().getNum();
            }
        }
        return num;
    }

    public double getPrayScore() {
        return prayScore;
    }

    public void setPrayScore(int prayScore) {
        this.prayScore = prayScore;
    }

    public Map<Integer, Integer> getComposeInfos() {
        return composeInfos;
    }

    public Map<Integer, HeroInfo> getHeros() {
        return herosInfoKV;
    }

    public void putHero(HeroInfo heroInfo) {
        herosInfoKV.put(heroInfo.getCid(), heroInfo);
    }

    public Map<Integer, FriendInfo> getFriendInfos() {
        return friends;
    }

    public void putFriend(FriendInfo friend) {
        friends.put(friend.getPid(), friend);
    }

    public Map<String, MailInfo> getMails() {
        return mails;
    }

    public Map<Integer, FriendInfo> getRecommendFriendInfos() {
        return recommendFriends;
    }

    public void putRecommendFriend(FriendInfo friend) {
        recommendFriends.put(friend.getPid(), friend);
    }

    public S2CPlayerMsg.PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(S2CPlayerMsg.PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }

    public int getServerTime() {
        return serverTime;
    }

    public void setServerTime(int serverTime) {
        this.serverTime = serverTime;
    }


    public Map<String, S2CItemMsg.EquipmentInfo> getEquipmentKV() {
        return equipmentKV;
    }

    public void updateEquipInfo(int cid, String id, EquipmentInfo equipmentInfo) {
        if (equipmentInfo.getCt() == ChangeType.DELETE) {
            if (equipmentKV.containsKey(equipmentInfo.getId())) {
                equipmentKV.remove(equipmentInfo.getId());
            }
        } else if (equipmentInfo.getCt() == ChangeType.UPDATE) {
            EquipmentInfo info = equipmentKV.get(equipmentInfo.getId());
            if (info == null) {
                return;
            }
            EquipmentInfo.Builder builder = EquipmentInfo.newBuilder(info);
            builder.setHeroId(equipmentInfo.getHeroId());
            builder.setPosition(equipmentInfo.getPosition());
            builder.setExp(equipmentInfo.getExp());
            builder.setLevel(equipmentInfo.getLevel());
            equipmentKV.put(equipmentInfo.getId(), builder.build());
        } else {
            equipmentKV.put(equipmentInfo.getId(), equipmentInfo);
        }
    }

    public Map<Integer, Map<Integer, TaskInfo>> getTaskInfoKV() {
        return taskInfoKV;
    }

    public void updateTask(List<TaskInfo> infoList) {
        for (TaskInfo info : infoList) {
            Map<Integer, TaskInfo> map = taskInfoKV.get(info.getStatus());
            if (map == null) {
                map = new HashMap<>();
                taskInfoKV.put(info.getStatus(), map);
            }
            map.put(info.getCid(), info);
            for (int key : taskInfoKV.keySet()) {
                if (key == info.getStatus()) {
                    continue;
                }
                if (taskInfoKV.get(key).containsKey(info.getCid())) {
                    taskInfoKV.get(key).remove(info.getCid());
                }
            }
        }
    }

    public Map<Integer, StoreInfo> getStoreInfoKV() {
        return storeInfoKV;
    }

    public Map<Integer, StoreCommodityBuyInfo> getBuyHistoryKV() {
        return buyHistoryKV;
    }

    public void updateStroe(List<StoreInfo> storeList) {
        for (StoreInfo storeInfo : storeList) {
            storeInfoKV.put(storeInfo.getCid(), storeInfo);
        }
    }

    public void updateBuyHistory(List<StoreCommodityBuyInfo> buyList) {
        for (StoreCommodityBuyInfo info : buyList) {
            buyHistoryKV.put(info.getCid(), info);
        }
    }

    public void updateDungeonInfos(S2CDungeonMsg.GetLevelInfo infos) {
        // 单个关卡
        if (infos.hasLevelInfos()) {
            for (LevelInfo level : infos.getLevelInfos().getLevelInfosList()) {
                dungeons.put(level.getCid(), level);
            }
        }
        // 关卡组
        if (infos.hasGroups()) {
            for (DungeonLevelGroupInfo group : infos.getGroups().getGroupList()) {
                dungeonGroups.put(group.getCid(), group);
            }
        }
        resetDungeonTempLevel();
    }

    public void resetDungeonTempLevel() {
        // 当前可用
        List<DungeonLevelCfgBean> tempInfos = new LinkedList<>();
        for (DungeonLevelCfgBean tempCfg : GameDataManager.getDungeonLevelCfgBeans()) {
            if (playerInfo.getLvl() < tempCfg.getPlayerLv())
                continue;
            boolean need2add = true;
            if (tempCfg.getPreLevelId() != null) {
                for (int preId : tempCfg.getPreLevelId()) {
                    if (dungeons.containsKey(preId) && dungeons.get(preId).getWin())
                        continue;
                    need2add = false;
                    break;
                }
            }
            if (need2add) {
                tempInfos.add(tempCfg);
            }
        }
        for (DungeonLevelCfgBean dungeonLevelCfgBean : tempInfos) {
            if (dungeonLevelCfgBean.getLevelGroupServerID() <= 0)
                continue;
            dungeons.put(dungeonLevelCfgBean.getId(), LevelInfo.newBuilder()
                    .setCid(dungeonLevelCfgBean.getId()).setFightCount(0).setWin(false).build());
            if (!dungeonGroups.containsKey(dungeonLevelCfgBean.getLevelGroupServerID())) {
                dungeonGroups.put(dungeonLevelCfgBean.getLevelGroupServerID(),
                        DungeonLevelGroupInfo.newBuilder()
                                .setCid(dungeonLevelCfgBean.getLevelGroupServerID())
                                .setFightCount(0).setId("").setMainLineCid(0).setMaxMainLine(0)
                                .setBuyCount(0).build());
            }
        }
    }

    public Map<Integer, LevelInfo> getDungeons() {
        return dungeons;
    }

    public Map<Integer, DungeonLevelGroupInfo> getDungeonGroups() {
        return dungeonGroups;
    }

    public int getDungeonLevelId() {
        return dungeonLevelId;
    }

    public void setDungeonLevelId(int dungeonLevelId) {
        this.dungeonLevelId = dungeonLevelId;
    }


    public void updateNewBuildingInfo(List<NewBuildingInfo> infos) {
        for (NewBuildingInfo info : infos) {
            newBuildingInfos.put(info.getBuildingId(), info);
        }

    }

    /** 增加建筑事件 **/
    public void addRemindEvent(RemindEvent remind) {
        reminds.add(remind);
    }


    /** 移除返回的移除列表 **/
    public void descRemindEvent(List<RemindEvent> removeList) {

        List<RemindEvent> newList = new ArrayList<RemindEvent>();

        if (reminds.size() <= 0 || removeList == null || removeList.size() <= 0)
            return;
        reminds.forEach((event -> {
            if (!removeList.contains(event))
                newList.add(event);
        }));
        reminds = newList;
    }

    /** 通过类型获取活动信息 **/
    public ActivityInfoMsg getActivityByType(int type) {
        for (ActivityInfoMsg activity : signList) {
            if (activity.getActivityType() == type)
                return activity;
        }
        return null;
    }


    public void updateChasmInfo(List<S2CChasmMsg.ChasmInfo> chasmInfoList) {
        for (S2CChasmMsg.ChasmInfo chasmInfo : chasmInfoList) {
            chasmInfos.put(chasmInfo.getId(), chasmInfo);
        }
    }

    public Map<Integer, S2CChasmMsg.ChasmInfo> getChasmInfos() {
        return chasmInfos;
    }

    public void setTeamInfo(S2CTeamMsg.TeamInfo teamInfo) {
        this.teamInfo = teamInfo;
    }

    public S2CTeamMsg.TeamInfo getTeamInfo() {
        return teamInfo;
    }

    public long getGiftSendTime() {
        return giftSendTime;
    }

    public void setGiftSendTime(long giftSendTime) {
        this.giftSendTime = giftSendTime;
    }

    public List<RemindEvent> getReminds() {
        return reminds;
    }

    public void setReminds(List<RemindEvent> reminds) {
        this.reminds = reminds;
    }

    public List<JobInfoList> getJobInfoList() {
        return jobInfoList;
    }

    public void setJobInfoList(List<JobInfoList> jobInfoList) {
        this.jobInfoList = jobInfoList;
    }

    public JobInfo getJobEvent() {
        return jobEvent;
    }

    public void setJobEvent(JobInfo jobEvent) {
        this.jobEvent = jobEvent;
    }

    public int getDayType() {
        return dayType;
    }

    public void setDayType(int dayType) {
        this.dayType = dayType;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getIntegral() {
        return integral;
    }

    public void setIntegral(int integral) {
        this.integral = integral;
    }

    public List<ActivityInfoMsg> getSignList() {
        return signList;
    }

    public void setSignList(List<ActivityInfoMsg> signList) {
        this.signList = signList;
    }

    public List<NotFinishDating> getNotFinishDating() {
        return notFinishDating;
    }

    public void setNotFinishDating(List<NotFinishDating> notFinishDating) {
        this.notFinishDating = notFinishDating;
    }

    public List<Integer> getEndings() {
        return endings;
    }

    public void setEndings(List<Integer> endings) {
        this.endings = endings;
    }

    public List<CityDatingInfo> getCityDatingInfoList() {
        return cityDatingInfoList;
    }

    public void setCityDatingInfoList(List<CityDatingInfo> cityDatingInfoList) {
        this.cityDatingInfoList = cityDatingInfoList;
    }

    public List<UpdateTriggerDating> getTriggerDating() {
        return triggerDating;
    }

    public void setTriggerDating(List<UpdateTriggerDating> triggerDating) {
        this.triggerDating = triggerDating;
    }

    public List<Integer> getEntrances() {
        return entrances;
    }

    public void setEntrances(List<Integer> entrances) {
        this.entrances = entrances;
    }

    public int getChooseEntranceId() {
        return chooseEntranceId;
    }

    public void setChooseEntranceId(int chooseEntranceId) {
        this.chooseEntranceId = chooseEntranceId;
    }

    public List<Integer> getChoices() {
        return choices;
    }

    public void setChoices(List<Integer> choices) {
        this.choices = choices;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public boolean getIsLogin() {
        return this.isLogin;
    }

    public EndlessCloisterInfo getEndlessInfo() {
        return endlessInfo;
    }

    public void setEndlessInfo(EndlessCloisterInfo endlessInfo) {
        this.endlessInfo = endlessInfo;
    }

    public int getEndlessNowStage() {
        return endlessNowStage;
    }

    public void setEndlessNowStage(int endlessNowStage) {
        this.endlessNowStage = endlessNowStage;
    }

    public HandWorkInfo getHandWorkInfo() {
        return handWorkInfo;
    }

    public void setHandWorkInfo(HandWorkInfo handWorkInfo) {
        this.handWorkInfo = handWorkInfo;
    }

    public List<RoleInfo> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleInfo> roles) {
        this.roles = roles;
    }

    // public List<org.game.protobuf.s2c.S2CDatingMsg.BranchNode> getBranchNodeList() {
    // return branchNodeList;
    // }
    //
    // public void setBranchNodeList(List<org.game.protobuf.s2c.S2CDatingMsg.BranchNode>
    // branchNodeList) {
    // this.branchNodeList = branchNodeList;
    // }

    public List<Integer> getRuleCidList() {
        return ruleCidList;
    }

    public void setRuleCidList(List<Integer> ruleCidList) {
        this.ruleCidList = ruleCidList;
    }

    public int getDatingRuleCid() {
        return datingRuleCid;
    }

    public void setDatingRuleCid(int datingRuleCid) {
        this.datingRuleCid = datingRuleCid;
    }

    public int getDatingNodeId() {
        return datingNodeId;
    }

    public void setDatingNodeId(int datingNodeId) {
        this.datingNodeId = datingNodeId;
    }

    public Map<Integer, S2CRoleMsg.RoleInfo> getRoleMap() {
        return roleMap;
    }

    public void setRoleMap(Map<Integer, S2CRoleMsg.RoleInfo> roleMap) {
        this.roleMap = roleMap;
    }

    public void updateRoleMsg(List<S2CRoleMsg.RoleInfo> infos) {
        for (S2CRoleMsg.RoleInfo role : infos) {
            if (role.getCt() == ChangeType.DELETE) {
                roleMap.remove(role.getCid());
            } else if (role.getCt() == ChangeType.UPDATE) {
                S2CRoleMsg.RoleInfo temp = roleMap.get(role.getCid());
                if (temp == null) {
                    return;
                }
                roleMap.put(role.getCid(), role);
            } else {
                roleMap.put(role.getCid(), role);
            }
        }
    }

    public long getDatingId() {
        return datingId;
    }

    public void setDatingId(long datingId) {
        this.datingId = datingId;
    }

    public Map<Integer, List<CommentInfo>> getHotComment() {
        return hotMap;
    }

    public void setHotComment(Map<Integer, List<CommentInfo>> hotMap) {
        this.hotMap = hotMap;

    }

    public Map<Integer, List<CommentInfo>> getNewComment() {
        return newMap;
    }

    public void setNewComment(Map<Integer, List<CommentInfo>> newMap) {
        this.newMap = newMap;
    }

    public List<ActivityConfigMsg> getActivitys() {
        return activitys;
    }

    public void setActivitys(List<ActivityConfigMsg> activitys) {
        this.activitys = activitys;
    }

    public List<ActivityItemMsg> getActivityItems() {
        return activityItems;
    }

    public void setActivityItems(List<ActivityItemMsg> activityItems) {
        this.activityItems = activityItems;
    }

    public List<ActivityProgressMsg> getActivityRecords() {
        return activityRecords;
    }

    public void setActivityRecords(List<ActivityProgressMsg> activityRecords) {
        this.activityRecords = activityRecords;
    }
    

}
