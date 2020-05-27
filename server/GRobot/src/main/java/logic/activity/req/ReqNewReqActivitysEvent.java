package logic.activity.req;

import org.game.protobuf.c2s.C2SActivityMsg;
import org.game.protobuf.s2c.S2CActivityMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.activity.ReqActivityOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.ACTIVITY,
        order = ReqActivityOrder.REQ_GET_ACTIVITY)
public class ReqNewReqActivitysEvent extends AbstractEvent {

    public ReqNewReqActivitysEvent(RobotThread robot) {
        super(robot, S2CActivityMsg.NewRespActivitys.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        Log4jManager.getInstance().debug(robot.getWindow(), "robot:" + robot.getName() + "请求活动信息");
        C2SActivityMsg.NewReqActivitys.Builder builder =
                C2SActivityMsg.NewReqActivitys.newBuilder();
        SMessage msg = new SMessage(C2SActivityMsg.NewReqActivitys.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }
}
