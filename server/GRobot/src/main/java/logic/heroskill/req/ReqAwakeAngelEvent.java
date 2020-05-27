package logic.heroskill.req;

import org.game.protobuf.c2s.C2SHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import logic.heroskill.ReqHeroSkillOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.HERO_SKILL,
        order = ReqHeroSkillOrder.REQ_WAKE_ANGEL)
public class ReqAwakeAngelEvent extends AbstractEvent {

    public ReqAwakeAngelEvent(RobotThread robot) {
        super(robot, S2CHeroMsg.ResAwakeAngel.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        if(robotPlayer.awakeHeroId != 0) {
            C2SHeroMsg.ReqAwakeAngel.Builder reqAwakeAngelBuilder = C2SHeroMsg.ReqAwakeAngel.newBuilder();
            reqAwakeAngelBuilder.setHeroId(String.valueOf(robotPlayer.awakeHeroId));
            SMessage msg = new SMessage(C2SHeroMsg.ReqAwakeAngel.MsgID.eMsgID_VALUE,
                    reqAwakeAngelBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
        } else {
            super.robotSkipRun();
        }
    }
}
