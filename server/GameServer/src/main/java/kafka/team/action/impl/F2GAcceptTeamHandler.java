package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.f2g.F2GAcceptTeamHandlerParam;
import logic.support.LogicScriptsUtils;

public class F2GAcceptTeamHandler implements TeamActionHandler<F2GAcceptTeamHandlerParam> {

    @Override
    public void process(F2GAcceptTeamHandlerParam json) {
        LogicScriptsUtils.getChasmScript().acceptTeamF2G(json);
    }

}
