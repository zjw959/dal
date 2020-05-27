package logic.heroskill.resp;

import org.game.protobuf.s2c.S2CHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg.CrystalInfo;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CHeroMsg.ResActiveCrystal.MsgID.eMsgID_VALUE)
public class ResActiveCrystalEvent extends AbstractEvent {

    public ResActiveCrystalEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            
            S2CHeroMsg.ResActiveCrystal resActiveCrystal = S2CHeroMsg.ResActiveCrystal.parseFrom(data);
            String heroIdStr = resActiveCrystal.getHeroId();
            int rarity = resActiveCrystal.getRarity();
            int gridId = resActiveCrystal.getGridId();
            
            HeroInfo heroInfo = robotPlayer.getHeros().get(Integer.parseInt(heroIdStr));
            HeroInfo.Builder heroInfoBuilder = HeroInfo.newBuilder(heroInfo);
            CrystalInfo.Builder crystalInfoBuilder = CrystalInfo.newBuilder();
            crystalInfoBuilder.setRarity(rarity);
            crystalInfoBuilder.setGridId(gridId);
            heroInfoBuilder.getCrystalInfoList().add(crystalInfoBuilder.build());
            robotPlayer.putHero(heroInfoBuilder.build());
        }
    }

}
