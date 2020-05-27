package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.f2g.F2GTreatMemberHandlerParam;
import logic.support.LogicScriptsUtils;

public class F2GTreatMemberHandler implements TeamActionHandler<F2GTreatMemberHandlerParam> {

    @Override
    public void process(F2GTreatMemberHandlerParam json) {
        LogicScriptsUtils.getChasmScript().treatMemberF2G(json);
    }

}
