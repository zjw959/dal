package logic.constant;

public enum EQuality {
    WHITE(1), GREEN(2), BLUE(3), PURPLE(4), ORANGE(5), RED(6);
    private final int value;

    EQuality(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static EQuality valueOfType(int value) {
        for (EQuality eQuality : values()) {
            if (eQuality.value == value) {
                return eQuality;
            }
        }
        throw new RuntimeException("can not find quality type");
    }

    public boolean compare(int value) {
        return this.value == value;
    }
}
