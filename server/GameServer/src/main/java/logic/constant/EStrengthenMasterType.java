package logic.constant;

/**
 * 强化大师类型
 */
public enum EStrengthenMasterType {
    EQUIP_STRENGTHEN(1), TREASURE_STRENGTHEN(2), EQUIP_REFINE(3), TREASURE_REFINE(4), EQUIP_AWAKE(
            5), TREASURE_AWAKE(6),;
    private final int value;

    EStrengthenMasterType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public boolean compare(int value) {
        return this.value == value;
    }
}
