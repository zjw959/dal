package logic.chasm.InnerHandler;

import logic.chasm.bean.TeamInfo;
import logic.support.LogicScriptsUtils;
import thread.BaseHandler;
import thread.TeamProcessor;

public class LExitTeamHandler extends BaseHandler {
    private int pid;
    private long teamId;
    private TeamProcessor teamProcessor;
    
    public LExitTeamHandler(int pid, long teamId, TeamProcessor teamProcessor) {
        this.pid = pid;
        this.teamId = teamId;
        this.teamProcessor = teamProcessor;
    }
    
    @Override
    public void action() throws Exception {
        TeamInfo teamInfo = teamProcessor.getRoom(teamId);
        // kcp依赖超时关闭，所以此处为空时玩家已经退出房间了
        if(teamInfo != null) {
            LogicScriptsUtils.getChasmScript().exitTeam(pid, teamInfo, teamProcessor);
        }
    }
    
}
