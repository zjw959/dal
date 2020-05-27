package logic.chasm.req;

import java.util.List;
import java.util.Map;

import org.game.protobuf.c2s.C2STeamMsg;
import org.game.protobuf.c2s.C2STeamMsg.ReqChangeHero;
import org.game.protobuf.s2c.S2CPlayerMsg.PlayerInfo;
import org.game.protobuf.s2c.S2CTeamMsg;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.chasm.ReqChasmOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CHASM,
        order = ReqChasmOrder.REQ_CHANGE_HERO)
public class ReqChangeHeroEvent extends AbstractEvent {

    public ReqChangeHeroEvent(RobotThread robot) {
        super(robot, S2CTeamMsg.RespChangeHero.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        S2CTeamMsg.TeamInfo teamInfo = robotPlayer.getTeamInfo();
        PlayerInfo playerInfo = robotPlayer.getPlayerInfo();
        if(teamInfo != null) {
            int useHeroCid = 0;
            List<S2CTeamMsg.TeamMember> teamMembers = teamInfo.getMembersList();
            for(S2CTeamMsg.TeamMember teamMember : teamMembers) {
                if(teamMember.getPid() == playerInfo.getPid()) {
                    useHeroCid = teamMember.getHeroCid();
                    break;
                }
            }
            
            int changeHeroCid = 0;
            Map<Integer, HeroInfo> heros = robotPlayer.getHeros();
            for(Map.Entry<Integer, HeroInfo> entry : heros.entrySet()) {
                if(entry.getKey() != useHeroCid) {
                    changeHeroCid = entry.getKey();
                    break;
                }
            }
            
            if(changeHeroCid != 0) {
                ReqChangeHero.Builder reqChangeHeroBuilder = ReqChangeHero.newBuilder();
                reqChangeHeroBuilder.setHeroCid(changeHeroCid);
                SMessage msg = new SMessage(C2STeamMsg.ReqChangeHero.MsgID.eMsgID_VALUE,
                        reqChangeHeroBuilder.build().toByteArray(), resOrder);
                sendMsg(msg);
            } else {
                super.robotSkipRun();
            }
        } else {
            super.robotSkipRun();
        }
    }

}
