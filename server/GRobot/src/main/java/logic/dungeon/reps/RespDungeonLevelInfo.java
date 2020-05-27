package logic.dungeon.reps;

import org.game.protobuf.s2c.S2CDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg.LevelInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.DUNGEON,
        order = S2CDungeonMsg.LevelInfos.MsgID.eMsgID_VALUE)
public class RespDungeonLevelInfo extends AbstractEvent {

    public RespDungeonLevelInfo(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len <= 0)
            return;

        S2CDungeonMsg.LevelInfos msg = S2CDungeonMsg.LevelInfos.parseFrom(data);
        if (msg.getLevelInfosList().size() > 0) {
            for (LevelInfo level : msg.getLevelInfosList()) {
                robot.getPlayer().getDungeons().put(level.getCid(), level);
            }
            robot.getPlayer().resetDungeonTempLevel();
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "刷新关卡信息--size:" + msg.getLevelInfosList().size());
        }
    }

}
