package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2f.ExitTeamHandlerParam;
import logic.chasm.bean.TeamInfo;
import logic.support.LogicScriptsUtils;
import thread.BaseHandler;
import thread.TeamProcessor;
import thread.TeamProcessorManager;

public class G2FExitTeamHandler implements TeamActionHandler<ExitTeamHandlerParam> {
//    private static final Logger LOGGER = Logger.getLogger(G2FExitTeamHandler.class);
    @Override
    public void process(ExitTeamHandlerParam json) {
        // 1 如果是队长 队伍还有人 换队长 并把人踢走 通知所有成员
        // 2 如果队伍只剩他一个 则解散删除队伍 并通知他 离开队伍
        long teamId = json.getTeamId();
        int playerId = json.getPlayerId();
        TeamInfo teamInfo = TeamProcessorManager.getInstance().getTeamInfo(teamId);
        if (teamInfo == null) {
            return;
        }
        int processorId = teamInfo.getProcessorId();
        TeamProcessor teamProcessor =
                (TeamProcessor) TeamProcessorManager.getInstance().getRoomProcessor(processorId);
        teamProcessor.executeHandler(new BaseHandler() {
            public void action() {
                LogicScriptsUtils.getChasmScript().exitTeam(playerId, teamInfo, teamProcessor);
            }
        });
    }

}
