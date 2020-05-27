package logic.chasmfight.req;

import org.game.protobuf.c2s.C2SChasmMsg;
import org.game.protobuf.c2s.C2SChasmMsg.ReqEnterChasm;
import org.game.protobuf.s2c.S2CChasmMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.chasmfight.ReqChasmFightOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CHASM_FIGHT,
        order = ReqChasmFightOrder.REQ_ENTER_CHASM)
public class ReqEnterChasmEvent extends AbstractEvent {

    public ReqEnterChasmEvent(RobotThread robot) {
        super(robot, S2CChasmMsg.RsepEnterChasm.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        ReqEnterChasm.Builder reqEnterChasmBuilder = ReqEnterChasm.newBuilder();
        SMessage msg = new SMessage(C2SChasmMsg.ReqEnterChasm.MsgID.eMsgID_VALUE,
                reqEnterChasmBuilder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

}
