package kafka.team.action.impl;

import org.apache.log4j.Logger;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2f.TreatMemberHandlerParam;
import logic.chasm.InnerHandler.LTreatMemberHandler;
import logic.chasm.bean.TeamInfo;
import thread.TeamProcessor;
import thread.TeamProcessorManager;

public class G2FTreatMemberHandler implements TeamActionHandler<TreatMemberHandlerParam> {
    private static final Logger LOGGER = Logger.getLogger(G2FTreatMemberHandler.class);
    @Override
    public void process(TreatMemberHandlerParam json) {
        long teamId = json.getTeamId();
        TeamInfo teamInfo = TeamProcessorManager.getInstance().getTeamInfo(teamId);
        if (teamInfo == null) {
            LOGGER.error("teamInfo not exist!teamId=" + teamId);
            return;
        }
        int processorId = teamInfo.getProcessorId();
        TeamProcessor teamProcessor =
                (TeamProcessor) TeamProcessorManager.getInstance().getRoomProcessor(processorId);
        teamProcessor.executeHandler(new LTreatMemberHandler(json, teamInfo, teamProcessor));
    }

}
