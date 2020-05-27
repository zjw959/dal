package logic.msgBuilder;

import java.util.List;

import logic.character.bean.Player;
import logic.constant.EItemType;
import logic.equip.bean.EquipSpecialAttr;
import logic.item.bean.DressItem;
import logic.item.bean.EquipItem;
import logic.item.bean.Item;
import logic.support.MessageUtils;

import org.game.protobuf.s2c.S2CItemMsg;
import org.game.protobuf.s2c.S2CItemMsg.DressInfo;
import org.game.protobuf.s2c.S2CItemMsg.ItemInfo;
import org.game.protobuf.s2c.S2CItemMsg.ItemList;
import org.game.protobuf.s2c.S2CShareMsg.ChangeType;
import org.game.protobuf.s2c.S2CShareMsg.RewardsMsg;

import data.GameDataManager;
import data.bean.BaseGoods;
import data.bean.ItemTimeCfgBean;

/**
 * 
 * @Description 道具消息构建器
 * @author LiuJiang
 * @date 2018年6月4日 上午11:54:13
 *
 */
public class ItemMsgBuilder {

    /**
     * 创建单个道具信息
     */
    public static ItemInfo createItemInfo(ChangeType type, Item item) {
        ItemInfo.Builder builder = ItemInfo.newBuilder();
        builder.setCt(type);
        builder.setId(Long.toString(item.getId()));
        builder.setNum(item.getNum());
        int cid = item.getTemplateId();
        BaseGoods goods = GameDataManager.getBaseGoods(cid);
        if (goods != null && goods instanceof ItemTimeCfgBean) {
            ItemTimeCfgBean bean = (ItemTimeCfgBean) goods;
            cid = bean.getItemId();
        }
        builder.setCid(cid);
        if (item.getDeadTime() > 0) {
            builder.setOutTime((int) (item.getDeadTime() / 1000));
        }
        return builder.build();
    }

    /**
     * 创建单个时装信息
     */
    public static DressInfo createDressInfo(ChangeType type, DressItem dressItem) {
        DressInfo.Builder builder = DressInfo.newBuilder();
        if (dressItem.getDeadTime() > 0) {
            builder.setOutTime((int) (dressItem.getDeadTime() / 1000));
        }
        builder.setCt(type);
        builder.setId(Long.toString(dressItem.getId()));
        builder.setCid(dressItem.getTemplateId());
        builder.setRoleId(String.valueOf(dressItem.getRoleId()));
        // 属性
        return builder.build();
    }

    /**
     * 创建道具组信息
     */
    public static ItemList.Builder createItemList(ChangeType type, List<Item> items) {
        ItemList.Builder builder = ItemList.newBuilder();
        for (Item item : items) {
            packageItemInfo(type, builder, item);
        }
        // 属性
        return builder;
    }

    /**
     * 封装item信息
     */
    public static void packageItemInfo(ChangeType type, ItemList.Builder builder, Item item) {
        BaseGoods bean = GameDataManager.getBaseGoods(item.getTemplateId());
        if (bean.getSuperType() == EItemType.DRESS.getValue()) {
            builder.addDresss(createDressInfo(type, (DressItem) item));
        } else if (bean.getSuperType() == EItemType.EQUIP.getValue()) {
            builder.addEquipments(createEquipmentInfo(type, (EquipItem) item));
        } else {
            builder.addItems(createItemInfo(type, item));
        }
    }

    /**
     * 灵装
     */
    public static S2CItemMsg.EquipmentInfo createEquipmentInfo(ChangeType type, EquipItem eq) {
        S2CItemMsg.EquipmentInfo.Builder builder = S2CItemMsg.EquipmentInfo.newBuilder();
        builder.setCt(type).setId(Long.toString(eq.getId())).setCid(eq.getTemplateId())
                .setExp(eq.getExp()).setHeroId(Long.toString(eq.getHeroId()))
                .setPosition(eq.getPosition()).setLevel(eq.getLevel()).setIsLock(eq.getLock());
        // .setPosition(eq.getPosition()).setLevel(eq.getLevel());
        if (eq.getDeadTime() > 0) {
            builder.setOutTime((int) (eq.getDeadTime() / 1000));
        }
        // 特殊属性
        List<EquipSpecialAttr> attrList = eq.getSpecialAttrList();
        if (attrList != null) {
            for (int i = 0; i < attrList.size(); i++) {
                EquipSpecialAttr eAttr = attrList.get(i);
                S2CItemMsg.SpecialAttr.Builder attrBuilder = S2CItemMsg.SpecialAttr.newBuilder();
                attrBuilder.setCid(eAttr.getTemplateId()).setValue(eAttr.getValue())
                        .setIndex(eAttr.getIndex());
                builder.addAttrs(attrBuilder);
            }
        }
        // 临时属性，跟客户端确认没用到下面3条数据
        // EquipSpecialAttr tempSpecialAttr = eq.getTempSpecialAttr();
        // if (tempSpecialAttr != null) {
        // builder.setOldAttrIndex(tempSpecialAttr.getIndex());
        // builder.setNewAttrType(tempSpecialAttr.getType());
        // builder.setNewAttrValue(tempSpecialAttr.getValue());
        // }
        return builder.build();
    }

    /**
     * 创建奖励消息
     *
     * @param id
     * @param val
     * @return
     */
    public static RewardsMsg createRewardsMsg(int id, int val) {
        return RewardsMsg.newBuilder().setId(id).setNum(val).build();
    }

    // 发送道具信息
    public static void sendItemMsg(Player player, ChangeType type, Item... items) {
        ItemList.Builder builder = ItemList.newBuilder();
        for (Item item : items) {
            if (item == null) {
                continue;
            }
            packageItemInfo(type, builder, item);
        }
        MessageUtils.send(player, builder);
    }

    public static void addItemMsg(ItemList.Builder itemChange, ChangeType type, Item... items) {
        for (Item item : items) {
            packageItemInfo(type, itemChange, item);
        }
    }

}
