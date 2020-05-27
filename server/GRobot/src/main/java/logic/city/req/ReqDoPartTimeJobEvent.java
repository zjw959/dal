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
 * 请求兼职任务
 * 
 * @author
 *
 */

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CITY,
        order = ReqCityOrder.REQ_DOPARTTIMEJOB)
public class ReqDoPartTimeJobEvent extends AbstractEvent {

    public ReqDoPartTimeJobEvent(RobotThread robot) {
        super(robot, S2CNewBuildingMsg.RespDoPartTimeJob.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {

        C2SNewBuildingMsg.ReqDoPartTimeJob.Builder builder =
                C2SNewBuildingMsg.ReqDoPartTimeJob.newBuilder();

        int buildingId = 106;
        int jobId = 106001;

        if (robot.getPlayer().getJobEvent() != null
                && robot.getPlayer().getJobEvent().getEtime() != 0) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "兼职任务已经存在");
            robotSkipRun();

        } else {
            // 随机生成一个
            JobInfo info = getAbleJob(robot.getPlayer());
            if (info != null) {
                buildingId = info.getBuildingId();
                jobId = info.getJobId();
            }
            builder.setBuildingId(buildingId);
            builder.setJobId(jobId);

            SMessage msg = new SMessage(C2SNewBuildingMsg.ReqDoPartTimeJob.MsgID.eMsgID_VALUE,
                    builder.build().toByteArray(), resOrder);
            sendMsg(msg, false);

            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "请求兼职任务");
        }

    }

    /** 获取可用兼职 **/
    public JobInfo getAbleJob(RobotPlayer player) {
        for (JobInfoList list : player.getJobInfoList()) {
            if (list.getJobInfosList() != null) {
                for (JobInfo info : list.getJobInfosList()) {
                    if (info.getType() == player.getDayType() && info.getJobType() == 1) {
                        return info;
                        // if (player.getJobEvent() == null)
                        // return info;
                        // if (player.getJobEvent().getJobId() == 0)
                        // return info;
                        // if (player.getJobEvent().getJobId() != info.getJobId())
                        // return info;
                    }
                }
            }
        }

        return null;

    }

}
