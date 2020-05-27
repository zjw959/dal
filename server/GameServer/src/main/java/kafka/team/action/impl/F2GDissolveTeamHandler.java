package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.f2g.F2GDissolveTeamParam;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.chasm.InnerHandler.LDissolveTeamHandler;
import thread.player.PlayerProcessorManager;

public class F2GDissolveTeamHandler implements TeamActionHandler<F2GDissolveTeamParam> {

    @Override
    public void process(F2GDissolveTeamParam json) {
        Player player = PlayerManager.getPlayerByPlayerId(json.getPlayerId());
        PlayerProcessorManager.getInstance().addPlayerHandler(player, new LDissolveTeamHandler(player));
    }

}
