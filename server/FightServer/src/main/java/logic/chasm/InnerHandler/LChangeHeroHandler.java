package logic.chasm.InnerHandler;

import kafka.team.param.g2f.ChangeHeroParam;
import logic.chasm.bean.TeamInfo;
import logic.chasm.bean.TeamMember;
import logic.support.LogicScriptsUtils;
import thread.BaseHandler;

public class LChangeHeroHandler extends BaseHandler {
    private TeamInfo teamInfo;
    private ChangeHeroParam json;
    
    public LChangeHeroHandler(TeamInfo teamInfo, ChangeHeroParam json) {
        this.teamInfo = teamInfo;
        this.json = json;
    }
    
    @Override
    public void action() throws Exception {
        TeamMember member = teamInfo.getMember(json.getPlayerId());
        if (member == null)
            return;
        member.setHeroCid(json.getHeroCid());
        member.setSkinCid(json.getSkinCid());
        LogicScriptsUtils.getChasmScript().modifyTeamRedis(teamInfo);
        LogicScriptsUtils.getChasmScript().refreshTeam(teamInfo);
    }

}
