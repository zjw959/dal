package logic.summon.req;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.game.protobuf.c2s.C2SSummonMsg;
import org.game.protobuf.c2s.C2SSummonMsg.ReqHistoryRecord;
import org.game.protobuf.s2c.S2CSummonMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.summon.ReqSummonOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.SUMMON,
        order = ReqSummonOrder.REQ_HISTORY_RECORD)
public class ReqHistoryRecordEvent extends AbstractEvent {

    public ReqHistoryRecordEvent(RobotThread robot) {
        super(robot, S2CSummonMsg.ResHistoryRecord.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        List<Integer> types = new ArrayList<>();
        types.add(1);
        types.add(2);
        types.add(3);
        int index = RandomUtils.nextInt(types.size());

        ReqHistoryRecord.Builder reqHistoryRecordBuilder = ReqHistoryRecord.newBuilder();
        reqHistoryRecordBuilder.addType(types.get(index));
        SMessage msg = new SMessage(C2SSummonMsg.ReqHistoryRecord.MsgID.eMsgID_VALUE,
                reqHistoryRecordBuilder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

}
