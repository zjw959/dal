package logic.friend.resp;

import java.util.List;

import org.game.protobuf.s2c.S2CFriendMsg;
import org.game.protobuf.s2c.S2CFriendMsg.FriendInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CFriendMsg.RespFriends.MsgID.eMsgID_VALUE)
public class ResFriendListEvent extends AbstractEvent {

    public ResFriendListEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CFriendMsg.RespFriends friendListMsg = S2CFriendMsg.RespFriends.parseFrom(data);
            List<FriendInfo> friendList = friendListMsg.getFriendsList();
            RobotPlayer robotPlayer = robot.getPlayer();
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "收到好友列表 " + friendList.size());
            for (FriendInfo friend : friendList) {
                robotPlayer.putFriend(friend);
            }
        }
        return;
    }

}
