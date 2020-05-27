package logic.chasm.InnerHandler;

import kafka.team.param.g2f.AcceptTeamHandlerParam;
import logic.support.LogicScriptsUtils;
import thread.BaseHandler;
import thread.TeamProcessor;

public class LAcceptTeamHandler extends BaseHandler {
    private TeamProcessor teamProcessor;
    private AcceptTeamHandlerParam json;
    
    public LAcceptTeamHandler(TeamProcessor teamProcessor, AcceptTeamHandlerParam json) {
        this.teamProcessor = teamProcessor;
        this.json = json;
    }
    
    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getChasmScript().acceptTeam(json, teamProcessor);
    }
}
