package logic.chasm.resp;

import org.game.protobuf.s2c.S2CTeamMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CTeamMsg.RespJoinTeam.MsgID.eMsgID_VALUE)
public class RespJoinTeamEvent extends AbstractEvent {

    public RespJoinTeamEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            S2CTeamMsg.RespJoinTeam respJoinTeam = S2CTeamMsg.RespJoinTeam.parseFrom(data);
            S2CTeamMsg.TeamInfo teamInfo = respJoinTeam.getTeam();
            robotPlayer.setTeamInfo(teamInfo);
        }
    }

}
