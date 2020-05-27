package logic.item.res;

import org.game.protobuf.c2s.C2SItemMsg;
import org.game.protobuf.s2c.S2CItemMsg;

import core.event.AbstractEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;

// @IsEvent(eventT = EventType.REQUSET_ONCE, functionT = FunctionType.NULL,
// order = ReqOnceOrder.REQ_ITEM_LIST)
public class ReqOnceItemListEvent extends AbstractEvent {

    public ReqOnceItemListEvent(RobotThread robot) {
        super(robot, S2CItemMsg.ItemList.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        C2SItemMsg.GetItems.Builder reqItemList = C2SItemMsg.GetItems.newBuilder();
        SMessage msg = new SMessage(C2SItemMsg.GetItems.MsgID.eMsgID_VALUE,
                reqItemList.build().toByteArray(), resOrder);
        sendMsg(msg);
    }
}
