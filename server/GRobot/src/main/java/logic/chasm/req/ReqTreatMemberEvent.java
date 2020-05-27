//package logic.chasm.req;
//
//import java.util.List;
//
//import org.game.protobuf.c2s.C2STeamMsg;
//import org.game.protobuf.c2s.C2STeamMsg.ReqTreatMember;
//import org.game.protobuf.s2c.S2CPlayerMsg;
//import org.game.protobuf.s2c.S2CTeamMsg;
//
//import core.event.AbstractEvent;
//import core.event.EventType;
//import core.event.FunctionType;
//import core.event.IsEvent;
//import core.net.message.SMessage;
//import core.robot.RobotThread;
//import logic.chasm.ReqChasmOrder;
//import logic.robot.entity.RobotPlayer;
//
//@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CHASM,
//        order = ReqChasmOrder.REQ_TREAT_MEMBER)
//public class ReqTreatMemberEvent extends AbstractEvent {
//
//    public ReqTreatMemberEvent(RobotThread robot) {
//        super(robot, S2CTeamMsg.RespTreatMember.MsgID.eMsgID_VALUE);
//    }
//
//    @Override
//    public void action(Object... obj) throws Exception {
//        RobotPlayer robotPlayer = robot.getPlayer();
//        S2CTeamMsg.TeamInfo teamInfo = robotPlayer.getTeamInfo();
//        S2CPlayerMsg.PlayerInfo playerInfo = robotPlayer.getPlayerInfo();
//        if (teamInfo != null && playerInfo.getPid() == teamInfo.getLeaderPid()) {
//            List<S2CTeamMsg.TeamMember> teamMembers = teamInfo.getMembersList();
//            if (teamMembers.size() > 1) {
//                for(S2CTeamMsg.TeamMember teamMember : teamMembers) {
//                    if(teamMember.getPid() != playerInfo.getPid()) {
//                        ReqTreatMember.Builder reqTreatMemberBuilder = ReqTreatMember.newBuilder();
//                        reqTreatMemberBuilder.setTargetPid(teamMember.getPid());
//                        reqTreatMemberBuilder.setType(1);
//                        SMessage msg = new SMessage(C2STeamMsg.ReqTreatMember.MsgID.eMsgID_VALUE,
//                                reqTreatMemberBuilder.build().toByteArray(), resOrder);
//                        sendMsg(msg);
//                        break;
//                    }
//                }
//            } else {
//                super.robotSkipRun();
//            }
//        } else {
//            super.robotSkipRun();
//        }
//    }
//
//}
