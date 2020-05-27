package logic.store.req;

import org.game.protobuf.c2s.C2SStoreMsg;
import org.game.protobuf.s2c.S2CStoreMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.store.ReqStoreOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.STORE,
        order = ReqStoreOrder.BUY_HISTORY)
public class ReqBuyHistoryEvent extends AbstractEvent {

    public ReqBuyHistoryEvent(RobotThread robot) {
        super(robot, S2CStoreMsg.CommodityBuyLogs.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        C2SStoreMsg.GetCommodityBuyLog.Builder builder =
                C2SStoreMsg.GetCommodityBuyLog.newBuilder();
        SMessage message = new SMessage(C2SStoreMsg.GetCommodityBuyLog.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(message);
    }

}
