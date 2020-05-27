package logic.chasm.InnerHandler;

import kafka.team.param.g2f.CreateTeamHandlerParam;
import logic.support.LogicScriptsUtils;
import thread.BaseHandler;
import thread.TeamProcessor;

public class LCreateTeamHandler extends BaseHandler {
    private CreateTeamHandlerParam json;
    private TeamProcessor teamProcessor;
    
    public LCreateTeamHandler(CreateTeamHandlerParam json, TeamProcessor teamProcessor) {
        this.json = json;
        this.teamProcessor = teamProcessor;
    }
    
    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getChasmScript().createTeam(json, teamProcessor);
    }

}
