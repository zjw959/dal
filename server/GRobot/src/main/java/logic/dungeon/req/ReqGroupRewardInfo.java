package logic.dungeon.req;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import logic.dungeon.DungeonOrder;

import org.game.protobuf.c2s.C2SDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg.DungeonLevelGroupInfo;
import org.game.protobuf.s2c.S2CDungeonMsg.LevelInfo;
import org.game.protobuf.s2c.S2CShareMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.DungeonLevelCfgBean;
import data.bean.DungeonLevelGroupCfgBean;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.DUNGEON,
        order = DungeonOrder.REQ_GROUP_REWARD)
public class ReqGroupRewardInfo extends AbstractEvent {
    private static final String REWARD_SEPARATOR = "_";
    private static final int REWARD_BLANK_HEAD = 1;
    private static final int REWARD_INFO_ID = 0;
    private static final int REWARD_INFO_STAR = 1;
    private static final int REWARD_INFO_HEART = 2;
    private static final int DUNGEON_TYPE_GENERAL_DATING = 2;
    private static final int DUNGEON_TYPE_GENERAL_CITY_DATING = 3;

    public ReqGroupRewardInfo(RobotThread robot) {
        super(robot, S2CDungeonMsg.GetLevelGroupReward.MsgID.eMsgID_VALUE);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void action(Object... obj) throws Exception {
        // 计算星数
        Map<Integer, Map<Integer, Integer>> groupStars = new HashMap<>();
        DungeonLevelCfgBean levelCfg;
        for (LevelInfo info : robot.getPlayer().getDungeons().values()) {
            levelCfg = GameDataManager.getDungeonLevelCfgBean(info.getCid());
            if (!info.getWin())
                continue;
            int type;
            if (levelCfg.getDungeonType() == DUNGEON_TYPE_GENERAL_DATING
                    || levelCfg.getDungeonType() == DUNGEON_TYPE_GENERAL_CITY_DATING)
                type = REWARD_INFO_HEART;
            else
                type = REWARD_INFO_STAR;
            Map<Integer, Integer> previousMap = groupStars.get(levelCfg.getLevelGroupServerID());
            if (previousMap == null)
                groupStars.put(levelCfg.getLevelGroupServerID(), previousMap =
                        new HashMap<Integer, Integer>());
            previousMap.put(type, info.getGoalsCount() + previousMap.getOrDefault(type, 0));
        }
        // 可领取的
        DungeonLevelGroupCfgBean groupCfg;
        Map<Integer, List<Integer>> record;
        Map<Integer, Map<Integer, Set<String>>> validRecord =
                new HashMap<Integer, Map<Integer, Set<String>>>();
        for (DungeonLevelGroupInfo groupInfo : robot.getPlayer().getDungeonGroups().values()) {
            record = new HashMap<Integer, List<Integer>>();
            // 领取过的
            for (S2CShareMsg.ListMap rewardInfo : groupInfo.getRewardInfoList()) {
                if (rewardInfo.getListCount() <= 0)
                    continue;
                record.put(rewardInfo.getKey(), rewardInfo.getListList());
            }
            groupCfg = GameDataManager.getDungeonLevelGroupCfgBean(groupInfo.getCid());
            // 如果没有相应的奖励配置
            if (groupCfg.getReward() == null)
                continue;
            for (Map.Entry<Integer, Map> difct : ((Map<Integer, Map>) groupCfg.getReward())
                    .entrySet()) {
                for (String rewardInfo : ((Map<String, Map>) (difct.getValue())).keySet()) {
                    String starInfo = rewardInfo.substring(REWARD_BLANK_HEAD);
                    String[] starInfos = starInfo.split(REWARD_SEPARATOR);
                    int rewardId = Integer.valueOf(starInfos[REWARD_INFO_ID]);
                    int starNums = Integer.valueOf(starInfos[REWARD_INFO_STAR]);
                    int heartNums = Integer.valueOf(starInfos[REWARD_INFO_HEART]);
                    // 星数是否满足
                    Map<Integer, Integer> typeStar = groupStars.get(difct.getKey());
                    if (typeStar == null)
                        continue;
                    if (typeStar.getOrDefault(REWARD_INFO_HEART, 0) < heartNums
                            || typeStar.getOrDefault(REWARD_INFO_STAR, 0) < starNums)
                        continue;
                    // 是否领取过
                    List<Integer> rewardIds = record.get(difct.getKey());
                    if (rewardIds == null || !rewardIds.contains(rewardId)) {
                        Map<Integer, Set<String>> groupRecord = validRecord.get(groupCfg.getId());
                        if (groupRecord == null)
                            validRecord.put(groupCfg.getId(), groupRecord =
                                    new HashMap<Integer, Set<String>>());
                        Set<String> rewardStrIds = groupRecord.get(difct.getKey());
                        if (rewardStrIds == null)
                            groupRecord.put(difct.getKey(), rewardStrIds = new HashSet<String>());
                        rewardStrIds.add(rewardInfo);
                    }
                }
            }
        }
        if (validRecord.size() <= 0) {
            super.robotSkipRun();
            return;
        }
        boolean skipRun = true;
        int randomIndex = (int) (Math.random() * validRecord.size());
        for (Entry<Integer, Map<Integer, Set<String>>> entry : validRecord.entrySet()) {
            if (randomIndex-- > 0)
                continue;
            randomIndex = (int) (Math.random() * entry.getValue().size());
            for (Entry<Integer, Set<String>> dfctInfo : entry.getValue().entrySet()) {
                if (randomIndex-- > 0)
                    continue;
                randomIndex = (int) (Math.random() * dfctInfo.getValue().size());
                for (String rewardInfo : dfctInfo.getValue()) {
                    if (randomIndex-- > 0)
                        continue;
                    C2SDungeonMsg.GetLevelGroupReward.Builder build =
                            C2SDungeonMsg.GetLevelGroupReward.newBuilder();
                    build.setCid(entry.getKey());
                    build.setDifficulty(dfctInfo.getKey());
                    build.setStarNum(rewardInfo);
                    SMessage msg =
                            new SMessage(C2SDungeonMsg.GetLevelGroupReward.MsgID.eMsgID_VALUE,
                                    build.build().toByteArray(), resOrder);
                    sendMsg(msg);
                    skipRun = false;
                    break;
                }
                break;
            }
            break;
        }
        if (skipRun) {
            super.robotSkipRun();
            return;
        }
    }
}
