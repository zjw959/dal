package logic.hero.resp;

import java.util.List;

import org.game.protobuf.s2c.S2CHeroMsg;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CHeroMsg.HeroUpgradeResult.MsgID.eMsgID_VALUE)
public class ResHeroUpgradeResultEvent extends AbstractEvent {

    public ResHeroUpgradeResultEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CHeroMsg.HeroUpgradeResult heroUpgradeResultMsg = S2CHeroMsg.HeroUpgradeResult.parseFrom(data);
            List<RewardsMsg> rewards = heroUpgradeResultMsg.getRewardsList();
            StringBuilder info = new StringBuilder();
            for(RewardsMsg reward : rewards) {
                info.append(reward.getId());
                info.append(":");
                info.append(reward.getNum());
                info.append(",");
            }
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "升级英雄  " + info.toString());
        }
    }

}
