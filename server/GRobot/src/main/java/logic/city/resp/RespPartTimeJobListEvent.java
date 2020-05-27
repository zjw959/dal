package logic.city.resp;

import org.game.protobuf.s2c.S2CNewBuildingMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

/***
 * 获取兼职信息
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespPartTimeJobList.MsgID.eMsgID_VALUE)
public class RespPartTimeJobListEvent extends AbstractEvent {

    public RespPartTimeJobListEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CNewBuildingMsg.RespPartTimeJobList msg =
                    S2CNewBuildingMsg.RespPartTimeJobList.parseFrom(data);
            // 兼职信息
            if (msg.getJobListsList() != null && msg.getJobListsList().size() > 0)
                player.setJobInfoList(msg.getJobListsList());
            // 执行中的兼职任务
            if (msg.getJobEvent() != null)
                player.setJobEvent(msg.getJobEvent());
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "兼职响应事件");
        }
    }

}
