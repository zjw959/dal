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
 * 请求兼职奖励回调
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespPartTimeJobAward.MsgID.eMsgID_VALUE)
public class RespPartTimeJobAwardEvent extends AbstractEvent {

    public RespPartTimeJobAwardEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CNewBuildingMsg.RespPartTimeJobAward msg =
                    S2CNewBuildingMsg.RespPartTimeJobAward.parseFrom(data);
            if (msg.getJobList() != null) {
                player.getJobInfoList().forEach((list -> {
                    if (list.getBuildingId() == msg.getJobList().getBuildingId()) {
                        list = msg.getJobList();
                    }
                }));
            }
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求兼职奖励回调成功");
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求兼职奖励回调失败");
        }
    }

}
