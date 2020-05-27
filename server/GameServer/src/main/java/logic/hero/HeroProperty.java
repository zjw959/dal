package logic.hero;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.game.protobuf.s2c.S2CShareMsg;

import com.google.common.collect.Maps;

import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import data.bean.EquipmentCfgBean;
import data.bean.EquipmentRandomCfgBean;
import data.bean.EvolutionCfgBean;
import data.bean.HeroCfgBean;
import data.bean.HeroProgressCfgBean;
import logic.constant.DiscreteDataID;
import logic.constant.EPropertyModule;
import logic.constant.EPropertyType;
import logic.equip.bean.EquipSpecialAttr;
import logic.hero.bean.Hero;
import logic.hero.bean.HeroEquip;
import logic.hero.skill.Angel;
import logic.hero.skill.PassiveSkillGrid;
import logic.hero.skill.SkillStrategy;
import logic.item.bean.EquipItem;

public class HeroProperty {
    private Hero hero;
    /** 每个模块的属性 */
    private Map<EPropertyModule, Property> propertyModuleMap = new HashMap<>();
    /** 每个模块的战斗力 */
    private Map<EPropertyModule, Integer> fightPowerModuleMap = new HashMap<>();
    
    /**
     * 初始化属性模块
     * 
     * @param hero
     */
    public void init(Hero hero) {
        this.hero = hero;
        for (EPropertyModule module : EPropertyModule.values()) {
            calculateModuleProperty(module);
            calculateModuleFightPower(module);
        }
    }

    /**
     * 获取某个模块的属性
     * 
     * @param module
     * @return
     */
    public Property getModuleProperty(EPropertyModule module) {
        Property property = propertyModuleMap.get(module);
        return property;
    }

    /**
     * 获取所有属性
     * 
     * @return
     */
    public Property getProperty() {
        Property property = new Property();
        for (EPropertyModule module : EPropertyModule.values()) {
            Property propertyTemp = getModuleProperty(module);
            property.addTo(propertyTemp);
        }
        return property;
    }

    /**
     * 获取某个属性的值
     * 
     * @return
     */
    public int getPropertyValue(EPropertyType propertyType) {
        Property property = getProperty();
        return property.getProperty(propertyType);
    }

    public void calculateProperty(boolean isNotify, List<EPropertyModule> propertyModules, List<EPropertyModule> fightPowerModules) {
        for (EPropertyModule propertyModule : propertyModules) {
            calculateModuleProperty(propertyModule);
        }
        for (EPropertyModule fightPowerModule : fightPowerModules) {
            calculateModuleFightPower(fightPowerModule);
        }
        if(isNotify) {
            hero.sendPropertyChange();
        }
    }
    
    
    /**
     * 计算指定模块的属性
     * 
     * @param module
     * @return
     */
    private void calculateModuleProperty(EPropertyModule module) {
        Property property;
        switch (module) {
            case PROPERTY_HERO_BASE:
                property = this.calculateHeroProperty();
                break;
            case PROPERTY_HERO_GROW:
                property = this.calculateGrowProperty();
                break;
            case PROPERTY_HERO_EQUIP:
                property = this.calculateEquipProperty();
                break;
            case PROPERTY_HERO_SKILL:
                property = this.calculateSkillProperty();
                break;
            case PROPERTY_HERO_CRYSTAL:
                property = this.calculateCrystalProperty();
                break;
            default:
                throw new RuntimeException("can not find EPropertyModule :" + module.name());
        }
        propertyModuleMap.put(module, property);
    }
    
