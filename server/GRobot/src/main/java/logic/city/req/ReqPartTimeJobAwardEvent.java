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
 * 领取兼职任务奖励
 * 
 * @author
 *
 */

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_GETPARTTIMEAWAR)
public class ReqPartTimeJobAwardEvent extends AbstractEvent {

    public ReqPartTimeJobAwardEvent(RobotThread robot) {
        super(robot, S2CNewBuildingMsg.RespPartTimeJobAward.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        C2SNewBuildingMsg.ReqPartTimeJobAward.Builder builder =
                C2SNewBuildingMsg.ReqPartTimeJobAward.newBuilder();
        RobotPlayer player = robot.getPlayer();
        int timeNow = (int) (System.currentTimeMillis() / 1000);
        if (player.getJobEvent() == null || player.getJobEvent().getJobId() == 0
                || player.getJobEvent().getEtime() == 0
                || player.getJobEvent().getEtime() > timeNow) {
            robotSkipRun();
        } else {
            builder.setBuildingId(player.getJobEvent().getBuildingId());
            builder.setJobId(player.getJobEvent().getJobId());

            SMessage msg = new SMessage(C2SNewBuildingMsg.ReqPartTimeJobAward.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), resOrder);
            sendMsg(msg, false);

            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + " 领取兼职奖励 ");
        }

    }

}
