package logic.role.resp;

import org.game.protobuf.s2c.S2CRoleMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CRoleMsg.RoleInfoList.MsgID.eMsgID_VALUE)
public class RespRoleListEvent extends AbstractEvent {


    public RespRoleListEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = this.robot.getPlayer();
            S2CRoleMsg.RoleInfoList msg = S2CRoleMsg.RoleInfoList.parseFrom(data);
            player.updateRoleMsg(msg.getRolesList());
        }
    }

}
