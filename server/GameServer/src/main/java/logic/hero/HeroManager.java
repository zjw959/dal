package logic.hero;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.game.protobuf.c2s.C2SHeroMsg;
import org.game.protobuf.c2s.C2SHeroMsg.ReqActiveCrystal;
import org.game.protobuf.c2s.C2SHeroMsg.ReqEquipPassiveSkill;
import org.game.protobuf.c2s.C2SHeroMsg.ReqModifyStrategyName;
import org.game.protobuf.c2s.C2SPlayerMsg;
import org.game.protobuf.s2c.S2CPlayerMsg.FormationInfo;
import org.game.protobuf.s2c.S2CPlayerMsg.FormationInfoList;
import org.game.protobuf.s2c.S2CShareMsg;

import com.google.gson.JsonElement;

import data.bean.HeroCfgBean;
import logic.basecore.ICreatePlayerInitialize;
import logic.basecore.IRoleJsonConverter;
import logic.basecore.IView;
import logic.basecore.PlayerBaseFunctionManager;
import logic.constant.EReason;
import logic.hero.bean.Formation;
import logic.hero.bean.Hero;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;
import utils.GsonUtils;

public class HeroManager extends PlayerBaseFunctionManager
        implements IRoleJsonConverter, ICreatePlayerInitialize, IView {

    private Map<Integer, Hero> heroKV = new HashMap<>();
    /** 助战id */
    private int helpFightHeroCid;
    /** 阵型 key:阵型类型;value:阵型 */
    private Map<Integer, Formation> formations = new HashMap<>();
    /** 当前阵型 */
    private int formationType;

    @Override
    public void createPlayerInitialize() {
        for (Hero hero : heroKV.values()) {
            hero.init(player);
        }
    }

    /**
     * 获取英雄列表
     */
    public void reqGetHeroList() {
        LogicScriptsUtils.getIHeroScript().reqGetHeroList(player, heroKV);
    }

    /**
     * 发送单个英雄信息
     */
    public void sendHeroInfoSingle(int heroId) {
        LogicScriptsUtils.getIHeroScript().sendHeroInfoSingle(player, heroKV, heroId);
    }

    /**
     * 请求合成英雄
     * 
     * @param msg
     */
    public void reqComposeHero(C2SHeroMsg.HeroCompose msg) {
        LogicScriptsUtils.getIHeroScript().reqComposeHero(player, heroKV, msg);
    }

    /**
     * 请求升级英雄
     * 
     * @param msg
     */
    public void reqUpgradeHero(C2SHeroMsg.HeroUpgrade msg) {
        LogicScriptsUtils.getIHeroScript().reqUpgradeHero(player, heroKV, msg);
    }

    /**
     * 请求进阶英雄
     * 
     * @param msg
     */
    public void reqUpQuality(C2SHeroMsg.ReqUpQuality msg) {
        LogicScriptsUtils.getIHeroScript().reqUpQuality(player, heroKV, msg);
    }

    /**
     * 请求更换英雄皮肤
     * 
     * @param msg
     */
    public void reqChangeHeroSkin(C2SHeroMsg.ReqChangeHeroSkin msg) {
        LogicScriptsUtils.getIHeroScript().reqChangeHeroSkin(player, heroKV, msg);
    }

    /**
     * 请求觉醒天使
     * 
     * @param msg
     */
    public void reqAwakeAngel(C2SHeroMsg.ReqAwakeAngel msg) {
        LogicScriptsUtils.getIHeroScript().reqAwakeAngel(player, heroKV, msg);
    }

    /**
     * 请求升级技能
     * 
     * @param msg
     */
    public void reqUpgradeSkill(C2SHeroMsg.ReqUpgradeSkill msg) {
        LogicScriptsUtils.getIHeroScript().reqUpgradeSkill(player, heroKV, msg);
    }

    /**
     * 请求修改天使页的名字
     * 
     * @param msg
     */
    public void reqModifyStrategyName(ReqModifyStrategyName msg) {
        LogicScriptsUtils.getIHeroScript().reqModifyStrategyName(player, heroKV, msg);
    }

    /**
     * 请求使用技能
     * 
     * @param msg
     */
    public void reqUseSkillStrategy(C2SHeroMsg.ReqUseSkillStrategy msg) {
        LogicScriptsUtils.getIHeroScript().reqUseSkillStrategy(player, heroKV, msg);
    }

    /**
     * 请求装备/卸下被动技能
     * 
     * @param msg
     */
    public void reqEquipPassiveSkill(ReqEquipPassiveSkill msg) {
        LogicScriptsUtils.getIHeroScript().reqEquipPassiveSkill(player, heroKV, msg);
    }

    public void reqActiveCrystal(ReqActiveCrystal msg) {
        LogicScriptsUtils.getIHeroScript().reqActiveCrystal(player, heroKV, msg);
    }

    public List<Integer> getHeroEquipSkill(Hero hero) {
        return LogicScriptsUtils.getIHeroScript().getHeroEquipSkill(hero);
    }

    /**
     * 添加一个英雄
     */
    public void addHero(HeroCfgBean heroCfgBean, EReason reason) {
        LogicScriptsUtils.getIHeroScript().addHero(player, heroKV, heroCfgBean, reason);
    }

    public boolean isExistHero(int id) {
        Hero hero = heroKV.get(id);
        if (hero != null) {
            return true;
        }
        return false;
    }

    /**
     * 生成技能id
     * 
     * @param heroId
     * @param type
     * @param pos
     * @return
     */
    public static int generateSkillId(int indexId, int type, int pos) {
        return indexId * 100000 + type * 1000 + pos * 10 + 1;
    }

    public Hero getHero(int id) {
        return heroKV.get(id);
    }

    /**
     * 根据CID & quality 取得持有英雄数量
     * 
     */
    public int getHeroCountByHeroIdAndQuality(int heroCid, int quality) {
        Hero hero = null;
        int count = 0;
        for (Entry<Integer, Hero> e : heroKV.entrySet()) {
            hero = e.getValue();
            if (heroCid != 0 && heroCid != hero.getCid()) {
                continue;
            }

            if (quality != 0 && quality != hero.getQuality()) {
                continue;
            }
            count++;
        }
        return count;
    }

    /**
     * level 取得持有英雄数量
     * 
     */
    public int getHeroCountByLevel(int level) {
        Hero hero = null;
        int count = 0;
        for (Entry<Integer, Hero> e : heroKV.entrySet()) {
            hero = e.getValue();

            if (hero.getLevel() < level) {
                continue;
            }
            count++;
        }
        return count;
    }

    /**
     * 根据CID 取得持有英雄最大等级
     */
    public int getMaxHeroLvl(int heroCid) {
        Hero hero = null;
        int maxLvl = 0;
        for (Entry<Integer, Hero> e : heroKV.entrySet()) {
            hero = e.getValue();
            if (heroCid != 0 && heroCid != hero.getCid()) {
                continue;
            }
            maxLvl = Math.max(maxLvl, hero.getLevel());
        }
        return maxLvl;
    }


    public Collection<Hero> getHeroList() {
        return heroKV.values();
    }

    public List<Hero> getAllHero() {
        return new ArrayList<>(heroKV.values());
    }

    public int getHelpFightHeroCid() {
        return helpFightHeroCid;
    }

    public void setHelpFightHeroCid(int helpFightHeroCid) {
        this.helpFightHeroCid = helpFightHeroCid;
    }

    public Map<Integer, Formation> getFormations() {
        return formations;
    }

    public int getFormationType() {
        return formationType;
    }

    public void setFormationType(int formationType) {
        this.formationType = formationType;
    }

    public void reqChangeHelpFightHero(C2SPlayerMsg.ChangeHelpFightHero msg) {
        LogicScriptsUtils.getIHeroScript().reqChangeHelpFightHero(player, msg);
    }

    public int getHelpHeroFightPower() {
        Hero hero = heroKV.get(helpFightHeroCid);
        if (hero == null)
            return 0;
        return hero.getFightPower();
    }

    @Override
    public IView toView() {
        // 根据需要进行返回. 需要返回所有精灵信息
        return this;
    }

    @Override
    public JsonElement toViewJson(String fullJsonData) {
        HeroManager baseFunMan = (HeroManager) GsonUtils.fromJson(fullJsonData, getClass());
        JsonElement _json = GsonUtils.toJsonTree(baseFunMan.toView());
        return _json;
    }

    public void reqGetFormations() {
        FormationInfoList.Builder formationInfoListBuilder = FormationInfoList.newBuilder();
        for (Map.Entry<Integer, Formation> entry : formations.entrySet()) {
            Formation formation = entry.getValue();
            FormationInfo.Builder formationInfoBuilder =
                    formation.buildFormationInfo(S2CShareMsg.ChangeType.DEFAULT);
            formationInfoListBuilder.addFormations(formationInfoBuilder);
        }
        MessageUtils.send(player, formationInfoListBuilder);
    }

    public void reqSwitchFormation(C2SPlayerMsg.ReqSwitchFormation msg) {
        LogicScriptsUtils.getIHeroScript().reqSwitchFormation(player, msg);
    }

    public void reqOperateFormation(C2SPlayerMsg.OperateFormation msg) {
        LogicScriptsUtils.getIHeroScript().reqOperateFormation(player, heroKV, msg);
    }

    public void reqResetSkill(C2SHeroMsg.ReqResetSkill msg) {
        LogicScriptsUtils.getIHeroScript().reqResetSkill(player, msg);
    }

}
