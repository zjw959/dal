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
 * 请求手工信息
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_GETHANDWORKINFO)
public class ReqGetManualInfoEvent extends AbstractEvent {

    public ReqGetManualInfoEvent(RobotThread robot) {
        super(robot, S2CNewBuildingMsg.RespGetHandWorkInfo.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        C2SNewBuildingMsg.ReqGetHandWorkInfo.Builder builder =
                C2SNewBuildingMsg.ReqGetHandWorkInfo.newBuilder();
        builder.setNeedSave(false);

        SMessage msg = new SMessage(C2SNewBuildingMsg.ReqGetHandWorkInfo.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg);
        Log4jManager.getInstance().debug(robot.getWindow(), "robot:" + robot.getName() + "请求手工信息");
    }


}
