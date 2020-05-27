package kafka.team.action.impl;

import org.apache.log4j.Logger;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2f.ChangeStatusHandlerParam;
import logic.chasm.InnerHandler.LChangeStatusHandler;
import logic.chasm.bean.TeamInfo;
import thread.TeamProcessor;
import thread.TeamProcessorManager;

public class G2FChangeStatusHandler implements TeamActionHandler<ChangeStatusHandlerParam> {
    private static final Logger LOGGER = Logger.getLogger(G2FChangeStatusHandler.class);
    
    @Override
    public void process(ChangeStatusHandlerParam json) {
        long teamId = json.getTeamId();
        TeamInfo teamInfo = TeamProcessorManager.getInstance().getTeamInfo(teamId);
        if (teamInfo == null) {
            LOGGER.error("teamInfo not exist!teamId=" + teamId);
            return;
        }
        int processorId = teamInfo.getProcessorId();
        TeamProcessor teamProcessor =
                (TeamProcessor) TeamProcessorManager.getInstance().getRoomProcessor(processorId);
        teamProcessor.executeHandler(new LChangeStatusHandler(json,teamInfo));
    }

}
