package logic.heroskill.resp;

import org.game.protobuf.s2c.S2CHeroMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CHeroMsg.ResModifyStrategyName.MsgID.eMsgID_VALUE)
public class ResModifyStrategyNameEvent extends AbstractEvent {

    public ResModifyStrategyNameEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {}
}
