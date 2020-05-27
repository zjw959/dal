package kafka.team.action.impl;

import org.apache.log4j.Logger;

import kafka.team.action.TeamActionHandler;
import kafka.team.constants.TeamErrorCodeType;
import kafka.team.param.g2f.AcceptTeamHandlerParam;
import logic.chasm.InnerHandler.LAcceptTeamHandler;
import logic.chasm.bean.TeamInfo;
import logic.support.LogicScriptsUtils;
import thread.TeamProcessor;
import thread.TeamProcessorManager;

public class G2FAcceptTeamHandler implements TeamActionHandler<AcceptTeamHandlerParam> {
    private static final Logger LOGGER = Logger.getLogger(G2FAcceptTeamHandler.class);
    
    @Override
    public void process(AcceptTeamHandlerParam json) {
        long teamId = json.getTeamId();
        TeamInfo teamInfo = TeamProcessorManager.getInstance().getTeamInfo(teamId);
        if (teamInfo == null) {
            LOGGER.error("teamInfo not exist!teamId=" + teamId);
            LogicScriptsUtils.getChasmScript().sendErrorCode(json.getPlayerId(), TeamErrorCodeType.TEAM_NOT_EXIST,
                    json.getGameServerId());
            return;
        }
        int processorId = teamInfo.getProcessorId();
        TeamProcessor teamProcessor =
                (TeamProcessor) TeamProcessorManager.getInstance().getRoomProcessor(processorId);
        teamProcessor.executeHandler(new LAcceptTeamHandler(teamProcessor, json));
    }

}
