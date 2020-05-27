package logic.chasm.req;

import java.util.List;

import org.game.protobuf.c2s.C2STeamMsg;
import org.game.protobuf.c2s.C2STeamMsg.ReqTreatMember;
import org.game.protobuf.s2c.S2CPlayerMsg;
import org.game.protobuf.s2c.S2CTeamMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.chasm.ReqChasmOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CHASM,
        order = ReqChasmOrder.REQ_KICK_OUT_MEMBER)
public class ReqKickOutMemberEvent extends AbstractEvent {

    public ReqKickOutMemberEvent(RobotThread robot) {
        super(robot, S2CTeamMsg.RespTreatMember.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        S2CTeamMsg.TeamInfo teamInfo = robotPlayer.getTeamInfo();
        S2CPlayerMsg.PlayerInfo playerInfo = robotPlayer.getPlayerInfo();
        if (teamInfo != null && playerInfo.getPid() == teamInfo.getLeaderPid()) {
            List<S2CTeamMsg.TeamMember> teamMembers = teamInfo.getMembersList();
            if(teamMembers.size() > 1) {
                S2CTeamMsg.TeamMember operateMember = null;
                for(S2CTeamMsg.TeamMember teamMember : teamMembers) {
                    if(teamMember.getPid() != playerInfo.getPid()) {
                        operateMember = teamMember;
                        break;
                    }
                }
                if(operateMember != null) {
                    ReqTreatMember.Builder reqTreatMemberBuilder = ReqTreatMember.newBuilder();
                    reqTreatMemberBuilder.setTargetPid(operateMember.getPid());
                    reqTreatMemberBuilder.setType(2);
                    SMessage msg = new SMessage(C2STeamMsg.ReqTreatMember.MsgID.eMsgID_VALUE,
                            reqTreatMemberBuilder.build().toByteArray(), resOrder);
                    sendMsg(msg);
                } else {
                    super.robotSkipRun();
                }
            } else {
                super.robotSkipRun(); 
            }
        } else {
            super.robotSkipRun();
        }
    }

}
