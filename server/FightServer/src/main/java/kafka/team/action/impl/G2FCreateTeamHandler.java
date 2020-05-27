package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2f.CreateTeamHandlerParam;
import logic.chasm.InnerHandler.LCreateTeamHandler;
import thread.TeamProcessor;
import thread.TeamProcessorManager;

public class G2FCreateTeamHandler implements TeamActionHandler<CreateTeamHandlerParam> {

    @Override
    public void process(CreateTeamHandlerParam json) {
        TeamProcessor teamProcessor =
                (TeamProcessor) TeamProcessorManager.getInstance().chooseLineBySequence();
        teamProcessor.executeHandler(new LCreateTeamHandler(json, teamProcessor));
    }
}
