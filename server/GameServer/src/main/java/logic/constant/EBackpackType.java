package logic.constant;

/**
 * 背包类型
 */
public enum EBackpackType {
    /** 道具背包 */
    BAG_ITEM(1),
    /** 宝物背包 */
    BAG_GEM(2),
    /** 神兵背包 */
    BAG_ARMAMENT(3),
    /** 武将碎片 */
    BAG_HERO_FRAGMENT(4),
    /** 装备背包 */
    BAG_EQUIPMENT(5),;

    private int value;

    EBackpackType(int value) {
        this.value = value;
    }

    public static EBackpackType itemType(int typeValue) {
        for (EBackpackType type : EBackpackType.values()) {
            if (type.value == typeValue) {
                return type;
            }
        }
        throw new IllegalArgumentException("undefine item bag type : " + typeValue);
    }

    public static boolean contains(int typeValue) {
        for (EBackpackType type : EBackpackType.values()) {
            if (type.value == typeValue) {
                return true;
            }
        }
        return false;
    }

}
