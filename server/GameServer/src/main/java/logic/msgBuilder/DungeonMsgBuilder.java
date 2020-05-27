package logic.msgBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import logic.character.bean.Player;
import logic.dungeon.DungeonManager;
import logic.dungeon.bean.DungeonBean;
import logic.dungeon.bean.DungeonGroupBean;
import logic.dungeon.scene.SingleDungeonScene;

import org.game.protobuf.s2c.S2CDungeonMsg;
import org.game.protobuf.s2c.S2CShareMsg;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import com.google.common.collect.Lists;

import data.GameDataManager;
import data.bean.DailyChapterMultipleCfgBean;

public class DungeonMsgBuilder {


    public static S2CDungeonMsg.FightStartMsg.Builder getSceneStartMsg(SingleDungeonScene scene) {
        S2CDungeonMsg.FightStartMsg.Builder builder = S2CDungeonMsg.FightStartMsg.newBuilder();
        builder.setLevelCid(scene.getSceneCfgId());

        if (scene.getHelpHero() != null) {
            builder.setHero(scene.getHelpHero());
        }

        builder.setHelpPid(scene.getHelpPid());
        builder.setFightId(scene.getSceneId());

        // 随机种子
        builder.setRandomSeed(scene.getRandomSeed());

        // 奖励消息
        List<RewardsMsg> rewards = Lists.newArrayList();
        if (scene.getRewards() != null) {
            scene.getRewards().forEach((k, v) -> {
                rewards.add(RewardsMsg.newBuilder().setId(k).setNum(v).build());
            });
        }
        builder.addAllRewards(rewards);
        builder.setIsDuelMod(scene.isDuel());
        return builder;
    }

    /**
     * 获取副本记录信息
     *
     * @param player 玩家对象
     */
    public static S2CDungeonMsg.GetLevelInfo.Builder getDungeonRecordMsg(Player player) {
        S2CDungeonMsg.GetLevelInfo.Builder builder = S2CDungeonMsg.GetLevelInfo.newBuilder();
        DungeonManager dm = player.getDungeonManager();
        List<DungeonBean> dungeonLevelList = new ArrayList<DungeonBean>(dm.getDungeons().values());
        if (dungeonLevelList != null && dungeonLevelList.size() > 0) {
            builder.setLevelInfos(getLevelInfoList(dungeonLevelList));
        }
        List<DungeonGroupBean> dungeonLevelGroupList =
                new ArrayList<DungeonGroupBean>(dm.getDungeonGroups().values());
        S2CDungeonMsg.RefreshDungeonLevelGroupList.Builder groupListBuilder =
                S2CDungeonMsg.RefreshDungeonLevelGroupList.newBuilder();
        dungeonLevelGroupList.forEach(group -> {
            groupListBuilder.addGroup(getDungeonLevelGroupInfo(group));
        });
        builder.setGroups(groupListBuilder);
        return builder;
    }

    public static S2CDungeonMsg.LevelInfos.Builder getLevelInfoList(List<DungeonBean> levelList) {
        S2CDungeonMsg.LevelInfos.Builder builder = S2CDungeonMsg.LevelInfos.newBuilder();
        levelList.forEach(level -> {
            builder.addLevelInfos(getLevelInfo(level));
        });
        return builder;
    }

    public static S2CDungeonMsg.LevelInfo getLevelInfo(DungeonBean level) {
        S2CDungeonMsg.LevelInfo.Builder levelInfoBuilder = S2CDungeonMsg.LevelInfo.newBuilder();
        levelInfoBuilder.setCid(level.getCid()).setWin(level.isWin())
                .setFightCount(level.getSceneCount());
        levelInfoBuilder.addAllGoals(level.getAchieveGoals());
        return levelInfoBuilder.build();
    }

    /**
     * 获取战斗失败消息
     */
    public static S2CDungeonMsg.FightOverMsg.Builder getSceneFailMsg(DungeonBean level) {
        S2CDungeonMsg.FightOverMsg.Builder builder = S2CDungeonMsg.FightOverMsg.newBuilder();
        builder.setLevelInfo(getLevelInfo(level)).setWin(false);
        return builder;
    }

    /**
     * 获取战斗胜利消息
     */
    public static S2CDungeonMsg.FightOverMsg.Builder getSceneWinMsg(DungeonBean level,
            List<S2CShareMsg.RewardsMsg> rewardsMsgs) {
        S2CDungeonMsg.FightOverMsg.Builder builder = S2CDungeonMsg.FightOverMsg.newBuilder();
        builder.setWin(true).setLevelInfo(getLevelInfo(level));
        if (rewardsMsgs != null && rewardsMsgs.size() > 0) {
            builder.addAllRewards(rewardsMsgs);
        }
        return builder;
    }

