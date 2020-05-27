package logic.dungeon.req;

import logic.dungeon.DungeonOrder;

import org.game.protobuf.c2s.C2SDungeonMsg;
import org.game.protobuf.s2c.S2CDungeonMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.DUNGEON,
        order = DungeonOrder.REQ_DUNGEON_INFO)
public class ReqDungeonInfo extends AbstractEvent {

    public ReqDungeonInfo(RobotThread robot) {
        super(robot, S2CDungeonMsg.GetLevelInfo.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        C2SDungeonMsg.GetLevelInfo.Builder build = C2SDungeonMsg.GetLevelInfo.newBuilder();
        SMessage msg =
                new SMessage(C2SDungeonMsg.GetLevelInfo.MsgID.eMsgID_VALUE, build.build()
                        .toByteArray(), resOrder);
        sendMsg(msg);
    }
}
