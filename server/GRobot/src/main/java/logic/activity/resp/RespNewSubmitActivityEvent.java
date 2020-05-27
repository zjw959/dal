package logic.activity.resp;

import org.game.protobuf.s2c.S2CActivityMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.ACTIVITY,
        order = S2CActivityMsg.NewResultSubmitActivity.MsgID.eMsgID_VALUE)
public class RespNewSubmitActivityEvent extends AbstractEvent {

    public RespNewSubmitActivityEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            // RobotPlayer player = robot.getPlayer();
            // S2CActivityMsg.NewResultSubmitActivity msg =
            // S2CActivityMsg.NewResultSubmitActivity.parseFrom(data);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "返回领奖信息正确");

        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "返回领奖信息错误");

        }


    }
}
