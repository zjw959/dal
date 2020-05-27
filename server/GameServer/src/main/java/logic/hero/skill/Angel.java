package logic.hero.skill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CHeroMsg;

import data.GameDataManager;
import data.bean.AngelPassiveSkillGroovesCfgBean;
import data.bean.AngelSkillPageCfgBean;
import logic.constant.EPropertyModule;
import logic.hero.bean.Hero;

/**
 * 天使
 * 
 * @author ouyangcheng
 *
 */
public class Angel {
    private transient Hero hero;
    /** 技能觉醒等级 */
    private int awakeLevel;
    /** 正在使用的页签 */
    private int useSkillStrategy;
    /** 天使页签 */
    private Map<Integer, SkillStrategy> skillStrategyKV = new HashMap<>();

    public void createAngel(Hero hero) {
        this.hero = hero;
        this.awakeLevel = 1;
        this.useSkillStrategy = 1;
        // 根据配置初始化天使页签
        Map<Integer, AngelSkillPageCfgBean> angelSkillPageMap = GameDataManager.getAngelSkillPageCfgBeanKV();
        for(Map.Entry<Integer, AngelSkillPageCfgBean> entry : angelSkillPageMap.entrySet()) {
            AngelSkillPageCfgBean angelSkillPageCfgBean = entry.getValue();
            SkillStrategy skillStrategy = new SkillStrategy();
            skillStrategy.create(this.hero, angelSkillPageCfgBean.getId(), angelSkillPageCfgBean.getNameId());
            skillStrategyKV.put(skillStrategy.getId(), skillStrategy);
        }
    }

    public void init(Hero hero) {
        this.hero = hero;
        Map<Integer, AngelSkillPageCfgBean> angelSkillPageMap = GameDataManager.getAngelSkillPageCfgBeanKV();
        for(Map.Entry<Integer, AngelSkillPageCfgBean> entry : angelSkillPageMap.entrySet()) {
            AngelSkillPageCfgBean angelSkillPageCfgBean = entry.getValue();
            if(!skillStrategyKV.containsKey(angelSkillPageCfgBean.getId())) {
                SkillStrategy skillStrategy = new SkillStrategy();
                skillStrategy.create(this.hero, angelSkillPageCfgBean.getId(), angelSkillPageCfgBean.getNameId());
                skillStrategyKV.put(skillStrategy.getId(), skillStrategy);
            }
        }
        for (Map.Entry<Integer, SkillStrategy> entry : skillStrategyKV.entrySet()) {
            SkillStrategy skillStrategy = entry.getValue();
            skillStrategy.init(this.hero);
        }
    }

    public int getAwakeLevel() {
        return awakeLevel;
    }

    public void awake() {
        awakeLevel = awakeLevel + 1;
        Map<Integer, AngelPassiveSkillGroovesCfgBean> cfgBeanKV =
                GameDataManager.getAngelPassiveSkillGroovesCfgBeanKV();
        for (Map.Entry<Integer, AngelPassiveSkillGroovesCfgBean> entry : cfgBeanKV.entrySet()) {
            AngelPassiveSkillGroovesCfgBean cfgBean = entry.getValue();
            if (hero.getLevel() >= cfgBean.getNeedHeroLvl() && hero.getAngel().getAwakeLevel() >= cfgBean.getAngelLvl()) {
                unlockPassiveSkillGrid(cfgBean.getId());
            }
        }
    }

    public void setUseSkillStrategyId(int useSkillStrategy) {
        this.useSkillStrategy = useSkillStrategy;
        List<EPropertyModule> propertyModules = Arrays.asList(EPropertyModule.PROPERTY_HERO_SKILL);
        List<EPropertyModule> fightPowerModules = Arrays.asList(EPropertyModule.PROPERTY_HERO_SKILL);
        hero.getHeroProperty().calculateProperty(true, propertyModules, fightPowerModules);
    }

    public int getUseSkillStrategyId() {
        return useSkillStrategy;
    }

    public SkillStrategy getUseSkillStrategy() {
        return skillStrategyKV.get(useSkillStrategy);
    }

    public SkillStrategy getSkillStrategyById(int skillStrategyId) {
        return skillStrategyKV.get(skillStrategyId);
    }

    public List<S2CHeroMsg.SkillStrategy> buildSkillStrategy() {
        List<S2CHeroMsg.SkillStrategy> skillStrategyList = new ArrayList<>();
        for (Map.Entry<Integer, SkillStrategy> entry : skillStrategyKV.entrySet()) {
            SkillStrategy skillStrategy = entry.getValue();
            skillStrategyList.add(skillStrategy.buildSkillStrategyInfo());
        }
        return skillStrategyList;
    }
    
    public void unlockPassiveSkillGrid(int gridId) {
        for(Map.Entry<Integer, SkillStrategy> entry : skillStrategyKV.entrySet()) {
            SkillStrategy value = entry.getValue();
            value.unlockPassiveSkillGrid(gridId);
        }
    }
}
