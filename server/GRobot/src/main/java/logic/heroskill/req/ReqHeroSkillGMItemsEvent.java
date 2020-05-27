package logic.heroskill.req;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.game.protobuf.c2s.C2SChatMsg;
import org.game.protobuf.s2c.S2CChatMsg;
import org.game.protobuf.s2c.S2CHeroMsg.AngeSkillInfo;
import org.game.protobuf.s2c.S2CHeroMsg.CrystalInfo;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;
import org.game.protobuf.s2c.S2CHeroMsg.SkillStrategy;
import org.game.protobuf.s2c.S2CShareMsg.AttributeInfo;

import core.event.AbstractEvent;
import core.event.EventType;
import core.event.FunctionType;
import core.event.IsEvent;
import core.net.message.SMessage;
import core.robot.RobotThread;
import data.GameDataManager;
import data.bean.AngelPassiveSkillGroovesCfgBean;
import data.bean.AngelSkillPageCfgBean;
import data.bean.AngelSkillTreeCfgBean;
import data.bean.EvolutionCfgBean;
import data.bean.HeroCfgBean;
import logic.heroskill.ReqHeroSkillOrder;
import logic.robot.entity.RobotPlayer;

@IsEvent(eventT = EventType.REQUSET_REPEAT, functionT = FunctionType.HERO_SKILL,
        order = ReqHeroSkillOrder.REQ_GM_ITEMS)
public class ReqHeroSkillGMItemsEvent extends AbstractEvent {

    public ReqHeroSkillGMItemsEvent(RobotThread robot) {
        super(robot, S2CChatMsg.RespGMCallBack.MsgID.eMsgID_VALUE);
    }

    @Override
    public void action(Object... obj) throws Exception {
        RobotPlayer robotPlayer = robot.getPlayer();
        robotPlayer.operateHeroId = 0;
        robotPlayer.awakeHeroId = 0;
        robotPlayer.skillPageId = 0;
        robotPlayer.type = 0;
        robotPlayer.pos = 0;
        robotPlayer.passiveSkill = 0;
        robotPlayer.crystalCell = 0;

        StringBuilder str = new StringBuilder();
        str.append("./addItemList ");

        // 随机一个精灵觉醒天使
        Map<Integer, HeroInfo> heroInfos = robotPlayer.getHeros();
        List<HeroInfo> heroInfoList = new ArrayList<>(heroInfos.values());
        int index = RandomUtils.nextInt(heroInfoList.size());
        HeroInfo heroInfo = heroInfoList.get(index);
        robotPlayer.operateHeroId = heroInfo.getCid();
        
        HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(heroInfo.getCid());
        List<Map<Integer, Integer>> consumeItems = heroCfgBean.getAngelWakeCons();
        if (heroInfo.getAngelLvl() <= consumeItems.size()) {
            robotPlayer.awakeHeroId = heroInfo.getCid();
            Map<Integer, Integer> items = consumeItems.get(heroInfo.getAngelLvl() - 1);
            for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                str.append(entry.getKey());
                str.append(":");
                str.append(entry.getValue());
                str.append(",");
            }
        }

        // 随机使用一个技能页
        List<AngelSkillPageCfgBean> angelSkillPageCfgBeans =
                GameDataManager.getAngelSkillPageCfgBeans();
        index = RandomUtils.nextInt(angelSkillPageCfgBeans.size());
        AngelSkillPageCfgBean angelSkillPageCfgBean = angelSkillPageCfgBeans.get(index);
        robotPlayer.skillPageId = angelSkillPageCfgBean.getId();

