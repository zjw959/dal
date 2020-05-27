package logic.city.resp;

import org.game.protobuf.s2c.S2CNewBuildingMsg;
import org.game.protobuf.s2c.S2CNewBuildingMsg.HandWorkInfo;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

/***
 * 
 * 请求手工制作回调
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespDoHandWork.MsgID.eMsgID_VALUE)
public class RespDoHandWorkEvent extends AbstractEvent {

    public RespDoHandWorkEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CNewBuildingMsg.RespDoHandWork msg = S2CNewBuildingMsg.RespDoHandWork.parseFrom(data);

            player.setHandWorkInfo(
                    S2CNewBuildingMsg.HandWorkInfo.newBuilder().setManualId(msg.getManualId())
                            .setEndTime(msg.getEndTime()).setIntegral(0).build());
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求手工制作回调正确：");
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求手工制作回调错误：");
        }


    }

}
