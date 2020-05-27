package logic.hero.skill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CHeroMsg.AngeSkillInfo;

/**
 * 技能树
 * 
 * @author ouyangcheng
 *
 */
public class HeroSkillTree {
    public Map<Integer, HeroSkill> skillTree = new HashMap<>();

    public void init() {
        for (Map.Entry<Integer, HeroSkill> entry : skillTree.entrySet()) {
            HeroSkill heroSkill = entry.getValue();
            heroSkill.init();
        }
    }

    public HeroSkill getHeroSkill(int pos) {
        HeroSkill heroSkill = skillTree.get(pos);
        return heroSkill;
    }

    public void putHeroSkill(int pos, HeroSkill heroSkill) {
        skillTree.put(pos, heroSkill);
    }

    public void removeSkill(int pos) {
        skillTree.remove(pos);
    }

    public int getSkillTreeSize() {
        return skillTree.size();
    }

    public List<AngeSkillInfo> buildAngeSkillInfoList() {
        List<AngeSkillInfo> angeSkillInfoList = new ArrayList<>();
        for (Map.Entry<Integer, HeroSkill> entry : skillTree.entrySet()) {
            HeroSkill heroSkill = entry.getValue();
            angeSkillInfoList.add(heroSkill.buildAngelSkillInfo());
        }
        return angeSkillInfoList;
    }
}
