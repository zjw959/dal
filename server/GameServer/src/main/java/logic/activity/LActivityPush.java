package logic.activity;

import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import gm.db.global.bean.ActivityConfigure;
import logic.character.bean.Player;
import thread.base.GameInnerHandler;

/**  
 * 玩家线程内部验证任务信息
 * @author lihongji
 *
 */
public class LActivityPush extends GameInnerHandler {
    Player player;

    ActivityConfigure config;

    ChangeType type;

    public LActivityPush(Player player, ActivityConfigure config, ChangeType type) {
        this.player = player;
        this.config = config;
        this.type = type;
    }

    @Override
    public void action() throws Exception {
//        ActivityCmdUtils.getDefault().sendSingleActivityConfigToPlayer(config, player, type);
        ActivityCmdUtils.getDefault().sendAllActivityInfo(player);
    }
}
