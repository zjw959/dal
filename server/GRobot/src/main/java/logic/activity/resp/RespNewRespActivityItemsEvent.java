package logic.activity.resp;

import org.game.protobuf.s2c.S2CActivityMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

/***
 * 返回条目信息
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.ACTIVITY,
        order = S2CActivityMsg.NewRespActivityItems.MsgID.eMsgID_VALUE)
public class RespNewRespActivityItemsEvent extends AbstractEvent {

    public RespNewRespActivityItemsEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CActivityMsg.NewRespActivityItems msg =
                    S2CActivityMsg.NewRespActivityItems.parseFrom(data);
            player.setActivityItems(msg.getItemsList());
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "返回条目信息正确");

        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "返回条目信息错误");
        }
    }
}
