package logic.friend.resp;

import org.game.protobuf.s2c.S2CPlayerMsg;
import org.game.protobuf.s2c.S2CPlayerMsg.PlayerInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CPlayerMsg.RespTargetPlayerInfo.MsgID.eMsgID_VALUE)
public class ResPlayerInfoEvent extends AbstractEvent {

    public ResPlayerInfoEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CPlayerMsg.RespTargetPlayerInfo _friend =
                    S2CPlayerMsg.RespTargetPlayerInfo.parseFrom(data);
            PlayerInfo _player = _friend.getPlayerInfo();
            if (_player == null) {
                Log4jManager.getInstance().error(robot.getWindow(),
                        "robot:" + robot.getName() + "好友查询结果空 ");
            } else {
                Log4jManager.getInstance().debug(robot.getWindow(),
                        "robot:" + robot.getName() + "好友查询结果 " + _player.getPid());
            }
        }
        return;
    }

}
