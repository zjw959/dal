package logic.dating.resp;

import org.game.protobuf.s2c.S2CDatingMsg;
import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

/**
 * 获取所有约会剧本的返回
 * 
 * @author lihongji
 *
 */

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.DATING,
        order = S2CDatingMsg.GetDatingInfo.MsgID.eMsgID_VALUE)
public class RespGetDatingInfoEvent extends AbstractEvent {

    public RespGetDatingInfoEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        if (data.length > 0) {
            RobotPlayer player = robot.getPlayer();
            S2CDatingMsg.GetDatingInfo msg = S2CDatingMsg.GetDatingInfo.parseFrom(data);
            // 城市约会
            player.setCityDatingInfoList(msg.getCityDatingInfoListList());
            // 已经通过的剧本id
            player.setRuleCidList(msg.getDatingRuleCidList());
            // 剧本结束节点
            player.setEndings(msg.getEndingsList());
            // 未完成约会
            player.setNotFinishDating(msg.getNotFinishDatingList());
            // 触发约会
            player.setTriggerDating(msg.getTriggerDatingList());

            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "获取所有的剧本success");
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "获取所有的剧本fail");
        }
    }

}
