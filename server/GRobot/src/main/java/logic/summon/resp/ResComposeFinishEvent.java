package logic.summon.resp;

import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;
import org.game.protobuf.s2c.S2CSummonMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CSummonMsg.ComposeFinish.MsgID.eMsgID_VALUE)
public class ResComposeFinishEvent extends AbstractEvent {

    public ResComposeFinishEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            S2CSummonMsg.ComposeFinish composeFinishBuilder = S2CSummonMsg.ComposeFinish.parseFrom(data);
            List<RewardsMsg> items = composeFinishBuilder.getItemList();
            int pointType = composeFinishBuilder.getZPointType();
            int score = composeFinishBuilder.getScore();
            Map<Integer, Integer> composeInfos = robotPlayer.getComposeInfos();
            composeInfos.remove(pointType);
            robotPlayer.setPrayScore(score);
            
            StringBuilder info = new StringBuilder();
            for(RewardsMsg item : items) {
               info.append(item.getId());
               info.append(":");
               info.append(item.getNum());
               info.append(",");
            }
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + ",质点" + pointType + "合成奖励:" + info.toString() + ",祈愿分:" + score); 
        }
    }

}
