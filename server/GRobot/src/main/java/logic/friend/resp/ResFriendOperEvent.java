package logic.friend.resp;

import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CFriendMsg;
import org.game.protobuf.s2c.S2CFriendMsg.FriendInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.friend.FriendConstants;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CFriendMsg.RespOperate.MsgID.eMsgID_VALUE)
public class ResFriendOperEvent extends AbstractEvent {

    public ResFriendOperEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CFriendMsg.RespOperate friendListMsg = S2CFriendMsg.RespOperate.parseFrom(data);
            int _type = friendListMsg.getType();
            List<Integer> _friendList = friendListMsg.getTargetsList();
            if (_friendList == null || _friendList.isEmpty()) {
                return;
            }
            if (_type == FriendConstants.OPERATE_APPLY_FRIEND
                    || _type == FriendConstants.OPERATE_DELETE_FRIEND
                    || _type == FriendConstants.OPERATE_SHIELD_PLAYER
                    || _type == FriendConstants.OPERATE_REFUSE_APPLY) {
                Map<Integer, FriendInfo> _friends = robot.getPlayer().getFriendInfos();
                for (int _id : _friendList) {
                    _friends.remove(_id);
                }
            }
        }
    }
}