        // 随机操作一个技能
        SkillStrategy useSkillStrategy = null;
        List<SkillStrategy> skillStrategyInfos = heroInfo.getSkillStrategyInfoList();
        for (SkillStrategy skillStrategyInfo : skillStrategyInfos) {
            if (skillStrategyInfo.getId() == heroInfo.getUseSkillStrategy()) {
                useSkillStrategy = skillStrategyInfo;
                break;
            }
        }
        List<AngeSkillInfo> angeSkillInfos = useSkillStrategy.getAngeSkillInfosList();
        List<AngelSkillTreeCfgBean> randomCfgBeans = new ArrayList<>();
        List<AngelSkillTreeCfgBean> angelSkillTreeCfgBeans =
                GameDataManager.getAngelSkillTreeCfgBeans();
        for (AngelSkillTreeCfgBean temp : angelSkillTreeCfgBeans) {
            if (temp.getHeroId() == heroInfo.getCid()
                    && (temp.getSkillType() == 1
                    || temp.getSkillType() == 2
                    || temp.getSkillType() == 3
                    || temp.getSkillType() == 4)) {
                randomCfgBeans.add(temp);
            }
        }
        if(randomCfgBeans.size() > 0) {
            index = RandomUtils.nextInt(randomCfgBeans.size());
            AngelSkillTreeCfgBean angelSkillTreeCfgBean = randomCfgBeans.get(index);
            AngelSkillTreeCfgBean targetCfgBean = GameDataManager.getAngelSkillTreeCfgBean(angelSkillTreeCfgBean.getId() + 1);
            if(targetCfgBean != null) {
                if (heroInfo.getLvl() >= targetCfgBean.getNeedHeroLvl() && heroInfo.getAngelLvl() >= targetCfgBean.getNeedAngelLvl()) {
                    List<List<Integer>> conditionList = angelSkillTreeCfgBean.getFrontCondition();
                    boolean isSatisfy = true;
                    if (conditionList != null) {
                        Label:for (List<Integer> e : conditionList) {
                            for (Integer conditionId : e) {
                                AngelSkillTreeCfgBean conditionCfgBean = GameDataManager.getAngelSkillTreeCfgBean(conditionId);
                                int type = conditionCfgBean.getSkillType();
                                int pos = conditionCfgBean.getPosition();
                                boolean isContain = false;
                                for(AngeSkillInfo angeSkillInfo : angeSkillInfos) {
                                    if(angeSkillInfo.getType() == type && angeSkillInfo.getPos() == pos) {
                                        isContain = true;
                                        break;
                                    }
                                }
                                if(!isContain) {
                                    isSatisfy = false;
                                    break Label;
                                } 
                            }
                        }
                    }
                    
                    if(isSatisfy) {
                        int alreadyUseSkillPiont = useSkillStrategy.getAlreadyUseSkillPiont();
                        List<AttributeInfo> attributeInfos = heroInfo.getAttrList();
                        int skillPiontAttr = 0;
                        for(AttributeInfo attributeInfo : attributeInfos) {
                            if(attributeInfo.getType() == 13) {
                                skillPiontAttr = attributeInfo.getVal();
                                break;
                            }
                        }
                        int surplusSkillPoint = skillPiontAttr / 100 - alreadyUseSkillPiont;
                        if (surplusSkillPoint >= angelSkillTreeCfgBean.getNeedSkillPiont()) {
                            robotPlayer.type = angelSkillTreeCfgBean.getSkillType();
                            robotPlayer.pos = angelSkillTreeCfgBean.getPosition();
                        }
                    }
                }
            }
        }
        
        // 装备被动技能
        boolean isCanEquip = false;
        int heroLevel = heroInfo.getLvl();
        List<AngelPassiveSkillGroovesCfgBean> passiveSkillGroovesCfgBeans = GameDataManager.getAngelPassiveSkillGroovesCfgBeans();
        for(AngelPassiveSkillGroovesCfgBean beans : passiveSkillGroovesCfgBeans) {
            if(heroLevel >= beans.getNeedHeroLvl()) {
                isCanEquip = true;
                break;
            }
        }
        if(isCanEquip) {
            randomCfgBeans = new ArrayList<>();
            for (AngelSkillTreeCfgBean temp : angelSkillTreeCfgBeans) {
                if (temp.getHeroId() == heroInfo.getCid()
                        && temp.getSkillType() == 10) {
                    randomCfgBeans.add(temp);
                }
            }
            if(randomCfgBeans.size() > 0) {
                index = RandomUtils.nextInt(randomCfgBeans.size());
                AngelSkillTreeCfgBean angelSkillTreeCfgBean = randomCfgBeans.get(index);
                int awakeLevel = heroInfo.getAngelLvl();
                if (awakeLevel >= angelSkillTreeCfgBean.getNeedAngelLvl() && heroLevel >= angelSkillTreeCfgBean.getNeedHeroLvl()) {
                    robotPlayer.passiveSkill = angelSkillTreeCfgBean.getId();
                }
            }
        }
        
