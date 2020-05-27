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
 * 请求放弃兼职
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_GIVE_UP_JOB)
public class ReqReqGiveUpJobEvent extends AbstractEvent {

    public ReqReqGiveUpJobEvent(RobotThread robot) {
        super(robot, S2CNewBuildingMsg.RespGiveUpJob.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();

        if (player.getJobEvent() == null || player.getJobEvent().getEtime() <= 0) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求放弃兼职-当前兼职不存在");
            robotSkipRun();

        } else {
            C2SNewBuildingMsg.ReqGiveUpJob.Builder builder =
                    C2SNewBuildingMsg.ReqGiveUpJob.newBuilder();

            builder.setBuildingId(player.getJobEvent().getBuildingId());
            builder.setJobId(player.getJobEvent().getJobId());
            SMessage msg = new SMessage(C2SNewBuildingMsg.ReqGiveUpJob.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), resOrder);
            sendMsg(msg);

            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "正在请求放弃兼职");
        }

    }

}
