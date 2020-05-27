package logic.hero.skill;

import org.game.protobuf.s2c.S2CHeroMsg.AngeSkillInfo;

import data.GameDataManager;
import data.bean.AngelSkillTreeCfgBean;
import logic.constant.ConstDefine;
import utils.ExceptionEx;

/**
 * 英雄技能
 * 
 * @author ouyangcheng
 *
 */
public class HeroSkill {
    /** 技能id */
    private int id;
    /** 技能类型 */
    private transient int skillType;
    /** 技能位置 */
    private transient int pos;
    /** 技能等级 */
    private transient int level;

    public void createHeroSkill(AngelSkillTreeCfgBean cfgBean) {
        this.id = cfgBean.getId();
        this.skillType = cfgBean.getSkillType();
        this.pos = cfgBean.getPosition();
        this.level = cfgBean.getLvl();
    }

    public void init() {
        try {
            AngelSkillTreeCfgBean cfgBean = GameDataManager.getAngelSkillTreeCfgBean(id);
            this.skillType = cfgBean.getSkillType();
            this.pos = cfgBean.getPosition();
            this.level = cfgBean.getLvl();
        } catch (Exception e) {
            throw new RuntimeException(ConstDefine.LOG_ERROR_CONFIG_PREFIX + "技能表" + id + "," + ExceptionEx.e2s(e));
        }
        
    }

    public int getId() {
        return id;
    }

    public int getSkillType() {
        return skillType;
    }

    public int getPos() {
        return pos;
    }

    public int getLevel() {
        return level;
    }

    public void upLevel(AngelSkillTreeCfgBean cfgBean) {
        this.id = cfgBean.getId();
        this.level = cfgBean.getLvl();
    }

    public AngeSkillInfo buildAngelSkillInfo() {
        AngeSkillInfo.Builder angeSkillInfoBuilder = AngeSkillInfo.newBuilder();
        angeSkillInfoBuilder.setType(skillType);
        angeSkillInfoBuilder.setPos(pos);
        angeSkillInfoBuilder.setLvl(level);
        return angeSkillInfoBuilder.build();
    }

}
