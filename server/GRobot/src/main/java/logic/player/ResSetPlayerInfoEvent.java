package logic.player;

import org.game.protobuf.s2c.S2CPlayerMsg.setPlayerInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = setPlayerInfo.MsgID.eMsgID_VALUE)
public class ResSetPlayerInfoEvent extends AbstractEvent {

    public ResSetPlayerInfoEvent(RobotThread robot) {
        super(robot);
        this.resOrder = setPlayerInfo.MsgID.eMsgID_VALUE;
    }

    @Override
    public void action(Object... obj) throws Exception {
    }

}
