package javascript.logic.dungeon;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import logic.bag.BagManager;
import logic.character.PlayerViewService;
import logic.character.bean.Player;
import logic.constant.DiscreteDataID;
import logic.constant.DiscreteDataKey;
import logic.constant.DungeonConstant;
import logic.constant.DungeonTypeConstant;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.GameErrorCode;
import logic.constant.ItemConstantId;
import logic.dungeon.DungeonManager;
import logic.dungeon.bean.ConfigHeroVO;
import logic.dungeon.bean.DungeonBean;
import logic.dungeon.bean.DungeonGroupBean;
import logic.dungeon.bean.DungeonLimitHeroTO;
import logic.dungeon.bean.GroupRewardTO;
import logic.dungeon.bean.SceneOverTO;
import logic.dungeon.bean.SceneStartTO;
import logic.dungeon.scene.ActivityDungeonScene;
import logic.dungeon.scene.DatingDungeonScene;
import logic.dungeon.scene.GeneralDungeonScene;
import logic.dungeon.scene.MainLineDungeonScene;
import logic.dungeon.scene.SingleDungeonScene;
import logic.dungeon.script.IDungeonManagerScript;
import logic.friend.FriendManager;
import logic.functionSwitch.FunctionSwitchService;
import logic.hero.bean.Hero;
import logic.item.ItemPackageHelper;
import logic.item.bean.Item;
import logic.mail.MailService;
import logic.msgBuilder.DungeonMsgBuilder;
import logic.role.bean.Role;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;

import org.apache.commons.lang.time.DateUtils;
import org.game.protobuf.s2c.S2CHeroMsg.HeroExpInfo;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import utils.ConfigDateTimeUtil;
import utils.ExceptionEx;
import utils.TimeUtil;
import utils.ToolMap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import data.GameDataManager;
import data.bean.BaseGoods;
import data.bean.DailyChapterMultipleCfgBean;
import data.bean.DiscreteDataCfgBean;
import data.bean.DungeonChapterCfgBean;
import data.bean.DungeonLevelCfgBean;
import data.bean.DungeonLevelGroupCfgBean;
import data.bean.HeroLimitforDungeonCfgBean;
import data.bean.MainLineCfgBean;

public class DungeonManagerScript extends IDungeonManagerScript {
    
    @Override
    public List<ConfigHeroVO> createDungeonConfigHeroVOs(int leveId) {
        DungeonLevelCfgBean levelCfg = GameDataManager.getDungeonLevelCfgBean(leveId);
        if (levelCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                    String.valueOf(leveId), "] not exists");
        if (levelCfg.getHeroLimitID() == null || levelCfg.getHeroLimitID().length <= 0)
            MessageUtils.throwCondtionError(GameErrorCode.CURRENT_DUNGEON_NOT_MATCH);
        List<ConfigHeroVO> heros = new ArrayList<ConfigHeroVO>(levelCfg.getHeroLimitID().length);
        for (int dungeonLimitHero : levelCfg.getHeroLimitID()) {
            heros.add(createConfigHero(dungeonLimitHero));
        }
        return heros;
    }

