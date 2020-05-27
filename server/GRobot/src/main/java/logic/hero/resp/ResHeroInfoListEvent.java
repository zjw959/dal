package logic.hero.resp;

import java.util.List;

import org.game.protobuf.s2c.S2CHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CHeroMsg.HeroInfoList.MsgID.eMsgID_VALUE)
public class ResHeroInfoListEvent extends AbstractEvent {

    public ResHeroInfoListEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            S2CHeroMsg.HeroInfoList heroInfoListMsg = S2CHeroMsg.HeroInfoList.parseFrom(data);
            List<HeroInfo> heroInfList = heroInfoListMsg.getHerosList();
            RobotPlayer robotPlayer = robot.getPlayer();
            for(HeroInfo heroInfo : heroInfList) {
                robotPlayer.putHero(heroInfo);
            }
        }
    }

}
