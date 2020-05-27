package logic.chat.resp;

import org.game.protobuf.s2c.S2CChatMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CChatMsg.RespInitChatInfo.MsgID.eMsgID_VALUE)
public class RespChatInitEvent extends AbstractEvent {

    public RespChatInitEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len <= 0)
            return;
        S2CChatMsg.RespInitChatInfo initMsg = S2CChatMsg.RespInitChatInfo.parseFrom(data);
        Log4jManager.getInstance().debug(robot.getWindow(), "init房间：" + initMsg.getRoomId());
    }

}
