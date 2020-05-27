package javascript.logic.dungeon.scene;

import logic.character.bean.Player;
import logic.constant.DungeonConstant;
import logic.constant.EScriptIdDefine;
import logic.dungeon.DungeonManager;
import logic.dungeon.bean.DungeonBean;
import logic.dungeon.scene.DungeonScene;
import logic.dungeon.scene.SingleDungeonScene;
import logic.dungeon.script.ISingleDungeonSceneScript;
import logic.support.LogicScriptsUtils;
import data.GameDataManager;
import data.bean.MainLineCfgBean;

public class MainLineSceneScript implements ISingleDungeonSceneScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.DUNGEON_MAIN_SCRIPT.Value();
    }

    @Override
    public boolean sceneOver(SingleDungeonScene scene, boolean normal) {
        if (!scene.isWin())
            return true;
        DungeonBean dungeonLevel = scene.getDungeonLevel();
        // 主线副本无星级
        // 设置玩家副本数据对象
        dungeonLevel.setWin(scene.isWin());
        dungeonLevel.setSceneCount(dungeonLevel.getSceneCount() + scene.getBattleCount());
        dungeonLevel.setStar(dungeonLevel.getAchieveGoals().size());
        dungeonLevel.setTotalSceneCount(dungeonLevel.getTotalSceneCount() + scene.getBattleCount());

        // MainLineCfgBean mainLineCfgBean =
        // MainLineCfgBeanCache.me().getByTypeArgument(DungeonConstant.MAIN_LINE_TYPE_CHAPTER,dungeonLevel.getCid()
        // + "");
        // 根据关联参数获取主线副本配置对象
        MainLineCfgBean mainLineCfgBean = null;
        for (MainLineCfgBean entry : GameDataManager.getMainLineCfgBeans()) {
            if (entry.getType() != DungeonConstant.MAIN_LINE_TYPE_CHAPTER)
                continue;
            if (!String.valueOf(dungeonLevel.getCid()).equals(entry.getArgument()))
                continue;
            mainLineCfgBean = entry;
            break;
        }
        if (mainLineCfgBean != null) {
            DungeonManager dm = scene.getPlayer().getDungeonManager();
            dm.promoteStory(mainLineCfgBean.getId());
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
        return LogicScriptsUtils.getIBaseSingleDungeonSceneScript().sceneStart(scene);
    }
}
