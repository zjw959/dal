package logic.dungeon.reps;

import org.game.protobuf.s2c.S2CDungeonMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.DUNGEON,
        order = S2CDungeonMsg.FightStartMsg.MsgID.eMsgID_VALUE)
public class RespDungeonStartInfo extends AbstractEvent {

    public RespDungeonStartInfo(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len <= 0)
            return;
        S2CDungeonMsg.FightStartMsg infoMsg = S2CDungeonMsg.FightStartMsg.parseFrom(data);
        robot.getPlayer().setDungeonLevelId(infoMsg.getLevelCid());
        // 不需要的临时数据
        Log4jManager.getInstance().debug(robot.getWindow(),
                "开始关卡--leveluid:" + infoMsg.getFightId() + ", levelcid:" + infoMsg.getLevelCid());
    }

}
