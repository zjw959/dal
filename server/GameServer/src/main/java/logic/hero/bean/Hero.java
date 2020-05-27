package logic.hero.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.game.protobuf.s2c.S2CHeroMsg;
import org.game.protobuf.s2c.S2CHeroMsg.CrystalInfo;
import org.game.protobuf.s2c.S2CHeroMsg.HeroInfo;
import org.game.protobuf.s2c.S2CHeroMsg.ResPropertyChange;
import org.game.protobuf.s2c.S2CShareMsg;

import data.GameDataManager;
import data.bean.AngelPassiveSkillGroovesCfgBean;
import data.bean.HeroCfgBean;
import logic.character.bean.Player;
import logic.constant.EPropertyModule;
import logic.constant.EPropertyType;
import logic.constant.EReason;
import logic.constant.GameErrorCode;
import logic.hero.HeroManager;
import logic.hero.HeroProperty;
import logic.hero.skill.Angel;
import logic.item.bean.EquipItem;
import logic.item.bean.Item;
import logic.item.bean.SkinItem;
import logic.msgBuilder.ItemMsgBuilder;
import logic.support.MessageUtils;

/**
 * 英雄
 */
public class Hero {
    private transient Player player;
    /** 配置id */
    private int cid;
    /** 品质 */
    private int quality;
    /** 等级 */
    private int level;
    /** 经验 */
    private long exp;
    /** 皮肤 */
    private HeroSkin skin;
    /** 英雄的属性 */
    private transient HeroProperty heroProperty = new HeroProperty();
    /** 天使 */
    private Angel angel;
    /** 结晶 */
    private Map<Integer, List<Integer>> crystalKV = new HashMap<>();
    /** 英雄身上装备 */
    private Map<Integer, HeroEquip> heroEquip = new HashMap<>();


    public void createHero(HeroCfgBean heroCfgBean, Player player) {
        this.player = player;
        this.cid = heroCfgBean.getId();
        this.quality = heroCfgBean.getRarity();
        this.level = 1;
        this.exp = 0;
        SkinItem skinItem = getHeroSkinItem(heroCfgBean);
        skinItem.setHeroId(cid);
        this.skin = new HeroSkin();
        skin.create(skinItem.getId());
        angel = new Angel();
        angel.createAngel(this);

        for (int i = 1; i <= quality; i++) {
            crystalKV.put(i, new ArrayList<>());
        }
    }

    public void init(Player player) {
        this.player = player;
        initHeroSkin(player);
        checkAndInitEquip(player);
        this.angel.init(this);
        this.heroProperty.init(this);
    }

    public int getCid() {
        return cid;
    }

    public int getQuality() {
        return quality;
    }

    public void upQuality() {
        quality = quality + 1;
        List<EPropertyModule> propertyModules = Arrays.asList(EPropertyModule.PROPERTY_HERO_BASE,
                EPropertyModule.PROPERTY_HERO_GROW);
        List<EPropertyModule> fightPowerModules = Arrays.asList(EPropertyModule.PROPERTY_HERO_BASE,
                EPropertyModule.PROPERTY_HERO_GROW);
        heroProperty.calculateProperty(true, propertyModules, fightPowerModules);
        crystalKV.put(quality, new ArrayList<>());
    }

    public int getLevel() {
        return level;
    }

