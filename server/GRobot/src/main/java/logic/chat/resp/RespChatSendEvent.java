package logic.chat.resp;

import org.game.protobuf.s2c.S2CChatMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CChatMsg.ChatInfo.MsgID.eMsgID_VALUE)
public class RespChatSendEvent extends AbstractEvent {

    public RespChatSendEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len <= 0)
            return;
        S2CChatMsg.ChatInfo chatInfoMsg = S2CChatMsg.ChatInfo.parseFrom(data);
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + chatInfoMsg.getPname() + " 内容：" + chatInfoMsg.getContent());
    }

}
