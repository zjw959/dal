package logic.hero;

import java.util.List;
import java.util.Map;

import org.game.protobuf.c2s.C2SHeroMsg;
import org.game.protobuf.c2s.C2SHeroMsg.HeroCompose;
import org.game.protobuf.c2s.C2SHeroMsg.HeroUpgrade;
import org.game.protobuf.c2s.C2SHeroMsg.ReqActiveCrystal;
import org.game.protobuf.c2s.C2SHeroMsg.ReqAwakeAngel;
import org.game.protobuf.c2s.C2SHeroMsg.ReqChangeHeroSkin;
import org.game.protobuf.c2s.C2SHeroMsg.ReqEquipPassiveSkill;
import org.game.protobuf.c2s.C2SHeroMsg.ReqModifyStrategyName;
import org.game.protobuf.c2s.C2SHeroMsg.ReqUpQuality;
import org.game.protobuf.c2s.C2SHeroMsg.ReqUpgradeSkill;
import org.game.protobuf.c2s.C2SHeroMsg.ReqUseSkillStrategy;
import org.game.protobuf.c2s.C2SPlayerMsg;

import data.bean.HeroCfgBean;
import logic.character.bean.Player;
import logic.constant.EReason;
import logic.hero.bean.Hero;
import script.IScript;

public abstract class IHeroScript implements IScript {

    protected abstract void reqComposeHero(Player player, Map<Integer, Hero> heroKV,
            HeroCompose msg);

    protected abstract void reqGetHeroList(Player player, Map<Integer, Hero> heroKV);

    protected abstract void reqUpQuality(Player player, Map<Integer, Hero> heroKV,
            ReqUpQuality msg);

    protected abstract void reqUpgradeHero(Player player, Map<Integer, Hero> heroKV,
            HeroUpgrade msg);

    protected abstract void reqUpgradeSkill(Player player, Map<Integer, Hero> heroKV,
            ReqUpgradeSkill msg);

    protected abstract void reqAwakeAngel(Player player, Map<Integer, Hero> heroKV,
            ReqAwakeAngel msg);

    protected abstract void reqModifyStrategyName(Player player, Map<Integer, Hero> heroKV,
            ReqModifyStrategyName msg);

    protected abstract void reqChangeHeroSkin(Player player, Map<Integer, Hero> heroKV,
            ReqChangeHeroSkin msg);

    protected abstract void reqUseSkillStrategy(Player player, Map<Integer, Hero> heroKV,
            ReqUseSkillStrategy msg);

    protected abstract void reqEquipPassiveSkill(Player player, Map<Integer, Hero> heroKV,
            ReqEquipPassiveSkill msg);

    protected abstract void reqActiveCrystal(Player player, Map<Integer, Hero> heroKV,
            ReqActiveCrystal msg);

    protected abstract void sendHeroInfoSingle(Player player, Map<Integer, Hero> heroKV,
            int heroId);

    protected abstract void addHero(Player player, Map<Integer, Hero> heroKV,
            HeroCfgBean heroCfgBean, EReason reason);

    protected abstract void reqChangeHelpFightHero(Player player,
            C2SPlayerMsg.ChangeHelpFightHero msg);

    protected abstract void reqSwitchFormation(Player player, C2SPlayerMsg.ReqSwitchFormation msg);

    protected abstract void reqOperateFormation(Player player, Map<Integer, Hero> heroKV,
            C2SPlayerMsg.OperateFormation msg);

    public abstract boolean addExp(Player player, long addExp, Hero hero);

    public abstract void reqResetSkill(Player player, C2SHeroMsg.ReqResetSkill msg);

    public abstract List<Integer> getHeroEquipSkill(Hero hero);
}
