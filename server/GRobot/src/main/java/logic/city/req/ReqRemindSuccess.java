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
import logic.robot.entity.RobotPlayer;

/***
 * 
 * 取消 建筑解封已经完成提示
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_REMINDSUCCESS)
public class ReqRemindSuccess extends AbstractEvent {

    public ReqRemindSuccess(RobotThread robot) {
        super(robot, S2CNewBuildingMsg.RespRemindSuccess.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        C2SNewBuildingMsg.ReqRemindSuccess.Builder builder =
                C2SNewBuildingMsg.ReqRemindSuccess.newBuilder();
        RobotPlayer player = robot.getPlayer();
        if (player.getReminds().size() <= 0) {
            robotSkipRun();
        } else {
            int eventType = player.getReminds().get(0).getEventType();
            builder.setEventType(eventType);
            SMessage msg = new SMessage(C2SNewBuildingMsg.ReqRemindSuccess.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), resOrder);
            sendMsg(msg);

            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求刪除建筑解封信息");
        }


    }

}