    public void upLevel() {
        level = level + 1;
        exp = 0;
        List<EPropertyModule> propertyModules = Arrays.asList(EPropertyModule.PROPERTY_HERO_GROW);
        List<EPropertyModule> fightPowerModules = Arrays.asList(EPropertyModule.PROPERTY_HERO_GROW);
        heroProperty.calculateProperty(true, propertyModules, fightPowerModules);
        Map<Integer, AngelPassiveSkillGroovesCfgBean> cfgBeanKV =
                GameDataManager.getAngelPassiveSkillGroovesCfgBeanKV();
        for (Map.Entry<Integer, AngelPassiveSkillGroovesCfgBean> entry : cfgBeanKV.entrySet()) {
            AngelPassiveSkillGroovesCfgBean cfgBean = entry.getValue();
            if (level >= cfgBean.getNeedHeroLvl() && angel.getAwakeLevel() >= cfgBean.getAngelLvl()) {
                angel.unlockPassiveSkillGrid(cfgBean.getId());
            }
        }
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public void setSkin(HeroSkin skin) {
        this.skin = skin;
    }

    public HeroSkin getSkin() {
        return skin;
    }

    public Angel getAngel() {
        return angel;
    }

    public void awakeAngel() {
        angel.awake();
        List<EPropertyModule> propertyModules = Arrays.asList(EPropertyModule.PROPERTY_HERO_SKILL);
        List<EPropertyModule> fightPowerModules =
                Arrays.asList(EPropertyModule.PROPERTY_HERO_SKILL);
        heroProperty.calculateProperty(true, propertyModules, fightPowerModules);
    }

    public int getPropertyValue(EPropertyType propertyType) {
        return heroProperty.getPropertyValue(propertyType);
    }

    public HeroProperty getHeroProperty() {
        return heroProperty;
    }

    public int getFightPower() {
        return heroProperty.calculateFightPower();
    }

    public HeroInfo.Builder buildHeroInfo(S2CShareMsg.ChangeType changeType) {
        HeroInfo.Builder heroInfoBuilder = HeroInfo.newBuilder();
        heroInfoBuilder.setCt(changeType);
        heroInfoBuilder.setId(String.valueOf(cid));
        heroInfoBuilder.setCid(cid);
        heroInfoBuilder.setLvl(level);
        heroInfoBuilder.setExp(exp);
        heroInfoBuilder.setQuality(quality);
        heroInfoBuilder.addAllAttr(heroProperty.getAttributeInfoBuilder());
        heroInfoBuilder.setProvide(0);
        heroInfoBuilder.setAngelLvl(angel.getAwakeLevel());
        heroInfoBuilder.setHelpFight(isHelpFightHero());
        heroInfoBuilder.setUseSkillPiont(0);
        heroInfoBuilder.setFightPower(heroProperty.calculateFightPower());
        heroInfoBuilder.setSkinCid(skin.getSkinItem().getTemplateId());
        heroInfoBuilder.addAllSkillStrategyInfo(angel.buildSkillStrategy());
        heroInfoBuilder.setUseSkillStrategy(angel.getUseSkillStrategyId());
        heroInfoBuilder.addAllCrystalInfo(buildCrystalInfo());
        getEquipmentBuilder(heroInfoBuilder, changeType);
        return heroInfoBuilder;
    }

    /**
     * 是否是助战英雄
     */
    protected boolean isHelpFightHero() {
        HeroManager heroManager = player.getHeroManager();
        return heroManager.getHelpFightHeroCid() == cid;
    }

    public List<CrystalInfo> buildCrystalInfo() {
        List<CrystalInfo> crystalInfos = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : crystalKV.entrySet()) {
            int rarity = entry.getKey();
            List<Integer> gridIds = entry.getValue();
            for (Integer gridId : gridIds) {
                CrystalInfo.Builder crystalInfoBuilder = CrystalInfo.newBuilder();
                crystalInfoBuilder.setRarity(rarity);
                crystalInfoBuilder.setGridId(gridId);
                crystalInfos.add(crystalInfoBuilder.build());
            }
        }
        return crystalInfos;
    }

    public void getEquipmentBuilder(HeroInfo.Builder heroInfoBuilder,
            S2CShareMsg.ChangeType changeType) {

        for (HeroEquip heroEquip : heroEquip.values()) {
            EquipItem equipItem = getHeroEquipItem(heroEquip);
            if (equipItem == null) {
                continue;
            }
            S2CHeroMsg.HeroEquipment.Builder equipment = S2CHeroMsg.HeroEquipment.newBuilder();
            equipment.setEquipmentId(String.valueOf(equipItem.getId()));
            equipment.setPosition(equipItem.getPosition());
            equipment.setEquip(ItemMsgBuilder.createEquipmentInfo(changeType, equipItem));
            heroInfoBuilder.addEquipments(equipment);
        }
    }

    /**
     * 获取装备道具副本
     */
    protected EquipItem getHeroEquipItem(HeroEquip heroEquip) {
        return (EquipItem) player.getBagManager().getItemCopy(heroEquip.getEquipId());
    }

