package logic.chat.change;

import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.c2s.C2SChatMsg.ReqChangeRoom;
import org.game.protobuf.s2c.S2CChatMsg;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.ReqOnceOrder;

@IsEvent(eventT = EventType.REQUSET_ONCE, functionT = FunctionType.CHAT_CHANGE,
        order = ReqOnceOrder.REQ_CHAT_CHANGE_ROOM)
public class ReqChatChangeRoomEvent extends AbstractEvent {

    public ReqChatChangeRoomEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespChangeRoom.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        // TODO 让机器人把99号房间灌满
        ReqChangeRoom.Builder build = ReqChangeRoom.newBuilder().setRoomId(99);
        SMessage msg = new SMessage(C2SChatMsg.ReqChangeRoom.MsgID.eMsgID_VALUE,
                build.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

}
