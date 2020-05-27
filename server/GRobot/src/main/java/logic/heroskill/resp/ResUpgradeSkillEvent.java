package logic.heroskill.resp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.game.protobuf.s2c.S2CHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg.AngeSkillInfo;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;
import org.game.protobuf.s2c.S2CHeroMsg.ResUpgradeSkill;
import org.game.protobuf.s2c.S2CHeroMsg.SkillStrategy;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.robot.RobotThread;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.RESPONSE, functionT = FunctionType.NULL,
        order = S2CHeroMsg.ResUpgradeSkill.MsgID.eMsgID_VALUE)
public class ResUpgradeSkillEvent extends AbstractEvent {

    public ResUpgradeSkillEvent(RobotThread robot) {
        super(robot);
    }

    @Override
    public void action(Object... obj) throws Exception {
        byte[] data = (byte[]) obj[0];
        int len = data.length;
        if (len > 0) {
            RobotPlayer robotPlayer = robot.getPlayer();
            
            ResUpgradeSkill resUpgradeSkill = ResUpgradeSkill.parseFrom(data);
            String heroIdStr = resUpgradeSkill.getHeroId();
            AngeSkillInfo angeSkillInfo = resUpgradeSkill.getAngeSkillInfo();
            int useSkillPiont = resUpgradeSkill.getUseSkillPiont();
            
            HeroInfo heroInfo = robotPlayer.getHeros().get(Integer.parseInt(heroIdStr));
            HeroInfo.Builder heroInfoBuilder = HeroInfo.newBuilder(heroInfo);
            SkillStrategy useSkillStrategy = null;
            List<SkillStrategy> skillStrategyInfos = heroInfo.getSkillStrategyInfoList();
            List<SkillStrategy> skillStrategyInfoCopys = new ArrayList<>();
            for(SkillStrategy skillStrategyInfo : skillStrategyInfos) {
                if(skillStrategyInfo.getId() == heroInfo.getUseSkillStrategy()) {
                    useSkillStrategy = skillStrategyInfo;
                    continue;
                }
                skillStrategyInfoCopys.add(skillStrategyInfo);
            }
            
            SkillStrategy.Builder skillStrategyBuilder = SkillStrategy.newBuilder(useSkillStrategy);
            skillStrategyBuilder.setAlreadyUseSkillPiont(useSkillPiont);
            List<AngeSkillInfo> angeSkillInfos = skillStrategyBuilder.getAngeSkillInfosList();
            List<AngeSkillInfo> angeSkillInfoCopys = new ArrayList<>();
            Iterator<AngeSkillInfo> itr = angeSkillInfos.iterator();
            while(itr.hasNext()) {
                AngeSkillInfo temp = itr.next();
                if(temp.getType() == angeSkillInfo.getType() && temp.getPos() == angeSkillInfo.getPos()) {
                    continue;
                }
                angeSkillInfoCopys.add(temp);
            }
            angeSkillInfoCopys.add(angeSkillInfo);
            skillStrategyBuilder.addAllAngeSkillInfos(angeSkillInfoCopys);
            skillStrategyInfoCopys.add(skillStrategyBuilder.build());
            heroInfoBuilder.clearSkillStrategyInfo();
            heroInfoBuilder.addAllSkillStrategyInfo(skillStrategyInfoCopys);
            robotPlayer.putHero(heroInfoBuilder.build());
        }
    }

}
