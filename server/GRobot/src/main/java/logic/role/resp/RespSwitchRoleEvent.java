package logic.role.resp;

import org.game.protobuf.s2c.S2CRoleMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CRoleMsg.SwitchRoleResult.MsgID.eMsgID_VALUE)
public class RespSwitchRoleEvent extends AbstractEvent {

    public RespSwitchRoleEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "切换看板娘回调  ");
    }


}
