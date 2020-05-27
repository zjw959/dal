package logic.dating.resp;

import org.game.protobuf.s2c.S2CDatingMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;


/****
 * 获取剧本
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.DATING,
        order = S2CDatingMsg.DatingScriptMsg.MsgID.eMsgID_VALUE)
public class RespDatingScriptMsgEvent extends AbstractEvent {

    public RespDatingScriptMsgEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            S2CDatingMsg.DatingScriptMsg msg = S2CDatingMsg.DatingScriptMsg.parseFrom(data);
            robot.getPlayer().setDatingRuleCid(msg.getDatingRuleCid());
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "获取剧本成功");
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "获取剧本失败");
        }
    }

}
