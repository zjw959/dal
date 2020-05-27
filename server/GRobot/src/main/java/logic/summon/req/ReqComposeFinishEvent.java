package logic.summon.req;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
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
        order = ReqSummonOrder.REQ_COMPOSE_FINISH)
public class ReqComposeFinishEvent extends AbstractEvent {

    public ReqComposeFinishEvent(RobotThread robot) {
        super(robot, S2CSummonMsg.ComposeFinish.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        Map<Integer, Integer> composeInfos = robotPlayer.getComposeInfos();
        List<Integer> types = new ArrayList<>();
        for(Map.Entry<Integer, Integer> entry : composeInfos.entrySet()) {
            int type = entry.getKey();
            int time = entry.getValue();
            if((System.currentTimeMillis() / 1000) > time) {
                types.add(type);
            }
        }
        if(types.size() > 0) {
            int index = RandomUtils.nextInt(types.size());
            C2SSummonMsg.ComposeFinish.Builder composeFinishBuilder = C2SSummonMsg.ComposeFinish.newBuilder();
            composeFinishBuilder.setZPointType(types.get(index));
            SMessage msg = new SMessage(C2SSummonMsg.ComposeFinish.MsgID.eMsgID_VALUE,
                    composeFinishBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
        }else{
            super.robotSkipRun();
        }
        
    }

}