    /**
     * 计算指定模块的战斗力
     * 
     * @param module
     * @return
     */
    private void calculateModuleFightPower(EPropertyModule module) {
        int fightPower;
        switch (module) {
            case PROPERTY_HERO_BASE:
                fightPower = calculateHeroFightPower();
                break;
            case PROPERTY_HERO_GROW:
                fightPower = calculateGrowFightPower();
                break;
            case PROPERTY_HERO_EQUIP:
                fightPower = this.calculateEquipFightPower();
                break;
            case PROPERTY_HERO_SKILL:
                fightPower = calculateSkillFightPower();
                break;
            case PROPERTY_HERO_CRYSTAL:
                fightPower = calculateCrystalFightPower();
                break;
            default:
                throw new RuntimeException("can not find EPropertyModule :" + module.name());
        }
        fightPowerModuleMap.put(module, fightPower);
    }
    
    /**
     * 计算英雄的基础属性
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private Property calculateHeroProperty() {
        Property property = new Property();
        HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(hero.getCid());
        // 品质基本属性
        int actualQuality = heroCfgBean.getAttribute2() * 100 + hero.getQuality();
        HeroProgressCfgBean heroProgressCfgBean =
                GameDataManager.getHeroProgressCfgBean(actualQuality);
        Map<Integer, Integer> basePropertyMap = heroProgressCfgBean.getBaseAttr();
        for (Map.Entry<Integer, Integer> entry : basePropertyMap.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            property.addProperty(EPropertyType.getEPropertyType(key), value);
        }
        return property;
    }

    /**
     * 计算成长属性
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private Property calculateGrowProperty() {
        int level = hero.getLevel();
        Property property = new Property();
        // 品质影响成长属性
        HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(hero.getCid());
        int actualQuality = heroCfgBean.getAttribute2() * 100 + hero.getQuality();
        HeroProgressCfgBean heroProgressCfgBean =
                GameDataManager.getHeroProgressCfgBean(actualQuality);
        Map<Integer, Integer> growPropertyMap = heroProgressCfgBean.getUpAttr();
        for (Map.Entry<Integer, Integer> entry : growPropertyMap.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            value = value * (level - 1);
            property.setProperty(EPropertyType.getEPropertyType(key), value);
        }
        return property;
    }

    /**
     * 计算装备属性
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private Property calculateEquipProperty() {
        Property property = new Property();
        Map<Integer, Integer> attrMap = Maps.newHashMap();
        if (hero.getHeroEquip() == null || hero.getHeroEquip().size() == 0)
            return property;
        hero.getHeroEquip().values().forEach(heroEquip -> {
            EquipItem equipItem = heroEquip.getEquipItem();
            EquipmentCfgBean cfg = GameDataManager.getEquipmentCfgBean(equipItem.getTemplateId());
            int level = equipItem.getLevel();

            // 计算基础属性
            cfg.getBaseAttribute().forEach((k, v) -> {
                int attrType = (int) k;
                int attrValue = (int) v;
                int growth = (int) cfg.getGrowthAttribute().get(attrType);
                attrValue += growth * (level - 1);
                int otherValue = attrMap.computeIfAbsent(attrType, key -> 0);
                attrMap.put(attrType, otherValue + attrValue);
            });

            // 计算特殊属性
            List<EquipSpecialAttr> specialAttrList = equipItem.getSpecialAttrList();
            specialAttrList.forEach(attrInfo -> {
                EquipmentRandomCfgBean specialCfg =
                        GameDataManager.getEquipmentRandomCfgBean(attrInfo.getTemplateId());
                int type = specialCfg.getAttrType();
                int value = attrInfo.getValue();
                int otherValue = attrMap.computeIfAbsent(type, key -> 0);
                attrMap.put(type, otherValue + value);
            });
        });
        attrMap.forEach((k, v) -> {
            int key = (int) k;
            int value = (int) v;
            property.addProperty(EPropertyType.getEPropertyType(key), value);
        });
        return property;
    }

    /**
     * 计算技能提供的属性
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    private Property calculateSkillProperty() {
        Property property = new Property();
        Angel angel = hero.getAngel();
        int awakeLevel = angel.getAwakeLevel();
        HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(hero.getCid());
        List<Map<Integer, Integer>> attrs = heroCfgBean.getSkillPoint();
        Map<Integer, Integer> attr = attrs.get(awakeLevel - 1);
        for (Map.Entry<Integer, Integer> entry : attr.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            property.addProperty(EPropertyType.getEPropertyType(key), value);
        }
        return property;
    }

    /**
     * 计算结晶提供的属性
     * @return
     */
    @SuppressWarnings("unchecked")
    private Property calculateCrystalProperty() {
        Property property = new Property();
        Map<Integer, List<Integer>> crystalKV = hero.getCrystalKV();
        List<EvolutionCfgBean> cfgBeans = new ArrayList<>();
        List<EvolutionCfgBean> evolutionCfgBeans = GameDataManager.getEvolutionCfgBeans();
        for(EvolutionCfgBean evolutionCfgBean : evolutionCfgBeans) {
            if(evolutionCfgBean.getHeroId() == hero.getCid()) {
                cfgBeans.add(evolutionCfgBean);
            }
        }
        for(Map.Entry<Integer, List<Integer>> entry : crystalKV.entrySet()) {
            List<Integer> crystalList = entry.getValue();
            for(Integer crystal : crystalList) {
                EvolutionCfgBean evolutionCfgBean = null;
                for(EvolutionCfgBean cfgBean : cfgBeans) {
                    if(cfgBean.getPartition() == entry.getKey() && cfgBean.getCell() == crystal) {
                        evolutionCfgBean = cfgBean;
                        break;
                    }
                }
                Map<Integer, Integer> attrMap = evolutionCfgBean.getAttribute();
                for (Map.Entry<Integer, Integer> attr : attrMap.entrySet()) {
                    int key = attr.getKey();
                    int value = attr.getValue();
                    property.addProperty(EPropertyType.getEPropertyType(key), value);
                }
            }
        }
        return property;
    }
    
