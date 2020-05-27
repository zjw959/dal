package logic.chat.req;

import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.c2s.C2SChatMsg.ReqInitChatInfo;
import org.game.protobuf.s2c.S2CChatMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.ReqOnceOrder;

@IsEvent(eventT = EventType.REQUSET_ONCE, functionT = FunctionType.NULL,
        order = ReqOnceOrder.REQ_CHAT_INIT_INFO)
public class ReqChatInitEvent extends AbstractEvent {

    public ReqChatInitEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespInitChatInfo.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        ReqInitChatInfo.Builder build = ReqInitChatInfo.newBuilder();
        SMessage msg = new SMessage(C2SChatMsg.ReqInitChatInfo.MsgID.eMsgID_VALUE,
                build.build().toByteArray(), resOrder);
        sendMsg(msg);
    }
}
