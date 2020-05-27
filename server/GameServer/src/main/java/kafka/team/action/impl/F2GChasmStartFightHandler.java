package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2f.ResChasmStartFightParam;
import logic.support.LogicScriptsUtils;

public class F2GChasmStartFightHandler implements TeamActionHandler<ResChasmStartFightParam> {
    
    @Override
    public void process(ResChasmStartFightParam param) {
        LogicScriptsUtils.getChasmScript().startChasmFightF2G(param);
    }

}
