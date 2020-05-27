package logic.activity.resp;

import org.game.protobuf.s2c.S2CActivityMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;


/***
 * 返回活动接受消息
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.ACTIVITY,
        order = S2CActivityMsg.NewRespActivitys.MsgID.eMsgID_VALUE)
public class RespNewRespActivitysEvent extends AbstractEvent {

    public RespNewRespActivitysEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CActivityMsg.NewRespActivitys msg = S2CActivityMsg.NewRespActivitys.parseFrom(data);
            player.setActivitys(msg.getActivitysList());
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "返回活动信息正确");

        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "返回活动信息错误");
        }
    }
}
