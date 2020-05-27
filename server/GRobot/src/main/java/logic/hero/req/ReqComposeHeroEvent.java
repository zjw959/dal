package logic.hero.req;

import org.game.protobuf.c2s.C2SHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg;

import core.Log4jManager;
import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.hero.ReqHeroOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.HERO,
        order = ReqHeroOrder.REQ_COMPOSE_HERO)
public class ReqComposeHeroEvent extends AbstractEvent {

    public ReqComposeHeroEvent(RobotThread robot) {
        super(robot, S2CHeroMsg.HeroCompose.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        int composeHeroId = robotPlayer.composeHeroId;
        if(composeHeroId != 0) {
            C2SHeroMsg.HeroCompose.Builder composeHeroBuilder = C2SHeroMsg.HeroCompose.newBuilder();
            composeHeroBuilder.setHeroCid(composeHeroId);
            SMessage msg = new SMessage(C2SHeroMsg.HeroCompose.MsgID.eMsgID_VALUE,
                    composeHeroBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
        } else {
            Log4jManager.getInstance().debug(robot.getWindow(),
                    "robot:" + robot.getName() + "没有要合成的精灵");
            super.robotSkipRun();
            return;
        }
    }
}
