package logic.role.req;

import org.game.protobuf.c2s.C2SRoleMsg;
import org.game.protobuf.s2c.S2CRoleMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.role.ReqRoleOrder;

/***
 * 
 * 请求看板娘数据
 * @author 
 *
 */

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.ROLE,
        order = ReqRoleOrder.REQ_ROLE_LISE)
public class ReqRoleListEvent extends AbstractEvent {

    public ReqRoleListEvent(RobotThread robot) {
        super(robot, S2CRoleMsg.RoleInfoList.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        C2SRoleMsg.GetRole.Builder builder = C2SRoleMsg.GetRole.newBuilder();
        SMessage message = new SMessage(C2SRoleMsg.GetRole.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(message);
    }

}