    /**
     * 获取模块的战斗力
     * @return
     */
    public int getModuleFightPower(EPropertyModule module) {
        return fightPowerModuleMap.get(module);
    } 
    
    /**
     * 计算战斗力
     * 
     * @return
     */
    public int calculateFightPower() {
        int fightPower = 0;
        for (EPropertyModule module : EPropertyModule.values()) {
            fightPower = fightPower + getModuleFightPower(module);
        }
        return fightPower;
    }

    /**
     * 计算英雄基础战斗力
     * 
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private int calculateHeroFightPower() {
        int heroFightPower = 0;
        DiscreteDataCfgBean discreteDataCfgBean = GameDataManager.getDiscreteDataCfgBean(3005);
        Map data = discreteDataCfgBean.getData();
        List<Integer> rarityzl = (List<Integer>) data.get("rarityzl");
        heroFightPower = heroFightPower + rarityzl.get(hero.getQuality() - 1);
        return heroFightPower;
    }
    
    /**
     * 计算英雄成长战斗力
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private int calculateGrowFightPower() {
        int heroFightPower = 0;
        DiscreteDataCfgBean discreteDataCfgBean = GameDataManager.getDiscreteDataCfgBean(3005);
        Map data = discreteDataCfgBean.getData();
        List<Integer> herolvlzl = (List<Integer>) data.get("herolvlzl");
        heroFightPower = heroFightPower + hero.getLevel() * herolvlzl.get(hero.getQuality() - 1);
        return heroFightPower;
    }

    /**
     * 计算装备战斗力
     * 
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private int calculateEquipFightPower() {
        DiscreteDataCfgBean discreteDataCfgBean =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.FIGHT_POWER_RULE);
        Map data = discreteDataCfgBean.getData();
        List<Integer> equipzl = (List<Integer>) data.get("equipzl");
        List<Integer> equiplvlzl = (List<Integer>) data.get("equiplvlzl");
        int heroFightPower = 0;
        Map<Integer, HeroEquip> heroEquip = hero.getHeroEquip();
        for (Entry<Integer, HeroEquip> e : heroEquip.entrySet()) {
            EquipmentCfgBean cfgBean = GameDataManager
                    .getEquipmentCfgBean(e.getValue().getEquipItem().getTemplateId());
            int star = cfgBean.getStar();
            heroFightPower += (equipzl.get(star - 1)
                    + (equiplvlzl.get(star - 1)) * e.getValue().getEquipItem().getLevel());
        }

        return heroFightPower;
    }

    /**
     * 计算技能战斗力
     * 
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private int calculateSkillFightPower() {
        int heroFightPower = 0;
        DiscreteDataCfgBean discreteDataCfgBean = GameDataManager.getDiscreteDataCfgBean(3005);
        Map data = discreteDataCfgBean.getData();
        List<Integer> angelzl = (List<Integer>) data.get("angelzl");
        int angelpoint = (int) data.get("angelpoint");
        Angel angel = hero.getAngel();
        SkillStrategy skillStrategy = angel.getUseSkillStrategy();
        heroFightPower = heroFightPower + angelzl.get(angel.getAwakeLevel() - 1);
        heroFightPower = heroFightPower + skillStrategy.getAlreadyUseSkillPiont() * angelpoint;
        
        //计算被动技能战斗力
        Map<Integer, PassiveSkillGrid> passiveSkill = skillStrategy.getPassiveSkill();
        int passiveSkillNum = 0;
        for(Map.Entry<Integer, PassiveSkillGrid> entry : passiveSkill.entrySet()) {
            PassiveSkillGrid passiveSkillGrid = entry.getValue();
            if(passiveSkillGrid.isUnlock() && passiveSkillGrid.getSkillId() != 0) {
                passiveSkillNum++;
            }
        }
        List<Integer> fightPowerList = (List<Integer>) data.get("passivezl");
        heroFightPower = heroFightPower + fightPowerList.get(passiveSkillNum);
        return heroFightPower;
    }

    /**
     * 计算结晶战斗力
     * @return
     */
    private int calculateCrystalFightPower() {
        int heroFightPower = 0;
        Map<Integer, List<Integer>> crystalKV = hero.getCrystalKV();
        List<EvolutionCfgBean> cfgBeans = new ArrayList<>();
        List<EvolutionCfgBean> evolutionCfgBeans = GameDataManager.getEvolutionCfgBeans();
        for(EvolutionCfgBean evolutionCfgBean : evolutionCfgBeans) {
            if(evolutionCfgBean.getHeroId() == hero.getCid()) {
                cfgBeans.add(evolutionCfgBean);
            }
        }
        for(Map.Entry<Integer, List<Integer>> entry : crystalKV.entrySet()) {
            List<Integer> crystalList = entry.getValue();
            for(Integer crystal : crystalList) {
                EvolutionCfgBean evolutionCfgBean = null;
                for(EvolutionCfgBean cfgBean : cfgBeans) {
                    if(cfgBean.getPartition() == entry.getKey() && cfgBean.getCell() == crystal) {
                        evolutionCfgBean = cfgBean;
                        break;
                    }
                }
                heroFightPower = heroFightPower + evolutionCfgBean.getCombatPower();
            }
        }
        return heroFightPower;
    }
    
    public List<S2CShareMsg.AttributeInfo> getAttributeInfoBuilder() {
        List<S2CShareMsg.AttributeInfo> attributeInfo = new ArrayList<>();
        Property property = getProperty();
        for (EPropertyType type : EPropertyType.values()) {
            S2CShareMsg.AttributeInfo.Builder attributeInfoBuilder =
                    S2CShareMsg.AttributeInfo.newBuilder();
            attributeInfoBuilder.setType(type.value());
            attributeInfoBuilder.setVal(property.getProperty(type));
            attributeInfo.add(attributeInfoBuilder.build());
        }
        return attributeInfo;
    }
}
