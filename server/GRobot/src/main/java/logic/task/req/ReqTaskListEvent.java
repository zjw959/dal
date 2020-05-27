package logic.task.req;

import org.game.protobuf.c2s.C2STaskMsg;
import org.game.protobuf.s2c.S2CTaskMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.ReqOnceOrder;

@IsEvent(eventT = EventType.REQUSET_ONCE, functionT = FunctionType.NULL,
        order = ReqOnceOrder.REQ_GET_TASK_LIST)
public class ReqTaskListEvent extends AbstractEvent {

    public ReqTaskListEvent(RobotThread robot) {
        super(robot, S2CTaskMsg.RespTasks.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        C2STaskMsg.ReqTasks.Builder builder = C2STaskMsg.ReqTasks.newBuilder();
        SMessage message = new SMessage(C2STaskMsg.ReqTasks.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(message);
    }

}
