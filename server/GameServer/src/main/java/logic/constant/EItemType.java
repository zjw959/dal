package logic.constant;

import logic.item.bean.BasicItem;
import logic.item.bean.DressItem;
import logic.item.bean.EquipItem;
import logic.item.bean.Item;
import logic.item.bean.RoomItem;
import logic.item.bean.SkinItem;

/**
 * 道具类型
 * 
 * 道具类型,道具对应的脚本id,道具id(如果只有一个道具)
 */
public enum EItemType {

    /** 英雄 */
    HERO(1, EScriptIdDefine.ITEM_BASIC.Value()),
    /** 灵装(质点) */
    EQUIP(2, EScriptIdDefine.ITEM_BASIC.Value(), EquipItem.class),
    /** 天使 */
    ANGEL(3, EScriptIdDefine.ITEM_BASIC.Value()),
    /** 时装 */
    DRESS(4, EScriptIdDefine.ITEM_BASIC.Value(), DressItem.class),
    /** 代币 */
    TOKEN(5, EScriptIdDefine.ITEM_TOKEN.Value()),
    /** 材料 */
    MATERIALS(6, EScriptIdDefine.ITEM_BASIC.Value()),
    /** 礼包 */
    PACKAGE(7, EScriptIdDefine.ITEM_PACKAGE.Value()),
    /** 礼物 */
    GIFT(8, EScriptIdDefine.ITEM_PACKAGE.Value()),
    /** 家具 */
    FURNITURE(9, EScriptIdDefine.ITEM_BASIC.Value()),
    /** CG */
    CG(10, EScriptIdDefine.ITEM_BASIC.Value()),
    /** 皮肤 */
    SKIN(11, EScriptIdDefine.ITEM_BASIC.Value(), SkinItem.class),
    /** 勋章 */
    MEDAL(12, EScriptIdDefine.ITEM_BASIC.Value()),
    /** 房间 */
    ROOM(13, EScriptIdDefine.ITEM_BASIC.Value(), RoomItem.class),
    /** 外传约会 */
    OUTSIDE_DATING(14, EScriptIdDefine.ITEM_BASIC.Value()),
    /** 掉落 */
    DROPS(21, EScriptIdDefine.ITEM_DROP.Value()),
    /** 兼职奖励 **/
    PTJOBAWAR(22, EScriptIdDefine.ITEM_DROP.Value());

    private int value;
    private int scriptId;
    private Class<? extends Item> clazz;
    private int itemTempId;

    EItemType(int value, int scriptId) {
        _init(value, scriptId, BasicItem.class, -999);
    }

    EItemType(int value, int scriptId, int itemTempId) {
        _init(value, scriptId, BasicItem.class, itemTempId);
    }

    EItemType(int value, int scriptId, Class<? extends BasicItem> clazz) {
        _init(value, scriptId, clazz, -999);
    }

    private void _init(int value, int scriptId, Class<? extends BasicItem> clazz, int itemTempId) {
        this.value = value;
        if (scriptId > 199 || scriptId < 99) {
            throw new IllegalArgumentException("invalid script id  : " + scriptId);
        }
        this.scriptId = scriptId;
        this.clazz = clazz;
        this.itemTempId = itemTempId;
    }

    public int getValue() {
        return value;
    }

    public int getScriptId() {
        return scriptId;
    }

    public int getItemTempId() {
        if (this.itemTempId == -999) {
            throw new UnsupportedOperationException("this itemType not have default itemid");
        }
        return itemTempId;
    }

    public Class<? extends Item> getClazz() {
        return clazz;
    }

    public static int scriptId(int typeValue) {
        for (EItemType type : EItemType.values()) {
            if (type.value == typeValue) {
                return type.scriptId;
            }
        }
        throw new IllegalArgumentException("undefine item type : " + typeValue);
    }

    public static EItemType itemType(int typeValue, int itemId) {
        for (EItemType type : EItemType.values()) {
            if (type.value == typeValue) {
                return type;
            }
        }
        throw new IllegalArgumentException(ConstDefine.LOG_ERROR_CONFIG_PREFIX
                + "undefine item type : " + typeValue + " itemId:" + itemId);
    }
}
