package logic.friend.resp;

import org.game.protobuf.s2c.S2CFriendMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CFriendMsg.RespQueryPlayer.MsgID.eMsgID_VALUE)
public class ResQueryPlayerEvent extends AbstractEvent {

    public ResQueryPlayerEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CFriendMsg.RespQueryPlayer queryPlayer = S2CFriendMsg.RespQueryPlayer.parseFrom(data);
            Log4jManager.getInstance().debug(robot.getWindow(), "robot:" + robot.getName()
                    + " 查看了对方玩家信息 " + queryPlayer.getFriends(0).getName());
        }
    }

}
