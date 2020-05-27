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
 * 验证手工操作完成上传积分
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_UPLOADHANDINTEGRAL)
public class ReqUploadHandIntegralEvent extends AbstractEvent {

    public ReqUploadHandIntegralEvent(RobotThread robot) {
         super(robot, S2CNewBuildingMsg.RespUploadHandIntegral.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        HandWorkInfo handwork = robot.getPlayer().getHandWorkInfo();

        if (handwork == null || handwork.getEndTime() == 0 || handwork.getManualId() == 0
                || handwork.getEndTime() < (System.currentTimeMillis() / 1000)) {
            robotSkipRun();
        } else {
            C2SNewBuildingMsg.ReqUploadHandIntegral.Builder builder =
                    C2SNewBuildingMsg.ReqUploadHandIntegral.newBuilder();

            builder.setManualId(handwork.getManualId());
            builder.setIntegral(2);
            builder.setStime((int) System.currentTimeMillis() / 1000);
            builder.setEtime((int) System.currentTimeMillis() / 1000);

            SMessage msg = new SMessage(C2SNewBuildingMsg.ReqUploadHandIntegral.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), resOrder);
            sendMsg(msg);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "验证手工操作完成上传积分");
        }
    }


}
