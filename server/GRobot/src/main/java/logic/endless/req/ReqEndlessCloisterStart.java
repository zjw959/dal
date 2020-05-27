package logic.endless.req;

import logic.endless.EndlessCloisterOrder;

import org.game.protobuf.c2s.C2SEndlessCloisterMsg;
import org.game.protobuf.s2c.S2CEndlessCloisterMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.ENDLESS,
        order = EndlessCloisterOrder.ENDLESS_START)
public class ReqEndlessCloisterStart extends AbstractEvent {

    public ReqEndlessCloisterStart(RobotThread robot) {
        super(robot, S2CEndlessCloisterMsg.RspStartFightEndless.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        // 开启状态
        if (robot.getPlayer().getEndlessInfo().getStep() == 1) {
            C2SEndlessCloisterMsg.ReqStartFightEndless.Builder builder =
                    C2SEndlessCloisterMsg.ReqStartFightEndless.newBuilder();
            SMessage msg =
                    new SMessage(C2SEndlessCloisterMsg.ReqStartFightEndless.MsgID.eMsgID_VALUE,
                            builder.build().toByteArray(), resOrder);
            sendMsg(msg);
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(), "无尽副本当前未开启");
            super.robotSkipRun();
            return;
        }
    }
}
