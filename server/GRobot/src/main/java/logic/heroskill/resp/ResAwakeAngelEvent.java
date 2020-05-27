package logic.heroskill.resp;

import org.game.protobuf.s2c.S2CHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CHeroMsg.ResAwakeAngel.MsgID.eMsgID_VALUE)
public class ResAwakeAngelEvent extends AbstractEvent {

    public ResAwakeAngelEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            S2CHeroMsg.ResAwakeAngel heroComposeMsg = S2CHeroMsg.ResAwakeAngel.parseFrom(data);
            String heroId = heroComposeMsg.getHeroId();
            int angelLvl = heroComposeMsg.getAngelLvl();
            HeroInfo heroInfo = robotPlayer.getHeros().get(Integer.parseInt(heroId));
            HeroInfo.Builder heroInfoBuilder = HeroInfo.newBuilder(heroInfo);
            heroInfoBuilder.setAngelLvl(angelLvl);
            robotPlayer.putHero(heroInfoBuilder.build());
        }
    }
}
