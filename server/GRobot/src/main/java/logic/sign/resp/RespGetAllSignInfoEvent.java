package logic.sign.resp;

import org.game.protobuf.s2c.S2CActivityMsg;
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
 * 返回所有的签到信息
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.SIGNACTIVITY,
order = S2CActivityMsg.RespActivitys.MsgID.eMsgID_VALUE)
public class RespGetAllSignInfoEvent extends AbstractEvent {

    public RespGetAllSignInfoEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            S2CActivityMsg.RespActivitys msg = S2CActivityMsg.RespActivitys.parseFrom(data);
            RobotPlayer player = robot.getPlayer();
            player.setSignList(msg.getActivitysList());
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "返回所有的签到信息正确");
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "返回所有的签到信息错误");
        }

    }



}
