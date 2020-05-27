package logic.chasmfight.resp;

import org.game.protobuf.s2c.S2CFightMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CFightMsg.NotifyStartFight.MsgID.eMsgID_VALUE)
public class NotifyStartFightEvent extends AbstractEvent {

    public NotifyStartFightEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {}

}
