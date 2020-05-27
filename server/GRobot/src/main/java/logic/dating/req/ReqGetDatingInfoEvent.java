package logic.dating.req;

import org.game.protobuf.c2s.C2SDatingMsg;
import org.game.protobuf.s2c.S2CDatingMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.dating.ReqDatingOrder;

/***
 * 
 * 获取所有的剧本
 * 
 * @author lihongji
 *
 */
@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.DATING,
        order = ReqDatingOrder.REQ_GET_ALLDATING)
public class ReqGetDatingInfoEvent extends AbstractEvent {

    public ReqGetDatingInfoEvent(RobotThread robot) {
        super(robot, S2CDatingMsg.GetDatingInfo.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        if (robot.getPlayer().getDatingRuleCid() != 0) {
            robotSkipRun();
        } else {
            C2SDatingMsg.GetDatingInfo.Builder builder = C2SDatingMsg.GetDatingInfo.newBuilder();
            SMessage msg = new SMessage(C2SDatingMsg.GetDatingInfo.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), resOrder);
            sendMsg(msg);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "获取所有的剧本");
        }
    }

}