    public boolean checkSkinId(int skinCfgId) {
        HeroCfgBean heroCfgBean = GameDataManager.getHeroCfgBean(cid);
        int[] optionalSkin = heroCfgBean.getOptionalSkin();
        for (int i = 0; i < optionalSkin.length; i++) {
            if (skinCfgId == optionalSkin[i]) {
                return true;
            }
        }
        return false;
    }

    public void changeSkin(SkinItem beforeSkin, SkinItem skin) {
        beforeSkin.setHeroId(0);
        skin.setHeroId(cid);
        this.skin = new HeroSkin();
        this.skin.create(skin.getId());
        this.skin.setSkinItem(skin);
    }

    public Map<Integer, List<Integer>> getCrystalKV() {
        return crystalKV;
    }

    public void addCrystal(int rarity, int gridId) {
        List<Integer> crystal = crystalKV.get(rarity);
        crystal.add(gridId);
        List<EPropertyModule> propertyModules =
                Arrays.asList(EPropertyModule.PROPERTY_HERO_CRYSTAL);
        List<EPropertyModule> fightPowerModules =
                Arrays.asList(EPropertyModule.PROPERTY_HERO_CRYSTAL);
        heroProperty.calculateProperty(true, propertyModules, fightPowerModules);
    }

    public HeroEquip getEquipByPos(int position) {
        return heroEquip.get(position);
    }

    public void equipAndTakeOff() {
        List<EPropertyModule> propertyModules = Arrays.asList(EPropertyModule.PROPERTY_HERO_EQUIP);
        List<EPropertyModule> fightPowerModules =
                Arrays.asList(EPropertyModule.PROPERTY_HERO_EQUIP);
        heroProperty.calculateProperty(true, propertyModules, fightPowerModules);
    }

    public void removeEquip(int position) {
        heroEquip.remove(position);
    }

    public void putEquip(int key, HeroEquip value) {
        heroEquip.put(key, value);
    }

    public Map<Integer, HeroEquip> getHeroEquip() {
        return heroEquip;
    }

    public void sendPropertyChange() {
        ResPropertyChange.Builder propertyChangeBuilder = ResPropertyChange.newBuilder();
        propertyChangeBuilder.setHeroId(String.valueOf(cid));
        propertyChangeBuilder.addAllAttr(heroProperty.getAttributeInfoBuilder());
        propertyChangeBuilder.setFightPower(heroProperty.calculateFightPower());
        MessageUtils.send(player, propertyChangeBuilder);
    }

    protected void initHeroEquip(Player player, HeroEquip heroEquip) {
        heroEquip.init(player);
    }

    protected SkinItem getHeroSkinItem(HeroCfgBean heroCfgBean) {
        List<Item> items =
                this.player.getBagManager().getItemCopyByTemplateId(heroCfgBean.getDefaultSkin());
        if (items.size() > 0) {
            this.player.getBagManager().addItem(heroCfgBean.getDefaultSkin(), 1, true,
                    EReason.HERO_GAIN);
        } else {
            items = this.player.getBagManager().addItem(heroCfgBean.getDefaultSkin(), 1, true,
                    EReason.HERO_GAIN);
        }
        SkinItem skinItem =
                (SkinItem) this.player.getBagManager().getItemOrigin(items.get(0).getId());
        if (skinItem.getHeroId() != 0) {
            MessageUtils.throwConfigError(GameErrorCode.CFG_IS_ERR, "多个英雄共用一个皮肤");
        }
        return skinItem;
    }

    protected void initHeroSkin(Player player) {
        this.skin.init(player);
    }

    public void checkAndInitEquip(Player player) {
        Map<Integer, HeroEquip> map = new HashMap<>();
        map.putAll(heroEquip);
        for (Map.Entry<Integer, HeroEquip> entry : map.entrySet()) {
            long id = entry.getValue().getEquipId();
            Item item = player.getBagManager().getItemCopy(id);
            if (item == null) {
                heroEquip.remove(entry.getKey());
            }
        }
        for (HeroEquip heroEquip : heroEquip.values()) {
            initHeroEquip(player, heroEquip);
        }
    }
}
