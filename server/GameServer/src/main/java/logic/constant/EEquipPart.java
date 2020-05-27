package logic.constant;

public enum EEquipPart {
    /** 帽子 */
    HAT(1),
    /** 饰品 */
    ORNAMENT(2),
    /** 衣服 */
    CLOTHES(3),
    /** 鞋子 */
    SHOE(4),;

    EEquipPart(int value) {
        this.value = value;
    }

    private int value;

    public int value() {
        return this.value;
    }
}
