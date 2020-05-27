package logic.chasm.req;

import org.game.protobuf.c2s.C2STeamMsg;
import org.game.protobuf.c2s.C2STeamMsg.ReqMatchTeam;
import org.game.protobuf.c2s.C2STeamMsg.TeamFeature;
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
        order = ReqChasmOrder.REQ_MATCH_TEAM)
public class ReqMatchTeamEvent extends AbstractEvent {

    public ReqMatchTeamEvent(RobotThread robot) {
        super(robot, S2CTeamMsg.RespJoinTeam.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
//        RobotPlayer robotPlayer = robot.getPlayer();
//        if(robotPlayer.chasmDungeonId != 0 && !robotPlayer.isCreateTeam) {
//            ReqMatchTeam.Builder reqMatchTeamBuilder = ReqMatchTeam.newBuilder();
//            TeamFeature.Builder teamFeatureBuilder = TeamFeature.newBuilder();
//            teamFeatureBuilder.setTeamType(1);
//            teamFeatureBuilder.setDungeonCid(robotPlayer.chasmDungeonId);
//            SMessage msg = new SMessage(C2STeamMsg.ReqMatchTeam.MsgID.eMsgID_VALUE,
//                    reqMatchTeamBuilder.build().toByteArray(), resOrder);
//            sendMsg(msg);
//        } else {
//            super.robotSkipRun();
//        }
        
        RobotPlayer robotPlayer = robot.getPlayer();
        ReqMatchTeam.Builder reqMatchTeamBuilder = ReqMatchTeam.newBuilder();
        TeamFeature.Builder teamFeatureBuilder = TeamFeature.newBuilder();
        teamFeatureBuilder.setTeamType(1);
        teamFeatureBuilder.setDungeonCid(410001);
        reqMatchTeamBuilder.setFeature(teamFeatureBuilder);
        SMessage msg = new SMessage(C2STeamMsg.ReqMatchTeam.MsgID.eMsgID_VALUE,
                reqMatchTeamBuilder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

}
