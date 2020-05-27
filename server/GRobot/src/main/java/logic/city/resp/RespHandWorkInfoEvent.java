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
 * 请求手工信息回调
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespGetHandWorkInfo.MsgID.eMsgID_VALUE)
public class RespHandWorkInfoEvent extends AbstractEvent {

    public RespHandWorkInfoEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CNewBuildingMsg.RespGetHandWorkInfo msg =
                    S2CNewBuildingMsg.RespGetHandWorkInfo.parseFrom(data);
            player.setHandWorkInfo(msg.getHandWorkInfo());
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求手工信息回调正确");
        } else {
            RobotPlayer player = robot.getPlayer();
            player.setHandWorkInfo(null);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求手工信息回调错误：");
        }
    }



}
