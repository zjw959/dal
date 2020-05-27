package logic.chasm.InnerHandler;

import kafka.team.param.g2f.ChangeStatusHandlerParam;
import logic.chasm.bean.TeamInfo;
import logic.support.LogicScriptsUtils;
import thread.BaseHandler;

public class LChangeStatusHandler extends BaseHandler {
    private ChangeStatusHandlerParam json;
    private TeamInfo teamInfo;
    
    public LChangeStatusHandler(ChangeStatusHandlerParam json, TeamInfo teamInfo) {
        this.json = json;
        this.teamInfo = teamInfo;
    }
    
    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getChasmScript().changeStatus(json, teamInfo);
    }

}
