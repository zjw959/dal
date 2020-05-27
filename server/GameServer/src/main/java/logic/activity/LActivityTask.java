package logic.activity;

import logic.character.bean.Player;
import thread.base.GameInnerHandler;


/**
 * 玩家线程内部验证任务信息
 * 
 * @author lihongji
 *
 */
public class LActivityTask extends GameInnerHandler {
    Player player;

    public LActivityTask(Player player) {
        this.player = player;
    }

    @Override
    public void action() throws Exception {
        player.getActivityManager().activityInit();
    }
}
