package logic.summon;

public enum EHistoryType {
    SUMMON_DIAMOND(1),
    SUMMON_FRIENDSHIP(2),
    SUMMON_COMPOSE(3);
    
    private final int value;

    EHistoryType(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
    
    public static EHistoryType getHistoryType(int value) {
        for (EHistoryType rt : EHistoryType.values()) {
            if (rt.compare(value)) {
                return rt;
            }
        }
        throw new IllegalArgumentException("unknown history type value:" + value);
    }

    public boolean compare(int value) {
        return this.value == value;
    }
}
