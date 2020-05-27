package logic.city.resp;

import java.util.List;
import java.util.stream.Collectors;
import org.game.protobuf.s2c.S2CNewBuildingMsg;
import org.game.protobuf.s2c.S2CNewBuildingMsg.RemindEvent;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

/***
 * 
 * 返回成功移除提醒信息
 * 
 * @author
 *
 */

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespRemindSuccess.MsgID.eMsgID_VALUE)
public class RespRemindSuccessEvent extends AbstractEvent {

    public RespRemindSuccessEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {

        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CNewBuildingMsg.RespRemindSuccess msg =
                    S2CNewBuildingMsg.RespRemindSuccess.parseFrom(data);
            if (msg.getIsSuccess()) {
                player.descRemindEvent(removeList(player, msg.getEventType()));
                Log4jManager.getInstance().debug(robot.getWindow(),
                        "robot:" + robot.getName() + "成功移除城建某一种提醒信息");
            }
        }

    }

    /** 获取要移除的集合 **/
    public List<RemindEvent> removeList(RobotPlayer player, int eventType) {
        if (player.getReminds() == null || player.getReminds().size() <= 0)
            return null;
        return player.getReminds().stream().filter(e -> e.getEventType() == eventType)
                .collect(Collectors.toList());
    }



}
