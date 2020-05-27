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

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.FRIEND,
        order = ReqFriendOrder.REQ_FRIEND_RECOMMEND)
public class ReqFriendRecommendListEvent extends AbstractEvent {

    public ReqFriendRecommendListEvent(RobotThread robot) {
        super(robot, S2CFriendMsg.RespRecommendFriends.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        //Log4jManager.getInstance().error(robot.getWindow(),"ReqFriendRecommendListEvent");

        C2SFriendMsg.ReqRecommendFriends.Builder reqGetFriendRecommendList =
                C2SFriendMsg.ReqRecommendFriends.newBuilder();
        SMessage msg = new SMessage(C2SFriendMsg.ReqRecommendFriends.MsgID.eMsgID_VALUE,
                reqGetFriendRecommendList.build().toByteArray(), resOrder);
        sendMsg(msg);
        return;
    }
}
