package logic.dungeon.scene;

import java.util.List;
import java.util.Map;

import logic.character.bean.Player;
import logic.dungeon.bean.DungeonBean;
import logic.dungeon.bean.DungeonLimitHeroTO;
import logic.support.LogicScriptsUtils;
import data.bean.DungeonLevelCfgBean;
import data.bean.DungeonLevelGroupCfgBean;

/**
 * @author : Alan
 * @Description ：关卡相关检查
 */
public class DungeonCheck {

    /**
     * 检查前置关卡是否通关
     */
    public static void checkPreLevel(Player player, DungeonLevelCfgBean dungeonLevelCfgBean) {
        LogicScriptsUtils.getIDungeonCheckScript().checkPreLevel(player, dungeonLevelCfgBean);
    }


    /**
     * 检查战斗次数
     */
    public static void checkSceneCount(DungeonLevelCfgBean dungeonLevelCfgBean,
            DungeonBean dungeonLevel, int times) {
        LogicScriptsUtils.getIDungeonCheckScript().checkSceneCount(dungeonLevelCfgBean,
                dungeonLevel, times);
    }


    /**
     * 检查消耗
     */
    public static void checkCost(Player player, DungeonLevelCfgBean dungeonLevelCfgBean,
            Map<Integer, Integer> totalCost) {
        LogicScriptsUtils.getIDungeonCheckScript()
                .checkCost(player, dungeonLevelCfgBean, totalCost);
    }

    /**
     * 检查玩家等级
     */
    public static void checkPlayerLevel(Player player, DungeonLevelCfgBean dungeonLevelCfgBean) {
        LogicScriptsUtils.getIDungeonCheckScript().checkPlayerLevel(player, dungeonLevelCfgBean);
    }


    /**
     * 检查关卡是否开启
     */
    public static void checkLevelGroupIsOpenTime(DungeonLevelGroupCfgBean dungeonLevelGroupCfgBean) {
        LogicScriptsUtils.getIDungeonCheckScript().checkLevelGroupIsOpenTime(
                dungeonLevelGroupCfgBean);
    }

    /**
     * 检查战斗次数
     */
    public static void checkLevelGroupSceneCount(Player player,
            DungeonLevelGroupCfgBean dungeonLevelGroupCfgBean, int times) {
        LogicScriptsUtils.getIDungeonCheckScript().checkLevelGroupSceneCount(player,
                dungeonLevelGroupCfgBean, times);
    }

    /**
     * 检查英雄限定
     * 
     * @param player
     * @param selectedHeros 临时阵容信息
     * @param helpHero 助战英雄
     * @param dungeonLevelCfgBean
     */
    public static void checkLimitedHeros(Player player, List<DungeonLimitHeroTO> selectedHeros,
            int helpHero, DungeonLevelCfgBean dungeonLevelCfgBean) {
        LogicScriptsUtils.getIDungeonCheckScript().checkLimitedHeros(player, selectedHeros,
                helpHero, dungeonLevelCfgBean);
    }
}
