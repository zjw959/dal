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
        order = ReqRoleOrder.REQ_ROLE_DONATE)
public class ReqDonateRoleEvent extends AbstractEvent {

    public ReqDonateRoleEvent(RobotThread robot) {
        super(robot, S2CRoleMsg.Donate.MsgID.eMsgID_VALUE);
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

        long number = player.getItemCount(534011);
        if (number <= 0) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "没有食物");
            super.robotSkipRun();
            return;
        }
        List<Integer> list = new ArrayList<>(roleMap.keySet());
        int key = list.get(RandomUtils.nextInt(list.size()));

        C2SRoleMsg.Donate.Builder builder = C2SRoleMsg.Donate.newBuilder();
        builder.setItemCid(534011);
        builder.setNum(1);
        builder.setRoleId(String.valueOf(key));
        SMessage message = new SMessage(C2SRoleMsg.Donate.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(message);
    }


}
