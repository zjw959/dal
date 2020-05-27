package logic.hero.skill;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg.PassiveSkillInfo;

import data.GameDataManager;
import data.bean.AngelPassiveSkillGroovesCfgBean;
import logic.constant.EPropertyModule;
import logic.hero.HeroProperty;
import logic.hero.bean.Hero;

/**
 * 技能页签
 * 
 * @author ouyangcheng
 *
 */
public class SkillStrategy {
    private transient Hero hero;
    private int id;
    private String name;
    /** 已经使用的技能点 */
    private int alreadyUseSkillPiont;
    /** 技能类别 */
    private Map<Integer, HeroSkillTree> angelKV = new HashMap<>();
    /** 被动技能 */
    public Map<Integer, PassiveSkillGrid> passiveSkillKV = new LinkedHashMap<>();

    public void create(Hero hero, int id, String name) {
        this.hero = hero;
        this.id = id;
        this.name = name;
        initSkill();
    }

    public void initSkill() {
        alreadyUseSkillPiont = 0;
        angelKV.clear();
        passiveSkillKV.clear();
        Map<Integer, AngelPassiveSkillGroovesCfgBean> passiveSkillCfgKV = GameDataManager.getAngelPassiveSkillGroovesCfgBeanKV();
        for(Map.Entry<Integer, AngelPassiveSkillGroovesCfgBean> entry : passiveSkillCfgKV.entrySet()) {
            AngelPassiveSkillGroovesCfgBean cfgBean = entry.getValue();
            PassiveSkillGrid passiveSkillGrid = new PassiveSkillGrid();
            passiveSkillGrid.create(cfgBean.getId());
            if(hero.getLevel() >= cfgBean.getNeedHeroLvl() && hero.getAngel().getAwakeLevel() >= cfgBean.getAngelLvl()) {
                passiveSkillGrid.setUnlock(true);
            }
            passiveSkillKV.put(passiveSkillGrid.getId(), passiveSkillGrid);
        }
    }
    
    public void init(Hero hero) {
        this.hero = hero;
        for (Map.Entry<Integer, HeroSkillTree> entry : angelKV.entrySet()) {
            HeroSkillTree heroSkillTree = entry.getValue();
            heroSkillTree.init();
        }
        Map<Integer, AngelPassiveSkillGroovesCfgBean> passiveSkillCfgKV = GameDataManager.getAngelPassiveSkillGroovesCfgBeanKV();
        for(Map.Entry<Integer, AngelPassiveSkillGroovesCfgBean> entry : passiveSkillCfgKV.entrySet()) {
            AngelPassiveSkillGroovesCfgBean cfgBean = entry.getValue();
            if(!passiveSkillKV.containsKey(cfgBean.getId())) {
                PassiveSkillGrid passiveSkillGrid = new PassiveSkillGrid();
                passiveSkillGrid.create(cfgBean.getId());
                passiveSkillKV.put(passiveSkillGrid.getId(), passiveSkillGrid);
            }
        }
        
    }

    public void resetSkill() {
        alreadyUseSkillPiont = 0;
        angelKV.clear();
        for(Map.Entry<Integer, PassiveSkillGrid> entry : passiveSkillKV.entrySet()) {
            PassiveSkillGrid skillGrid = entry.getValue();
            skillGrid.setSkillId(0);
        }
    }
    
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getAlreadyUseSkillPiont() {
        return alreadyUseSkillPiont;
    }

    public void useSkillPiont(int skillPiont) {
        alreadyUseSkillPiont = alreadyUseSkillPiont + skillPiont;
        if(alreadyUseSkillPiont <= 0) {
            alreadyUseSkillPiont = 0;
        }
        HeroProperty heroProperty = hero.getHeroProperty();
        List<EPropertyModule> propertyModules = Arrays.asList(EPropertyModule.PROPERTY_HERO_SKILL);
        List<EPropertyModule> fightPowerModules = Arrays.asList(EPropertyModule.PROPERTY_HERO_SKILL);
        heroProperty.calculateProperty(true, propertyModules, fightPowerModules);
    }

    public HeroSkillTree getHeroSkillTree(int type) {
        HeroSkillTree heroSkillTree = angelKV.get(type);
        return heroSkillTree;
    }

    public void putHeroSkillTree(int type, HeroSkillTree heroSkillTree) {
        angelKV.put(type, heroSkillTree);
    }

    public void removeHeroSkillTree(int type) {
        angelKV.remove(type);
    }

    public PassiveSkillGrid isEquipPassiveSkill(int skillId) {
        PassiveSkillGrid passiveSkillGrid = null;
        for (Map.Entry<Integer, PassiveSkillGrid> entry : passiveSkillKV.entrySet()) {
            PassiveSkillGrid value = entry.getValue();
            if (value.isUnlock() && value.getSkillId() == skillId) {
                passiveSkillGrid = value;
                break;
            }
        }
        return passiveSkillGrid;
    }

    public PassiveSkillGrid getEmptyPassiveSkillGrid() {
        PassiveSkillGrid passiveSkillGrid = null;
        for (Map.Entry<Integer, PassiveSkillGrid> entry : passiveSkillKV.entrySet()) {
            PassiveSkillGrid value = entry.getValue();
            if (value.isUnlock() && value.getSkillId() == 0) {
                passiveSkillGrid = value;
                break;
            }
        }
        return passiveSkillGrid;
    }

    public PassiveSkillGrid getPassiveSkillGrid(int id) {
        PassiveSkillGrid passiveSkillGrid = passiveSkillKV.get(id);
        return passiveSkillGrid;
    }
    
    public S2CHeroMsg.SkillStrategy buildSkillStrategyInfo() {
        S2CHeroMsg.SkillStrategy.Builder skillStrategyBuilder =
                S2CHeroMsg.SkillStrategy.newBuilder();
        skillStrategyBuilder.setId(id);
        skillStrategyBuilder.setName(name);
        skillStrategyBuilder.setAlreadyUseSkillPiont(alreadyUseSkillPiont);
        for (Map.Entry<Integer, HeroSkillTree> entry : angelKV.entrySet()) {
            HeroSkillTree heroSkillTree = entry.getValue();
            skillStrategyBuilder.addAllAngeSkillInfos(heroSkillTree.buildAngeSkillInfoList());
        }
        for (Map.Entry<Integer, PassiveSkillGrid> entry : passiveSkillKV.entrySet()) {
            PassiveSkillGrid value = entry.getValue();
            if (value.isUnlock() && value.getSkillId() != 0) {
                PassiveSkillInfo.Builder passiveSkillInfoBuilder = PassiveSkillInfo.newBuilder();
                passiveSkillInfoBuilder.setPos(value.getId());
                passiveSkillInfoBuilder.setSkillId(value.getSkillId());
                skillStrategyBuilder.addPassiveSkillInfo(passiveSkillInfoBuilder);
            }
        }
        return skillStrategyBuilder.build();
    }
    
    public void unlockPassiveSkillGrid(int gridId) {
        PassiveSkillGrid passiveSkillGrid = passiveSkillKV.get(gridId);
        if(!passiveSkillGrid.isUnlock()) {
            passiveSkillGrid.setUnlock(true);
        }
    }
    
    public Map<Integer, PassiveSkillGrid> getPassiveSkill() {
        return passiveSkillKV;
    }
}
