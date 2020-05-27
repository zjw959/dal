package logic.task.req;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;
import org.game.protobuf.c2s.C2STaskMsg;
import org.game.protobuf.s2c.S2CTaskMsg;
import org.game.protobuf.s2c.S2CTaskMsg.TaskInfo;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;
import logic.task.ReqTaskOrder;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.TASK,
        order = ReqTaskOrder.SUBMIT_TASK)
public class ReqSubmitTaskEvent extends AbstractEvent {

    public ReqSubmitTaskEvent(RobotThread robot) {
        super(robot, S2CTaskMsg.ResultSubmitTask.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer player = robot.getPlayer();
        Map<Integer, Map<Integer, TaskInfo>> taskMap = player.getTaskInfoKV();
        // 进行中
        Map<Integer, TaskInfo> runMap = taskMap.get(1);
        if (runMap == null || runMap.isEmpty()) {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "完成任务  " + "任务列表不含有完成");
            super.robotSkipRun();
            return;
        }
        List<Integer> keyList = new ArrayList<>(runMap.keySet());
        int randomCid = keyList.get(RandomUtils.nextInt(keyList.size()));
        TaskInfo info = runMap.get(randomCid);
        C2STaskMsg.SubmitTask.Builder builder = C2STaskMsg.SubmitTask.newBuilder();
        builder.setTaskCid(info.getCid());

        SMessage msg = new SMessage(C2STaskMsg.SubmitTask.MsgID.eMsgID_VALUE,
                builder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

}
