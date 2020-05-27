package logic.chasm.req;

import org.game.protobuf.c2s.C2SHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.chasm.ReqChasmOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.CHASM,
        order = ReqChasmOrder.REQ_COMPOSE_HERO_TWO)
public class ReqComposeHeroTwoEvent extends AbstractEvent {

    public ReqComposeHeroTwoEvent(RobotThread robot) {
        super(robot, S2CHeroMsg.HeroCompose.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        if(robotPlayer.isComposeHeroTwo) {
            C2SHeroMsg.HeroCompose.Builder composeHeroBuilder = C2SHeroMsg.HeroCompose.newBuilder();
            composeHeroBuilder.setHeroCid(robotPlayer.composeHeroTwo);
            SMessage msg = new SMessage(C2SHeroMsg.HeroCompose.MsgID.eMsgID_VALUE,
                    composeHeroBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
        } else {
            super.robotSkipRun();
        }
    }

}
