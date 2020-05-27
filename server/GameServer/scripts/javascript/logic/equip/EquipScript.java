package javascript.logic.equip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.game.protobuf.s2c.S2CShareMsg.ChangeType;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import data.GameDataManager;
import data.bean.DiscreteDataCfgBean;
import data.bean.EquipmentCfgBean;
import data.bean.EquipmentGrowthCfgBean;
import data.bean.EquipmentRandomCfgBean;
import data.bean.EquipmentXilianCfgBean;
import logic.bag.BagManager;
import logic.character.bean.Player;
import logic.constant.DiscreteDataID;
import logic.constant.EEventType;
import logic.constant.EFunctionType;
import logic.constant.EPropertyType;
import logic.constant.EReason;
import logic.constant.EScriptIdDefine;
import logic.constant.EventConditionKey;
import logic.constant.EventConditionType;
import logic.constant.GameErrorCode;
import logic.constant.ItemConstantId;
import logic.equip.IEquipScript;
import logic.equip.bean.EquipSpecialAttr;
import logic.functionSwitch.FunctionSwitchService;
import logic.hero.bean.Hero;
import logic.hero.bean.HeroEquip;
import logic.item.bean.EquipItem;
import logic.item.bean.Item;
import logic.msgBuilder.EquipmentMsgBuilder;
import logic.msgBuilder.ItemMsgBuilder;
import logic.support.MessageUtils;

public class EquipScript extends IEquipScript {

    @Override
    public int getScriptId() {
        return EScriptIdDefine.EQUIP_SCRIPT.Value();
    }

