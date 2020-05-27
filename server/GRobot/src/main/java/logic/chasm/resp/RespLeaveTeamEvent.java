package logic.chasm.resp;

import org.game.protobuf.s2c.S2CTeamMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CTeamMsg.RespLeaveTeam.MsgID.eMsgID_VALUE)
public class RespLeaveTeamEvent extends AbstractEvent {

    public RespLeaveTeamEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            robotPlayer.setTeamInfo(null);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robotPlayer.getPlayerInfo().getPid() + "退出队伍");
        }
    }

}
