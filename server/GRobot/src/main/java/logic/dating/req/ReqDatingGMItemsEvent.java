package logic.dating.req;

import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.dating.ReqDatingOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.DATING,
        order = ReqDatingOrder.REQ_ADD_ITEM)
public class ReqDatingGMItemsEvent extends AbstractEvent {

    public ReqDatingGMItemsEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        StringBuilder str = new StringBuilder();
//        str.append("./addItemList ");
//
//        str.append(500007);
//        str.append(":");
//        str.append(200);
//        str.append(",");
        
        str.append("./pass ");
        str.append(101101);
//        str.append("./passGroup ");
//        str.append(1);
        

        C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
        builder.setChannel(1);
        builder.setFun(1);
        builder.setContent(str.toString());
        SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), this.resOrder);
        sendMsg(msg);
    }
}
