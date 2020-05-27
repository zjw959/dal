package logic.constant;

/**
 * 属性模块
 */
public enum EPropertyModule {
    /** 基础值 */
    PROPERTY_HERO_BASE(1),
    /** 成长值 */
    PROPERTY_HERO_GROW(2),
    /** 装备值 */
    PROPERTY_HERO_EQUIP(3),
    /** 英雄技能增加的属性 */
    PROPERTY_HERO_SKILL(4),
    /** 结晶 */
    PROPERTY_HERO_CRYSTAL(5);
    
    private final int value;

    EPropertyModule(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static EPropertyModule getEPropertyModule(int value) {
        for (EPropertyModule rt : EPropertyModule.values()) {
            if (rt.compare(value)) {
                return rt;
            }
        }
        throw new IllegalArgumentException("unknown property module value:" + value);
    }

    public boolean compare(int value) {
        return this.value == value;
    }
}
