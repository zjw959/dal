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
 * 返回条目进度信息
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.ACTIVITY,
        order = S2CActivityMsg.NewRespActivityProgress.MsgID.eMsgID_VALUE)
public class RespNewRespActivityProgressEvent extends AbstractEvent {

    public RespNewRespActivityProgressEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CActivityMsg.NewRespActivityProgress msg =
                    S2CActivityMsg.NewRespActivityProgress.parseFrom(data);
            player.setActivityRecords(msg.getItemsList());
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "返回条目进度信息正确");

        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "返回条目进度信息错误");
        }
    }
}
