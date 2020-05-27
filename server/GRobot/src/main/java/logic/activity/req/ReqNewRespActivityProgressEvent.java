package logic.activity.req;

import org.game.protobuf.c2s.C2SActivityMsg;
import org.game.protobuf.s2c.S2CActivityMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.activity.ReqActivityOrder;

/***
 * 
 * 请求玩家记录信息
 * 
 * @author lihongji
 *
 */
@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.ACTIVITY,
        order = ReqActivityOrder.REQ_GET_RECORD)
public class ReqNewRespActivityProgressEvent extends AbstractEvent {

    public ReqNewRespActivityProgressEvent(RobotThread robot) {
        super(robot, S2CActivityMsg.NewRespActivityProgress.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "请求玩家记录信息");
        C2SActivityMsg.NewReqActivityProgress.Builder builder =
                C2SActivityMsg.NewReqActivityProgress.newBuilder();
        SMessage msg = new SMessage(C2SActivityMsg.NewReqActivityProgress.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }
}
