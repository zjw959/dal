package logic.maindating.req;

import org.game.protobuf.c2s.C2SExtraDatingMsg;
import org.game.protobuf.s2c.S2CExtraDatingMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.maindating.ReqMaindatingOrder;


@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.MAINDATING,
        order = ReqMaindatingOrder.REQ_MAINDATING_INFO)
public class ReqMainDatingInfoEvent extends AbstractEvent {

    public ReqMainDatingInfoEvent(RobotThread robot) {
        super(robot, S2CExtraDatingMsg.RespMainInfo.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        C2SExtraDatingMsg.ReqExtraDatingInfo.Builder builder =
                C2SExtraDatingMsg.ReqExtraDatingInfo.newBuilder();
        builder.setDatingType(2);
        builder.setDatingValue(1010101);
        builder.setRoleId(101);
        SMessage msg = new SMessage(C2SExtraDatingMsg.ReqExtraDatingInfo.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg);
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "请求主线约会入口信息  ");
    }

}