        //激活结晶
        List<CrystalInfo> crystalInfos = heroInfo.getCrystalInfoList();
        List<EvolutionCfgBean> container = new ArrayList<>();
        List<EvolutionCfgBean> evolutionCfgBeans = GameDataManager.getEvolutionCfgBeans();
        for (EvolutionCfgBean evolutionCfgBean : evolutionCfgBeans) {
            if (evolutionCfgBean.getHeroId() == heroCfgBean.getId() && evolutionCfgBean.getPartition() == robotPlayer.crystalRarity) {
                container.add(evolutionCfgBean);
            }
        }
        
        boolean isForward = true;
        for(EvolutionCfgBean temp : container) {
            boolean isExist = false;
            for(CrystalInfo crystalInfo : crystalInfos) {
                if(crystalInfo.getRarity() == temp.getPartition() && crystalInfo.getGridId() == temp.getCell()) {
                    isExist = true;
                    break;        
                }    
            }
            if(!isExist) {
                isForward = false;
                break;    
            }
        }
        
        if(isForward && robotPlayer.crystalRarity < heroInfo.getQuality()) {
            robotPlayer.crystalRarity = robotPlayer.crystalRarity + 1;       
            container = new ArrayList<>();
            for (EvolutionCfgBean evolutionCfgBean : evolutionCfgBeans) {
                if (evolutionCfgBean.getHeroId() == heroCfgBean.getId() && evolutionCfgBean.getPartition() == robotPlayer.crystalRarity) {
                    container.add(evolutionCfgBean);
                }
            }
        }
        
        if(container.size() > 0) {
            index = RandomUtils.nextInt(container.size());   
            EvolutionCfgBean evolutionCfgBean = container.get(index);
            boolean isExist = false;
            for(CrystalInfo crystalInfo : crystalInfos) {
                if(crystalInfo.getRarity() == evolutionCfgBean.getPartition() && crystalInfo.getGridId() == evolutionCfgBean.getCell()) {
                    isExist = true;
                    break;    
                }
            }
            if(!isExist) {
                List<Integer> conditionList = evolutionCfgBean.getUnlockCondition();
                boolean isSatisfy = true;
                if(conditionList != null) {
                    isSatisfy = false;
                    label: for(Integer condition : conditionList) {
                        for(CrystalInfo crystalInfo : crystalInfos) {
                            if(crystalInfo.getGridId() == evolutionCfgBean.getCell()) {
                                isSatisfy = true;
                                break label;
                            }
                        }
                    }
                }
                if(isSatisfy) {
                    robotPlayer.crystalRarity = robotPlayer.crystalRarity;
                    robotPlayer.crystalCell = robotPlayer.crystalCell;
                    Map<Integer, Integer> items = evolutionCfgBean.getMaterial();
                    for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                        str.append(entry.getKey());
                        str.append(":");
                        str.append(entry.getValue());
                        str.append(",");
                    }
                }    
            }
        }
        
        String gmContent = str.toString();
        int length = StringUtils.split(gmContent, " ").length;
        if (length > 1) {
            if (robotPlayer.awakeHeroId != 0 || robotPlayer.crystalCell != 0) {
                C2SChatMsg.ChatMsg.Builder builder = C2SChatMsg.ChatMsg.newBuilder();
                builder.setChannel(1);
                builder.setFun(1);
                builder.setContent(str.toString());
                SMessage msg = new SMessage(C2SChatMsg.ChatMsg.MsgID.eMsgID_VALUE,
                        builder.build().toByteArray(), this.resOrder);
                sendMsg(msg);
            } else {
                super.robotSkipRun();
            }
        } else {
            robotPlayer.awakeHeroId = 0;
            robotPlayer.crystalCell = 0;
            super.robotSkipRun();
        }
    }
}
