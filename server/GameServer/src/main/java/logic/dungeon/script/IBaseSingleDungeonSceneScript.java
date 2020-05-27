package logic.dungeon.script;

import logic.character.bean.Player;
import logic.dungeon.scene.DungeonScene;
import logic.dungeon.scene.SingleDungeonScene;
import script.IScript;

public interface IBaseSingleDungeonSceneScript extends IScript {

    /** 初始化当前场景 */
    public void sceneInit(SingleDungeonScene scene);

    /** 放置场景至相应容器 */
    public void putScene(DungeonScene scene, Player player);

    /** 销毁当前场景 */
    public void destroyScene(String sceneId, Player player);

    /** 场景开始运作 */
    public boolean sceneStart(SingleDungeonScene scene);

    /** 副本胜利事件入口 */
    public void notifySceneWin(SingleDungeonScene scene);

    public void sceneEvent(SingleDungeonScene scene);
}
