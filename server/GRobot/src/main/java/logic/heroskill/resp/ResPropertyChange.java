package logic.heroskill.resp;

import java.util.List;

import org.game.protobuf.s2c.S2CHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;
import org.game.protobuf.s2c.S2CShareMsg.AttributeInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CHeroMsg.ResPropertyChange.MsgID.eMsgID_VALUE)
public class ResPropertyChange extends AbstractEvent {

    public ResPropertyChange(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            S2CHeroMsg.ResPropertyChange resPropertyChange = S2CHeroMsg.ResPropertyChange.parseFrom(data);
            String heroId = resPropertyChange.getHeroId();
            List<AttributeInfo> attrs = resPropertyChange.getAttrList();
            int fightPower = resPropertyChange.getFightPower();
            
            HeroInfo heroInfo = robotPlayer.getHeros().get(Integer.parseInt(heroId));
            HeroInfo.Builder heroInfoBuilder = HeroInfo.newBuilder(heroInfo);
            heroInfoBuilder.clearAttr();
            heroInfoBuilder.addAllAttr(attrs);
            heroInfoBuilder.setFightPower(fightPower);
            robotPlayer.putHero(heroInfoBuilder.build());
        }
    }

}
