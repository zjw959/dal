package logic.character.handler;

import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import logic.character.PlayerViewService;
import logic.character.bean.Player;
import logic.dungeon.DungeonManager;
import logic.friend.FriendManager;
import logic.friend.bean.FriendView;
import logic.msgBuilder.FriendMsgBuilder;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = org.game.protobuf.c2s.C2SPlayerMsg.ReqHelpFightPlayers.class)
public class MReqHelpFightPlayersHandler extends MessageHandler {
    private static final Logger LOGGER = Logger.getLogger(MReqHelpFightPlayersHandler.class);

    @Override
    public void action() throws Exception {
        Player player = (Player) getGameData();
        if (player == null) {
            LOGGER.error(this.getClass().getName() + " can not find player ");
            return;
        }
        // 检查清理好友助战CD
        DungeonManager dm = player.getDungeonManager();
        dm.checkHelpFightCD();
        // 获取助战路人（来自于推荐好友）
        FriendManager fm = player.getFriendManager();
        long ftime = fm.getLastFreshTime();
        long dis = System.currentTimeMillis() - ftime;
        if (fm.getRecommends() == null || dis > 1 * DateUtils.MILLIS_PER_MINUTE) {
            // 获取助战玩家（异步）
            ((PlayerViewService) PlayerViewService.getInstance())
                    .getHelpFightPlayers(player,
                    this.getMessage().getId());
            return;
        } else {
            List<FriendView> players = fm.getRecommends();
            FriendMsgBuilder.sendHelpFightPlayers(player, players);
        }
    }
}
