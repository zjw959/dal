package logic.hero.resp;

import org.game.protobuf.s2c.S2CHeroMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CHeroMsg.HeroCompose.MsgID.eMsgID_VALUE)
public class ResComposeHeroEvent extends AbstractEvent {

    public ResComposeHeroEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CHeroMsg.HeroCompose heroComposeMsg = S2CHeroMsg.HeroCompose.parseFrom(data);
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "合成英雄" + heroComposeMsg.getSuccess()); 
        }
    }
}
