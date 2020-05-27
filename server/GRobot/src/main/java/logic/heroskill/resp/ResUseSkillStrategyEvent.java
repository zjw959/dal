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
        order = S2CHeroMsg.ResUseSkillStrategy.MsgID.eMsgID_VALUE)
public class ResUseSkillStrategyEvent extends AbstractEvent {

    public ResUseSkillStrategyEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            S2CHeroMsg.ResUseSkillStrategy resUseSkillStrategyMsg = S2CHeroMsg.ResUseSkillStrategy.parseFrom(data);
            String heroId = resUseSkillStrategyMsg.getHeroId();
            int skillStrategyId = resUseSkillStrategyMsg.getSkillStrategyId();
            HeroInfo heroInfo = robotPlayer.getHeros().get(Integer.parseInt(heroId));
            HeroInfo.Builder heroInfoBuilder = HeroInfo.newBuilder(heroInfo);
            heroInfoBuilder.setUseSkillStrategy(skillStrategyId);
            robotPlayer.putHero(heroInfoBuilder.build());
        }
    }

}
