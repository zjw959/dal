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
 * 
 * 请求兼职信息的回调
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespDoPartTimeJob.MsgID.eMsgID_VALUE)
public class RespDoPartTimeJobEvent extends AbstractEvent {

    public RespDoPartTimeJobEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CNewBuildingMsg.RespDoPartTimeJob msg =
                    S2CNewBuildingMsg.RespDoPartTimeJob.parseFrom(data);
            player.setJobEvent(msg.getJobInfo());

            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求兼职信息的回调正确：" + msg.getJobInfo().getEtime());
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求兼职信息的回调错误：");
        }


    }

}