    /** 构造配置英雄数据 */
    @SuppressWarnings("unchecked")
    private ConfigHeroVO createConfigHero(int dungeonLimitHero) {
        // 获取配置数据
        HeroLimitforDungeonCfgBean heroLimit =
                GameDataManager.getHeroLimitforDungeonCfgBean(dungeonLimitHero);
        if (heroLimit == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "HeroLimitforDungeonCfgBean [",
                    String.valueOf(dungeonLimitHero), "] not exists");
        ConfigHeroVO hero =
                new ConfigHeroVO(heroLimit.getId(), heroLimit.getHeroID(), heroLimit.getLevel(),
                        heroLimit.getRarity(), heroLimit.getBreakthrough(),
                        heroLimit.getSephiroth(), heroLimit.getSkinID(), heroLimit.getAngelUp());
        return hero;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public DungeonGroupBean buyGroupCount(Player player, int levelGroupCid) {
        DungeonLevelGroupCfgBean cfg = GameDataManager.getDungeonLevelGroupCfgBean(levelGroupCid);
        // 玩家接口逻辑判定异常
        if (cfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelGroupCfgBean [",
                    String.valueOf(levelGroupCid), "] not exists");
        DungeonManager dm = player.getDungeonManager();
        DungeonGroupBean levelGroup = dm.getDungeonGroupBeanWithoutInit(levelGroupCid);
        // 对购买条件的判定
        if (levelGroup == null || levelGroup.getSceneCount() == 0)
            MessageUtils.throwCondtionError(GameErrorCode.BUY_COUNT_IS_LIMIT, "dungeon count did not be decreased");
        if(levelGroup.getBuyCount() >= cfg.getBuyCountLimit())
            MessageUtils.throwCondtionError(GameErrorCode.BUY_COUNT_IS_LIMIT, "购买次数已达上限");
        BagManager bm = player.getBagManager();
        bm.removeItemsByTemplateIdWithCheck(cfg.getPrice(), true, EReason.DUNGEON_DUNGEON_BUYCOUNT);
        levelGroup.setBuyCount(levelGroup.getBuyCount() + 1);
        return levelGroup;
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public DungeonGroupBean handleGroupReward(Player player, GroupRewardTO rewardTo) {
        DungeonManager dm = player.getDungeonManager();
        DungeonGroupBean levelGroup =
                dm.getDungeonGroupBeanWithoutInit(rewardTo.getLevelGroupCid());
        if (levelGroup == null
                || levelGroup.getStars().getOrDefault(rewardTo.getDifficulty(), 0) < rewardTo
                        .getStarNums()
                || levelGroup.getHearts().getOrDefault(rewardTo.getDifficulty(), 0) < rewardTo
                        .getHeartNums())
            MessageUtils.throwCondtionError(GameErrorCode.STAR_NUM_IS_NOT_ENOUGH, "星数不足");

        // 玩家接口逻辑判定异常
        if (dm.isGettedReward(rewardTo.getDifficulty(), rewardTo.getRewardId(), levelGroup))
            MessageUtils.throwCondtionError(GameErrorCode.LEVEL_GROUP_REWARD_IS_GETTED, "奖励已经领取");
        DungeonLevelGroupCfgBean groupCfg =
                GameDataManager.getDungeonLevelGroupCfgBean(rewardTo.getLevelGroupCid());
        if (groupCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelGroupCfgBean [",
                    String.valueOf(rewardTo.getLevelGroupCid()), "] not exists");
        // 获取奖励
        Map<Integer, Integer> reward = null;
        Map map = (Map) groupCfg.getReward().get(rewardTo.getDifficulty());
        if (map != null) {
            reward = (Map<Integer, Integer>) map.get(rewardTo.getRewardInfo());
        }
        dm.recordReward(rewardTo.getDifficulty(), rewardTo.getRewardId(), levelGroup);
        BagManager bm = player.getBagManager();
        bm.addItems(reward, true, EReason.DUNGEON_CHAPTER_OPENBOX);
        return levelGroup;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SingleDungeonScene sceneStart(Player player, SceneStartTO startTo) throws Exception {
        DungeonLevelCfgBean dungeonLevelCfg =
                GameDataManager.getDungeonLevelCfgBean(startTo.getCid());
        if (dungeonLevelCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "dungeon level cid [",
                    String.valueOf(startTo.getCid()), "] not exitsts");
        DungeonManager dm = player.getDungeonManager();
        // 获取关卡记录
        DungeonBean dungeonLevel = dm.getOrInitDungeonBean(startTo.getCid());
        // 首次通关不能使用决斗模式
        if (!dungeonLevel.isWin() && startTo.isDuelMod())
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "duel mode can not be used with the first time");
        int multipleRewardCount = 0;
        DungeonGroupBean tempDungeonGroup =
                dm.getDungeonGroupBeanWithoutInit(dungeonLevelCfg.getLevelGroupServerID());
        if (tempDungeonGroup != null)
            multipleRewardCount = tempDungeonGroup.getMultipleRewardCount();
        SingleDungeonScene dungeonFight = createScene(dungeonLevelCfg);
        dungeonFight.setDuel(startTo.isDuelMod());
        // 预先获得战斗奖励
        Map<Integer, Integer> rewards = Maps.newHashMap();
        // 预先获得战斗消耗
        Map<Integer, Integer> cost = Maps.newHashMap();
        int rewardId = 0;
        // 多倍快速需要单独进行开箱获取奖励
        int quickTimes =
                checkAndGetMultipleRewardTimes(startTo.getQuickBattleCount(), dungeonLevel,
                        dungeonLevelCfg);
        // 关卡未通关过即可以获取首通奖励
        if (dungeonLevelCfg.getFirstReward() != 0 && !dungeonLevel.isWin()) {
            rewardId = dungeonLevelCfg.getFirstReward();
        } else {
            rewardId = dungeonLevelCfg.getReward();
        }
        if (rewardId != 0) {
            BaseGoods cfg = GameDataManager.getBaseGoods(rewardId);
            try {
                Map<Integer, Integer> tempRewards = Maps.newHashMap();
                for (int i = 0; i < quickTimes; i++) {
                    tempRewards.clear();
                    ItemPackageHelper.unpack(cfg.getUseProfit(), null, tempRewards);
                    // 放置入总奖励
                    for (Entry<Integer, Integer> entry : tempRewards.entrySet()) {
                        rewards.put(entry.getKey(),
                                rewards.getOrDefault(entry.getKey(), 0) + entry.getValue());
                    }
                }
            } catch (Exception e) {
                MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                        "unpack item error, itemCid : ", String.valueOf(rewardId), "-->\r\n",
                        ExceptionEx.e2s(e));
            }
            // 获取倍数
            float times = 0f;
            // 关卡组多倍收益
            if (multipleRewardCount > 0) {
                // 如果是日常组
                for (DailyChapterMultipleCfgBean multiCfg : GameDataManager
                        .getDailyChapterMultipleCfgBeans()) {
                    if (multiCfg == null)
                        continue;
                    if (multipleRewardCount > multiCfg.getTimes()
                            && multiCfg.getRewardMultiple() > times)
                        times = multiCfg.getRewardMultiple();
                }
            }
            // 决斗模式
            times += checkAndGetDuelMode(startTo.isDuelMod(), dungeonLevelCfg, cost, quickTimes);
            // 整理倍数
            times = Math.max(1f, times);
            // 加倍奖励
            Map<Integer, Integer> multipleRewards = new HashMap<Integer, Integer>(rewards.size());
            for (Entry<Integer, Integer> item : rewards.entrySet()) {
                multipleRewards.put(item.getKey(), (int) (item.getValue() * times));
            }
            rewards = multipleRewards;
            // 日常暂时只有决斗模式，当前代码暂不支持日常快速模式
            dungeonFight.setMultiple(times);

        }
        // 多倍快速需要扣除相同倍数消耗
        if (dungeonLevelCfg.getCost() != null) {
            for (Entry<Integer, Integer> entry : ((Map<Integer, Integer>) dungeonLevelCfg.getCost())
                    .entrySet()) {
                cost.put(entry.getKey(), cost.getOrDefault(entry.getKey(), 0)
                        + (entry.getValue() * quickTimes));
            }
        }
        // 查找助战英雄
        HeroInfo helpHero = null;
        if (startTo.getHelpPlayerId() != 0) {
            // 对其他玩家英雄数据的获取
            if (dm.isHelpFightCD(startTo.getHelpPlayerId()))
                MessageUtils.throwCondtionError(GameErrorCode.HELP_FIGHTER_IS_NOT_COLD_DOWN,
                        "助战玩家还未冷却");
            Player targetPlayer =
                    ((PlayerViewService) PlayerViewService.getInstance()).getPlayerView(startTo
                            .getHelpPlayerId());
            // 找不到玩家
            if (targetPlayer == null)
                MessageUtils.throwCondtionError(GameErrorCode.HELP_FIGHTER_IS_NOT_COLD_DOWN,
                        "help player does not exist : ", String.valueOf(startTo.getHelpPlayerId()));
            Hero helpHeroObj = targetPlayer.getHeroManager().getHero(startTo.getHelpHeroCid());
            helpHero = helpHeroObj.buildHeroInfo(ChangeType.DEFAULT).build();
        }
        // 英雄限定新逻辑
        dungeonFight.setHelpPid(startTo.getHelpPlayerId());
        dungeonFight.setLimitedHeros(startTo.getLimitedHeros());
        dungeonFight.setHelpHero(helpHero);
        dungeonFight.setDungeonLevel(dungeonLevel);
        dungeonFight.setRewards(rewards);
        dungeonFight.setCost(cost);
        dungeonFight.setPlayer(player);
        // 默认战斗次数1
        dungeonFight.setBattleCount(Math.max(startTo.getQuickBattleCount(), 1));

        // 开始战斗
        dungeonFight.start();
        // 好友助战逻辑涉及冷却时间变动,这里待关卡通过所有检测以后进行设置
        // 好友才计入冷却时间
        if (player.getFriendManager().isFriend(startTo.getHelpPlayerId())) {
            // 设置助战冷却时间
            int min =
                    ToolMap.getInt(DiscreteDataKey.HELP_CD,
                            GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.HELP_FIGHTER)
                                    .getData(), 0);
            long cd = min * DateUtils.MILLIS_PER_MINUTE;
            long dis = 15 * DateUtils.MILLIS_PER_SECOND;// 容错误差处理
            long cdEndTime = System.currentTimeMillis() + cd - dis;
            dm.setHelpFightCD(startTo.getHelpPlayerId(), cdEndTime);
        }
        return dungeonFight;
    }
    
    @Override
    public SingleDungeonScene createScene(DungeonLevelCfgBean dungeonLevelCfg) {
        SingleDungeonScene dungeonFight = null;
        DungeonLevelGroupCfgBean dungeonGroup = GameDataManager
                .getDungeonLevelGroupCfgBean(dungeonLevelCfg.getLevelGroupServerID());
        if (dungeonGroup == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelGroupCfgBean [",
                    String.valueOf(dungeonLevelCfg.getLevelGroupServerID()), "] not exists");
        switch (dungeonGroup.getDungeonType()) {
        case DungeonTypeConstant.DUNGEON_TYPE_MAIN_LINE:
            dungeonFight = new MainLineDungeonScene(dungeonLevelCfg.getId());
            break;
        case DungeonTypeConstant.DUNGEON_TYPE_GENERAL:
            if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.NORMAL_DUNGEON)) {
                MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                        "FUNCTION_NOT_OPEN:normal_dungeon");
            }
            // 子类:约会关卡、城市约会关卡
            if (dungeonLevelCfg.getDungeonType() == DungeonTypeConstant.DUNGEON_TYPE_GENERAL_DATING
                    || dungeonLevelCfg.getDungeonType() == DungeonTypeConstant.DUNGEON_TYPE_GENERAL_CITY_DATING)
                dungeonFight = new DatingDungeonScene(dungeonLevelCfg.getId());
            else
                dungeonFight = new GeneralDungeonScene(dungeonLevelCfg.getId());
            break;
        case DungeonTypeConstant.DUNGEON_TYPE_ACTIVITY:
            if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.DAILY_DUNGEON)) {
                MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                        "FUNCTION_NOT_OPEN:daily_dungeon");
            }
            dungeonFight = new ActivityDungeonScene(dungeonLevelCfg.getId());
            break;
        }
        return dungeonFight;
    }

    /** 检测获取关卡收益倍数 */
    @SuppressWarnings("unchecked")
    private int checkAndGetMultipleRewardTimes(int quickBattleCount, DungeonBean dungeonLevel,
            DungeonLevelCfgBean dungeonLevelCfg) {
        // 消耗倍数
        int costTimes = 1;
        // 是否可以进行快速战斗
        if (quickBattleCount > 0) {
            // 不能快速战斗
            if (!dungeonLevelCfg.getIsQuickBattle())
                MessageUtils.throwCondtionError(GameErrorCode.QUICK_BATTLE_MOD_INVALID,
                        "DungeonLevelCfgBean [", String.valueOf(dungeonLevelCfg.getId()),
                        "] quick battle invalid");
            Map<Integer, Integer> condition =
                    (Map<Integer, Integer>) dungeonLevelCfg.getQuickBattleParameter();
            // 配置为空
            if (condition == null || condition.isEmpty())
                MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                        String.valueOf(dungeonLevelCfg.getId()), "] quick battle null");
            int start = condition.getOrDefault(quickBattleCount, Integer.MAX_VALUE);
            if (start > dungeonLevel.getStar())
                MessageUtils.throwCondtionError(GameErrorCode.QUICK_BATTLE_MOD_INVALID,
                        "DungeonLevelCfgBean [", String.valueOf(dungeonLevelCfg.getId()),
                        "] stars not enough");
            costTimes = quickBattleCount;
        }
        return costTimes;
    }

    @SuppressWarnings("unchecked")
    private int checkAndGetDuelMode(boolean isDuelMod, DungeonLevelCfgBean dungeonLevelCfg,
            Map<Integer, Integer> totalCost, int quickTimes) {
        // 是否可以进行决斗模式
        if (isDuelMod) {
            if (!dungeonLevelCfg.getIsDuelMod())
                MessageUtils.throwCondtionError(GameErrorCode.DUEL_MOD_INVALID,
                        "DungeonLevelCfgBean [", String.valueOf(dungeonLevelCfg.getId()),
                        "] duel mod invalid");
            for (Map.Entry<Integer, Integer> entry : ((Map<Integer, Integer>) dungeonLevelCfg
                    .getDuelModCost()).entrySet()) {
                totalCost
                        .put(entry.getKey(),
                                totalCost.getOrDefault(entry.getKey(), 0)
                                        + (entry.getValue() * quickTimes));
            }
            return dungeonLevelCfg.getRewardMultiple();
        }
        return 0;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public SingleDungeonScene sceneOver(Player player, List<Item> rewardItems, SceneOverTO overTo) {
        DungeonManager dm = player.getDungeonManager();
        SingleDungeonScene sds = (SingleDungeonScene) dm.getCurrentScene();
        // 判定是否属于本副本
        if (sds == null || sds.getSceneCfgId() != overTo.getCid())
            MessageUtils.throwCondtionError(GameErrorCode.CURRENT_DUNGEON_NOT_MATCH,
                    "current dungeon does not match");
        sds.setWin(overTo.isWin());
        sds.setGoals(overTo.getGoals());
        sds.setBatter(overTo.getBatter());
        sds.setPickUpCount(overTo.getPickUpCount());
        sds.setPickUpTypeCount(overTo.getPickUpTypeCount());
        DungeonBean dungeonLevel = sds.getDungeonLevel();
        boolean firstPass = !dungeonLevel.isWin();
        sds.over(true);
        if(!sds.isWin())
            return sds;
        // 扣除消耗
        BagManager bm = player.getBagManager();
        DungeonLevelCfgBean cfg = GameDataManager.getDungeonLevelCfgBean(sds.getSceneCfgId());
        if (cfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                    String.valueOf(sds.getSceneCfgId()), "] not exists");
        if (sds.getCost() != null && !sds.getCost().isEmpty())
            bm.removeItemsByTemplateIdNoCheck(sds.getCost(), true, EReason.DUNGEON_DUNGEON_PASS);
        // 发放奖励
        // List<RewardsMsg> rewardsMsgs = ItemManager.unpackNotify(player, rewards, cloneLog);
        Map<Integer, Integer> rewards = sds.getRewards();
        if (rewards != null && !rewards.isEmpty()) {
            // 如果是限定英雄模式,那么对英雄特别的奖励需要处理
            if (sds.getLimitedHeros() != null && !sds.getLimitedHeros().isEmpty()) {
                // 对英雄特殊物品的处理
                Integer exp = rewards.remove(ItemConstantId.HERO_EXP);
                Integer favor = rewards.remove(ItemConstantId.ROLE_FAVOR);
                Integer mood = rewards.remove(ItemConstantId.ROLE_MOOD);
                for (DungeonLimitHeroTO dto : sds.getLimitedHeros()) {
                    // 对玩家英雄的处理
                    if (dto.getType() == DungeonConstant.DUNGEON_HERO_LIST_NORMAL) {
                        int roleId = player.getRoleManager().getRoleCidByHeroCid(dto.getCid());
                        Role role = player.getRoleManager().getRole(roleId);
                        if (favor != null)
                            player.getRoleManager().changeFavor(role, favor,
                                    EReason.DUNGEON_DUNGEON_PASS);
                        if (mood != null)
                            player.getRoleManager().changeMood(role, mood,
                                    EReason.DUNGEON_DUNGEON_PASS);
                        if (exp != null) {
                            Hero hero = player.getHeroManager().getHero(dto.getCid());
                            if (LogicScriptsUtils.getIHeroScript().addExp(player, exp, hero)) {
                                // 英雄信息需要刷新
                                MessageUtils.send(player, hero.buildHeroInfo(ChangeType.UPDATE));
                            } else {
                                HeroExpInfo.Builder expBuilder = HeroExpInfo.newBuilder();
                                expBuilder.setCid(hero.getCid());
                                expBuilder.setId(Long.toString(hero.getCid()));
                                expBuilder.setExp(hero.getExp());
                                MessageUtils.send(player, expBuilder);
                            }
                        }
                    }
                }
            }
            List<Item> ownItems = bm.addItems(rewards, true, EReason.DUNGEON_DUNGEON_PASS);
            rewardItems.addAll(ownItems);
        }
        // 关卡组相关
        DungeonLevelGroupCfgBean groupCfg =
                GameDataManager.getDungeonLevelGroupCfgBean(cfg.getLevelGroupServerID());
        if (groupCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                    "DungeonLevelGroupCfgBean [", String.valueOf(cfg.getLevelGroupServerID()),
                    "] not exists");
        // 章节主线完成奖励
        if (cfg.getLastOne() && firstPass) {
            DungeonChapterCfgBean chapterCfg =
                    GameDataManager.getDungeonChapterCfgBean(groupCfg.getDungeonChapterId());
            if (chapterCfg == null)
                MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonChapterCfgBean [",
                        String.valueOf(groupCfg.getDungeonChapterId()), "] not exists");
            // 存在奖励
            if (chapterCfg.getUnlockHero() > 0) {
                List<Item> ownItems =
                        bm.addItem(chapterCfg.getUnlockHero(), 1, true,
                                EReason.DUNGEON_CHAPTER_SCRIPT_PASS);
                rewardItems.addAll(ownItems);
            }
        }
        
        // 日常关卡特殊奖励机制
        if (groupCfg.getDungeonType() == DungeonTypeConstant.DUNGEON_TYPE_ACTIVITY) {
            if (cfg.getDailyLevelReward() != null && cfg.getDailyLevelReward().length > 0) {
                // 这里可能有0星
                int packageId =
                        sds.getNowStar() >= cfg.getDailyLevelReward().length ? cfg
                                .getDailyLevelReward()[cfg.getDailyLevelReward().length - 1] : cfg
                                .getDailyLevelReward()[sds.getNowStar()];
                BaseGoods baseGoodCfg = GameDataManager.getBaseGoods(packageId);
                Map<Integer, Integer> dailyDungeonReward = new HashMap<Integer, Integer>();
                try {
                    ItemPackageHelper.unpack(baseGoodCfg.getUseProfit(), null, dailyDungeonReward);
                } catch (Exception e) {
                    MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                            "unpack item error, itemCid : ", String.valueOf(packageId), "-->\r\n",
                            ExceptionEx.e2s(e));
                }
                // 多倍收益加成
                if (sds.getMultiple() > 1f) {
                    Map<Integer, Integer> tempReward = new HashMap<Integer, Integer>();
                    for (Entry<Integer, Integer> item : dailyDungeonReward.entrySet()) {
                        tempReward.put(item.getKey(), (int) (item.getValue() * sds.getMultiple()));
                    }
                    dailyDungeonReward = tempReward;
                }
                List<Item> ownItems =
                        bm.addItems(dailyDungeonReward, true, EReason.DUNGEON_DAILY_STAR);
                rewardItems.addAll(ownItems);
            }
        }

        // 增加友情点
        if (sds.getHelpHero() != null) {
            int pid = sds.getHelpPid();// 获取助战玩家id
            if (pid > 0) {
                DiscreteDataCfgBean helpCfg =
                        GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.HELP_FIGHTER);
                int point = ToolMap.getInt("passer", helpCfg.getData(), 5);
                FriendManager fm = player.getFriendManager();
                if (fm.isFriend(pid)) {
                    point = ToolMap.getInt("friend", helpCfg.getData(), 10);
                }
                Map<Integer, Integer> points = Maps.newHashMap();
                points.put(ItemConstantId.FRIEND_SHIP_POINT, point);
                bm.addItems(points, true, EReason.DUNGEON_DUNGEON_PASS);
                // 邮件通知对方（自动提取附件）
                String title = "helpfight";
                String body =
                        "helpfight: pid=" + player.getPlayerId() + " dungeonId="
                                + sds.getDungeonLevel().getCid() + " star=" + sds.getNowStar();
                ((MailService) (MailService.getInstance())).sendPlayerMail(true, pid, title, body,
                        points, EReason.DUNGEON_HELP_FIGHT);
            }
        }
        return sds;
    }
    
    /**
     * 获取或新建副本对象
     */
    @Override
    public DungeonBean getOrInitDungeonBean(DungeonManager dm, int cid) {
        DungeonBean dungeon = dm.getDungeonWithoutInit(cid);
        if (dungeon == null) {
            dungeon = new DungeonBean(cid, 0, 0, false, Lists.newArrayList(),
                    dm.getGameAcrossDay(), 0);
            dm.addDungeonBean(dungeon, true);
        }
        return dungeon;
    }

    /**
     * 获取或新建副本组对象
     */
    @Override
    public DungeonGroupBean getOrInitDungeonLevelGroup(Player player, DungeonManager dm,
            int groupCid) {
        DungeonGroupBean group = dm.getDungeonGroupBeanWithoutInit(groupCid);
        if (group == null) {
            group = new DungeonGroupBean(0, 0, dm.getGameAcrossDay(), Maps.newHashMap(), groupCid, 0, 0);
            dm.addDungeonGroupBean(group, true);
            // 如果没有这个记录进行推送下发
            MessageUtils.send(player, DungeonMsgBuilder.getUpdateLevelGroupInfo(group));
        }
        return group;
    }

    /**
     * 检查助战玩家是否在冷却中
     */
    @Override
    public boolean isHelpFightCD(DungeonManager dm, int helpPlayerId) {
        long value = dm.getHelpFightCD(helpPlayerId);
        return value > System.currentTimeMillis();
    }

    @Override
    public void createPlayerInitialize() {
        checkDungeonValid();
    }

    @Override
    public void createRoleInitialize() throws Exception {

    }

    /**
     * 检测更新副本列表
     * 
     * @param now 当前检测时间点
     * @param isNotify 是否进行推送
     */
    private void updateDungeonList(Player player, DungeonManager dm, Date now, boolean isNotify) {
        List<DungeonBean> refreshLevelList = Lists.newArrayList();
        dm.getDungeons().values().forEach(level -> {
            boolean refreshed = refreshSceneCount(now, level);
            if (refreshed) {
                refreshLevelList.add(level);
            }
        });
        if (isNotify && !refreshLevelList.isEmpty())
            MessageUtils.send(player, DungeonMsgBuilder.getLevelInfoList(refreshLevelList));
    }

    /**
     * 检测更新副本组列表
     * 
     * @param now 当前检测时间点
     * @param isNotify 是否进行推送
     */
    public void updateGroupList(Player player, DungeonManager dm, Date now, boolean isNotify) {
        List<DungeonGroupBean> refreshGroupList = Lists.newArrayList();
        dm.getDungeonGroups().values().forEach(group -> {
            DungeonLevelGroupCfgBean groupCfg =
                    GameDataManager.getDungeonLevelGroupCfgBean(group.getCid());
                            if (groupCfg == null)
                                return;
            if (groupCfg.getCycleType() == DungeonConstant.FIGHT_COUNT_REFRESH_TYPE_DAILY) {
                if (refreshCountJustCheckDate(now, group, dm))
                    refreshGroupList.add(group);
            }
        });
        if (isNotify) {
            if (!refreshGroupList.isEmpty())
                MessageUtils.send(player,
                        DungeonMsgBuilder.getDungeonLevelGroupList(refreshGroupList));
            MessageUtils.send(player, DungeonMsgBuilder.getGroupMultipleRewardMsg(player));
        }
    }

    /** 修改主线剧情进度 */
    @Override
    public DungeonGroupBean updateGroupStoryProgress(Player player, DungeonManager dm,
            MainLineCfgBean cfg) {
        DungeonGroupBean group = getOrInitDungeonLevelGroup(player, dm, cfg.getLevelGroupId());
        group.setLastMainLine(cfg.getId());
        if (cfg.getId() > group.getMaxMainLine()) {
            group.setMaxMainLine(cfg.getId());
        }
        return group;
    }

    /** 推进主线剧情,向玩家推送进度数据 */
    @Override
    public void promoteStory(Player player, DungeonManager dm, int mainLineCid) {
        // 主线配置校验
        MainLineCfgBean mainLineCfgBean = GameDataManager.getMainLineCfgBean(mainLineCid);
        // 玩家接口逻辑判断异常
        if (mainLineCfgBean == null)
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR);
        // ToolError.isAndTrue(GameErrorCode.CLIENT_PARAM_IS_ERR, mainLineCfgBean == null);
        DungeonGroupBean group = updateGroupStoryProgress(player, dm, mainLineCfgBean);
        MessageUtils.send(player, DungeonMsgBuilder.getProgressMsg(group));
    }

    @Override
    public void tick(DungeonManager dm) {
    }

    @Override
    public void clientLoginInitOver(DungeonManager dm) {
    }

    @Override
    public void acrossDay(Player player, DungeonManager dm, boolean isNotify) {
        Date now = new Date();
        updateGroupList(player, dm, now, isNotify);
        updateDungeonList(player, dm, now, isNotify);
    }

    /** 对副本合法性的检测与更新 */
    @Override
    public void checkDungeonValid() {
        // 新增关卡检测、当前可用关卡检测(当前可用的关卡数据为客户端计算,待新规则完善后再行讨论)
    }

    /**
     * 助战冷却检测
     */
    public void checkHelpFightCD(DungeonManager dm) {
        List<Integer> result = Lists.newArrayList();
        List<Integer> outTimeList = Lists.newArrayList();
        dm.getHelpFightCD().forEach((k, v) -> {
            if (isHelpFightCD(dm, k)) {
                result.add(k);
            } else {
                outTimeList.add(k);
            }
        });
        if (outTimeList.size() > 0) {
            outTimeList.forEach(id -> dm.getHelpFightCD().remove(id));
        }
    }


    /** 刷新副本次数 */
    private boolean refreshSceneCount(Date lastAcross, DungeonBean dungeon) {
        dungeon.setLastRefreshTime(lastAcross.getTime());
        dungeon.setSceneCount(0);
        return true;
    }

    /**
     * 战斗次数是否达到上限
     */
    @Override
    public boolean checkFightCountIsLimit(int limit, DungeonGroupBean dungeonGroup) {
        if (limit == 0)
            return false;
        return dungeonGroup.getSceneCount() >= limit + dungeonGroup.getBuyCount();
    }

    /**
     * 查看奖励是否领取
     */
    @Override
    public boolean isGettedReward(int difficulty, int rewardId, DungeonGroupBean dungeonGroup) {
        Set<Integer> list = (Set<Integer>) dungeonGroup.getGetedReward().get(difficulty);
        if (list != null) {
            return list.contains(rewardId);
        }
        return false;
    }

    /**
     * 奖励领取记录
     */
    @Override
    public void recordReward(int difficulty, int rewardId, DungeonGroupBean dungeonGroup) {
        Set<Integer> list = (Set<Integer>) dungeonGroup.getGetedReward().computeIfAbsent(difficulty,
                k -> Sets.newHashSet());
        list.add(rewardId);
    }

    /** 是否刷新了副本组次数 */
    private boolean refreshCountJustCheckDate(Date now, DungeonGroupBean dungeonGroup, DungeonManager dm) {
     // 重置之前将进行溢出次数处理
        handleFlowoverCount(now, dungeonGroup, dm);
        dungeonGroup.setSceneCount(0);
        dungeonGroup.setBuyCount(0);
        dungeonGroup.setLastRefreshTime(now.getTime());
        return true;
    }

    /** 溢出挑战次数的处理 */
    @SuppressWarnings({"unchecked"})
    private void handleFlowoverCount(Date now, DungeonGroupBean dungeonGroup, DungeonManager dm) {
        DungeonLevelGroupCfgBean dungeonLevelGroupCfgBean =
                GameDataManager.getDungeonLevelGroupCfgBean(dungeonGroup.getCid());
        if (dungeonLevelGroupCfgBean == null)
            return;
        // 只对活动关卡有效
        if (dungeonLevelGroupCfgBean.getDungeonType() != DungeonTypeConstant.DUNGEON_TYPE_ACTIVITY)
            return;
        // 本次跨天时间
        Date currentAcorss = new Date(dm.getGameAcrossDay());
        // 时间点还未到当前下次跨天
        int maxSeq = 0;
        int seqCount = 0;
        int buyCount = dungeonGroup.getBuyCount();
        int useCount = dungeonGroup.getSceneCount();
        // 前一天
        Date lastDay = TimeUtil.addDayOfMonth(currentAcorss, -1);
        // 完整跨天才进行结算
        while (now.compareTo(currentAcorss) >= 0) {
            // 下次跨天时结算前一天的使用次数
            // 开放时段才进行溢出处理，即本次跨天时前一天处于开放时段
            if (ConfigDateTimeUtil.check(dungeonLevelGroupCfgBean.getOpenTimeType(),
                    dungeonLevelGroupCfgBean.getTimeFrame(), lastDay)) {
                // 连续中断,记录最大值
                if (useCount > 0) {
                    seqCount = 0;
                }
                seqCount += buyCount + dungeonLevelGroupCfgBean.getCountLimit() - useCount;
                if (seqCount > maxSeq)
                    maxSeq = seqCount;
            }
            // 跨天的默认没有操作
            buyCount = 0;
            useCount = 0;
            lastDay = currentAcorss;
            currentAcorss = new Date(dm.getGameNextAcrossDay(lastDay));
        }
        dungeonGroup.setMultipleRewardCount(dungeonGroup.getMultipleRewardCount() + maxSeq);
    }
    
    /** 判断副本是否通过 **/
    @Override
    public boolean checkDungeonPass(DungeonManager dungeonManager, int id) {
        DungeonBean dungeon = dungeonManager.getDungeonWithoutInit(id);
        if (dungeon == null)
            return false;
        return dungeon.isWin();
    }
}
