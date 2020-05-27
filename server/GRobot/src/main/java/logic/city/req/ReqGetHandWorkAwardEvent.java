package logic.city.req;

import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.s2c.S2CNewBuildingMsg;
import org.game.protobuf.s2c.S2CNewBuildingMsg.HandWorkInfo;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.city.ReqCityOrder;

/***
 * 请求手工奖励
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_GETHANDWORKAWARD)
public class ReqGetHandWorkAwardEvent extends AbstractEvent {

    public ReqGetHandWorkAwardEvent(RobotThread robot) {
        super(robot, S2CNewBuildingMsg.RespGetHandWorkAward.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        HandWorkInfo handwork = robot.getPlayer().getHandWorkInfo();
        if (handwork == null || handwork.getEndTime() == 0 || handwork.getManualId() == 0
                || handwork.getEndTime() > (System.currentTimeMillis() / 1000)) {
            robotSkipRun();
        } else {
            C2SNewBuildingMsg.ReqGetHandWorkAward.Builder builder =
                    C2SNewBuildingMsg.ReqGetHandWorkAward.newBuilder();
            builder.setManualId(handwork.getManualId());
            SMessage msg = new SMessage(C2SNewBuildingMsg.ReqGetHandWorkAward.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), resOrder);
            sendMsg(msg);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求手工奖励");

        }

    }

}
