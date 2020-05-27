package logic.chasm.InnerHandler;

import logic.chasm.bean.TeamInfo;
import logic.support.LogicScriptsUtils;
import thread.BaseHandler;

public class LDismissTeamHandler extends BaseHandler {
    private TeamInfo teamInfo;
    
    public LDismissTeamHandler(TeamInfo teamInfo) {
        this.teamInfo = teamInfo;
    }
    
    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getChasmScript().dismissTeam(teamInfo);
    }

}
