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
order = ReqHeroSkillOrder.REQ_UNLOAD_PASSIVE_SKILL)
public class ReqUnloadPassiveSkill extends AbstractEvent {

    public ReqUnloadPassiveSkill(RobotThread robot) {
        super(robot, S2CHeroMsg.ResEquipPassiveSkill.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        if(robotPlayer.passiveSkill != 0) {
            C2SHeroMsg.ReqEquipPassiveSkill.Builder reqEquipPassiveSkillBuilder = C2SHeroMsg.ReqEquipPassiveSkill.newBuilder(); 
            reqEquipPassiveSkillBuilder.setHeroId(String.valueOf(robotPlayer.operateHeroId));
            reqEquipPassiveSkillBuilder.setSkillId(robotPlayer.passiveSkill);
            
            SMessage msg = new SMessage(C2SHeroMsg.ReqEquipPassiveSkill.MsgID.eMsgID_VALUE,
                    reqEquipPassiveSkillBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
        } else {
            super.robotSkipRun();
        }
    }

}
