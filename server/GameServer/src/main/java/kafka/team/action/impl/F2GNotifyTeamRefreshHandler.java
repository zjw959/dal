package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.f2g.F2GNotifyTeamRefreshParam;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.chasm.TeamService;

public class F2GNotifyTeamRefreshHandler implements TeamActionHandler<F2GNotifyTeamRefreshParam> {

    @Override
    public void process(F2GNotifyTeamRefreshParam json) {
        // 给所有人发一个5898
        Player player = PlayerManager.getPlayerByPlayerId(json.getPlayerId());
        TeamService.getDefault().sendTeamInfoMsg(player, json.getTeamId());
    }

}
