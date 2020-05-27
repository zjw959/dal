package kafka.team.action.impl;

import org.apache.log4j.Logger;
import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2f.ChangeMemberStatusHandlerParam;
import logic.chasm.bean.TeamInfo;
import logic.chasm.bean.TeamMember;
import logic.chasm.bean.TeamMember.EMemberStatus;
import logic.support.LogicScriptsUtils;
import thread.BaseHandler;
import thread.TeamProcessor;
import thread.TeamProcessorManager;

public class G2FChangeMemberStatusHandler
        implements TeamActionHandler<ChangeMemberStatusHandlerParam> {
    private static final Logger LOGGER = Logger.getLogger(G2FChangeMemberStatusHandler.class);

    @Override
    public void process(ChangeMemberStatusHandlerParam json) {
        long teamId = json.getTeamId();
        TeamInfo teamInfo = TeamProcessorManager.getInstance().getTeamInfo(teamId);
        if (teamInfo == null) {
            LOGGER.error("teamInfo not exist!teamId=" + teamId);
            return;
        }
        int processorId = teamInfo.getProcessorId();
        TeamProcessor teamProcessor =
                (TeamProcessor) TeamProcessorManager.getInstance().getRoomProcessor(processorId);
        teamProcessor.executeHandler(new BaseHandler() {

            @Override
            public void action() throws Exception {
                TeamMember member = teamInfo.getMember(json.getPlayerId());
                if (member == null)
                    return;
                member.setStatus(EMemberStatus.getTeamStatus(json.getStatus()));
                LogicScriptsUtils.getChasmScript().modifyTeamRedis(teamInfo);
                LogicScriptsUtils.getChasmScript().refreshTeam(teamInfo);
            }
        });
    }

}
