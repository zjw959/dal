package logic.city.resp;

import org.game.protobuf.s2c.S2CNewBuildingMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

/***
 * 请求手工奖励回调
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespGetHandWorkAward.MsgID.eMsgID_VALUE)
public class RespGetHandWorkAwardEvent extends AbstractEvent {

    public RespGetHandWorkAwardEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            S2CNewBuildingMsg.RespGetHandWorkAward msg =
                    S2CNewBuildingMsg.RespGetHandWorkAward.parseFrom(data);
            robot.getPlayer().setHandWorkInfo(S2CNewBuildingMsg.HandWorkInfo.newBuilder()
                    .setEndTime(0).setIntegral(0).setManualId(0).build());
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求手工奖励回调正确:" + msg.getManualId());
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求手工奖励回调错误：");
        }
    }



}
