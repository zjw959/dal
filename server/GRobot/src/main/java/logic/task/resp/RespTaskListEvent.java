package logic.task.resp;

import org.game.protobuf.s2c.S2CTaskMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CTaskMsg.RespTasks.MsgID.eMsgID_VALUE)
public class RespTaskListEvent extends AbstractEvent {

    public RespTaskListEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        RobotPlayer player = robot.getPlayer();
        if (data.length > 0) {
            S2CTaskMsg.RespTasks msg = S2CTaskMsg.RespTasks.parseFrom(data);
            player.updateTask(msg.getTaksList());
        }
    }

}
