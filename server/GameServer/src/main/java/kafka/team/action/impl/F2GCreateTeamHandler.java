package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.f2g.F2GCreateTeamResultParam;
import logic.support.LogicScriptsUtils;

public class F2GCreateTeamHandler implements TeamActionHandler<F2GCreateTeamResultParam> {
    
    @Override
    public void process(F2GCreateTeamResultParam json) {
        LogicScriptsUtils.getChasmScript().createTeamF2G(json);
    }

}
