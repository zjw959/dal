package logic.chasm.InnerHandler;

import kafka.team.param.g2f.TreatMemberHandlerParam;
import logic.chasm.bean.TeamInfo;
import logic.support.LogicScriptsUtils;
import thread.BaseHandler;
import thread.TeamProcessor;

public class LTreatMemberHandler extends BaseHandler {
    private TreatMemberHandlerParam json;
    private TeamInfo teamInfo;
    private TeamProcessor teamProcessor;
    
    public LTreatMemberHandler(TreatMemberHandlerParam json, TeamInfo teamInfo, TeamProcessor teamProcessor) {
        this.json = json;
        this.teamInfo = teamInfo;
        this.teamProcessor = teamProcessor;
    }
    
    @Override
    public void action() throws Exception {
        LogicScriptsUtils.getChasmScript().treatMember(json, teamInfo, teamProcessor);
    }

}
