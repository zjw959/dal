package logic.city.req;

import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.s2c.S2CNewBuildingMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.city.ReqCityOrder;

/***
 * 请求料理奖励
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_GETFOODBASEAWARD)
public class ReqGetFoodBaseAwardEvent extends AbstractEvent {

    public ReqGetFoodBaseAwardEvent(RobotThread robot) {
        super(robot, S2CNewBuildingMsg.RespGetFoodBaseAward.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        C2SNewBuildingMsg.ReqGetFoodBaseAward.Builder builder =
                C2SNewBuildingMsg.ReqGetFoodBaseAward.newBuilder();
        if (robot.getPlayer().getEndTime() < (System.currentTimeMillis() / 1000)
                && robot.getPlayer().getFoodId() != 0) {
            builder.setFoodId(robot.getPlayer().getFoodId());
            SMessage msg = new SMessage(C2SNewBuildingMsg.ReqGetFoodBaseAward.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), resOrder);
            sendMsg(msg);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求料理奖励");

        } else {
            robotSkipRun();
        }

    }

}
