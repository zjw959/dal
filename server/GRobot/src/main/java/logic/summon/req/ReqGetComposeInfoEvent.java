package logic.summon.req;

import org.game.protobuf.c2s.C2SSummonMsg;
import org.game.protobuf.s2c.S2CSummonMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.summon.ReqSummonOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.SUMMON,
        order = ReqSummonOrder.REQ_GET_COMPOSE_INFO)
public class ReqGetComposeInfoEvent extends AbstractEvent {

    public ReqGetComposeInfoEvent(RobotThread robot) {
        super(robot, S2CSummonMsg.GetComposeInfo.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        C2SSummonMsg.GetComposeInfo.Builder reqGetComposeInfoBuilder =
                C2SSummonMsg.GetComposeInfo.newBuilder();
        SMessage msg = new SMessage(C2SSummonMsg.GetComposeInfo.MsgID.eMsgID_VALUE,
                reqGetComposeInfoBuilder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

}
