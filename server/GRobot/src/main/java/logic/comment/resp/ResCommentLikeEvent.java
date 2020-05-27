package logic.comment.resp;

import org.game.protobuf.s2c.S2CCommentMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CCommentMsg.RespPrise.MsgID.eMsgID_VALUE)
public class ResCommentLikeEvent extends AbstractEvent {

    public ResCommentLikeEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {}

}
