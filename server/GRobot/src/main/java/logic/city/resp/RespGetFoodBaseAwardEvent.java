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
 * 返回料理奖励
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespGetFoodBaseAward.MsgID.eMsgID_VALUE)
public class RespGetFoodBaseAwardEvent extends AbstractEvent {

    public RespGetFoodBaseAwardEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            // S2CNewBuildingMsg.RespDoPartTimeJob msg =
            // S2CNewBuildingMsg.RespDoPartTimeJob.parseFrom(data);
            player.setFoodId(0);
            player.setEndTime(0);
            player.setIntegral(0);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求返回料理奖励回调正确：");
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求返回料理奖励回调错误：");
        }

    }



}
