package kafka.team.action.impl;

import org.game.protobuf.s2c.S2CTeamMsg.RespLeaveTeam;
import kafka.team.action.TeamActionHandler;
import kafka.team.param.f2g.F2GExitTeamHandlerParam;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.chasm.TeamRedisService;
import logic.chasm.TeamService;
import logic.chasm.bean.TeamLeaveType;
import logic.support.MessageUtils;

public class F2GExitTeamHandler implements TeamActionHandler<F2GExitTeamHandlerParam> {

    @Override
    public void process(F2GExitTeamHandlerParam json) {
        if (json.getLeaver() == json.getPlayerId()) {
            TeamRedisService.delPlayerTeamId(json.getPlayerId());
            TeamRedisService.delPlayerTeamTime(json.getPlayerId());
        }
        Player player = PlayerManager.getPlayerByPlayerId(json.getPlayerId());
        if (player == null || !player.isOnline()) {
            return;
        }
        if (json.getLeaver() == json.getPlayerId()) {
            RespLeaveTeam.Builder builder =
                    RespLeaveTeam.newBuilder().setType(TeamLeaveType.ACTIVE.getType());
            MessageUtils.send(player, builder);
            return;
        }
        TeamService.getDefault().sendTeamInfoMsg(player, json.getTeamId());
    }

}
