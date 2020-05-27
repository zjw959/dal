package logic.friend.req;

import org.game.protobuf.c2s.C2SFriendMsg;
import org.game.protobuf.s2c.S2CFriendMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.friend.ReqFriendOrder;

/*@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.FRIEND,
        order = ReqFriendOrder.REQ_FRIEND_LIST_2)*/
public class ReqFriendListEvent2 extends AbstractEvent {

    public ReqFriendListEvent2(RobotThread robot) {
        super(robot,S2CFriendMsg.RespFriends.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        //Log4jManager.getInstance().error(robot.getWindow(),"ReqFriendListEvent2");
        C2SFriendMsg.ReqFriends.Builder reqGetFriendList = C2SFriendMsg.ReqFriends.newBuilder();
        SMessage msg = new SMessage(C2SFriendMsg.ReqFriends.MsgID.eMsgID_VALUE,
                reqGetFriendList.build().toByteArray(), resOrder);
        sendMsg(msg);
        return;
    }
}
