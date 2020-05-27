package logic.city.resp;

import org.game.protobuf.s2c.S2CNewBuildingMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

/****
 * 请求料理数据
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespgetFoodbaseInfo.MsgID.eMsgID_VALUE)
public class RespgetFoodbaseInfoEvent extends AbstractEvent {

    public RespgetFoodbaseInfoEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CNewBuildingMsg.RespgetFoodbaseInfo msg =
                    S2CNewBuildingMsg.RespgetFoodbaseInfo.parseFrom(data);
            player.setFoodId(msg.getFoodbaseInfo().getFoodId());
            player.setEndTime(msg.getFoodbaseInfo().getEndTime());
            player.setIntegral(msg.getFoodbaseInfo().getIntegral());
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求料理数据正确");
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求料理数据错误：");
        }
    }

}
