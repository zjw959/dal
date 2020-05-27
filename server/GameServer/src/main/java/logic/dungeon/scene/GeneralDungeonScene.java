package logic.dungeon.scene;

import logic.dungeon.script.ISingleDungeonSceneScript;
import logic.support.LogicScriptsUtils;

/**
 * 常规副本
 * 
 * @author win7
 *
 */
public class GeneralDungeonScene extends SingleDungeonScene {

    public GeneralDungeonScene(int sceneCid) {
        super(sceneCid);
    }

    @Override
    protected boolean sceneOver(boolean normal) {
        return getSceneScript().sceneOver(this, normal);
    }

    @Override
    protected ISingleDungeonSceneScript getSceneScript() {
        return LogicScriptsUtils.getGeneralDungeonSceneScript();
    }

}
