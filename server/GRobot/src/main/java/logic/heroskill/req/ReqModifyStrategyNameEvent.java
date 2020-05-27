package logic.heroskill.req;

import org.game.protobuf.c2s.C2SHeroMsg;
import org.game.protobuf.c2s.C2SHeroMsg.ReqModifyStrategyName;
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
        order = ReqHeroSkillOrder.REQ_MODIFY_STRATEGY_NAME)
public class ReqModifyStrategyNameEvent extends AbstractEvent {

    public ReqModifyStrategyNameEvent(RobotThread robot) {
        super(robot, S2CHeroMsg.ResModifyStrategyName.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        
        ReqModifyStrategyName.Builder reqModifyStrategyNameBuilder = ReqModifyStrategyName.newBuilder();
        reqModifyStrategyNameBuilder.setHeroId(String.valueOf(robotPlayer.operateHeroId));
        reqModifyStrategyNameBuilder.setSkillStrategyId(robotPlayer.skillPageId);
        reqModifyStrategyNameBuilder.setName("天使页123");
        SMessage msg = new SMessage(C2SHeroMsg.ReqModifyStrategyName.MsgID.eMsgID_VALUE,
                reqModifyStrategyNameBuilder.build().toByteArray(), resOrder);
        sendMsg(msg);
    }

}
