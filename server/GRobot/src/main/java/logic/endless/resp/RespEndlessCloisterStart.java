package logic.endless.resp;

import org.game.protobuf.s2c.S2CEndlessCloisterMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.ENDLESS,
        order = S2CEndlessCloisterMsg.RspStartFightEndless.MsgID.eMsgID_VALUE)
public class RespEndlessCloisterStart extends AbstractEvent {

    public RespEndlessCloisterStart(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len <= 0)
            return;
        S2CEndlessCloisterMsg.RspStartFightEndless infoMsg =
                S2CEndlessCloisterMsg.RspStartFightEndless.parseFrom(data);
        robot.getPlayer().setEndlessNowStage(infoMsg.getLevelCid());
        Log4jManager.getInstance().debug(robot.getWindow(), "无尽副本起始关卡:" + infoMsg.getLevelCid());
    }
}
