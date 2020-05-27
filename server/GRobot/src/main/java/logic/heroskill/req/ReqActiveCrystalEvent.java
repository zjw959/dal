package logic.heroskill.req;

import org.game.protobuf.c2s.C2SHeroMsg;
import org.game.protobuf.c2s.C2SHeroMsg.ReqActiveCrystal;
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
        order = ReqHeroSkillOrder.REQ_ACTIVE_CRYSTAL)
public class ReqActiveCrystalEvent extends AbstractEvent {

    public ReqActiveCrystalEvent(RobotThread robot) {
        super(robot, S2CHeroMsg.ResActiveCrystal.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        if(robotPlayer.crystalRarity != 0 && robotPlayer.crystalCell != 0) {
            ReqActiveCrystal.Builder reqActiveCrystalBuilder = ReqActiveCrystal.newBuilder();
            reqActiveCrystalBuilder.setHeroId(String.valueOf(robotPlayer.operateHeroId));
            reqActiveCrystalBuilder.setRarity(robotPlayer.crystalRarity);
            reqActiveCrystalBuilder.setGridId(robotPlayer.crystalCell);
            
            SMessage msg = new SMessage(C2SHeroMsg.ReqActiveCrystal.MsgID.eMsgID_VALUE,
                    reqActiveCrystalBuilder.build().toByteArray(), resOrder);
            sendMsg(msg);
        } else {
            super.robotSkipRun();
        }
    }

}
