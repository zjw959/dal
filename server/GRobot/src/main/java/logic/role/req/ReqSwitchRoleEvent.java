package logic.role.req;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
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
        order = ReqRoleOrder.REQ_ROLE_SWITCH)
public class ReqSwitchRoleEvent extends AbstractEvent {

    public ReqSwitchRoleEvent(RobotThread robot) {
        super(robot, S2CRoleMsg.SwitchRoleResult.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();
        Map<Integer, RoleInfo> roleMap = player.getRoleMap();
        if (roleMap.size() < 2) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "看板娘少于2");
            super.robotSkipRun();
            return;
        }
        List<RoleInfo> list = new ArrayList<>();
        for (RoleInfo info : roleMap.values()) {
            if (info.getStatus() != 1) {
                list.add(info);
            }
        }
        RoleInfo changeRole = list.get(RandomUtils.nextInt(list.size()));

        C2SRoleMsg.SwitchRole.Builder builder = C2SRoleMsg.SwitchRole.newBuilder();
        builder.setRoleId(changeRole.getId());
        SMessage message = new SMessage(C2SRoleMsg.SwitchRole.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(message);
    }


}
