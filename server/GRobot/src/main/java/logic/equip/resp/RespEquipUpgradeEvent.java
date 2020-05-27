package logic.equip.resp;

import org.game.protobuf.s2c.S2CEquipmentMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CEquipmentMsg.UpgradeMsg.MsgID.eMsgID_VALUE)
public class RespEquipUpgradeEvent extends AbstractEvent {

    public RespEquipUpgradeEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            S2CEquipmentMsg.UpgradeMsg msg = S2CEquipmentMsg.UpgradeMsg.parseFrom(data);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "响应装备强化 " + msg.getSuccess());
        }
    }

}
