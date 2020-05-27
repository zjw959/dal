package logic.city.resp;

import org.game.protobuf.s2c.S2CNewBuildingMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

/***
 * 请求放弃兼职回调
 * 
 * @author
 *
 */
@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.CITY,
        order = S2CNewBuildingMsg.RespGiveUpJob.MsgID.eMsgID_VALUE)
public class RespGiveUpJobEvent extends AbstractEvent {

    public RespGiveUpJobEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CNewBuildingMsg.RespGiveUpJob msg = S2CNewBuildingMsg.RespGiveUpJob.parseFrom(data);
            player.setJobEvent(msg.getJobEvent());
            if (msg.getJobList() != null) {
                player.getJobInfoList().forEach((list -> {
                    if (list.getBuildingId() == msg.getJobList().getBuildingId()) {
                        list = msg.getJobList();
                    }
                }));
            }
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求放弃兼职回调正确");
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求放弃兼职回调错误：");
        }
    }



}
