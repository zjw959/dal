package logic.role.req;

import java.util.Map;

import org.game.protobuf.c2s.C2SRoleMsg;
import org.game.protobuf.s2c.S2CRoleMsg;
import org.game.protobuf.s2c.S2CRoleMsg.RoleInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;
import logic.role.ReqRoleOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.ROLE,
        order = ReqRoleOrder.REQ_ROLE_TOUCH)
public class ReqTouchRoleEvent extends AbstractEvent {

    public ReqTouchRoleEvent(RobotThread robot) {
        super(robot, S2CRoleMsg.TouchRole.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();
        Map<Integer, RoleInfo> roleMap = player.getRoleMap();
        if (roleMap.isEmpty()) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "没有解锁看板娘  ");
            super.robotSkipRun();
            return;
        }
        long number = player.getItemCount(500012);
        if (number <= 0) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "没有触摸次数了  ");
            // robot.removeCurrentFun();
            super.robotSkipRun();
            return;
        }

        RoleInfo nowRole = null;
        for (RoleInfo info : roleMap.values()) {
            if (info.getStatus() == 1) {
                nowRole = info;
            }
        }
        if (nowRole == null) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "没有解锁看板娘  ");
            super.robotSkipRun();
            return;
        }

        C2SRoleMsg.TouchRole.Builder builder = C2SRoleMsg.TouchRole.newBuilder();
        SMessage message = new SMessage(C2SRoleMsg.TouchRole.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(message);
    }


}
