package logic.sign.req;

import org.game.protobuf.c2s.C2SActivityMsg;
import org.game.protobuf.s2c.S2CActivityMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityEntryInfoMsg;
import org.game.protobuf.s2c.S2CActivityMsg.ActivityInfoMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;
import logic.sign.ReqSignOrder;

/***
 * 
 * 领取体力
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.SIGNACTIVITY, order = ReqSignOrder.REQ_GET_STRENGH_AWAR)
public class ReqSubmitApplySignActivityEvent extends AbstractEvent {

	public ReqSubmitApplySignActivityEvent(RobotThread robot) {
		super(robot, S2CActivityMsg.ResultSubmitActivity.MsgID.eMsgID_VALUE);
	}

	@Override
	public void action(Object... obj) throws Exception {
		if (checkAward(robot.getPlayer(), 4)) {
			C2SActivityMsg.SubmitActivity.Builder builder = C2SActivityMsg.SubmitActivity.newBuilder();
			builder.setActivitId(4);
			builder.addActivitEntryId(1);
			builder.setExtendData("");
			SMessage msg = new SMessage(C2SActivityMsg.SubmitActivity.MsgID.eMsgID_VALUE, builder.build().toByteArray(),
					resOrder);
			sendMsg(msg, false);
			Log4jManager.getInstance().debug(robot.getWindow(), "robot:" + robot.getName() + "领取体力");
		} else {
			robotSkipRun();
		}
	}

	/** 检测是否有奖励可以领取 **/
	public boolean checkAward(RobotPlayer player, int activitType) {
		if (player.getSignList() == null || player.getSignList().size() <= 0)
			return false;
		for (ActivityInfoMsg msg : player.getSignList()) {
			if (msg.getActivityType() == activitType) {
				for (ActivityEntryInfoMsg info : msg.getEntrysList()) {
					if (info.getStatus() == 1)
						return true;
				}
				return false;
			}
		}
		return false;
	}

}
