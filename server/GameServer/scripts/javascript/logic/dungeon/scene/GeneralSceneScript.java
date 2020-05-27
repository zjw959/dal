package javascript.logic.dungeon.scene;

import java.util.List;

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
 * 普通副本脚本
 * 
 * @author Alan
 *
 */
public class GeneralSceneScript implements ISingleDungeonSceneScript {
    private static final Logger LOGGER = Logger.getLogger(GeneralSceneScript.class);
    @Override
    public int getScriptId() {
        return EScriptIdDefine.DUNGEON_GENERAL_SCRIPT.Value();
    }

    @Override
    public boolean sceneOver(SingleDungeonScene scene, boolean normal) {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.NORMAL_DUNGEON)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:normal_dungeon");
        }
        try {
            LogProcessor.getInstance().sendLog(
                    LogBeanFactory.createBattleLog(scene.getPlayer(), scene.getSceneCfgId(),
                            scene.isWin() ? 1 : 0, null, getEReasonOver(scene), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
        if (!scene.isWin())
            return true;
        DungeonBean dungeonLevel = scene.getDungeonLevel();
        // 设置玩家副本数据对象
        dungeonLevel.setWin(scene.isWin());
        dungeonLevel.setSceneCount(dungeonLevel.getSceneCount() + scene.getBattleCount());
        // 计算星
        List<Integer> goals = scene.getGoals();
        if (goals != null && goals.size() > 0) {
            goals.forEach(g -> {
                if (!dungeonLevel.getAchieveGoals().contains(g)) {
                    dungeonLevel.getAchieveGoals().add(g);
                }
            });
        }
        // 比较原星数
        scene.setOldStar(dungeonLevel.getStar());
        dungeonLevel.setStar(dungeonLevel.getAchieveGoals().size());
        scene.setNowStar(dungeonLevel.getStar());
        dungeonLevel.setTotalSceneCount(dungeonLevel.getTotalSceneCount() + scene.getBattleCount());
        // 获取副本组配置对象
        DungeonLevelCfgBean dungeonCfg =
                GameDataManager.getDungeonLevelCfgBean(dungeonLevel.getCid());
        if (dungeonCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                    String.valueOf(dungeonLevel.getCid()), "] not exists");
        DungeonManager dm = scene.getPlayer().getDungeonManager();
        DungeonGroupBean group = dm.getOrInitDungeonLevelGroup(scene.getPlayer(), dungeonCfg.getLevelGroupServerID());
        // 星数改变
        // 设置副本组总星数
        if (scene.getNowStar() > scene.getOldStar()) {
            int preStars = group.getStars().getOrDefault(dungeonCfg.getDifficulty(), 0);
            group.getStars().put(dungeonCfg.getDifficulty(),
                    preStars + (scene.getNowStar() - scene.getOldStar()));
        }
        notifySceneWin(scene);
        return true;
    }

    // 脚本动态编译模块尚未支持继承, 这里特殊处理下公共逻辑

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
                            null, getEReasonStart(scene), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
        return result;
    }

    private int getEReasonStart(SingleDungeonScene scene) {
        DungeonLevelCfgBean levelCfg =
                GameDataManager.getDungeonLevelCfgBean(scene.getSceneCfgId());
        if (levelCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                    String.valueOf(scene.getSceneCfgId()), "] does not exist");
        switch (levelCfg.getDungeonType()) {
            // 卡巴拉
//            case DungeonTypeConstant.DUNGEON_TYPE_GENERAL_kabala:
//                return EReason.DUNGEON_LOG_KABALA_START.value();
            default:
                return EReason.DUNGEON_LOG_GENERAL_START.value();
        }
    }
    
    private int getEReasonOver(SingleDungeonScene scene) {
        DungeonLevelCfgBean levelCfg =
                GameDataManager.getDungeonLevelCfgBean(scene.getSceneCfgId());
        if (levelCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                    String.valueOf(scene.getSceneCfgId()), "] does not exist");
        switch (levelCfg.getDungeonType()) {
            // 卡巴拉
//            case DungeonTypeConstant.DUNGEON_TYPE_GENERAL_kabala:
//                return EReason.DUNGEON_LOG_KABALA_OVER.value();
            default:
                return EReason.DUNGEON_LOG_GENERAL_OVER.value();
        }
    }
}
