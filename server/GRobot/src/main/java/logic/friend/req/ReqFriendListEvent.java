package logic.friend.req;

import org.game.protobuf.c2s.C2SFriendMsg;
import org.game.protobuf.s2c.S2CFriendMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.ReqOnceOrder;

@IsEvent(eventT = EventType.REQUSET_ONCE, functionT = FunctionType.FRIEND,
        order = ReqOnceOrder.REQ_GET_FRIEND)
public class ReqFriendListEvent extends AbstractEvent {

    public ReqFriendListEvent(RobotThread robot) {
        super(robot,S2CFriendMsg.RespFriends.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        //Log4jManager.getInstance().error(robot.getWindow(),"ReqFriendListEvent");
        C2SFriendMsg.ReqFriends.Builder reqGetFriendList = C2SFriendMsg.ReqFriends.newBuilder();
        SMessage msg = new SMessage(C2SFriendMsg.ReqFriends.MsgID.eMsgID_VALUE,
                reqGetFriendList.build().toByteArray(), resOrder);
        sendMsg(msg);
        return;
    }
}
