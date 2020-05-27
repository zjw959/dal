package logic.chasm.handler;

import org.game.protobuf.c2s.C2STeamMsg;
import logic.character.bean.Player;
import logic.chasm.TeamRedisService;
import logic.constant.GameErrorCode;
import logic.support.MessageUtils;
import message.MHandler;
import message.MessageHandler;

@MHandler(messageClazz = C2STeamMsg.ReqChangeMemberStatus.class)
public class MReqChangeMemberStatusHandler extends MessageHandler {

    @Override
    public void action() throws Exception {
        Player player = (Player) getGameData();
        long teamId = TeamRedisService.getPlayerTeamId(player.getPlayerId());
        if (teamId == 0) {
            // 返回客户端没有队伍
            MessageUtils.throwCondtionError(GameErrorCode.NOT_YET_JOIN_TEAM, player.getPlayerId() + "还没有队伍");
            return;
        }
        C2STeamMsg.ReqChangeMemberStatus msg =
                (C2STeamMsg.ReqChangeMemberStatus) getMessage().getData();
        int status = msg.getStatus();
        player.getTeamDungeonManager().changeMemberStatus(player.getPlayerId(), teamId, status);
        MessageUtils.returnEmptyBody();
    }

}
