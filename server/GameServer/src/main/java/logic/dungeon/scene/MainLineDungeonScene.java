package logic.dungeon.scene;

import logic.dungeon.script.ISingleDungeonSceneScript;
import logic.support.LogicScriptsUtils;

/**
 * 主线副本
 * 
 * @author win7
 *
 */
public class MainLineDungeonScene extends SingleDungeonScene {

    public MainLineDungeonScene(int sceneCid) {
        super(sceneCid);
    }

    @Override
    protected boolean sceneOver(boolean normal) {
        return getSceneScript().sceneOver(this, normal);
    }

    @Override
    protected ISingleDungeonSceneScript getSceneScript() {
        return LogicScriptsUtils.getMainDungeonSceneScript();
    }
}
