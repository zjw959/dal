package logic.dungeon.scene;

import logic.dungeon.script.ISingleDungeonSceneScript;
import logic.support.LogicScriptsUtils;

/**
 * 活动副本
 * 
 * @author win7
 *
 */
public class ActivityDungeonScene extends SingleDungeonScene {


    public ActivityDungeonScene(int sceneCid) {
        super(sceneCid);
    }


    @Override
    protected boolean sceneStart() {
        return getSceneScript().sceneStart(this);
    }



    @Override
    protected boolean sceneOver(boolean normal) {
        return getSceneScript().sceneOver(this, normal);

    }


    @Override
    protected ISingleDungeonSceneScript getSceneScript() {
        return LogicScriptsUtils.getActivityDungeonSceneScript();
    }

}
