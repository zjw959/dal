package logic.endless.req;

import logic.endless.EndlessCloisterOrder;

import org.game.protobuf.c2s.C2SEndlessCloisterMsg;
import org.game.protobuf.s2c.S2CEndlessCloisterMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.ENDLESS,
        order = EndlessCloisterOrder.ENDLESS_INFO)
public class ReqEndlessCloisterInfo extends AbstractEvent {

    public ReqEndlessCloisterInfo(RobotThread robot) {
        super(robot, S2CEndlessCloisterMsg.RspEndlessCloisterInfo.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        C2SEndlessCloisterMsg.ReqEndlessCloisterInfo.Builder builder =
                C2SEndlessCloisterMsg.ReqEndlessCloisterInfo.newBuilder();
        SMessage msg =
                new SMessage(C2SEndlessCloisterMsg.ReqEndlessCloisterInfo.MsgID.eMsgID_VALUE,
                        builder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }
}
