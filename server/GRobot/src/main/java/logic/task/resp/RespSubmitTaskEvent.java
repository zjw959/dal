package logic.task.resp;

import org.game.protobuf.s2c.S2CTaskMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CTaskMsg.ResultSubmitTask.MsgID.eMsgID_VALUE)
public class RespSubmitTaskEvent extends AbstractEvent {

    public RespSubmitTaskEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            S2CTaskMsg.ResultSubmitTask msg = S2CTaskMsg.ResultSubmitTask.parseFrom(data);
            int cid = msg.getTaskCid();
            String dbid = msg.getTaskDbId();
            Log4jManager.getInstance().debug(robot.getWindow(), "robot:" + robot.getName()
                    + "响应领取奖励: " + "taskCid:" + cid + "," + "dbid:" + dbid);
        }
    }

}
