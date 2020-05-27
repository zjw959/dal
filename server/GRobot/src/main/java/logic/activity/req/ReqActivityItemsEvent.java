package logic.activity.req;

import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.activity.ReqActivityOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.ACTIVITY,
        order = ReqActivityOrder.REQ_GM_ITEMS)
public class ReqActivityItemsEvent extends AbstractEvent {

    public ReqActivityItemsEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");

        str.append(500005);
        str.append(":");
        str.append(10000);
        str.append(",");

        C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
        builder.setChannel(1);
        builder.setFun(1);
        builder.setContent(str.toString());
        SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), this.resOrder);
        sendMsg(msg);

    }

}
