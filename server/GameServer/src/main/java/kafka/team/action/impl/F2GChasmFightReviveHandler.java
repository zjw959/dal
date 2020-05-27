package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.f2g.F2GChasmFightReviveParam;
import logic.support.LogicScriptsUtils;

public class F2GChasmFightReviveHandler implements TeamActionHandler<F2GChasmFightReviveParam> {

    @Override
    public void process(F2GChasmFightReviveParam param) {
        LogicScriptsUtils.getChasmScript().chasmFightReviveF2G(param);
    }
    
}