    public static S2CDungeonMsg.RefreshDungeonLevelGroupList.Builder getDungeonLevelGroupList(
            List<DungeonGroupBean> groupList) {
        S2CDungeonMsg.RefreshDungeonLevelGroupList.Builder builder =
                S2CDungeonMsg.RefreshDungeonLevelGroupList.newBuilder();
        groupList.forEach(group -> {
            builder.addGroup(getDungeonLevelGroupInfo(group));
        });
        return builder;
    }

    public static S2CDungeonMsg.DungeonLevelGroupInfo.Builder getDungeonLevelGroupInfo(
            DungeonGroupBean levelGroup) {
        S2CDungeonMsg.DungeonLevelGroupInfo.Builder builder =
                S2CDungeonMsg.DungeonLevelGroupInfo.newBuilder();
        builder.setId("").setCid(levelGroup.getCid()).setFightCount(levelGroup.getSceneCount())
                .setBuyCount(levelGroup.getBuyCount()).setMainLineCid(levelGroup.getLastMainLine())
                .setMaxMainLine(levelGroup.getMaxMainLine());
        levelGroup.getGetedReward().forEach(
                (difficulty, list) -> {
                    builder.addRewardInfo(ShareMsgBuilder.createListMap((Integer) difficulty,
                            new ArrayList<>(list)));
                });
        return builder;
    }

    /**
     * 获取活动副本更新信息
     */
    public static S2CDungeonMsg.UpdateActivityDungeon.Builder getUpdateActivityDungeonMsg(
            Map<Integer, List<Integer>> map) {
        S2CDungeonMsg.UpdateActivityDungeon.Builder builder =
                S2CDungeonMsg.UpdateActivityDungeon.newBuilder();
        // map.forEach((type, list) -> {
        // if (type == TimeFrameChecker.TIME_POINT_BEGIN) {
        // builder.addAllStartIds(list);
        // }
        // if (type == TimeFrameChecker.TIME_POINT_END) {
        // builder.addAllEndIds(list);
        // }
        // });
        return builder;
    }

    public static S2CDungeonMsg.GetLevelGroupReward.Builder getLevelGroupReward(int cid,
            int difficulty, int starNum, DungeonGroupBean levelGroup) {
        S2CDungeonMsg.GetLevelGroupReward.Builder builder =
                S2CDungeonMsg.GetLevelGroupReward.newBuilder();
        builder.setCid(cid).setDifficulty(difficulty).setStarNum(starNum);
        levelGroup.getGetedReward().forEach(
                (diff, list) -> builder.addRewardInfo(ShareMsgBuilder.createListMap((Integer) diff,
                        new ArrayList<>(list))));

        return builder;
    }

    public static S2CDungeonMsg.BuyFightCount.Builder buildBuySceneCountMsg(int cid) {
        S2CDungeonMsg.BuyFightCount.Builder builder = S2CDungeonMsg.BuyFightCount.newBuilder();
        builder.setCid(cid);
        return builder;
    }

    public static S2CDungeonMsg.UpdateLevelGroupInfo.Builder getUpdateLevelGroupInfo(
            DungeonGroupBean group) {
        S2CDungeonMsg.UpdateLevelGroupInfo.Builder builder =
                S2CDungeonMsg.UpdateLevelGroupInfo.newBuilder();
        builder.setGroup(getDungeonLevelGroupInfo(group));
        return builder;
    }

    public static S2CDungeonMsg.ProgressMsg.Builder getProgressMsg(DungeonGroupBean group) {
        S2CDungeonMsg.ProgressMsg.Builder builder = S2CDungeonMsg.ProgressMsg.newBuilder();
        builder.setGroup(getDungeonLevelGroupInfo(group));
        return builder;
    }

    public static S2CDungeonMsg.GroupMultipleRewardMsg.Builder getGroupMultipleRewardMsg(
            Player player) {
        S2CDungeonMsg.GroupMultipleRewardMsg.Builder builder =
                S2CDungeonMsg.GroupMultipleRewardMsg.newBuilder();
        DungeonManager dm = player.getDungeonManager();
        List<DungeonGroupBean> dungeonLevelGroupList =
                new ArrayList<DungeonGroupBean>(dm.getDungeonGroups().values());
        dungeonLevelGroupList.forEach(group -> {
            float multiple = 1f;
            // 关卡组多倍收益
                if (group.getMultipleRewardCount() > 0) {
                    // 如果是日常组
                    for (DailyChapterMultipleCfgBean multiCfg : GameDataManager
                            .getDailyChapterMultipleCfgBeans()) {
                        if (multiCfg == null)
                            continue;
                        // 规避配置顺序问题
                if (group.getMultipleRewardCount() >= multiCfg.getTimes()
                        && multiCfg.getRewardMultiple() > multiple)
                    multiple = multiCfg.getRewardMultiple();
            }
            S2CDungeonMsg.GroupMultipleInfo.Builder info =
                    S2CDungeonMsg.GroupMultipleInfo.newBuilder();
            info.setGroupId(group.getCid());
            info.setMultiple(Float.toString(multiple));
            builder.addMultipleInfo(info);
        }
    })  ;
        return builder;
    }
}
