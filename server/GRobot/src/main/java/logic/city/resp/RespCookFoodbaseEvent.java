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
 * 制作料理的返回
 * 
 * @author
 *
 */

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespCookFoodbase.MsgID.eMsgID_VALUE)
public class RespCookFoodbaseEvent extends AbstractEvent {

    public RespCookFoodbaseEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            S2CNewBuildingMsg.RespCookFoodbase msg =
                    S2CNewBuildingMsg.RespCookFoodbase.parseFrom(data);
            RobotPlayer player = robot.getPlayer();
            player.setFoodId(msg.getFoodId());
            player.setEndTime(msg.getEndTime());
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "制作料理的返回正确：");
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "制作料理的返回错误：");
        }
    }

}
