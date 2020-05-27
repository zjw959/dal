package javascript.logic.dungeon;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.DungeonConstant;
import logic.constant.EScriptIdDefine;
import logic.constant.FormationConstant;
import logic.constant.GameErrorCode;
import logic.dungeon.DungeonManager;
import logic.dungeon.bean.DungeonBean;
import logic.dungeon.bean.DungeonGroupBean;
import logic.dungeon.bean.DungeonLimitHeroTO;
import logic.dungeon.script.IDungeonCheckScript;
import logic.support.MessageUtils;
import utils.ConfigDateTimeUtil;
import data.GameDataManager;
import data.bean.DungeonLevelCfgBean;
import data.bean.DungeonLevelGroupCfgBean;
import data.bean.HeroLimitforDungeonCfgBean;

/**
 * @author : Alan
 * @Description ：关卡相关检查
 */
public class DungeonCheckScript implements IDungeonCheckScript {
    @Override
    public int getScriptId() {
        return EScriptIdDefine.DUNGEON_CHECK_SCRIPT.Value();
    }

    /**
     * 检查前置关卡是否通关
     */
    @Override
    public void checkPreLevel(Player player, DungeonLevelCfgBean dungeonLevelCfgBean) {
        if (dungeonLevelCfgBean.getPreLevelId() == null)
            return;
        DungeonManager dm = player.getDungeonManager();
        for (int preId : dungeonLevelCfgBean.getPreLevelId()) {
            DungeonBean preDungeonLevel = dm.getDungeonWithoutInit(preId);
            // 玩家接口的逻辑判定异常
            if (preDungeonLevel == null || !preDungeonLevel.isWin())
                MessageUtils.throwCondtionError(GameErrorCode.PRE_LEVEL_NOT_PASS);
            // ToolError.isAndTrue(GameErrorCode.PRE_LEVEL_NOT_PASS, preDungeonLevel == null ||
            // !preDungeonLevel.isWin());
        }
    }


    /**
     * 检查战斗次数
     */
    @Override
    public void checkSceneCount(DungeonLevelCfgBean dungeonLevelCfgBean, DungeonBean dungeonLevel,
            int times) {
        if (dungeonLevel == null)
            return;
        // 玩家接口的逻辑判定异常
        if (dungeonLevel.getSceneCount() + times > dungeonLevelCfgBean.getFightCount())
            MessageUtils.throwCondtionError(GameErrorCode.FIGHT_COUNT_IS_UPPER_LIMIT);
        // ToolError.isAndTrue(GameErrorCode.FIGHT_COUNT_IS_UPPER_LIMIT,dungeonLevel.getSceneCount()
        // >= dungeonLevelCfgBean.getFightCount());
    }


    /**
     * 检查消耗
     */
    @Override
    public void checkCost(Player player, DungeonLevelCfgBean dungeonLevelCfgBean,
            Map<Integer, Integer> totalCost) {
        BagManager bm = player.getBagManager();
        boolean enough = bm.enoughByTemplateId(totalCost);
        // 玩家接口的逻辑判定异常
        // ToolError.isAndTrue(GameErrorCode.GOODS_IS_NOT_ENOUGH, !enough);
        if (!enough)
            MessageUtils.throwCondtionError(GameErrorCode.GOODS_IS_NOT_ENOUGH);
    }

    /**
     * 检查玩家等级
     */
    @Override
    public void checkPlayerLevel(Player player, DungeonLevelCfgBean dungeonLevelCfgBean) {
        // 玩家接口的逻辑判定异常
        if (player.getLevel() < dungeonLevelCfgBean.getPlayerLv())
            MessageUtils.throwCondtionError(GameErrorCode.PLAYER_LEVEL_IS_NOT_ENOUGH, "玩家等级不足");
        // ToolError.isAndTrue(GameErrorCode.PLAYER_LEVEL_IS_NOT_ENOUGH, "玩家等级不足", player.getLevel()
        // < dungeonLevelCfgBean.getPlayerLv());
    }


    /**
     * 检查关卡是否开启
     */
    @SuppressWarnings("unchecked")
    @Override
    public void checkLevelGroupIsOpenTime(DungeonLevelGroupCfgBean dungeonLevelGroupCfgBean) {
        // 玩家接口的逻辑判定异常
        if (!ConfigDateTimeUtil.check(dungeonLevelGroupCfgBean.getOpenTimeType(),
                dungeonLevelGroupCfgBean.getTimeFrame()))
            MessageUtils.throwCondtionError(GameErrorCode.DUNGEON_LEVEL_IS_NOT_OPEN, "未到关卡开启时间");
        // ToolError.isAndTrue(GameErrorCode.DUNGEON_LEVEL_IS_NOT_OPEN, "未到关卡开启时间",
        // !ConfigDateTimeUtil.check(dungeonLevelGroupCfgBean.getOpenTimeType(),
        // dungeonLevelGroupCfgBean.getTimeFrame()));
    }

