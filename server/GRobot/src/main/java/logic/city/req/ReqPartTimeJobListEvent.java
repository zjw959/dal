package logic.city.req;

import org.game.protobuf.c2s.C2SNewBuildingMsg;
import org.game.protobuf.s2c.S2CNewBuildingMsg;
import org.game.protobuf.s2c.S2CNewBuildingMsg.JobInfo;
import org.game.protobuf.s2c.S2CNewBuildingMsg.JobInfoList;
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
 * 请求获取兼职信息
 * 
 * @author
 *
 */

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_GETPART_LIST)
public class ReqPartTimeJobListEvent extends AbstractEvent {

    public ReqPartTimeJobListEvent(RobotThread robot) {
        super(robot, S2CNewBuildingMsg.RespPartTimeJobList.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "请求兼职信息开始");
        C2SNewBuildingMsg.ReqPartTimeJobList.Builder builder =
                C2SNewBuildingMsg.ReqPartTimeJobList.newBuilder();
        SMessage msg = new SMessage(C2SNewBuildingMsg.ReqPartTimeJobList.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg);

        Log4jManager.getInstance().debug(robot.getWindow(),
                "robot:" + robot.getName() + "请求兼职信息发送消息");
    }



}
