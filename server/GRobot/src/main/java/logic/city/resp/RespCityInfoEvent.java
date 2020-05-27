package logic.city.resp;

import java.util.List;
import org.game.protobuf.s2c.S2CNewBuildingMsg;
import org.game.protobuf.s2c.S2CNewBuildingMsg.NewBuildingInfo;
import org.game.protobuf.s2c.S2CNewBuildingMsg.RemindEvent;
import org.game.protobuf.s2c.S2CNewBuildingMsg.RoleInRoom;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

/***
 * 
 * 获取城建信息
 * 
 * @author
 *
 */

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespGetAllBuildingInfo.MsgID.eMsgID_VALUE)
public class RespCityInfoEvent extends AbstractEvent {

    public RespCityInfoEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CNewBuildingMsg.RespGetAllBuildingInfo msg =
                    S2CNewBuildingMsg.RespGetAllBuildingInfo.parseFrom(data);
            List<NewBuildingInfo> infos = msg.getBuildinginfosList();
            List<RoleInRoom> cityRoles = msg.getRoleInRoomsList();
            List<RemindEvent> remindEvent = msg.getRemindEventsList();
            int dayType = msg.getDayType();
            if (infos != null) {
                player.updateNewBuildingInfo(infos);
                Log4jManager.getInstance().debug(robot.getWindow(),
                        "robot:" + robot.getName() + "建筑响应事件");
            }
            if (remindEvent != null) {
                player.setReminds(remindEvent);
            }
            player.setDayType(dayType);
        }
    }
}