    /**
     * 检查战斗次数
     */
    @Override
    public void checkLevelGroupSceneCount(Player player,
            DungeonLevelGroupCfgBean dungeonLevelGroupCfgBean, int times) {
        if (dungeonLevelGroupCfgBean.getCountLimit() == 0)
            return;
        DungeonManager dm = player.getDungeonManager();
        // 前置已经检测过关卡是否可用，这里可以直接进行初始化
        DungeonGroupBean group =
                dm.getOrInitDungeonLevelGroup(player, dungeonLevelGroupCfgBean.getId());
        // 玩家接口的逻辑判定异常
        if (group.getSceneCount() + times > dungeonLevelGroupCfgBean.getCountLimit()
                + group.getBuyCount())
            MessageUtils.throwCondtionError(GameErrorCode.NOT_MORE_FIGHT_COUNT, "战斗次数已达上限");
        // ToolError.isAndTrue(GameErrorCode.NOT_MORE_FIGHT_COUNT, "战斗次数已达上限",
        // (group.getSceneCount() >= dungeonLevelGroupCfgBean.getCountLimit() +
        // group.getBuyCount()));
    }

    /**
     * 检查英雄限定
     * 
     * @param player
     * @param selectedHeros 临时阵容信息
     * @param helpHero 助战英雄
     * @param dungeonLevelCfgBean
     */
    @Override
    public void checkLimitedHeros(Player player, List<DungeonLimitHeroTO> selectedHeros,
            int helpHero, DungeonLevelCfgBean dungeonLevelCfgBean) {
        // 前置判定
        HeroLimitforDungeonCfgBean heroLimit;
        Set<Integer> induplicateHeros = new HashSet<Integer>();
        Set<Integer> limitedHeros = new HashSet<Integer>();
        int[] limits = dungeonLevelCfgBean.getHeroLimitID();
        int[] forbidden = dungeonLevelCfgBean.getHeroForbiddenID();
        // 普通关卡没有配置
        if (selectedHeros != null) {
            // 阵型对列表数量做判定
            if (selectedHeros.size() > FormationConstant.MAX_HERO_NUM)
                MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                        "hero size larger than formation max");
            for (DungeonLimitHeroTO dungeonLimitHeroTO : selectedHeros) {
                int heroid;
                // 如果是限定英雄
                if (dungeonLimitHeroTO.getType() == DungeonConstant.DUNGEON_HERO_LIST_LIMITED) {
                    heroLimit =
                            GameDataManager.getHeroLimitforDungeonCfgBean(dungeonLimitHeroTO
                                    .getCid());
                    if (heroLimit == null)
                        MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                                "HeroLimitforDungeonCfgBean [",
                                String.valueOf(dungeonLimitHeroTO.getCid()), "] not exists");
                    heroid = heroLimit.getHeroID();
                    limitedHeros.add(dungeonLimitHeroTO.getCid());
                } else {
                    heroid = dungeonLimitHeroTO.getCid();
                    // 判断玩家是否拥有英雄
                    if (!player.getHeroManager().isExistHero(heroid))
                        MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO);
                }
                // 去重
                if (induplicateHeros.contains(heroid))
                    MessageUtils.throwCondtionError(GameErrorCode.LIMITED_HEROS_SELECTED_DUPLICATE);
                induplicateHeros.add(heroid);
            }
        }
        // 特殊判定
        switch (dungeonLevelCfgBean.getHeroLimitType()) {
        // 只允许配置的英雄
            case DungeonConstant.LIMITED_HEROS_OWNED_FORBIDDEN:
                // 如果有非配置英雄
                if (limitedHeros.size() != induplicateHeros.size())
                    MessageUtils.throwCondtionError(GameErrorCode.LIMITED_HEROS_SETTED_ONLY);
                // 不执行break,与下部分逻辑相同

                // 允许配置与玩家英雄混用
            case DungeonConstant.LIMITED_HEROS_OWNED_PERMITED:
                // 与配置id对比
                for (int tempId : limits) {
                    if (limitedHeros.contains(tempId))
                        continue;
                    MessageUtils.throwCondtionError(GameErrorCode.LIMITED_HEROS_SETTED_ONLY);
                }
                break;
            // 不允许使用的玩家英雄
            case DungeonConstant.LIMITED_HEROS_DESIGNATED_FORBIDDEN:
                for (int tempId : forbidden) {
                    if (induplicateHeros.contains(tempId) ||
                    // 助战英雄判定
                            helpHero == tempId)
                        MessageUtils
                                .throwCondtionError(GameErrorCode.LIMITED_HEROS_SELECTED_FORBIDDEN);
                }
                break;
        }
    }


}
