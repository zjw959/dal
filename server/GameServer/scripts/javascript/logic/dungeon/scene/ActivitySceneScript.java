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
import logic.dungeon.scene.DungeonCheck;
import logic.dungeon.scene.DungeonScene;
import logic.dungeon.scene.SingleDungeonScene;
import logic.dungeon.script.ISingleDungeonSceneScript;
import logic.functionSwitch.FunctionSwitchService;
import logic.msgBuilder.DungeonMsgBuilder;
import logic.support.LogBeanFactory;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import data.GameDataManager;
import data.bean.DungeonLevelCfgBean;
import data.bean.DungeonLevelGroupCfgBean;

/**
 * @author Administrator
 *
 */
public class ActivitySceneScript implements ISingleDungeonSceneScript {
    private static final Logger LOGGER = Logger.getLogger(ActivitySceneScript.class);
    @Override
    public int getScriptId() {
        return EScriptIdDefine.DUNGEON_ACT_SCRIPT.Value();
    }

    @Override
    public boolean sceneStart(SingleDungeonScene scene) {
        // 开始检查
        if (!LogicScriptsUtils.getIBaseSingleDungeonSceneScript().sceneStart(scene))
            return false;
        DungeonLevelCfgBean dungeonLevelCfg =
                GameDataManager.getDungeonLevelCfgBean(scene.getSceneCfgId());
        if (dungeonLevelCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelCfgBean [",
                    String.valueOf(scene.getSceneCfgId()), "] not exists");
        DungeonLevelGroupCfgBean dungeonLevelGroupCfg =
                GameDataManager.getDungeonLevelGroupCfgBean(dungeonLevelCfg.getLevelGroupServerID());
        if (dungeonLevelGroupCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "DungeonLevelGroupCfgBean [",
                    String.valueOf(dungeonLevelCfg.getLevelGroupServerID()), "] not exists");
        DungeonCheck.checkLevelGroupIsOpenTime(dungeonLevelGroupCfg);
        DungeonCheck.checkLevelGroupSceneCount(scene.getPlayer(), dungeonLevelGroupCfg, scene.getBattleCount());
        DungeonCheck.checkPlayerLevel(scene.getPlayer(), dungeonLevelCfg);
        try {
            LogProcessor.getInstance().sendLog(
                    LogBeanFactory.createBattleLog(scene.getPlayer(), scene.getSceneCfgId(), 0,
                            null, EReason.DUNGEON_LOG_ACTIVITY_START.value(), null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
        return true;
    }

    @Override
    public boolean sceneOver(SingleDungeonScene scene, boolean normal) {
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.DAILY_DUNGEON)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:daily_dungeon");
        }
        try {
            LogProcessor.getInstance().sendLog(
                    LogBeanFactory.createBattleLog(scene.getPlayer(), scene.getSceneCfgId(),
                            scene.isWin() ? 1 : 0, null, EReason.DUNGEON_LOG_ACTIVITY_OVER.value(),
                            null));
        } catch (Exception ex) {
            LOGGER.error(ExceptionEx.e2s(ex));
        }
        if (!scene.isWin())
            return true;
        DungeonBean dungeonLevel = scene.getDungeonLevel();
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
        // 日常不再记录星数
        dungeonLevel.setStar(0);
        dungeonLevel.setTotalSceneCount(dungeonLevel.getTotalSceneCount()  + scene.getBattleCount());
        // 根据关联参数获取主线副本配置对象
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
        group.setSceneCount(group.getSceneCount() + 1);
        // 设置多倍收益失效
        group.setMultipleRewardCount(0);
        // 活动副本完结需要主动推送
        MessageUtils.send(scene.getPlayer(), DungeonMsgBuilder.getUpdateLevelGroupInfo(group));
        // 触发事件
        LogicScriptsUtils.getIBaseSingleDungeonSceneScript().sceneEvent(scene);
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

}
