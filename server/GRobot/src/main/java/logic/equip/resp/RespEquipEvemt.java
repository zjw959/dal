package logic.equip.resp;

import org.game.protobuf.s2c.S2CEquipmentMsg;
import org.game.protobuf.s2c.S2CItemMsg.EquipmentInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CEquipmentMsg.EquipMsg.MsgID.eMsgID_VALUE)
public class RespEquipEvemt extends AbstractEvent {

    public RespEquipEvemt(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CEquipmentMsg.EquipMsg msg = S2CEquipmentMsg.EquipMsg.parseFrom(data);
            EquipmentInfo info = msg.getEquipment();
            EquipmentInfo oldInfo = msg.getOldEquipment();
            player.updateEquipInfo(info.getCid(), info.getId(), info);
            if (!oldInfo.getId().equals("")) {
                player.updateEquipInfo(oldInfo.getCid(), oldInfo.getId(), oldInfo);
            }
            String str = "{equipId: " + info.getId() + ", heroId:" + info.getHeroId() + ", pos:"
                    + info.getPosition() + "}";
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "响应装备事件  " + str);
        }
    }

}
