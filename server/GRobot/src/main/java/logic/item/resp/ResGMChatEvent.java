package logic.item.resp;

import org.game.protobuf.s2c.S2CChatMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE)
public class ResGMChatEvent extends AbstractEvent {

    public ResGMChatEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {}

}
