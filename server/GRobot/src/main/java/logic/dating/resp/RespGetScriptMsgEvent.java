package logic.dating.resp;

import org.game.protobuf.s2c.S2CDatingMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

/**
 * 获取约会剧本的返回
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.DATING,
        order = S2CDatingMsg.GetScriptMsg.MsgID.eMsgID_VALUE)
public class RespGetScriptMsgEvent extends AbstractEvent {

    public RespGetScriptMsgEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "获取约会剧本的返回");
        if (data.length > 0) {
            
        }
    }

}
