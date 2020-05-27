package logic.sign.resp;

import org.game.protobuf.s2c.S2CActivityMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

/***
 * 返回领取签到奖励
 * 
 * @author lihongji
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.SIGNACTIVITY,
order = S2CActivityMsg.ResultSubmitActivity.MsgID.eMsgID_VALUE)
public class RespSubmitActivityEvent extends AbstractEvent {

    public RespSubmitActivityEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            S2CActivityMsg.ResultSubmitActivity msg =
                    S2CActivityMsg.ResultSubmitActivity.parseFrom(data);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "返回领取签到奖励成功" + msg.getRewardsList());
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "返回领取签到奖励失败");
        }
    }

}
