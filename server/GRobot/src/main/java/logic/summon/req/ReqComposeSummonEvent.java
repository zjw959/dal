package logic.summon.req;

import org.game.protobuf.c2s.C2SSummonMsg;
import org.game.protobuf.s2c.S2CSummonMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;
import logic.summon.ReqSummonOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.SUMMON,
        order = ReqSummonOrder.REQ_COMPOSE_SUMMON)
public class ReqComposeSummonEvent extends AbstractEvent {

    public ReqComposeSummonEvent(RobotThread robot) {
        super(robot, S2CSummonMsg.ComposeSummon.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        
        if(robotPlayer.composeId != 0) {
            C2SSummonMsg.ComposeSummon.Builder composeSummonBuilder = C2SSummonMsg.ComposeSummon.newBuilder();
            composeSummonBuilder.setCid(robotPlayer.composeId);
            SMessage msg = new SMessage(C2SSummonMsg.ComposeSummon.MsgID.eMsgID_VALUE,
                    composeSummonBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
        } else {
            super.robotSkipRun();
        }
    }
}
