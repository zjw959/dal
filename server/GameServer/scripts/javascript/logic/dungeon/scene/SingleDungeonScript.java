package javascript.logic.dungeon.scene;

import java.util.Map;

import logic.character.bean.Player;
import logic.city.build.BuildingConstant;
import logic.constant.DungeonConstant;
import logic.constant.EEventType;
import logic.constant.EScriptIdDefine;
import logic.constant.EventConditionKey;
import logic.constant.GameErrorCode;
import logic.dungeon.DungeonManager;
import logic.dungeon.bean.DungeonBean;
import logic.dungeon.scene.DungeonCheck;
import logic.dungeon.scene.DungeonScene;
import logic.dungeon.scene.SingleDungeonScene;
import logic.dungeon.script.IBaseSingleDungeonSceneScript;
import logic.support.MessageUtils;
import cn.hutool.core.util.RandomUtil;

import com.google.common.collect.Maps;

import data.GameDataManager;
import data.bean.DungeonLevelCfgBean;
import data.bean.DungeonLevelGroupCfgBean;

/**
 * 单人副本脚本通用逻辑,此处暂存
 * 
 * @author Alan
 *
 */
public class SingleDungeonScript implements IBaseSingleDungeonSceneScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.DUNGEON_BASE_SCRIPT.Value();
    }

    /** 初始化当前场景 */
    public void sceneInit(SingleDungeonScene scene) {
        // 单机副本或许并不需要
        scene.setSceneId(RandomUtil.randomUUID());
        scene.setRandomSeed(RandomUtil.randomInt());
    }

    /** 放置场景至相应容器 */
    public void putScene(DungeonScene scene, Player player) {
        DungeonManager dm = player.getDungeonManager();
        dm.setCurrentScene(scene);
    }

    /** 销毁当前场景 */
    public void destroyScene(String sceneId, Player player) {
        DungeonManager dm = player.getDungeonManager();
        dm.setCurrentScene(null);
    }

    /** 场景开始运作 */
    public boolean sceneStart(SingleDungeonScene scene) {
        DungeonLevelCfgBean dungeonLevelCfg =
                GameDataManager.getDungeonLevelCfgBean(scene.getSceneCfgId());
        if (dungeonLevelCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                    String.valueOf(scene.getSceneCfgId()), "] not exists");
        DungeonCheck.checkPreLevel(scene.getPlayer(), dungeonLevelCfg);
        // 进行限定精灵关卡判定
        DungeonCheck.checkLimitedHeros(scene.getPlayer(), scene.getLimitedHeros(),
                scene.getHelpHero() == null ? 0 : scene.getHelpHero().getCid(), dungeonLevelCfg);
        DungeonCheck.checkCost(scene.getPlayer(), dungeonLevelCfg, scene.getCost());
        DungeonCheck.checkSceneCount(dungeonLevelCfg, scene.getDungeonLevel(), scene.getBattleCount());
        return true;
    }

    /** 副本胜利事件入口 */
    public void notifySceneWin(SingleDungeonScene scene) {
        // 副本事件触发
        sceneEvent(scene);
    }

    //
    // /** 触发关卡事件 */
    // public void sceneEvent(Player player, DungeonBean dungeonLevel, int oldStar, int
    // nowStar,DungeonLevelCfgBean dungeonLevelCfgBean,C2SDungeonMsg.FightOverMsg msg) {
    // // 触发事件
    // Map<String, Object> in = Maps.newHashMap();
    // // 关卡ID
    // in.put(EventConditionKey.DUNGEON_CID, dungeonLevel.getCid());
    // // 本次星数
    // in.put(EventConditionKey.STAR, dungeonLevel.getStar());
    // // 关卡难度
    // in.put(EventConditionKey.DIFFICULTY, dungeonLevelCfgBean.getDifficulty());
    //
    // // 新增3星关卡通关
    // if (oldStar!=nowStar && nowStar == DungeonConstant.CHAPTER_MAX_STAR) {
    // in.put(EventConditionKey.FIRST_3_STAR, 1);
    // }
    // // 新增星数
    // int addStar = nowStar - oldStar;
    // if (addStar > 0) {
    // in.put(EventConditionKey.ADD_STAR, addStar);
    // }
    // // 参与战斗英雄数
    // Formation formation = player.getPlayerProxy().getCurrentFormation();
    // in.put(EventConditionKey.FIGHT_HERO_COUNT, formation.getStance().size());
    // // 战斗持续时间
    // in.put(EventConditionKey.FIGHT_TIME, 0);
    // if (msg != null) {
    // // 本次最大连击数
    // in.put(EventConditionKey.BATTER, msg.getBatter());
    // // 拾取数量
    // in.put(EventConditionKey.PICK_UP_COUNT, msg.getPickUpCount());
    // // 拾取类型数量
    // in.put(EventConditionKey.PICK_UP_TYPE_COUNT, msg.getPickUpTypeCount());
    // }
    //
    //
    //
    // // 副本类型
    // in.put(EventConditionKey.DUNGOEN_TYPE, dungeonLevelCfgBean.getDungeonType());
    //
    // GameEvent event = new GameEvent(player, EventType.PASS_DUP, in);
    // GameEventPlugin.syncSubmit(event);
    // }
    /** 触发关卡事件 */
    public void sceneEvent(SingleDungeonScene scene) {
        DungeonBean dungeonLevel = scene.getDungeonLevel();
        DungeonLevelCfgBean dungeonLevelCfgBean =
                GameDataManager.getDungeonLevelCfgBean(scene.getSceneCfgId());
        if (dungeonLevelCfgBean == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                    String.valueOf(scene.getSceneCfgId()), "] not exists");
        DungeonLevelGroupCfgBean groupCfgBean =
                GameDataManager.getDungeonLevelGroupCfgBean(dungeonLevelCfgBean.getLevelGroupServerID());
        if (groupCfgBean == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelGroupCfgBean [",
                    String.valueOf(dungeonLevelCfgBean.getLevelGroupServerID()), "] not exists");

        // 触发事件
        Map<String, Object> in = Maps.newHashMap();
        // 关卡ID
        in.put(EventConditionKey.DUNGEON_CID, dungeonLevel.getCid());
        // 本次星数
        in.put(EventConditionKey.STAR, dungeonLevel.getStar());
        // 关卡难度
        in.put(EventConditionKey.DIFFICULTY, dungeonLevelCfgBean.getDifficulty());

        // 新增3星关卡通关
        if (scene.getOldStar() != scene.getNowStar()
                && scene.getNowStar() == DungeonConstant.CHAPTER_MAX_STAR) {
            in.put(EventConditionKey.FIRST_3_STAR, 1);
        }
        // 新增星数
        int addStar = scene.getNowStar() - scene.getOldStar();
        if (addStar > 0) {
            in.put(EventConditionKey.ADD_STAR, addStar);
        }
        // 参与战斗英雄数
        // Formation formation = player.getPlayerProxy().getCurrentFormation();
        // in.put(EventConditionKey.FIGHT_HERO_COUNT, formation.getStance().size());
        // 战斗持续时间
        in.put(EventConditionKey.FIGHT_TIME, 0);
        // if (msg != null) {
        // 本次最大连击数
        in.put(EventConditionKey.BATTER, scene.getBatter());
        // 拾取数量
        in.put(EventConditionKey.PICK_UP_COUNT, scene.getPickUpCount());
        // 拾取类型数量
        in.put(EventConditionKey.PICK_UP_TYPE_COUNT, scene.getPickUpTypeCount());
        // }

        // 副本类型
        int dungeonType = groupCfgBean == null ? 0 : groupCfgBean.getDungeonType();
        in.put(EventConditionKey.DUNGOEN_TYPE, dungeonType);
        scene.getPlayer()._fireEvent(in, EEventType.PASS_DUP.value());
        scene.getPlayer()._fireEvent(in, EEventType.MEDIA_EVENT.value());
        // 触发主线章节激活通知
        scene.getPlayer()._fireEvent(null, EEventType.MAINDATING_ACTIVE.value());
        //关卡检测
        Map<String, Object> info = Maps.newHashMap();
        info.put(BuildingConstant.EVENT_CONDITION_ID, BuildingConstant.PASS_DUP);
        info.put(BuildingConstant.EVENT_RESULT_DATA, dungeonLevel.getCid());
        scene.getPlayer()._fireEvent(info, EEventType.CHECK_UNLOCK_BUILDING.value());
    }
}
