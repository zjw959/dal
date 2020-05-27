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
import logic.ReqOnceOrder;

/**
 * 
 * 请求建筑信息
 * 
 * @author
 *
 */


@IsEvent(eventT = EventType.REQUSET_ONCE, functionT = FunctionType.CITY,
        order = ReqOnceOrder.REQ_GET_CITY)
public class ReqCityInfoEvent extends AbstractEvent {

    public ReqCityInfoEvent(RobotThread robot) {
        super(robot, S2CNewBuildingMsg.RespGetAllBuildingInfo.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        C2SNewBuildingMsg.ReqGetAllBuildingInfo.Builder builder =
                C2SNewBuildingMsg.ReqGetAllBuildingInfo.newBuilder();
        SMessage msg = new SMessage(C2SNewBuildingMsg.ReqGetAllBuildingInfo.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg);

        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "请求建筑信息  ");

    }

}
