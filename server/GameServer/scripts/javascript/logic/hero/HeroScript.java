package javascript.logic.hero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import logic.bag.BagManager;
import logic.character.PlayerViewService;
import logic.character.bean.Player;
import logic.constant.DiscreteDataID;
import logic.constant.EEventType;
import logic.constant.EPropertyModule;
import logic.constant.EPropertyType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.ESkillType;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.FormationConstant;
import logic.constant.GameErrorCode;
import logic.hero.HeroManager;
import logic.hero.HeroProperty;
import logic.hero.IHeroScript;
import logic.hero.bean.Formation;
import logic.hero.bean.Hero;
import logic.hero.bean.HeroEquip;
import logic.hero.skill.Angel;
import logic.hero.skill.HeroSkill;
import logic.hero.skill.HeroSkillTree;
import logic.hero.skill.PassiveSkillGrid;
import logic.hero.skill.SkillStrategy;
import logic.item.bean.EquipItem;
import logic.item.bean.Item;
import logic.item.bean.SkinItem;
import logic.support.LogBeanFactory;
import logic.support.MessageUtils;
import thread.log.LogProcessor;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SHeroMsg.HeroCompose;
import org.game.protobuf.c2s.C2SHeroMsg.HeroExpItem;
import org.game.protobuf.c2s.C2SHeroMsg.HeroUpgrade;
import org.game.protobuf.c2s.C2SHeroMsg.ReqActiveCrystal;
import org.game.protobuf.c2s.C2SHeroMsg.ReqAwakeAngel;
import org.game.protobuf.c2s.C2SHeroMsg.ReqChangeHeroSkin;
import org.game.protobuf.c2s.C2SHeroMsg.ReqEquipPassiveSkill;
import org.game.protobuf.c2s.C2SHeroMsg.ReqModifyStrategyName;
import org.game.protobuf.c2s.C2SHeroMsg.ReqResetSkill;
import org.game.protobuf.c2s.C2SHeroMsg.ReqUpQuality;
import org.game.protobuf.c2s.C2SHeroMsg.ReqUpgradeSkill;
import org.game.protobuf.c2s.C2SHeroMsg.ReqUseSkillStrategy;
import org.game.protobuf.c2s.C2SPlayerMsg.ChangeHelpFightHero;
import org.game.protobuf.c2s.C2SPlayerMsg.OperateFormation;
import org.game.protobuf.c2s.C2SPlayerMsg.ReqSwitchFormation;
import org.game.protobuf.s2c.S2CHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg.AngeSkillInfo;
import org.game.protobuf.s2c.S2CHeroMsg.HeroExpInfo;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfoList;
import org.game.protobuf.s2c.S2CHeroMsg.HeroUpgradeResult;
import org.game.protobuf.s2c.S2CHeroMsg.PassiveSkillInfo;
import org.game.protobuf.s2c.S2CHeroMsg.ResActiveCrystal;
import org.game.protobuf.s2c.S2CHeroMsg.ResAwakeAngel;
import org.game.protobuf.s2c.S2CHeroMsg.ResEquipPassiveSkill;
import org.game.protobuf.s2c.S2CHeroMsg.ResModifyStrategyName;
import org.game.protobuf.s2c.S2CHeroMsg.ResResetSkill;
import org.game.protobuf.s2c.S2CHeroMsg.ResUpgradeSkill;
import org.game.protobuf.s2c.S2CHeroMsg.ResUseSkillStrategy;
import org.game.protobuf.s2c.S2CPlayerMsg.FormationInfoList;
import org.game.protobuf.s2c.S2CPlayerMsg.PlayerInfo;
import org.game.protobuf.s2c.S2CShareMsg;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import utils.ExceptionEx;
import utils.SensitiveWordsUtil;

import com.google.common.collect.Maps;

import data.GameDataManager;
import data.bean.AngelPassiveSkillGroovesCfgBean;
import data.bean.AngelSkillTreeCfgBean;
import data.bean.DiscreteDataCfgBean;
import data.bean.EquipmentCfgBean;
import data.bean.EquipmentCombinationCfgBean;
import data.bean.EquipmentSuitCfgBean;
import data.bean.EvolutionCfgBean;
import data.bean.HeroCfgBean;
import data.bean.HeroProgressCfgBean;
import data.bean.ItemCfgBean;
import data.bean.LevelUpCfgBean;

public class HeroScript extends IHeroScript {
    private static final Logger LOGGER = Logger.getLogger(HeroScript.class);
    
