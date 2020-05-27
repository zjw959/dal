package logic.hero.resp;

import org.game.protobuf.s2c.S2CHeroMsg;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CHeroMsg.HeroInfo.MsgID.eMsgID_VALUE)
public class ResHeroInfoEvent extends AbstractEvent {

    public ResHeroInfoEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CHeroMsg.HeroInfo heroInfoMsg = S2CHeroMsg.HeroInfo.parseFrom(data);
            RobotPlayer robotPlayer = robot.getPlayer();
            robotPlayer.putHero(heroInfoMsg);    
        }
    }

}
