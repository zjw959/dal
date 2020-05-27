package logic.friend.resp;

import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CFriendMsg;
import org.game.protobuf.s2c.S2CFriendMsg.FriendInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CFriendMsg.RespRecommendFriends.MsgID.eMsgID_VALUE)
public class ResFriendRecommendEvent extends AbstractEvent {

    public ResFriendRecommendEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CFriendMsg.RespRecommendFriends friendRecommends =
                    S2CFriendMsg.RespRecommendFriends.parseFrom(data);
            List<FriendInfo> friendList = friendRecommends.getFriendsList();
            if (friendList == null || friendList.isEmpty()) {
                return;
            }
            Map<Integer, FriendInfo> _robotRecommends = robot.getPlayer().getRecommendFriendInfos();
            _robotRecommends.clear();
            for (FriendInfo _competition : friendList) {
                robot.getPlayer().putRecommendFriend(_competition);
            }
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "收到好友推荐消息 " + friendList);
        }
    }

}
