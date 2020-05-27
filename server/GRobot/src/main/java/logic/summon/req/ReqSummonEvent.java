package logic.summon.req;

import java.util.ArrayList;

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
        order = ReqSummonOrder.REQ_SUMMON)
public class ReqSummonEvent extends AbstractEvent {

    public ReqSummonEvent(RobotThread robot) {
        super(robot, S2CSummonMsg.Summon.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        robotPlayer.isSummonAward = true;
        robotPlayer.summonAward = new ArrayList<>();
        
        if(robotPlayer.summonId != 0) {
            C2SSummonMsg.Summon.Builder reqSummonBuilder = C2SSummonMsg.Summon.newBuilder();
            reqSummonBuilder.setCid(robotPlayer.summonId);
            reqSummonBuilder.setCost(robotPlayer.summonCost);
            SMessage msg = new SMessage(C2SSummonMsg.Summon.MsgID.eMsgID_VALUE,
                    reqSummonBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);  
        } else {
            super.robotSkipRun();
        }
    }

}
