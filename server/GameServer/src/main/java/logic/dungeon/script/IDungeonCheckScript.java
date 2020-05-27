package logic.dungeon.script;

import java.util.List;
import java.util.Map;

import logic.character.bean.Player;
import logic.dungeon.bean.DungeonBean;
import logic.dungeon.bean.DungeonLimitHeroTO;
import script.IScript;
import data.bean.DungeonLevelCfgBean;
import data.bean.DungeonLevelGroupCfgBean;

public interface IDungeonCheckScript extends IScript {


    /**
     * 检查前置关卡是否通关
     */
    public void checkPreLevel(Player player, DungeonLevelCfgBean dungeonLevelCfgBean);


    /**
     * 检查战斗次数
     */
    public void checkSceneCount(DungeonLevelCfgBean dungeonLevelCfgBean, DungeonBean dungeonLevel,
            int times);


    /**
     * 检查消耗
     */
    public void checkCost(Player player, DungeonLevelCfgBean dungeonLevelCfgBean,
            Map<Integer, Integer> totalCost);

    /**
     * 检查玩家等级
     */
    public void checkPlayerLevel(Player player, DungeonLevelCfgBean dungeonLevelCfgBean);


    /**
     * 检查关卡是否开启
     */
    public void checkLevelGroupIsOpenTime(DungeonLevelGroupCfgBean dungeonLevelGroupCfgBean);

    /**
     * 检查战斗次数
     */
    public void checkLevelGroupSceneCount(Player player,
            DungeonLevelGroupCfgBean dungeonLevelGroupCfgBean, int times);

    /**
     * 检查英雄限定
     * 
     * @param player
     * @param selectedHeros 临时阵容信息
     * @param helpHero 助战英雄
     * @param dungeonLevelCfgBean
     */
    public void checkLimitedHeros(Player player, List<DungeonLimitHeroTO> selectedHeros,
            int helpHero, DungeonLevelCfgBean dungeonLevelCfgBean);

}
