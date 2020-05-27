package kafka.team.action.impl;

import kafka.team.action.TeamActionHandler;
import kafka.team.param.g2f.ReqChasmFightReviveParam;
import logic.chasm.bean.TeamInfo;
import logic.support.LogicScriptsUtils;
import thread.BaseHandler;
import thread.TeamProcessor;
import thread.TeamProcessorManager;

public class G2FChasmFightReviveHandler implements TeamActionHandler<ReqChasmFightReviveParam> {
    
    @Override
    public void process(ReqChasmFightReviveParam param) {
        TeamInfo teamInfo = TeamProcessorManager.getInstance().getTeamInfo(param.getTeamId());
        if(teamInfo != null) {
            TeamProcessor teamProcessor = (TeamProcessor) TeamProcessorManager.getInstance()
                    .getRoomProcessor(teamInfo.getProcessorId());
            teamProcessor.executeHandler(new BaseHandler() {

                @Override
                public void action() throws Exception {
                    LogicScriptsUtils.getChasmScript().reqChasmFightRevive(teamInfo, param);
                }
            });
        }
    }
}
