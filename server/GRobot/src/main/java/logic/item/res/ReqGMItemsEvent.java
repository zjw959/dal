package logic.item.res;

import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;

import core.event.AbstractEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.item.GoodTypes;

// @IsEvent(eventT = EventType.REQUSET_ONCE, functionT = FunctionType.NULL,
// order = ReqOnceOrder.REQ_GM_ITEMS)
public class ReqGMItemsEvent extends AbstractEvent {

    public ReqGMItemsEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");
        for (GoodTypes goodType : GoodTypes.values()) {
            str.append(goodType.getId());
            str.append(":");
            str.append(goodType.getNumber());
            str.append(",");
        }
        C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
        builder.setChannel(1);
        builder.setFun(1);
        builder.setContent(str.toString());
        SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), this.resOrder);
        sendMsg(msg);
    }
}
