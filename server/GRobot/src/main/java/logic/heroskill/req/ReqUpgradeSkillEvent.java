package logic.heroskill.req;

import org.game.protobuf.c2s.C2SHeroMsg;
import org.game.protobuf.c2s.C2SHeroMsg.ReqUpgradeSkill;
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
        order = ReqHeroSkillOrder.REQ_UPGRADE_SKILL)
public class ReqUpgradeSkillEvent extends AbstractEvent {

    public ReqUpgradeSkillEvent(RobotThread robot) {
        super(robot, S2CHeroMsg.ResUpgradeSkill.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        
        if(robotPlayer.type != 0 && robotPlayer.pos != 0) {
            ReqUpgradeSkill.Builder reqUpgradeSkillBuilder = ReqUpgradeSkill.newBuilder();
            reqUpgradeSkillBuilder.setHeroId(String.valueOf(robotPlayer.operateHeroId));
            reqUpgradeSkillBuilder.setType(robotPlayer.type);
            reqUpgradeSkillBuilder.setPos(robotPlayer.pos);
            reqUpgradeSkillBuilder.setOperation(1);
            SMessage msg = new SMessage(C2SHeroMsg.ReqUpgradeSkill.MsgID.eMsgID_VALUE,
                    reqUpgradeSkillBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
        } else {
            super.robotSkipRun();
        }
    }
}
