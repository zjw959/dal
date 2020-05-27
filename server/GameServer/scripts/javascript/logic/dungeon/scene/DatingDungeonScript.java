package javascript.logic.dungeon.scene;

import org.apache.log4j.Logger;

import thread.log.LogProcessor;
import utils.ExceptionEx;
import logic.character.bean.Player;
import logic.constant.EFunctionType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.dungeon.DungeonManager;
import logic.dungeon.bean.DungeonBean;
import logic.dungeon.bean.DungeonGroupBean;
import logic.dungeon.scene.DungeonScene;
import logic.dungeon.scene.SingleDungeonScene;
import logic.dungeon.script.ISingleDungeonSceneScript;
import logic.functionSwitch.FunctionSwitchService;
import logic.support.LogBeanFactory;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import data.GameDataManager;
import data.bean.DungeonLevelCfgBean;

/**
 * 约会关卡脚本
 * 
 * @author Alan
 *
 */
public class DatingDungeonScript implements ISingleDungeonSceneScript {
    private static final Logger LOGGER = Logger.getLogger(DatingDungeonScript.class);
    @Override
    public int getScriptId() {
        return EScriptIdDefine.DUNGEON_DATING_SCRIPT.Value();
    }

    @Override
    public boolean sceneOver(SingleDungeonScene scene, boolean normal) {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.NORMAL_DUNGEON)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:normal_dungeon");
        }
        // 约会关卡没有失败
        scene.setWin(true);
        try {
            LogProcessor.getInstance().sendLog(
                    LogBeanFactory.createBattleLog(scene.getPlayer(), scene.getSceneCfgId(),
                            scene.isWin() ? 1 : 0, null, EReason.DUNGEON_LOG_DATING_OVER.value(),
                            null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
        DungeonBean dungeonLevel = scene.getDungeonLevel();
        // 结算心数
        scene.setOldStar(scene.getDungeonLevel().getStar());
        if (scene.getGoals() != null && scene.getGoals().size() > 0) {
            scene.getGoals().forEach(g -> {
                if (!scene.getDungeonLevel().getAchieveGoals().contains(g)) {
                    scene.getDungeonLevel().getAchieveGoals().add(g);
                }
            });
        }
        scene.getDungeonLevel().setStar(scene.getDungeonLevel().getAchieveGoals().size());
        // 关卡结算时设置关卡通关状态，不再于约会结算设置，与星数保持同步
        scene.getDungeonLevel().setWin(true);
        scene.setNowStar(scene.getDungeonLevel().getStar());
        dungeonLevel.setSceneCount(dungeonLevel.getSceneCount() + scene.getBattleCount());

        // 获取副本组配置对象
        DungeonLevelCfgBean dungeonCfg =
                GameDataManager.getDungeonLevelCfgBean(dungeonLevel.getCid());
        if (dungeonCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                    String.valueOf(dungeonLevel.getCid()), "] not exists");
        // 原关卡与这里都是记录累计完成的目标,不需要比较原星数与心数
        // 星数与心数只会有其一,这里使用原逻辑处理心数
        scene.setOldStar(dungeonLevel.getStar());
        dungeonLevel.setStar(dungeonLevel.getAchieveGoals().size());
        scene.setNowStar(dungeonLevel.getStar());
        dungeonLevel.setTotalSceneCount(dungeonLevel.getTotalSceneCount() + scene.getBattleCount());

        DungeonManager dm = scene.getPlayer().getDungeonManager();
        DungeonGroupBean group = dm.getOrInitDungeonLevelGroup(scene.getPlayer(), dungeonCfg.getLevelGroupServerID());
        // 心数改变
        // 设置副本组总心数
        if (scene.getNowStar() > scene.getOldStar()) {
            int preStars = group.getHearts().getOrDefault(dungeonCfg.getDifficulty(), 0);
            group.getHearts().put(dungeonCfg.getDifficulty(),
                    preStars + (scene.getNowStar() - scene.getOldStar()));
        }
        notifySceneWin(scene);
        return true;

    }
    
    /** 初始化当前场景 */
    @Override
    public void sceneInit(SingleDungeonScene scene) {
        LogicScriptsUtils.getIBaseSingleDungeonSceneScript().sceneInit(scene);
    }

    /** 放置场景至相应容器 */
    @Override
    public void putScene(DungeonScene scene, Player player) {
        LogicScriptsUtils.getIBaseSingleDungeonSceneScript().putScene(scene, player);
    }

    /** 销毁当前场景 */
    @Override
    public void destroyScene(String sceneId, Player player) {
        LogicScriptsUtils.getIBaseSingleDungeonSceneScript().destroyScene(sceneId, player);
    }

    /** 副本胜利事件入口 */
    @Override
    public void notifySceneWin(SingleDungeonScene scene) {
        LogicScriptsUtils.getIBaseSingleDungeonSceneScript().notifySceneWin(scene);
    }

    @Override
    public boolean sceneStart(SingleDungeonScene scene) {
        boolean result = LogicScriptsUtils.getIBaseSingleDungeonSceneScript().sceneStart(scene);
        try {
            LogProcessor.getInstance().sendLog(
                    LogBeanFactory.createBattleLog(scene.getPlayer(), scene.getSceneCfgId(), 0,
                            null, EReason.DUNGEON_LOG_DATING_START.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
        return result;
    }
}
