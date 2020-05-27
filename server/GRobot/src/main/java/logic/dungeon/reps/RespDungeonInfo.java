package logic.dungeon.reps;

import org.game.protobuf.s2c.S2CDungeonMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.DUNGEON,
        order = S2CDungeonMsg.GetLevelInfo.MsgID.eMsgID_VALUE)
public class RespDungeonInfo extends AbstractEvent {

    public RespDungeonInfo(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len <= 0)
            return;
        S2CDungeonMsg.GetLevelInfo infoMsg = S2CDungeonMsg.GetLevelInfo.parseFrom(data);
        robot.getPlayer().updateDungeonInfos(infoMsg);
        Log4jManager.getInstance().debug(
                robot.getWindow(),
                "获取关卡--level:" + robot.getPlayer().getDungeons().size() + ", group:"
                        + robot.getPlayer().getDungeonGroups().size());
    }

}
