package logic.chasm.resp;

import org.game.protobuf.s2c.S2CTeamMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CTeamMsg.TeamInfo.MsgID.eMsgID_VALUE)
public class RespTeamInfoEvent extends AbstractEvent {

    public RespTeamInfoEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            S2CTeamMsg.TeamInfo teamInfo = S2CTeamMsg.TeamInfo.parseFrom(data);
            robotPlayer.setTeamInfo(teamInfo);
        }
    }

}