    @Override
    public int getScriptId() {
        return EScriptIdDefine.HERO_SCRIPT.Value();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void reqComposeHero(Player player, Map<Integer, Hero> heroKV, HeroCompose msg) {
        int cid = msg.getHeroCid();
        Hero hero = heroKV.get(cid);
        if (hero != null) {
            MessageUtils.throwCondtionError(GameErrorCode.ALREADY_HAVE_HERO, "已有该英雄:" + cid);
            return;
        }
        HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(cid);
        if (heroCfgBean == null) {
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "英雄合成，cid错误:" + cid);
            return;
        }
        Map<Integer, Integer> costItemMap = heroCfgBean.getCompose();
        Map<Integer, Integer> heroItemMap = new HashMap<>();
        BagManager bagManager = player.getBagManager();
        boolean isEnough = bagManager.removeItemsByTemplateIdWithCheck(costItemMap, true,
                EReason.HERO_COMPOUND);
        if (!isEnough) {
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "合成英雄碎片不足");
            return;
        }
        heroItemMap.put(heroCfgBean.getId(), 1);
        bagManager.addItems(heroItemMap, true, EReason.HERO_COMPOUND);
        S2CHeroMsg.HeroCompose.Builder heroComposeBuilder = S2CHeroMsg.HeroCompose.newBuilder();
        heroComposeBuilder.setSuccess(true);
        MessageUtils.send(player, heroComposeBuilder);
    }

    @Override
    protected void reqGetHeroList(Player player, Map<Integer, Hero> heroKV) {
        for (Hero hero : heroKV.values()) {
            hero.init(player);
        }
        
        HeroInfoList.Builder heroInfoListBuilder = HeroInfoList.newBuilder();
        for (Map.Entry<Integer, Hero> entry : heroKV.entrySet()) {
            Hero hero = entry.getValue();
            heroInfoListBuilder.addHeros(hero.buildHeroInfo(S2CShareMsg.ChangeType.DEFAULT));
        }
        MessageUtils.send(player, heroInfoListBuilder);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void reqUpQuality(Player player, Map<Integer, Hero> heroKV, ReqUpQuality msg) {
        String heroIdStr = msg.getHeroId();
        int heroId = Integer.valueOf(heroIdStr);
        Hero hero = heroKV.get(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO, "英雄[" + heroId + "]未找到");
            return;
        }
        int quality = hero.getQuality();
        HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(hero.getCid());
        int targetQuality = heroCfgBean.getAttribute2() * 100 + quality + 1;
        HeroProgressCfgBean heroProgressCfgBean =
                GameDataManager.getHeroProgressCfgBean(targetQuality);
        if (heroProgressCfgBean == null) {
            MessageUtils.throwCondtionError(GameErrorCode.MAX_QUALITY_LVL, "已达最大进阶等级");
            return;
        }
        Map<Integer, Integer> consumeItemMap = heroProgressCfgBean.getConsume();
        BagManager bagManager = player.getBagManager();
        boolean isEnough = bagManager.removeItemsByTemplateIdWithCheck(consumeItemMap, true,
                EReason.HERO_QUALITY_UP);
        if (!isEnough) {
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH,
                    "请求进阶" + targetQuality + ",英雄" + heroId + ",消耗道具不足");
            return;
        }
        hero.upQuality();
        MessageUtils.send(player, hero.buildHeroInfo(ChangeType.UPDATE));
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createHeroLog(player, heroId, 0, 0, 0, 0, 1, hero.getQuality(), EReason.HERO_QUALITY_UP.value(), null));
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        MessageUtils.returnEmptyBody();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    protected void reqUpgradeHero(Player player, Map<Integer, Hero> heroKV, HeroUpgrade msg) {
        String heroIdStr = msg.getHeroId();
        int heroId = Integer.valueOf(heroIdStr);
        Hero hero = heroKV.get(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO, "英雄[" + heroId + "]未找到");
            return;
        }

        DiscreteDataCfgBean discreteDataCfgBean =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.MAX_LVL_CONFIG);
        Map<String, Integer> data = discreteDataCfgBean.getData();
        int maxLvl = data.get("hmaxlvl");
        int level = hero.getLevel();
        LevelUpCfgBean nextLevelCfgBean = GameDataManager.getLevelUpCfgBean(level + 1);
        int limitLevel = player.getLevel();

        limitLevel = Math.min(maxLvl, limitLevel);

        if (nextLevelCfgBean == null || level >= limitLevel) {
            // 无法升英雄等级
            MessageUtils.throwCondtionError(GameErrorCode.MAX_ADVANCE_LVL, "英雄[" + heroId
                    + "]当前已达到最大等级");
            return;
        }

        List<HeroExpItem> items = msg.getItemsList();
        Map<Integer, Integer> consumeItemMap = Maps.newHashMap();
        for (HeroExpItem heroExpItem : items) {
            consumeItemMap.put(heroExpItem.getItemId(), heroExpItem.getNum());
        }
        if (consumeItemMap.isEmpty()) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "请求升级英雄，消耗道具为空");
            return;
        }
        BagManager bagManager = player.getBagManager();
        boolean isEnough = bagManager.removeItemsByTemplateIdWithCheck(consumeItemMap, true,
                EReason.HERO_LEVEL_UP);
        if (!isEnough) {
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "请求升级英雄，消耗道具不足");
            return;
        }

        // 指定英雄经验，特殊处理，配置表中没有指定对应英雄
        Map<Integer, Integer> awardItemMap = new HashMap<>();
        int totalExp = 0;
        for (Map.Entry<Integer, Integer> entry : consumeItemMap.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            ItemCfgBean itemCfgBean = GameDataManager.getItemCfgBean(key);
            Map profitMap = itemCfgBean.getUseProfit();
            profitMap = (Map) profitMap.get("fix");
            List profitList = (List) profitMap.get("items");
            for (int i = 0; i < profitList.size(); i++) {
                profitMap = (Map) profitList.get(i);
                int id = (int) profitMap.get("id");
                int num = (int) profitMap.get("num");
                totalExp = totalExp + num;

                if (awardItemMap.containsKey(id)) {
                    awardItemMap.put(id, awardItemMap.get(id) + num);
                } else {
                    awardItemMap.put(id, num);
                }
            }
            totalExp = totalExp * value;
        }
        
        addExp(player, totalExp, hero);

        HeroUpgradeResult.Builder heroUpgradeResultBuilder = HeroUpgradeResult.newBuilder();
        RewardsMsg.Builder rewardsMsgBuilder = RewardsMsg.newBuilder();
        for (Map.Entry<Integer, Integer> entry : awardItemMap.entrySet()) {
            rewardsMsgBuilder.setId(entry.getKey());
            rewardsMsgBuilder.setNum(entry.getKey());
            heroUpgradeResultBuilder.addRewards(rewardsMsgBuilder);
        }
        MessageUtils.send(player, heroUpgradeResultBuilder);
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createHeroLog(player, heroId, totalExp, hero.getExp(), hero.getLevel() - level, hero.getLevel(), 0, 0, EReason.HERO_LEVEL_UP.value(), null));
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        
        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.HERO_UP_LVL_COUNT);
        in.put(EventConditionKey.HERO_CID, hero.getCid());
        player._fireEvent(in, EEventType.HERO_CHANGE.value());
       
        in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.ADVANCED_LVL_COUNT);
        in.put(EventConditionKey.OLD_LEVEL, level);
        in.put(EventConditionKey.NOW_LEVEL, hero.getLevel());
        in.put(EventConditionKey.ROLE_CID, hero.getCid());
        player._fireEvent(in, EEventType.HERO_CHANGE.value());
    }

    @Override
    protected void reqChangeHeroSkin(Player player, Map<Integer, Hero> heroKV,
            ReqChangeHeroSkin msg) {
        String heroIdStr = msg.getHeroId();
        String skinIdStr = msg.getSkinId();
        int heroId = Integer.parseInt(heroIdStr);
        long skinId = Long.parseLong(skinIdStr);
        Hero hero = heroKV.get(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO, "英雄[" + heroId + "]未找到");
            return;
        }
        BagManager bagManager = player.getBagManager();
        Item item = bagManager.getItemOrigin(skinId);
        if (item == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "皮肤[" + skinId + "]未找到");
            return;
        }
        SkinItem skinItem = (SkinItem) item;
        SkinItem beforeSkin = hero.getSkin().getSkinItem();
        if (skinItem.getHeroId() != 0 || skinItem.getHeroId() == beforeSkin.getHeroId()) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "皮肤[" + skinId + "]已使用,皮肤道具id:" + skinItem.getTemplateId() + ",所属英雄:"
                            + skinItem.getHeroId() + "要使用的英雄:" + heroIdStr);
            return;
        }

        if (!hero.checkSkinId(skinItem.getTemplateId())) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "皮肤[" + skinItem.getTemplateId() + "]不属于[" + hero.getCid() + "]");
            return;
        }

        hero.changeSkin(beforeSkin, skinItem);
        MessageUtils.send(player, hero.buildHeroInfo(ChangeType.UPDATE));
        MessageUtils.returnEmptyBody();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean addExp(Player player, long addExp, Hero hero) {
        boolean levelUpFlag = false;
        DiscreteDataCfgBean discreteDataCfgBean =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.MAX_LVL_CONFIG);
        Map<String, Integer> data = discreteDataCfgBean.getData();
        int maxLvl = data.get("hmaxlvl");
        int level = hero.getLevel();
        int limitLevel = player.getLevel();
        LevelUpCfgBean levelCfgBean = GameDataManager.getLevelUpCfgBean(level);

        limitLevel = Math.min(maxLvl, limitLevel);
        if(level >= limitLevel) {
            hero.setExp(Math.min(addExp, levelCfgBean.getHeroExp()));
            return levelUpFlag;
        }   
        
        while (addExp > 0) {
            long needExp = levelCfgBean.getHeroExp() - hero.getExp();
            if (addExp >= needExp) {
                addExp = addExp - needExp;
                hero.upLevel();
                levelUpFlag = true;
                levelCfgBean = GameDataManager.getLevelUpCfgBean(hero.getLevel());
                LevelUpCfgBean nextLevelCfgBean =
                        GameDataManager.getLevelUpCfgBean(hero.getLevel() + 1);
                if (nextLevelCfgBean == null || hero.getLevel() >= limitLevel) {
                    hero.setExp(Math.min(addExp, levelCfgBean.getHeroExp()));
                    break;
                }
            } else {
                hero.setExp(addExp + hero.getExp());
                addExp = 0;
            }
        }
        if (level != hero.getLevel()) {
            // 触发事件
            Map<String, Object> in = Maps.newHashMap();
            in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.HERO_MAX_LVL);
            in.put(EventConditionKey.HERO_CID, hero.getCid());
            in.put(EventConditionKey.OLD_LEVEL, level);
            in.put(EventConditionKey.NOW_LEVEL, hero.getLevel());
            player._fireEvent(in, EEventType.HERO_CHANGE.value());
        }
        if (levelUpFlag) {
            // 英雄信息需要刷新
            MessageUtils.send(player, hero.buildHeroInfo(ChangeType.UPDATE));
        } else {
            HeroExpInfo.Builder builder = HeroExpInfo.newBuilder();
            builder.setCid(hero.getCid());
            builder.setId(Long.toString(hero.getCid()));
            builder.setExp(hero.getExp());
            MessageUtils.send(player, builder);
        }
        return levelUpFlag;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void reqAwakeAngel(Player player, Map<Integer, Hero> heroKV, ReqAwakeAngel msg) {
        String heroIdStr = msg.getHeroId();
        int heroId = Integer.parseInt(heroIdStr);
        Hero hero = heroKV.get(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO, "英雄[" + heroId + "]未找到");
            return;
        }
        HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(hero.getCid());
        if (heroCfgBean == null) {
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR,
                    "英雄配置表[" + hero.getCid() + "]未找到");
            return;
        }
        List<Map<Integer, Integer>> consumeItems = heroCfgBean.getAngelWakeCons();
        Angel angel = hero.getAngel();
        if (angel.getAwakeLevel() > consumeItems.size()) {
            MessageUtils.throwCondtionError(GameErrorCode.MAX_ANGEL_LVL,
                    "天使已达最大等级" + angel.getAwakeLevel());
            return;
        }
        BagManager bagManager = player.getBagManager();
        Map<Integer, Integer> items = consumeItems.get(angel.getAwakeLevel() - 1);
        boolean isEnough =
                bagManager.removeItemsByTemplateIdWithCheck(items, true, EReason.HERO_ANGEL_AWAKE);
        if (!isEnough) {
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH,
                    player.getPlayerId() + "请求觉醒天使" + heroId + ",消耗道具不足");
            return;
        }
        hero.awakeAngel();
        ResAwakeAngel.Builder awakeAngelBuilder = ResAwakeAngel.newBuilder();
        awakeAngelBuilder.setHeroId(heroIdStr);
        awakeAngelBuilder.setAngelLvl(angel.getAwakeLevel());
        MessageUtils.send(player, awakeAngelBuilder);
        
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createHeroAngelLog(player, heroId, hero.getLevel(), 1, hero.getAngel().getAwakeLevel(), 0, 0, EReason.HERO_ANGEL_AWAKE.value(), null));
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void reqUpgradeSkill(Player player, Map<Integer, Hero> heroKV, ReqUpgradeSkill msg) {
        String heroIdStr = msg.getHeroId();
        int type = msg.getType();
        int pos = msg.getPos();
        int operation = msg.getOperation();
        int heroId = Integer.parseInt(heroIdStr);
        Hero hero = heroKV.get(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO, "英雄[" + heroId + "]未找到");
            return;
        }
        
        HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(hero.getCid());
        int skillId = HeroManager.generateSkillId(heroCfgBean.getAngelID(), type, pos);
        AngelSkillTreeCfgBean angelSkillTreeCfgBean =
                GameDataManager.getAngelSkillTreeCfgBean(skillId);
        if (angelSkillTreeCfgBean == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "AngelSkillTree中没有找到精灵" + heroId + "类型" + type + "效果" + pos + "的技能");
            return;
        }
        // 闪避，觉醒，出场技能不能升级降级
        if (angelSkillTreeCfgBean.getSkillType() != ESkillType.ATTACK.value()
                && angelSkillTreeCfgBean.getSkillType() != ESkillType.SKILL_ONE.value()
                && angelSkillTreeCfgBean.getSkillType() != ESkillType.SKILL_TWO.value()
                && angelSkillTreeCfgBean.getSkillType() != ESkillType.KILLING_SKILL.value()) {
            MessageUtils.throwCondtionError(GameErrorCode.SKILL_TYPE_IS_NOT_MATCH, "技能类型不正确");
            return;
        }
        Angel angel = hero.getAngel();
        SkillStrategy skillStrategy = angel.getUseSkillStrategy();
        if (operation == 1) {
            // 升级
            HeroSkillTree heroSkillTree = skillStrategy.getHeroSkillTree(type);
            int targetId = 0;
            if (heroSkillTree != null) {
                HeroSkill heroSkill = heroSkillTree.getHeroSkill(pos);
                if (heroSkill == null) {
                    targetId = angelSkillTreeCfgBean.getId();
                } else {
                    targetId = heroSkill.getId() + 1;
                }
            } else {
                targetId = angelSkillTreeCfgBean.getId();
            }
            angelSkillTreeCfgBean = GameDataManager.getAngelSkillTreeCfgBean(targetId);
            if (angelSkillTreeCfgBean == null) {
                MessageUtils.throwCondtionError(GameErrorCode.MAX_SKILL_ANGEL_ADDBIT, "天使已达到满级");
                return;
            }
            if (hero.getLevel() < angelSkillTreeCfgBean.getNeedHeroLvl()) {
                // 英雄等级不足
                MessageUtils.throwCondtionError(GameErrorCode.ANGEL_SKILL_CONDITION_NOT_SATISFIED,
                        "请求天使加点，英雄等级不足");
                return;
            }
            if (angel.getAwakeLevel() < angelSkillTreeCfgBean.getNeedAngelLvl()) {
                // 天使星级不足
                MessageUtils.throwCondtionError(GameErrorCode.ANGEL_SKILL_CONDITION_NOT_SATISFIED,
                        "请求天使加点，天使星级不足");
                return;
            }

            boolean reach = true;
            List<List<Integer>> frontCondition = angelSkillTreeCfgBean.getFrontCondition();
            if (frontCondition != null) {
                Label: for (List<Integer> e : frontCondition) {
                    for (Integer conditionId : e) {
                        AngelSkillTreeCfgBean conditionCfg =
                                GameDataManager.getAngelSkillTreeCfgBean(conditionId);
                        HeroSkillTree heroSkillTreeTemp =
                                skillStrategy.getHeroSkillTree(conditionCfg.getSkillType());
                        if (heroSkillTreeTemp != null) {
                            HeroSkill heroSkillTemp =
                                    heroSkillTreeTemp.getHeroSkill(conditionCfg.getPosition());
                            if (heroSkillTemp == null) {
                                reach = false;
                            }
                        } else {
                            reach = false;
                        }
                        if (!reach) {
                            break Label;
                        }
                    }
                }
            }

            if (!reach) {
                MessageUtils.throwCondtionError(GameErrorCode.ANGEL_SKILL_CONDITION_NOT_SATISFIED,
                        "加点条件不满足");
                return;
            }

            int alreadyUseSkillPiont = skillStrategy.getAlreadyUseSkillPiont();
            int skillPiontAttr = hero.getPropertyValue(EPropertyType.SKIILL_POINT);
            int surplusSkillPoint = skillPiontAttr / 100 - alreadyUseSkillPiont;
            if (surplusSkillPoint < angelSkillTreeCfgBean.getNeedSkillPiont()) {
                MessageUtils.throwCondtionError(GameErrorCode.ANGEL_SKILL_POINT_LACKING, "技能点数不够");
                return;
            }

            skillStrategy.useSkillPiont(angelSkillTreeCfgBean.getNeedSkillPiont());

            heroSkillTree = skillStrategy.getHeroSkillTree(type);
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

            ResUpgradeSkill.Builder upgradeSkillBuilder = ResUpgradeSkill.newBuilder();
            upgradeSkillBuilder.setHeroId(heroIdStr);
            upgradeSkillBuilder.setAngeSkillInfo(heroSkill.buildAngelSkillInfo());
            upgradeSkillBuilder.setUseSkillPiont(skillStrategy.getAlreadyUseSkillPiont());
            MessageUtils.send(player, upgradeSkillBuilder);
            
            try {
                LogProcessor.getInstance().sendLog(LogBeanFactory.createHeroAngelLog(player, heroId, hero.getLevel(), 0, angel.getAwakeLevel(), skillStrategy.getId(), angelSkillTreeCfgBean.getId(), EReason.HERO_ANGEL_LEVEL_UP.value(), null));
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
        } else {
            // 降级
            HeroSkillTree heroSkillTree = skillStrategy.getHeroSkillTree(type);
            if (heroSkillTree == null) {
                MessageUtils.throwCondtionError(GameErrorCode.ANGEL_SKILL_CONDITION_NOT_SATISFIED,
                        "降级条件不满足");
                return;
            }
            HeroSkill heroSkill = heroSkillTree.getHeroSkill(pos);
            if (heroSkill == null) {
                MessageUtils.throwCondtionError(GameErrorCode.ANGEL_SKILL_CONDITION_NOT_SATISFIED,
                        "降级条件不满足");
                return;
            }
            int currentSkillId = (skillId / 10) * 10 + heroSkill.getLevel();
            angelSkillTreeCfgBean = GameDataManager.getAngelSkillTreeCfgBean(currentSkillId);
            // 需要配技能的后置条件
            List<Integer> postCondition = angelSkillTreeCfgBean.getPostPositionSkill();
            boolean reach = true;
            if (postCondition != null) {
                for (Integer conditionId : postCondition) {
                    AngelSkillTreeCfgBean conditionCfg =
                            GameDataManager.getAngelSkillTreeCfgBean(conditionId);
                    int skillLvl = 0;
                    HeroSkillTree heroSkillTreeTemp =
                            skillStrategy.getHeroSkillTree(conditionCfg.getSkillType());
                    if (heroSkillTreeTemp != null) {
                        HeroSkill heroSkillTemp =
                                heroSkillTreeTemp.getHeroSkill(conditionCfg.getPosition());
                        if (heroSkillTemp != null) {
                            skillLvl = heroSkillTemp.getLevel();
                        }
                    }
                    if (skillLvl >= conditionCfg.getLvl()) {
                        reach = false;
                        break;
                    }
                }
            }

            if (!reach) {
                MessageUtils.throwCondtionError(GameErrorCode.ANGEL_SKILL_CONDITION_NOT_SATISFIED,
                        "降级条件不满足");
                return;
            }
            int needSkillPiont = angelSkillTreeCfgBean.getNeedSkillPiont();
            skillStrategy.useSkillPiont(-needSkillPiont);
            int targetId = heroSkill.getId() - 1;
            angelSkillTreeCfgBean = GameDataManager.getAngelSkillTreeCfgBean(targetId);
            if (angelSkillTreeCfgBean == null) {
                heroSkillTree.removeSkill(pos);
                if (heroSkillTree.getSkillTreeSize() == 0) {
                    skillStrategy.removeHeroSkillTree(type);
                }
                ResUpgradeSkill.Builder upgradeSkillBuilder = ResUpgradeSkill.newBuilder();
                upgradeSkillBuilder.setHeroId(heroIdStr);
                AngeSkillInfo.Builder angeSkillInfoBuilder = AngeSkillInfo.newBuilder();
                angeSkillInfoBuilder.setType(heroSkill.getSkillType());
                angeSkillInfoBuilder.setPos(heroSkill.getPos());
                angeSkillInfoBuilder.setLvl(0);
                upgradeSkillBuilder.setAngeSkillInfo(angeSkillInfoBuilder);
                upgradeSkillBuilder.setUseSkillPiont(skillStrategy.getAlreadyUseSkillPiont());
                MessageUtils.send(player, upgradeSkillBuilder);
            } else {
                heroSkill.upLevel(angelSkillTreeCfgBean);

                ResUpgradeSkill.Builder upgradeSkillBuilder = ResUpgradeSkill.newBuilder();
                upgradeSkillBuilder.setHeroId(heroIdStr);
                upgradeSkillBuilder.setAngeSkillInfo(heroSkill.buildAngelSkillInfo());
                upgradeSkillBuilder.setUseSkillPiont(skillStrategy.getAlreadyUseSkillPiont());
                MessageUtils.send(player, upgradeSkillBuilder);
            }
            
            try {
                LogProcessor.getInstance().sendLog(LogBeanFactory.createHeroAngelLog(player, heroId, hero.getLevel(), 0, angel.getAwakeLevel(), skillStrategy.getId(), angelSkillTreeCfgBean.getId(), EReason.HERO_ANGEL_DEGRADE.value(), null));
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
        }
    }

    @Override
    protected void reqModifyStrategyName(Player player, Map<Integer, Hero> heroKV,
            ReqModifyStrategyName msg) {
        String heroIdStr = msg.getHeroId();
        int skillStrategyId = msg.getSkillStrategyId();
        String name = msg.getName();
        int heroId = Integer.parseInt(heroIdStr);
        Hero hero = heroKV.get(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO, "英雄[" + heroId + "]未找到");
            return;
        }
        Angel angel = hero.getAngel();
        SkillStrategy skillStrategy = angel.getSkillStrategyById(skillStrategyId);
        if (skillStrategy == null) {
            // 没有找到对应的技能页
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_SKILL_STRATEGY,
                    "没有找到对应的技能页" + skillStrategyId);
            return;
        }
        // 敏感词屏蔽
        name = SensitiveWordsUtil.filterAndReplace(name);
        skillStrategy.setName(name);
        ResModifyStrategyName.Builder modifyStrategyNameBuilder =
                ResModifyStrategyName.newBuilder();
        modifyStrategyNameBuilder.setHeroId(heroIdStr);
        modifyStrategyNameBuilder.setSkillStrategyId(skillStrategyId);
        modifyStrategyNameBuilder.setName(name);
        MessageUtils.send(player, modifyStrategyNameBuilder);
    }

    @Override
    protected void reqUseSkillStrategy(Player player, Map<Integer, Hero> heroKV,
            ReqUseSkillStrategy msg) {
        String heroIdStr = msg.getHeroId();
        int skillStrategyId = msg.getSkillStrategyId();
        int heroId = Integer.parseInt(heroIdStr);
        Hero hero = heroKV.get(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO, "英雄[" + heroId + "]未找到");
            return;
        }
        Angel angel = hero.getAngel();
        SkillStrategy skillStrategy = angel.getSkillStrategyById(skillStrategyId);
        if (skillStrategy == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_SKILL_STRATEGY,
                    "没有找到对应的技能页" + skillStrategyId);
            return;
        }
        angel.setUseSkillStrategyId(skillStrategyId);

        ResUseSkillStrategy.Builder useSkillStrategyBuilder = ResUseSkillStrategy.newBuilder();
        useSkillStrategyBuilder.setHeroId(heroIdStr);
        useSkillStrategyBuilder.setSkillStrategyId(skillStrategyId);
        MessageUtils.send(player, useSkillStrategyBuilder);
    }

    @Override
    protected void reqEquipPassiveSkill(Player player, Map<Integer, Hero> heroKV,
            ReqEquipPassiveSkill msg) {
        String heroIdStr = msg.getHeroId();
        int skillId = msg.getSkillId();
        int heroId = Integer.parseInt(heroIdStr);
        Hero hero = heroKV.get(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO, "英雄[" + heroId + "]未找到");
            return;
        }
        AngelSkillTreeCfgBean cfgBean = GameDataManager.getAngelSkillTreeCfgBean(skillId);
        if (cfgBean == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "未找到被动技能" + skillId);
            return;
        }
        int skillType = cfgBean.getSkillType();
        if (skillType != ESkillType.PASSIVE_SKILL.value()) {
            MessageUtils.throwCondtionError(GameErrorCode.SKILL_TYPE_IS_NOT_MATCH, "技能类型不正确");
            return;
        }
        // 判断该技能是否解锁
        Angel angel = hero.getAngel();
        int heroLevel = hero.getLevel();
        int awakeLevel = angel.getAwakeLevel();
        if (cfgBean.getNeedAngelLvl() > awakeLevel || cfgBean.getNeedHeroLvl() > heroLevel) {
            MessageUtils.throwCondtionError(GameErrorCode.SKILL_IS_NO_UNLOCK,
                    "被动技能" + skillId + "未解锁");
            return;
        }
        SkillStrategy skillStrategy = angel.getUseSkillStrategy();
        List<AngelPassiveSkillGroovesCfgBean> passiveSkillCfgBeans = GameDataManager.getAngelPassiveSkillGroovesCfgBeans();
        for(AngelPassiveSkillGroovesCfgBean passiveSkillCfgBean : passiveSkillCfgBeans) {
            if(!skillStrategy.passiveSkillKV.containsKey(passiveSkillCfgBean.getId())) {
                PassiveSkillGrid passiveSkillGrid = new PassiveSkillGrid();
                passiveSkillGrid.create(passiveSkillCfgBean.getId());
                if(hero.getLevel() >= passiveSkillCfgBean.getNeedHeroLvl() && hero.getAngel().getAwakeLevel() >= passiveSkillCfgBean.getAngelLvl()) {
                    passiveSkillGrid.setUnlock(true);
                }
                skillStrategy.passiveSkillKV.put(passiveSkillGrid.getId(), passiveSkillGrid);
            }
        }
        
        PassiveSkillGrid passiveSkillGrid = skillStrategy.isEquipPassiveSkill(skillId);
        if (passiveSkillGrid != null) {
            passiveSkillGrid.setSkillId(0);
            HeroProperty heroProperty = hero.getHeroProperty();
            List<EPropertyModule> propertyModules =
                    Arrays.asList(EPropertyModule.PROPERTY_HERO_SKILL);
            List<EPropertyModule> fightPowerModules =
                    Arrays.asList(EPropertyModule.PROPERTY_HERO_SKILL);
            heroProperty.calculateProperty(true, propertyModules, fightPowerModules);
        } else {
            int pos = msg.getPos();
            passiveSkillGrid = skillStrategy.getPassiveSkillGrid(pos);
            if (passiveSkillGrid != null && passiveSkillGrid.isUnlock()) {
                passiveSkillGrid.setSkillId(skillId);
                HeroProperty heroProperty = hero.getHeroProperty();
                List<EPropertyModule> propertyModules =
                        Arrays.asList(EPropertyModule.PROPERTY_HERO_SKILL);
                List<EPropertyModule> fightPowerModules =
                        Arrays.asList(EPropertyModule.PROPERTY_HERO_SKILL);
                heroProperty.calculateProperty(true, propertyModules, fightPowerModules);
            } else {
                MessageUtils.throwCondtionError(GameErrorCode.NOT_ENOUGH_SKILL_POSITION,
                        "没有装备槽可以装备技能");
                return;
            }
        }
        ResEquipPassiveSkill.Builder equipPassiveSkillBuilder = ResEquipPassiveSkill.newBuilder();
        equipPassiveSkillBuilder.setHeroId(heroIdStr);
        PassiveSkillInfo.Builder passiveSkillInfoBuilder = PassiveSkillInfo.newBuilder();
        passiveSkillInfoBuilder.setPos(passiveSkillGrid.getId());
        passiveSkillInfoBuilder.setSkillId(passiveSkillGrid.getSkillId());
        equipPassiveSkillBuilder.setPassiveSkillInfo(passiveSkillInfoBuilder);
        MessageUtils.send(player, equipPassiveSkillBuilder);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void reqActiveCrystal(Player player, Map<Integer, Hero> heroKV,
            ReqActiveCrystal msg) {
        String heroIdStr = msg.getHeroId();
        int rarity = msg.getRarity();
        int gridId = msg.getGridId();
        int heroId = Integer.parseInt(heroIdStr);
        Hero hero = heroKV.get(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO, "英雄[" + heroId + "]未找到");
            return;
        }

        if (hero.getQuality() < rarity) {
            MessageUtils.throwCondtionError(GameErrorCode.UN_ACTIVE_CRYSTAL,
                    "英雄[" + heroId + "],稀有度[" + hero.getQuality() + "],条件稀有度[" + rarity + "]");
            return;
        }
        EvolutionCfgBean evolutionCfgBean = null;
        List<EvolutionCfgBean> evolutionCfgBeans = GameDataManager.getEvolutionCfgBeans();
        for (EvolutionCfgBean cfgBean : evolutionCfgBeans) {
            if (cfgBean.getHeroId() == heroId && cfgBean.getPartition() == rarity
                    && cfgBean.getCell() == gridId) {
                evolutionCfgBean = cfgBean;
                break;
            }
        }
        if (evolutionCfgBean == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "英雄[" + heroId + "],稀有度[" + rarity + "],格子[" + gridId + "]结晶找不到");
            return;
        }
        Map<Integer, List<Integer>> crystalKV = hero.getCrystalKV();
        List<Integer> crystal = crystalKV.get(rarity);
        if (crystal.contains(gridId)) {
            MessageUtils.throwCondtionError(GameErrorCode.CRYSTAL_HAS_ACTIVED,
                    "英雄" + heroId + ",稀有度" + rarity + ",格子" + gridId + "结晶已激活");
            return;
        }

        List<Integer> conditionList = evolutionCfgBean.getUnlockCondition();
        boolean isSatisfy = true;
        if (conditionList != null) {
            isSatisfy = false;
            label: for (Integer condition : conditionList) {
                for (Map.Entry<Integer, List<Integer>> entry : crystalKV.entrySet()) {
                    List<Integer> crystalTemp = entry.getValue();
                    if (crystalTemp.contains(condition)) {
                        isSatisfy = true;
                        break label;
                    }
                }
            }
        }
        if (!isSatisfy) {
            MessageUtils.throwCondtionError(GameErrorCode.UN_ACTIVE_CRYSTAL, "激活结晶前置条件不满足");
            return;
        }
        Map<Integer, Integer> consumeMap = evolutionCfgBean.getMaterial();
        BagManager bagManager = player.getBagManager();
        boolean isEnough = bagManager.removeItemsByTemplateIdWithCheck(consumeMap, true,
                EReason.HERO_CRYSTAL_ACTIVE);
        if (!isEnough) {
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "请求激活结晶，消耗道具不足");
            return;
        }
        hero.addCrystal(rarity, gridId);

        ResActiveCrystal.Builder activeCrystalBuilder = ResActiveCrystal.newBuilder();
        activeCrystalBuilder.setHeroId(heroIdStr);
        activeCrystalBuilder.setRarity(rarity);
        activeCrystalBuilder.setGridId(gridId);
        MessageUtils.send(player, activeCrystalBuilder);
        
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createHeroCrystalLog(player, heroId, rarity, gridId, EReason.HERO_CRYSTAL_ACTIVE.value(), null));
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    @Override
    protected void sendHeroInfoSingle(Player player, Map<Integer, Hero> heroKV, int heroId) {
        Hero hero = heroKV.get(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO, "英雄[" + heroId + "]不存在");
            return;
        }
        MessageUtils.send(player, hero.buildHeroInfo(S2CShareMsg.ChangeType.UPDATE));
    }

    @Override
    protected void addHero(Player player, Map<Integer, Hero> heroKV, HeroCfgBean heroCfgBean, EReason reason) {
        HeroManager heroManager = player.getHeroManager();
        Hero hero = heroKV.get(heroCfgBean.getId());
        if (hero == null) {
            Map<Integer, Formation> formations = heroManager.getFormations();
            hero = new Hero();
            hero.createHero(heroCfgBean, player);
            hero.init(player);
            heroKV.put(hero.getCid(), hero);
            player.getRoleManager().activateRole(heroCfgBean.getRole());
            // 新创建的账号
            if (heroManager.getHelpFightHeroCid() == 0) {
                heroManager.setHelpFightHeroCid(heroCfgBean.getId());
            }
            if (formations.size() == 0) {
                Formation formation = new Formation();
                formation.create(FormationConstant.TYPE_MAIN);
                formation.setStatus(FormationConstant.STATUS_USE);
                formation.getHeroIds().add(hero.getCid());
                formations.put(formation.getType(), formation);
                heroManager.setFormationType(formation.getType());
            }
            HeroInfo.Builder heroInfoBuilder = hero.buildHeroInfo(S2CShareMsg.ChangeType.ADD);
            if(reason == EReason.SUMMON) {
                heroInfoBuilder.setProvide(10036);
            }
            MessageUtils.send(player, heroInfoBuilder);
            player.getRoleManager().obtainEvent(player, 1, heroCfgBean, 0);
        }
    }

    @Override
    protected void reqChangeHelpFightHero(Player player, ChangeHelpFightHero msg) {
        HeroManager heroManager = player.getHeroManager();
        String heroIdStr = msg.getHeroId();
        int heroId = Integer.parseInt(heroIdStr);
        Hero hero = heroManager.getHero(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO, "助战英雄未找到");
            return;
        }
        Hero oldFightHero = heroManager.getHero(heroManager.getHelpFightHeroCid());
        heroManager.setHelpFightHeroCid(hero.getCid());

        PlayerInfo.Builder playerInfoBuilder = PlayerInfo.newBuilder();
        playerInfoBuilder.setHelpFightHeroCid(heroManager.getHelpFightHeroCid());
        MessageUtils.send(player, playerInfoBuilder);

        ((PlayerViewService) PlayerViewService.getInstance())
                .updatePlayerView(player.toPlayerBean(), false, -1);

        HeroInfoList.Builder builder = HeroInfoList.newBuilder();
        builder.addHeros(oldFightHero.buildHeroInfo(ChangeType.UPDATE));
        builder.addHeros(hero.buildHeroInfo(ChangeType.UPDATE));
        MessageUtils.send(player, builder);
        MessageUtils.returnEmptyBody();
        return;
    }

    @Override
    protected void reqSwitchFormation(Player player, ReqSwitchFormation msg) {
        HeroManager heroManager = player.getHeroManager();
        Map<Integer, Formation> formations = heroManager.getFormations();
        int type = msg.getFormationType();
        Formation newFormation = formations.get(type);
        if (newFormation == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "阵营类型" + type + "找不到");
            return;
        }
        Formation oldFormation = formations.get(heroManager.getFormationType());
        oldFormation.setStatus(FormationConstant.STATUS_DEFAULT);
        newFormation.setStatus(FormationConstant.STATUS_USE);
        heroManager.setFormationType(type);

        FormationInfoList.Builder formationInfoListBuilder = FormationInfoList.newBuilder();
        formationInfoListBuilder
                .addFormations(oldFormation.buildFormationInfo(S2CShareMsg.ChangeType.UPDATE));
        formationInfoListBuilder
                .addFormations(newFormation.buildFormationInfo(S2CShareMsg.ChangeType.UPDATE));
        MessageUtils.send(player, formationInfoListBuilder);
        MessageUtils.returnEmptyBody();
    }

    @Override
    protected void reqOperateFormation(Player player, Map<Integer, Hero> heroKV,
            OperateFormation msg) {
        HeroManager heroManager = player.getHeroManager();
        Map<Integer, Formation> formations = heroManager.getFormations();
        int type = msg.getFormationType();
        Formation formation = formations.get(type);
        if (formation == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "阵营类型" + type + "不存在");
            return;
        }

        List<Integer> heroIds = formation.getHeroIds();
        String sourceHeroIdStr = msg.getSourceHeroId();
        String targetHeroIdStr = msg.getTargetHeroId();

        Hero sourceHero = null;
        Hero targetHero = null;
        if (!sourceHeroIdStr.equals("")) {
            int sourceHeroId = Integer.parseInt(sourceHeroIdStr);
            sourceHero = heroKV.get(sourceHeroId);
            if (sourceHero == null) {
                MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                        "源英雄id" + sourceHeroId + "找不到");
                return;
            }
        }

        if (!targetHeroIdStr.equals("")) {
            int targetHeroId = Integer.parseInt(targetHeroIdStr);
            targetHero = heroKV.get(targetHeroId);
            if (targetHero == null) {
                MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                        "目标英雄id" + targetHeroId + "找不到");
                return;
            }
        }

        if (sourceHero == null && targetHero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "源英雄和目标英雄都为空");
            return;
        }

        if (sourceHero != null && targetHero == null) {
            int index = heroIds.indexOf(sourceHero.getCid());
            if (index < 0) {
                if (checkRepeatHero(heroIds, sourceHero.getCid())) {
                    MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "不能上重复的英雄");
                    return;
                } else {
                    if (formation.getHeroIds().size() >= FormationConstant.MAX_HERO_NUM) {
                        MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "阵型已满，不能上阵");
                        return;
                    }
                    heroIds.add(sourceHero.getCid());
                }
            } else {
                heroIds.remove(index);
            }
        } else if (sourceHero != null && targetHero != null) {
            int targetIndex = heroIds.indexOf(targetHero.getCid());
            if(targetIndex != -1) {
                int sourceIndex = heroIds.indexOf(sourceHero.getCid());
                if(sourceIndex == -1) {
                    MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                            "源英雄id" + sourceIndex + "不在阵容上!");
                }
                heroIds.set(targetIndex, sourceHero.getCid());
                heroIds.set(sourceIndex, targetHero.getCid());
            } else {
                List<Integer> heroIdsCopy = new ArrayList<>(heroIds);
                heroIdsCopy.remove(Integer.valueOf(sourceHero.getCid()));
                if (checkRepeatHero(heroIdsCopy, targetHero.getCid())) {
                    MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "不能上重复的英雄");
                    return;
                } else {
                    int index = heroIds.indexOf(sourceHero.getCid());
                    heroIds.set(index, targetHero.getCid());
                }
            }
        }
        MessageUtils.send(player, formation.buildFormationInfo(S2CShareMsg.ChangeType.UPDATE));
    }

    private boolean checkRepeatHero(List<Integer> heroIds, int targetHeroId) {
        if (heroIds.indexOf(targetHeroId) == -1) {
            HeroCfgBean targetHeroCfgBean = GameDataManager.getHeroCfgBean(targetHeroId);
            for (Integer heroId : heroIds) {
                HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(heroId);
                if (heroCfgBean.getRole() == targetHeroCfgBean.getRole()) {
                    return true;
                }
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void reqResetSkill(Player player, ReqResetSkill msg) {
        HeroManager heroManager = player.getHeroManager();
        String heroIdStr = msg.getHeroId();
        int skillStrategyId = msg.getSkillStrategyId();
        int heroId = Integer.parseInt(heroIdStr);
        Hero hero = heroManager.getHero(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.NOT_FOND_HERO, "请求重置技能,英雄未找到");
            return;
        }
        Angel angel = hero.getAngel();
        SkillStrategy skillStrategy = angel.getSkillStrategyById(skillStrategyId);
        skillStrategy.resetSkill();
        ResResetSkill.Builder resResetSkillBuilder = ResResetSkill.newBuilder();
        resResetSkillBuilder.setHeroId(heroIdStr);
        resResetSkillBuilder.setSkillStrategy(skillStrategy.buildSkillStrategyInfo());
        MessageUtils.send(player, resResetSkillBuilder);
        
        try {
            LogProcessor.getInstance().sendLog(LogBeanFactory.createHeroAngelLog(player, heroId, hero.getLevel(), 0, angel.getAwakeLevel(), skillStrategy.getId(), 0, EReason.HERO_ANGEL_RESET.value(), null));
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    /**
     * 装备固定buff
     */
    public void fixedBuff(Hero hero, List<Integer> buffList) {
        Map<Integer, HeroEquip> heroEquipMap = hero.getHeroEquip();
        EquipmentCfgBean cfgBean = null;
        for (HeroEquip heroEquip : heroEquipMap.values()) {
            EquipItem equipItem = heroEquip.getEquipItem();
            cfgBean = GameDataManager.getEquipmentCfgBean(equipItem.getTemplateId());
            int buffId = cfgBean.getInherentAttribute();
            if (buffId != 0) {
                buffList.add(buffId);
            }
        }
    }

    /**
     * 装备组合buff
     */
    public void CombinationBuff(Hero hero, List<Integer> buffList) {
        Map<Integer, HeroEquip> heroEquipMap = hero.getHeroEquip();
        // 不足组合条件
        if (heroEquipMap.size() < 2) {
            return;
        }
        EquipmentCfgBean cfgBean = null;
        List<HeroEquip> equipList = new ArrayList<>(heroEquipMap.values());
        for (int i = 0; i < equipList.size(); i++) {
            HeroEquip heroEquip = equipList.get(i);
            EquipItem equipItem = heroEquip.getEquipItem();
            cfgBean = GameDataManager.getEquipmentCfgBean(equipItem.getTemplateId());
            int[] combId = cfgBean.getCombination();
            int star = cfgBean.getStar();
            for (int j = i + 1; j < equipList.size(); j++) {
                HeroEquip nextHeroEquip = equipList.get(j);
                EquipItem nextEquipItem = nextHeroEquip.getEquipItem();
                cfgBean = GameDataManager.getEquipmentCfgBean(nextEquipItem.getTemplateId());
                int[] nextCombId = cfgBean.getCombination();
                int sameCombId = getSameCombinationId(combId, nextCombId);
                if (sameCombId == -1) {
                    continue;
                }
                int nextStar = cfgBean.getStar();
                int minStar = Math.min(star, nextStar);
                EquipmentCombinationCfgBean combinationCfgBean =
                        GameDataManager.getEquipmentCombinationCfgBean(sameCombId);
                int[] skill = combinationCfgBean.getSkill();
                if (minStar > skill.length) {
                    continue;
                }
                int skillId = skill[minStar - 1];
                buffList.add(skillId);
            }
        }
    }

    /**
     * 套装buff
     */
    public void suitBuff(Hero hero, List<Integer> buffList) {
        Map<Integer, HeroEquip> heroEquipMap = hero.getHeroEquip();
        // 不足套装条件
        if (heroEquipMap.size() < 3) {
            return;
        }
        EquipmentCfgBean cfgBean = null;
        Set<Integer> set = new HashSet<>();
        int suitId = 0;
        boolean code = false;
        for (HeroEquip heroEquip : heroEquipMap.values()) {
            EquipItem equipItem = heroEquip.getEquipItem();
            cfgBean = GameDataManager.getEquipmentCfgBean(equipItem.getTemplateId());
            suitId = cfgBean.getSuit();
            if (suitId > 0) {
                set.add(suitId);
            } else {
                code = true;
                break;
            }
        }
        // 不足套装条件
        if (code || set.size() > 1) {
            return;
        }
        EquipmentSuitCfgBean suitCfgBean = GameDataManager.getEquipmentSuitCfgBean(suitId);
        if (suitCfgBean == null) {
            return;
        }
        int skillId = suitCfgBean.getSuitSkill();
        buffList.add(skillId);
    }

    /**
     * 取出相同的
     */
    public int getSameCombinationId(int[] comId, int[] nextCombId) {
        for (int i : comId) {
            for (int j : nextCombId) {
                if (i == j) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public List<Integer> getHeroEquipSkill(Hero hero) {
        List<Integer> buffList = new ArrayList<>();
        // 固有buff
        fixedBuff(hero, buffList);
        // 组合buff
        CombinationBuff(hero, buffList);
        // 套装buff
        suitBuff(hero, buffList);

        return buffList;
    }
}
