package logic.chasm.req;

import org.game.protobuf.c2s.C2STeamMsg;
import org.game.protobuf.c2s.C2STeamMsg.ReqLeaveTeam;
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
        order = ReqChasmOrder.REQ_LEAVE_TEAM)
public class ReqLeaveTeamEvent extends AbstractEvent {

    public ReqLeaveTeamEvent(RobotThread robot) {
        super(robot, S2CTeamMsg.RespLeaveTeam.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        S2CPlayerMsg.PlayerInfo playerInfo = robotPlayer.getPlayerInfo();
        S2CTeamMsg.TeamInfo teamInfo = robotPlayer.getTeamInfo();
//        if (teamInfo != null && playerInfo.getPid() != teamInfo.getLeaderPid()) {
            ReqLeaveTeam.Builder reqLeaveTeamBuilder = ReqLeaveTeam.newBuilder();
            SMessage msg = new SMessage(C2STeamMsg.ReqLeaveTeam.MsgID.eMsgID_VALUE,
                    reqLeaveTeamBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
//        } else {
//            super.robotSkipRun();
//        }
    }

}
