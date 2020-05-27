package logic.dungeon.reps;

import org.game.protobuf.s2c.S2CDungeonMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.DUNGEON,
        order = S2CDungeonMsg.FightOverMsg.MsgID.eMsgID_VALUE)
public class RespDungeonEndInfo extends AbstractEvent {

    public RespDungeonEndInfo(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len <= 0)
            return;
        S2CDungeonMsg.FightOverMsg infoMsg = S2CDungeonMsg.FightOverMsg.parseFrom(data);
        robot.getPlayer().getDungeons()
                .put(infoMsg.getLevelInfo().getCid(), infoMsg.getLevelInfo());
        robot.getPlayer().resetDungeonTempLevel();
        // 不需要的临时数据
        Log4jManager.getInstance().debug(
                robot.getWindow(),
                "结束关卡--levelu:" + infoMsg.getLevelInfo().getCid() + ", result:"
                        + infoMsg.getLevelInfo().getWin());
    }

}
