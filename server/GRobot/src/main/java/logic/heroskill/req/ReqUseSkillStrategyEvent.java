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
        order = ReqHeroSkillOrder.REQ_USE_SKILL_STRATEGY)
public class ReqUseSkillStrategyEvent extends AbstractEvent {

    public ReqUseSkillStrategyEvent(RobotThread robot) {
        super(robot, S2CHeroMsg.ResUseSkillStrategy.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        
        C2SHeroMsg.ReqUseSkillStrategy.Builder reqUseSkillStrategyBuilder = C2SHeroMsg.ReqUseSkillStrategy.newBuilder();
        reqUseSkillStrategyBuilder.setHeroId(String.valueOf(robotPlayer.operateHeroId));
        reqUseSkillStrategyBuilder.setSkillStrategyId(robotPlayer.skillPageId);
        SMessage msg = new SMessage(C2SHeroMsg.ReqUseSkillStrategy.MsgID.eMsgID_VALUE,
                reqUseSkillStrategyBuilder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

}