    @SuppressWarnings("unchecked")
    @Override
    /**
     * 装备洗练,之前随机规则，现在改为不随机
     */
    protected void changeSpecialAttr(Player player, String eqId, String costId, List<Integer> index,
            List<Integer> costIndex) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance()
                .isOpenFunction(EFunctionType.EQUIP_CHANGE_SPECIAL_ATTR)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:EquipChangeSpecialAttr");
        }
        if (index.isEmpty() || costIndex.isEmpty() || index.size() != costIndex.size()) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "特殊属性条目不匹配:");
            return;
        }
        BagManager manager = player.getBagManager();
        long id = Long.valueOf(eqId);
        long cId = Long.valueOf(costId);
        Item item = manager.getItemOrigin(id);
        Item cItem = manager.getItemOrigin(cId);
        if (item == null || cItem == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "背包内根据id没有找到item, id:" + id);
            return;
        }
        // 特殊属性替换
        EquipItem eItem = (EquipItem) item;
        EquipItem costItem = (EquipItem) cItem;
        if (costItem.getLock()) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "装备已经被锁定:");
            return;
        }

        EquipmentCfgBean bean = GameDataManager.getEquipmentCfgBean(item.getTemplateId());
        EquipmentCfgBean cBean = GameDataManager.getEquipmentCfgBean(cItem.getTemplateId());
        if (bean.getSubType() != cBean.getSubType()) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "灵装类型不同:" + id);
            return;
        }
        // 检测消耗
        EquipmentXilianCfgBean cfgBean = getXiLianConfig(bean.getStar(), index.size());

        boolean isEnough = manager.enoughByTemplateId(cfgBean.getGifts());
        if (!isEnough) {
            // 所需消耗道具不足
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "消耗资源不足");
            return;
        }


        Map<Integer, EquipSpecialAttr> newAttr = new HashMap<>();
        for (int i = 0; i < index.size(); i++) {
            newAttr.put(index.get(i), costItem.getSpecialAttrList().get(costIndex.get(i)).copy());
        }
        eItem.setTempSpecialAttr(newAttr);
        eItem.setChEquip(costItem.getId() + "_" + index.size());

        org.game.protobuf.s2c.S2CEquipmentMsg.ChangeSpecialAttrMsg.Builder builder =
                org.game.protobuf.s2c.S2CEquipmentMsg.ChangeSpecialAttrMsg.newBuilder();
        builder.setSuccess(true);
        MessageUtils.send(player, builder);

        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.EQUIP_CHANGER);
        in.put(EventConditionKey.EQUIP_STAR, bean.getStar());
        player._fireEvent(in, EEventType.EQUIP.value());
    }

    public EquipmentXilianCfgBean getXiLianConfig(int star, int number) {
        List<EquipmentXilianCfgBean> list = GameDataManager.getEquipmentXilianCfgBeans();
        for (EquipmentXilianCfgBean cfg : list) {
            if (cfg.getStar() == star && cfg.getAttributeNum() == number) {
                return cfg;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void replaceSpecialAttr(Player player, String equipmentId, boolean isReplace) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance()
                .isOpenFunction(EFunctionType.EQUIP_CHANGE_SPECIAL_ATTR)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:EquipChangeSpecialAttr");
        }
        BagManager manager = player.getBagManager();
        long id = Long.valueOf(equipmentId);
        Item item = manager.getItemOrigin(id);
        if (item == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "背包内根据id没有找到item, id:" + id);
            return;
        }
        EquipItem equipItem = (EquipItem) item;
        EquipmentCfgBean bean = GameDataManager.getEquipmentCfgBean(item.getTemplateId());
        if (bean == null || equipItem.getChEquip() == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "该装备无特殊属性可替换:");
            return;
        }
        String[] info = equipItem.getChEquip().split("_");
        long costId = Long.valueOf(info[0]);
        int number = Integer.parseInt(info[1]);

        EquipmentXilianCfgBean cfgBean = getXiLianConfig(bean.getStar(), number);
        if (cfgBean == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "找不到配置:star :" + bean.getStar() + " number:" + number);
            return;
        }

        org.game.protobuf.s2c.S2CEquipmentMsg.ReplaceSpecialAttrMsg.Builder builder =
                org.game.protobuf.s2c.S2CEquipmentMsg.ReplaceSpecialAttrMsg.newBuilder();
        if (!isReplace) {
            // 不替换的时候也清空
            equipItem.removeTempAttr();
            builder.setSuccess(false);
        } else {
            // 检测消耗
            boolean isEnough = manager.removeItemsByTemplateIdWithCheck(cfgBean.getGifts(), true,
                    EReason.EQUIP);
            if (!isEnough) {
                // 所需消耗道具不足
                MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "消耗资源不足");
                return;
            }
            // 移除质点
            boolean code = manager.removeItemsByIds(MapUtil.of(costId, 1), true, EReason.EQUIP);
            if (!code) {
                // 所需消耗道具不足
                MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "扣除失败");
                return;
            }
            if (equipItem.getHeroId() != 0) {
                Hero hero = player.getHeroManager().getHero(equipItem.getHeroId());
                if (hero != null) {
                    hero.equipAndTakeOff();
                    // 发送英雄信息
                    player.getHeroManager().sendHeroInfoSingle(hero.getCid());
                }
            }
            equipItem.changeAttr();
            builder.setSuccess(true);
        }

        sendEquipmentInfo(player, equipItem, org.game.protobuf.s2c.S2CShareMsg.ChangeType.UPDATE);
        MessageUtils.send(player, builder);

    }

    @Override
    protected void upgrade(Player player, String sourceEquipment, List<String> costEquipmentList) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.EQUIP_LEVEL_UP)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:EquipUpgrade");
        }
        BagManager manager = player.getBagManager();
        long id = Long.valueOf(sourceEquipment);
        EquipItem item = (EquipItem) manager.getItemOrigin(id);
        if (item == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "背包内根据id没有找到item, id:" + id);
            return;
        }

        int star = GameDataManager.getEquipmentCfgBean(item.getTemplateId()).getStar();
        EquipmentGrowthCfgBean growthCfg = GameDataManager.getEquipmentGrowthCfgBean(star);
        int[] expList = growthCfg.getNeedExp();
        List<Item> actualCostList = Lists.newArrayList();

        int costGold = 0;
        Map<Long, Integer> costInfo = Maps.newHashMap();
        DiscreteDataCfgBean coumse =
                GameDataManager.getDiscreteDataCfgBean(DiscreteDataID.EQUIP_UPGRADE);
        Object object = coumse.getData().get(DiscreteDataID.EQUIPMENT_UPDATE_COST_RATIO);
        int costRatio = Integer.parseInt(object.toString());

        StringBuilder costStr = new StringBuilder();
        // 逐件消耗灵装
        for (String costEquipment : costEquipmentList) {
            // 判断强化的灵装是否达到上限
            long costId = Long.valueOf(costEquipment);
            Item costItem = manager.getItemOrigin(costId);
            if (costItem == null) {
                MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                        "背包内根据id没有找到item, id:" + costId);
                return;
            }
            EquipItem cost = (EquipItem) costItem;
            if (cost.getHeroId() != 0) {
                continue;
            }
            if (item.getLevel() >= expList.length)
                break;
            // 判断金钱是否足够
            int exp = getEquipmentExp(cost, star);
            int gold = (int) (((long) exp) * costRatio / 10000);
            // if (ItemManager.getGoldNum(player) < exp + costGold)
            if (player.getGold() < gold + costGold)
                break;
            costInfo.put(costId, 1);
            addEquipmentExp(item, exp, expList);
            actualCostList.add(costItem);
            costGold += gold;
            costStr.append(costId).append(",");
        }
        if (costInfo.isEmpty()) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR, "可被消耗为空:");
            return;
        }
        // 扣除金币
        boolean isEnough = manager.removeItemsByTemplateIdWithCheck(
                MapUtil.of(ItemConstantId.GOLD, costGold), true, EReason.EQUIP);
        if (!isEnough) {
            // 所需消耗道具不足
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "消耗资源不足");
            return;
        }
        // 移除质点
        boolean code = manager.removeItemsByIds(costInfo, true, EReason.EQUIP);
        if (!code) {
            // 所需消耗道具不足
            MessageUtils.throwCondtionError(GameErrorCode.ITEM_NOT_ENOUGH, "扣除失败");
            return;
        }
        sendEquipmentInfo(player, (EquipItem) item,
                org.game.protobuf.s2c.S2CShareMsg.ChangeType.UPDATE);
        EquipItem equipItem = (EquipItem) item;
        if (equipItem.getHeroId() != 0) {
            Hero hero = player.getHeroManager().getHero(equipItem.getHeroId());
            if (hero != null) {
                hero.equipAndTakeOff();
                // 发送英雄信息
                player.getHeroManager().sendHeroInfoSingle(hero.getCid());
            }
        }
        org.game.protobuf.s2c.S2CEquipmentMsg.UpgradeMsg.Builder builder =
                org.game.protobuf.s2c.S2CEquipmentMsg.UpgradeMsg.newBuilder();
        builder.setSuccess(true);
        MessageUtils.send(player, builder);

        Map<String, Object> in = Maps.newHashMap();
        in.put(EventConditionKey.CONDITION_TYPE, EventConditionType.EQUIP_UPGRADE);
        player._fireEvent(in, EEventType.EQUIP.value());

    }

    /**
     * 根据权重随机获得新的Attribute（从被消耗的灵装的特殊属性里筛选）
     */
    private Map<Integer, EquipSpecialAttr> getNewAttr(EquipItem oldItem) {
        int totalWeight = 0;
        List<EquipmentRandomCfgBean> cfgList = Lists.newArrayList();
        for (EquipSpecialAttr special : oldItem.getSpecialAttrList()) {
            EquipmentRandomCfgBean cfg =
                    GameDataManager.getEquipmentRandomCfgBean(special.getTemplateId());
            cfgList.add(cfg);
            totalWeight += cfg.getWeight();
        }

        int randomWeight = RandomUtil.randomInt(totalWeight);
        int weightSum = 0;

        Map<Integer, EquipSpecialAttr> newAttr = new ConcurrentHashMap<Integer, EquipSpecialAttr>();
        for (int i = 0; i < cfgList.size(); i++) {
            EquipmentRandomCfgBean cfg = cfgList.get(i);
            weightSum += cfg.getWeight();
            if (randomWeight <= weightSum) {
                // newAttr.addAll(oldItem.getSpecialAttrList().get(i));
                newAttr.put(i, oldItem.getSpecialAttrList().get(i).copy());
                break;
            }
        }
        return newAttr;
    }

    @SuppressWarnings("unchecked")
    public Map<Integer, Integer> getCoumse(JSONObject coumseJson, int star) {
        try {
            JSONObject object = (JSONObject) coumseJson.get(star);
            Map<Integer, Integer> itemMap = JSONObject.toJavaObject(object, Map.class);
            return itemMap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 计算被消耗的灵装提供的经验
     */
    private int getEquipmentExp(EquipItem equipment, int star) {
        EquipmentGrowthCfgBean growthCfg = GameDataManager.getEquipmentGrowthCfgBean(star);

        int growthExp = 0;
        if (equipment.getLevel() > 1) {

            for (int i = 0; i < growthCfg.getNeedExp().length; i++) {
                if (i >= equipment.getLevel() - 1)
                    break;
                growthExp += (int) growthCfg.getNeedExp()[i];
            }
        }
        int exp = GameDataManager.getEquipmentCfgBean(equipment.getTemplateId()).getExp()
                + equipment.getExp();
        return exp + growthExp;
    }

    /**
     * 灵装增加经验
     *
     * @param equipmentVO 灵装
     * @param gainExp 获得的经验
     * @param expList 灵装升级经验列表
     */
    private void addEquipmentExp(EquipItem equipItem, int gainExp, int[] expList) {

        int maxLevel = GameDataManager.getEquipmentCfgBean(equipItem.getTemplateId()).getMaxLevel();
        while (gainExp > 0 && equipItem.getLevel() < expList.length
                && equipItem.getLevel() <= maxLevel) {
            int upgradeExp = expList[equipItem.getLevel() - 1] - equipItem.getExp();
            if (gainExp >= upgradeExp) {
                equipItem.setLevel(equipItem.getLevel() + 1);
                equipItem.setExp(0);
                gainExp -= upgradeExp;
            } else {
                equipItem.addExp(gainExp);
                gainExp = 0;
            }
        }
    }

    /**
     * 发送灵装信息到前台
     * 
     * @param player 玩家
     * @param equipment 灵装
     */
    private void sendEquipmentInfo(Player player, EquipItem equipment, ChangeType type) {
        org.game.protobuf.s2c.S2CItemMsg.ItemList.Builder builder =
                org.game.protobuf.s2c.S2CItemMsg.ItemList.newBuilder();
        ItemMsgBuilder.packageItemInfo(type, builder, equipment);
        MessageUtils.send(player, builder);
    }

    @Override
    protected void takeOff(Player player, Hero hero, int position) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.EQUIP_TAKE)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:EquipTakeOff");
        }
        HeroEquip heroEquip = hero.getEquipByPos(position);
        EquipItem oldEquipment = null;
        if (heroEquip != null) {
            oldEquipment = heroEquip.getEquipItem();
            oldEquipment.takeOff();
            hero.getHeroEquip().remove(position);
        }

        // 发送英雄信息
        player.getHeroManager().sendHeroInfoSingle(hero.getCid());
        hero.equipAndTakeOff();
        sendEquipmentInfo(player, oldEquipment,
                org.game.protobuf.s2c.S2CShareMsg.ChangeType.UPDATE);

        MessageUtils.send(player,
                EquipmentMsgBuilder.getTakeOffMsg(oldEquipment, hero).toBuilder());
    }


    @Override
    protected void equip(Player player, Hero hero, EquipItem equipment, int position) {
        // 功能是否关闭
        if (!FunctionSwitchService.getInstance().isOpenFunction(EFunctionType.EQUIP_TAKE)) {
            MessageUtils.throwCondtionError(GameErrorCode.FUNCTION_NOT_OPEN,
                    "FUNCTION_NOT_OPEN:EquipTakeOn");
        }
        HeroEquip heroEquip = hero.getEquipByPos(position);
        EquipItem oldEquipment = null;
        if (heroEquip != null) {
            oldEquipment = heroEquip.getEquipItem();
        }
        // 检测负载
        boolean code = checkHeroCost(hero, equipment, oldEquipment);
        if (code) {
            MessageUtils.throwCondtionError(GameErrorCode.COST_OVERLOAD, "cost过载，无法装备");
            return;
        }
        if (oldEquipment != null) {
            oldEquipment.takeOff();
            hero.getHeroEquip().remove(position);
        }
        Hero oldHero = null;
        int oldPostion = equipment.getPosition();
        int oldHeroId = equipment.getHeroId();
        if (oldPostion != 0 && oldHeroId != 0) {
            oldHero = player.getHeroManager().getHero(equipment.getHeroId());
        }

        equipment.equip(hero.getCid(), position);
        HeroEquip newHeroEquip = new HeroEquip();
        newHeroEquip.create(equipment.getId());
        newHeroEquip.init(player);

        hero.putEquip(position, newHeroEquip);
        hero.equipAndTakeOff();
        // 发送英雄信息
        player.getHeroManager().sendHeroInfoSingle(hero.getCid());
        if (oldHero != null) {
            oldHero.removeEquip(oldPostion);
            oldHero.equipAndTakeOff();
            player.getHeroManager().sendHeroInfoSingle(oldHero.getCid());
        }
        MessageUtils.send(player,
                EquipmentMsgBuilder.getEquipMsg(equipment, oldEquipment).toBuilder());
    }


    /**
     * 检查hero负载
     * 
     * @param hero 英雄
     * @param newEquipment 新装备
     * @param oldEquipment 旧装备
     */
    public boolean checkHeroCost(Hero hero, EquipItem newEquipment, EquipItem oldEquipment) {
        int totalCost = hero.getPropertyValue(EPropertyType.BURDEN);
        EquipmentCfgBean cfgBean =
                GameDataManager.getEquipmentCfgBean(newEquipment.getTemplateId());
        int afterCost = cfgBean.getCost();

        for (HeroEquip heroEquip : hero.getHeroEquip().values()) {
            if (oldEquipment != null && heroEquip.getEquipId() == oldEquipment.getId()) {
                continue;
            }
            cfgBean = GameDataManager.getEquipmentCfgBean(heroEquip.getEquipItem().getTemplateId());
            afterCost += cfgBean.getCost();
        }
        return afterCost > totalCost;
    }

    @Override
    protected void lock(Player player, EquipItem equipItem) {
        boolean isLock = equipItem.getLock();
        // boolean finalLock = isLock == true ? false:true;
        // 如果已经被锁定,则解锁
        if (isLock) {
            equipItem.setLock(false);
        } else {
            equipItem.setLock(true);

        }
        sendEquipmentInfo(player, equipItem, org.game.protobuf.s2c.S2CShareMsg.ChangeType.UPDATE);

        org.game.protobuf.s2c.S2CEquipmentMsg.LockMsg.Builder builder =
                org.game.protobuf.s2c.S2CEquipmentMsg.LockMsg.newBuilder();
        builder.setIsLock(equipItem.getLock());
        builder.setSuccess(true);
        MessageUtils.send(player, builder);
    }
}
