package logic.chasm.req;

import java.util.List;

import org.game.protobuf.c2s.C2STeamMsg;
import org.game.protobuf.c2s.C2STeamMsg.ReqChangeMemberStatus;
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
        order = ReqChasmOrder.REQ_CHANGE_MEMBER_STATUS)
public class ReqChangeMemberStatusEvent extends AbstractEvent {

    public ReqChangeMemberStatusEvent(RobotThread robot) {
        super(robot, S2CTeamMsg.RespChangeMenberStatus.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        S2CTeamMsg.TeamInfo teamInfo = robotPlayer.getTeamInfo();
        S2CPlayerMsg.PlayerInfo playerInfo = robotPlayer.getPlayerInfo();
        if (teamInfo != null && playerInfo.getPid() != teamInfo.getLeaderPid()) {
            List<S2CTeamMsg.TeamMember> teamMembers = teamInfo.getMembersList();
            for(S2CTeamMsg.TeamMember teamMember : teamMembers) {
                if(teamMember.getPid() == playerInfo.getPid()) {
                    ReqChangeMemberStatus.Builder reqChangeMemberStatusBuilder = ReqChangeMemberStatus.newBuilder();
                    if(teamMember.getStatus() == 1) {
                        reqChangeMemberStatusBuilder.setStatus(2);
                    }
                    SMessage msg = new SMessage(C2STeamMsg.ReqChangeMemberStatus.MsgID.eMsgID_VALUE,
                            reqChangeMemberStatusBuilder.build().toByteArray(), resOrder);
                    sendMsg(msg);
                    break;
                }
            }
        } else {
            super.robotSkipRun();
        }
    }

}
