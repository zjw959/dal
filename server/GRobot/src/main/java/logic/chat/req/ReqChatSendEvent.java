package logic.chat.req;

import java.util.concurrent.atomic.AtomicInteger;
import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.c2s.C2SChatMsg.ChatMsg;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.chat.ReqChatOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CHAT,
        order = ReqChatOrder.REQ_CHAT_SEND)
public class ReqChatSendEvent extends AbstractEvent {

    private static AtomicInteger index = new AtomicInteger();

    // S2CChatMsg.ChatInfo.MsgID.eMsgID_VALUE
    // 此处不能依赖这个回调,因为聊天的推送都是这条.
    public ReqChatSendEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        ChatMsg.Builder build = ChatMsg.newBuilder();
        build.setChannel(1).setContent("说话说话" + index.addAndGet(1)).setFun(1);
        SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                build.build().toByteArray(), resOrder);
        sendMsg(msg, true);
    }

}
