package logic.sign.req;

import org.game.protobuf.c2s.C2SActivityMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.ReqOnceOrder;

@IsEvent(eventT = EventType.REQUSET_ONCE, functionT = FunctionType.SIGNACTIVITY, order = ReqOnceOrder.REQ_GET_SIGNINFO)
public class ReqGetAllSignInfoEvent extends AbstractEvent {

	public ReqGetAllSignInfoEvent(RobotThread robot) {
		super(robot, C2SActivityMsg.ReqActivitys.MsgID.eMsgID_VALUE);
	}

	@Override
	public void action(Object... obj) throws Exception {

		C2SActivityMsg.ReqActivitys.Builder builder = C2SActivityMsg.ReqActivitys.newBuilder();

		SMessage msg = new SMessage(C2SActivityMsg.ReqActivitys.MsgID.eMsgID_VALUE, builder.build().toByteArray(),
				resOrder);
		sendMsg(msg, false);

		Log4jManager.getInstance().debug(robot.getWindow(), "robot:" + robot.getName() + "请求所有签到信息");

	}

}
