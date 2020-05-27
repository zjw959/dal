package logic.dungeon.bean;

import java.util.Collections;
import java.util.List;

import logic.character.bean.Player;
import logic.constant.GameErrorCode;
import logic.hero.bean.Hero;
import logic.hero.bean.HeroEquip;
import logic.hero.skill.HeroSkill;
import logic.hero.skill.HeroSkillTree;
import logic.hero.skill.SkillStrategy;
import logic.item.bean.EquipItem;
import logic.item.bean.SkinItem;
import logic.support.MessageUtils;
import data.GameDataManager;
import data.bean.AngelSkillTreeCfgBean;
import data.bean.EquipmentCfgBean;
import data.bean.HeroCfgBean;

/**
 * 配置的英雄数据
 * <p>
 * 数据来源：HeroLimitforDungeon
 * 
 * @author Alan
 *
 */
public class ConfigHeroVO extends Hero {
    int cfgId;
    int skinCid;

    @SuppressWarnings("unchecked")
    public ConfigHeroVO(int cfgId, int heroCid, int lvl, int quality, int stageLvl,
            List<List<Integer>> equips, int skinCid, int[] angelUp) {
        super();
        this.cfgId = cfgId;
        this.skinCid = skinCid;
        HeroCfgBean heroCfg = GameDataManager.getHeroCfgBean(heroCid);
        if (heroCfg == null)
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "HeroCfgBean [",
                    String.valueOf(heroCid), "] does not exist");
        // 以下注意this指针逃逸的问题
        createHero(heroCfg, null);
        init(null);
        // 处理质点装备
        if (equips != null) {
            for (int i = 0; i < equips.size(); i++) {
                List<Integer> equipConfig = equips.get(i);
                EquipmentCfgBean cfg = GameDataManager.getEquipmentCfgBean(equipConfig.get(0));
                if(cfg == null)
                    MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                            "EquipmentCfgBean [", String.valueOf(equipConfig.get(0)), "] does not exist");
                int level = equipConfig.get(1);
                // 组装临时装备对象
                HeroEquip he = new HeroEquip();
                // 组装临时道具对象
                EquipItem ei = new EquipItem();
                // 客户端位置,从1开始
                ei.setPosition(i + 1);
                ei.setLevel(level);
                ei.setNum(1);
                ei.setTemplateId(cfg.getId());
                ei.setHeroId(heroCid);
                ei.setId(cfg.getId());
                // 为了适配未判定空的逻辑
                ei.setSpecialAttrList(Collections.EMPTY_LIST);
                he.setEquipId(cfg.getId());
                he.setEquipItem(ei);
                putEquip(ei.getPosition(), he);
            }
            equipAndTakeOff();
        }
        // 天使
        if (angelUp != null) {
            for (Integer treeId : angelUp) {
                AngelSkillTreeCfgBean angelSkillTreeCfgBean =
                        GameDataManager.getAngelSkillTreeCfgBean(treeId);
                if(angelSkillTreeCfgBean == null)
                    MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                            "AngelSkillTreeCfgBean [", String.valueOf(treeId), "] does not exist");
                SkillStrategy skillStrategy = getAngel().getUseSkillStrategy();
                skillStrategy.useSkillPiont(angelSkillTreeCfgBean.getNeedSkillPiont());
                int type = angelSkillTreeCfgBean.getSkillType();
                int pos = angelSkillTreeCfgBean.getPosition();
                HeroSkillTree heroSkillTree = skillStrategy.getHeroSkillTree(type);
                HeroSkill heroSkill = null;
                if (heroSkillTree != null) {
                    heroSkill = heroSkillTree.getHeroSkill(pos);
                    if (heroSkill != null) {
                        heroSkill.upLevel(angelSkillTreeCfgBean);
                    } else {
                        heroSkill = new HeroSkill();
                        heroSkill.createHeroSkill(angelSkillTreeCfgBean);
                        heroSkillTree.putHeroSkill(pos, heroSkill);
                    }
                } else {
                    heroSkill = new HeroSkill();
                    heroSkill.createHeroSkill(angelSkillTreeCfgBean);
                    heroSkillTree = new HeroSkillTree();
                    heroSkillTree.putHeroSkill(pos, heroSkill);
                    skillStrategy.putHeroSkillTree(type, heroSkillTree);
                }
            }
        }
        // 处理级数
        while (lvl > getLevel())
            upLevel();
        // 处理品质
        while (quality > getQuality())
            upQuality();
    }

    @Override
    protected EquipItem getHeroEquipItem(HeroEquip heroEquip) {
        // 直接获取临时对象
        return heroEquip.getEquipItem();
    }

    @Override
    protected SkinItem getHeroSkinItem(HeroCfgBean heroCfgBean) {
        SkinItem skinItem = new SkinItem();
        skinItem.setId(skinCid);
        skinItem.setTemplateId(skinCid);
        skinItem.setHeroId(getCid());
        skinItem.setNum(1);
        return skinItem;
    }

    @Override
    protected void initHeroEquip(Player player, HeroEquip heroEquip) {
        // 临时数据对象已经初始化,这里不需要调用
    }

    @Override
    protected void initHeroSkin(Player player) {
        // 设置临时数据对象
        getSkin().setSkinItem(getHeroSkinItem(null));
    }

    @Override
    protected boolean isHelpFightHero() {
        // 配置英雄不能作为助战
        return false;
    }
    
    @Override
    public void sendPropertyChange() {
        // 不需要推送改变,因为不会改变
    }

    public int getCfgId() {
        return cfgId;
    }

    public void setCfgId(int cfgId) {
        this.cfgId = cfgId;
    }
}
