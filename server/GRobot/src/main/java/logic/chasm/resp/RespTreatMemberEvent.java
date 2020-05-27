package logic.chasm.resp;

import org.game.protobuf.s2c.S2CTeamMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CTeamMsg.RespTreatMember.MsgID.eMsgID_VALUE)
public class RespTreatMemberEvent extends AbstractEvent {

    public RespTreatMemberEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {}

}
