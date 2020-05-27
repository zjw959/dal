package logic.chasm.req;

import org.game.protobuf.c2s.C2STeamMsg;
import org.game.protobuf.c2s.C2STeamMsg.ReqChangeTeamStatus;
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
        order = ReqChasmOrder.REQ_CHANGE_TEAM_STATUS)
public class ReqChangeTeamStatusEvent extends AbstractEvent {

    public ReqChangeTeamStatusEvent(RobotThread robot) {
        super(robot, S2CTeamMsg.RespChangeTeamStatus.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        S2CTeamMsg.TeamInfo teamInfo = robotPlayer.getTeamInfo();
        S2CPlayerMsg.PlayerInfo playerInfo = robotPlayer.getPlayerInfo();
        if (teamInfo != null && playerInfo.getPid() == teamInfo.getLeaderPid()) {
            ReqChangeTeamStatus.Builder reqChangeTeamStatusBuilder =
                    ReqChangeTeamStatus.newBuilder();
            if (teamInfo.getStatus() == 2) {
                reqChangeTeamStatusBuilder.setStatus(1);
            }
            SMessage msg = new SMessage(C2STeamMsg.ReqChangeTeamStatus.MsgID.eMsgID_VALUE,
                    reqChangeTeamStatusBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
        } else {
            super.robotSkipRun();
        }
    }

}
