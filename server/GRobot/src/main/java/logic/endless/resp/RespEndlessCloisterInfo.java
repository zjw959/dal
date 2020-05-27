package logic.endless.resp;

import org.game.protobuf.s2c.S2CEndlessCloisterMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.ENDLESS,
        order = S2CEndlessCloisterMsg.RspEndlessCloisterInfo.MsgID.eMsgID_VALUE)
public class RespEndlessCloisterInfo extends AbstractEvent {

    public RespEndlessCloisterInfo(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len <= 0)
            return;
        S2CEndlessCloisterMsg.RspEndlessCloisterInfo infoMsg =
                S2CEndlessCloisterMsg.RspEndlessCloisterInfo.parseFrom(data);
        robot.getPlayer().setEndlessInfo(infoMsg.getInfo());
        // 不需要的临时数据
        Log4jManager.getInstance().debug(robot.getWindow(), "无尽副本信息:" + infoMsg.getInfo());
    }
}
