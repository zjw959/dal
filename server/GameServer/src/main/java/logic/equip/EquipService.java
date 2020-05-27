package logic.equip;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.RandomUtils;

import com.google.common.collect.Lists;

import data.GameDataManager;
import data.bean.EquipmentCfgBean;
import data.bean.EquipmentRandomCfgBean;
import data.bean.EquipmentSuitCfgBean;
import logic.character.bean.Player;
import logic.constant.EquipmentConstant;
import logic.constant.GameErrorCode;
import logic.equip.bean.EquipSpecialAttr;
import logic.hero.bean.Hero;
import logic.item.bean.EquipItem;
import logic.item.bean.Item;
import logic.support.LogicScriptsUtils;
import logic.support.MessageUtils;

/**
 * 
 * @Description 装备服务类
 * @author LiuJiang
 * @date 2018年6月8日 上午11:51:36
 *
 */
public class EquipService {

    public static EquipService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        EquipService instance;

        private Singleton() {
            instance = new EquipService();
        }

        EquipService getInstance() {
            return instance;
        }
    }

    /**
     * 初始化灵装 随机灵装的特殊属性
     */
    public void initEquip(EquipItem item) {
        EquipmentCfgBean equipmentCfg = GameDataManager.getEquipmentCfgBean(item.getTemplateId());
        List<Integer> attrTypeList = getSpecialAttrIdList(equipmentCfg);
        List<EquipSpecialAttr> attrList = createSpecialAttrMap(equipmentCfg, attrTypeList);
        item.setSpecialAttrList(attrList);
    }


    // /**
    // * 根据权重随机获得新的Attribute（从被消耗的灵装的特殊属性里筛选）
    // */
    // private List<Integer> getNewAttr(EquipmentVO costEquipment) {
    // int totalWeight = 0;
    // List<EquipmentRandomCfg> cfgList = Lists.newArrayList();
    // for (List<Integer> attrInfoList : costEquipment.getSpecialAttrList()) {
    // EquipmentRandomCfg cfg =
    // EquipmentRandomCfgCache.me().getById(attrInfoList.get(EquipmentVO.SPECIAL_ATTR_INDEX_CFG_ID));
    // cfgList.add(cfg);
    // totalWeight += cfg.getWeight();
    // }
    //
    // int randomWeight = RandomUtil.randomInt(totalWeight);
    // int weightSum = 0;
    //
    // List<Integer> newAttr = Lists.newArrayList();
    // for (int i = 0; i < cfgList.size(); i++) {
    // EquipmentRandomCfg cfg = cfgList.get(i);
    // weightSum += cfg.getWeight();
    // if (randomWeight <= weightSum) {
    // newAttr.addAll(costEquipment.getSpecialAttrList().get(i));
    // newAttr.add(i);
    // break;
    // }
    // }
    // return newAttr;
    // }
    //
    /**
     * 获取特殊属性id 随机获取，不重复
     */
    @SuppressWarnings("unchecked")
    private List<Integer> getSpecialAttrIdList(EquipmentCfgBean equipmentCfg) {
        int specialAttrNum = EquipmentConstant.SPECIAL_ATTR_NUM[equipmentCfg.getStar()];
        List<Integer> selectedAttr = new ArrayList<Integer>();
        // 计算特殊属性总权重
        int maxWeight = 0;
        for (Object o : equipmentCfg.getSpecialAttribute().entrySet()) {
            Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>) o;
            maxWeight += entry.getValue();
        }
        for (int i = 0; i < specialAttrNum; i++) {
            int randomWeight = RandomUtils.nextInt(maxWeight);
            int weightSum = 0;
            for (Object e : equipmentCfg.getSpecialAttribute().entrySet()) {
                Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>) e;
                int id = entry.getKey();
                int weight = entry.getValue();
                if (selectedAttr.contains(id))
                    continue;
                weightSum += weight;
                if (weightSum >= randomWeight) {
                    selectedAttr.add(id);
                    maxWeight -= weight;
                    break;
                }
            }
        }
        return selectedAttr;
    }

    /**
     * 生成特殊属性 1.根据权重获取特殊属性 2.在特殊属性取值范围内随机一个值作为属性值
     */
    @SuppressWarnings("unchecked")
    public List<EquipSpecialAttr> createSpecialAttrMap(EquipmentCfgBean equipmentCfg,
            List<Integer> typeList) {
        List<EquipSpecialAttr> result = new ArrayList<EquipSpecialAttr>();
        List<List<Integer>> attrLevelRange = equipmentCfg.getSpecialAttrLevelRange();
        for (int i = 0; i < typeList.size(); i++) {
            int type = typeList.get(i);
            int minLev = attrLevelRange.get(i).get(0);
            int maxLev = attrLevelRange.get(i).get(1);
            List<EquipmentRandomCfgBean> cfgList = getBySuperTypeLevelRange(type, minLev, maxLev);
            int maxWeight = 0;
            for (EquipmentRandomCfgBean erCfg : cfgList) {
                maxWeight += erCfg.getWeight();
            }
            int randomWeight = RandomUtils.nextInt(maxWeight);
            int weightSum = 0;
            for (EquipmentRandomCfgBean erCfg : cfgList) {
                weightSum += erCfg.getWeight();
                if (weightSum >= randomWeight) {
                    int min = erCfg.getAttribute()[0];
                    int max = erCfg.getAttribute()[1];
                    int attrValue = (int) (min + RandomUtils.nextFloat() * (max - min));
                    EquipSpecialAttr attr = new EquipSpecialAttr();
                    attr.setType(type);
                    attr.setValue(attrValue);
                    attr.setTemplateId(erCfg.getId());
                    attr.setIndex(result.size());
                    result.add(attr);
                    break;
                }
            }
        }
        return result;
    }

    //
    // /**
    // * 检查hero负载
    // * @param hero 英雄
    // * @param newEquipment 新装备
    // * @param oldEquipment 旧装备
    // */
    // private void checkHeroCost(Hero hero, EquipmentVO newEquipment, EquipmentVO oldEquipment) {
    // HeroProxy heroExt = hero.getHeroProxy();
    // int totalCost = heroExt.getAttr().get(AttrConstant.ATTR_COST);
    // int afterCost = newEquipment.getCfg().getCost();
    // for (EquipmentVO equipmentVO : heroExt.getEquipment().values()) {
    // if (equipmentVO == oldEquipment) {
    // continue;
    // }
    // afterCost += equipmentVO.getCfg().getCost();
    // }
    // ToolError.isAndTrue(GameErrorCode.COST_OVERLOAD, "cost过载，无法装备", afterCost > totalCost);
    // }
    //
    // /**
    // * 发送灵装信息到前台
    // * @param player 玩家
    // * @param equipment 灵装
    // */
    // private void sendEquipmentInfo(Player player, EquipmentVO equipment) {
    // S2CItemMsg.ItemList.Builder builder = S2CItemMsg.ItemList.newBuilder();
    // ItemMsgBuilder.packageItemInfo(S2CShareMsg.ChangeType.UPDATE, builder,equipment);
    // player.getPlayerProxy().sendMsg(MessageManager.me().create(ItemBO.getItems,
    // ProtoUnit.toByte(builder.build())));
    // }
    //
    // /**
    // * 检查拥有套装
    // * @param coverId
    // */
    // public boolean checkHaveCover(Player player,int coverId){
    // EquipmentSuitCfg suitCfg = EquipmentSuitCfgCache.me().getById(coverId);
    // if (suitCfg != null) {
    // List<EquipmentCfg> equipCfgs = EquipmentCfgCache.me().getBySuit(suitCfg.getId());
    // BackpackVO backpackVO = player.getPlayerProxy().getBackpackByType(BagType.EQUIPMENT);
    // boolean bool = true;
    // for (EquipmentCfg equipmentCfg : equipCfgs) {
    // bool &= (backpackVO.countByCfg(equipmentCfg) > 0);
    // }
    // return bool;
    // }else {
    // return false;
    // }
    // }


    /**
     * 根据类型和等级范围获取配置
     */
    private List<EquipmentRandomCfgBean> getBySuperTypeLevelRange(int superType, int minLev,
            int maxLev) {
        List<EquipmentRandomCfgBean> beans = GameDataManager.getEquipmentRandomCfgBeans();
        List<EquipmentRandomCfgBean> cfgList = new ArrayList<EquipmentRandomCfgBean>();
        for (EquipmentRandomCfgBean bean : beans) {
            if (superType != bean.getSuperType()) {// 排除非指定类型的
                continue;
            }
            if (bean.getLevel() < minLev || bean.getLevel() > maxLev) {// 排除不在等级范围内的
                continue;
            }
            cfgList.add(bean);
        }
        return cfgList;
    }

    /**
     * 质点装备
     */
    public void equip(Player player, String hId, String eqId, int position) {
        int heroId = Integer.parseInt(hId);
        long equipmentId = Long.valueOf(eqId);

        Hero hero = player.getHeroManager().getHero(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.HERO_ID_ERR, "heroId错误, id:" + heroId);
            return;
        }
        Item item = player.getBagManager().getItemOrigin(equipmentId);
        if (item == null) {
            MessageUtils.throwCondtionError(GameErrorCode.EQUIPMENT_ID_ERR,
                    "equipmentId错误:" + equipmentId);
            return;
        }
        EquipItem equipItem = (EquipItem) item;
        // if (equipItem.getPosition() != EquipmentConstant.NO_EQUIP_POSITION) {
        // MessageUtils.throwCondtionError(GameErrorCode.ALREADY_EQUIP, "灵装已被装备:" + equipmentId);
        // return;
        // }
        if (position > EquipmentConstant.EQUIP_POSITION_NUM || position <= 0) {
            MessageUtils.throwCondtionError(GameErrorCode.POSITION_ERR, "装备位置错误:" + equipmentId);
            return;
        }
        LogicScriptsUtils.getIEquipScript().equip(player, hero, equipItem, position);
    }

    /**
     * 质点卸下
     */
    public void takeOff(Player player, String hId, int position) {
        int heroId = Integer.parseInt(hId);

        Hero hero = player.getHeroManager().getHero(heroId);
        if (hero == null) {
            MessageUtils.throwCondtionError(GameErrorCode.HERO_ID_ERR, "heroId错误, id:" + heroId);
            return;
        }
        if (position > EquipmentConstant.EQUIP_POSITION_NUM || position <= 0) {
            MessageUtils.throwCondtionError(GameErrorCode.POSITION_ERR, "装备位置错误:" + position);
            return;
        }
        if (hero.getEquipByPos(position) == null) {
            MessageUtils.throwCondtionError(GameErrorCode.POSITION_NO_EQUIPMENT, "该位置没有装备");
            return;
        }
        LogicScriptsUtils.getIEquipScript().takeOff(player, hero, position);
    }

    /**
     * 装备洗练
     */
    public void changeSpecialAttr(Player player, String eqId, String costId, List<Integer> index,
            List<Integer> costIndex) {
        LogicScriptsUtils.getIEquipScript().changeSpecialAttr(player, eqId, costId, index,
                costIndex);
    }

    /**
     * 特殊属性替换
     */
    public void replaceSpecialAttr(Player player, String equipmentId, boolean isReplace) {
        LogicScriptsUtils.getIEquipScript().replaceSpecialAttr(player, equipmentId, isReplace);
    }

    public void upgrade(Player player, String sourceEquipment, List<String> costEquipmentList) {
        LogicScriptsUtils.getIEquipScript().upgrade(player, sourceEquipment, costEquipmentList);
    }

    public void lockAndUnLock(Player player, String equipmentId) {
        Item item = player.getBagManager().getItemOrigin(Long.valueOf(equipmentId));
        if (item == null) {
            MessageUtils.throwCondtionError(GameErrorCode.CLIENT_PARAM_IS_ERR,
                    "背包内根据id没有找到item, id:" + equipmentId);
            return;
        }
        LogicScriptsUtils.getIEquipScript().lock(player, (EquipItem) item);
    }

    /**
     * 检查拥有套装
     * 
     * @param coverId
     */
    public boolean checkHaveCover(Player player, int coverId) {
        EquipmentSuitCfgBean suitCfg = GameDataManager.getEquipmentSuitCfgBean(coverId);
        if (suitCfg != null) {
            List<EquipmentCfgBean> equipCfgs = Lists.newArrayList();
            List<EquipmentCfgBean> AllequipCfgs = GameDataManager.getEquipmentCfgBeans();
            for (EquipmentCfgBean bean : AllequipCfgs) {
                if (bean.getSuit() == suitCfg.getId()) {
                    equipCfgs.add(bean);
                }
            }
            boolean bool = true;
            for (EquipmentCfgBean equipmentCfg : equipCfgs) {
                List<Item> itemList =
                        player.getBagManager().getItemCopyByTemplateId(equipmentCfg.getId());
                if (itemList.isEmpty()) {
                    bool = false;
                }
                // bool &= (backpackVO.countByCfg(equipmentCfg) > 0);
            }
            return bool;
        } else {
            return false;
        }
    }
}
