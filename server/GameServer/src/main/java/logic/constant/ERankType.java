package logic.constant;

public enum ERankType {
    UNKNOWN(-1), LEVEL(1), FIGHT_POWER(2), DUNGEON_STAR(4),;

    private final int val;

    ERankType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }

    public static ERankType getType(int type) {
        for (ERankType rt : ERankType.values()) {
            if (rt.val == type) {
                return rt;
            }
        }
        return UNKNOWN;
    }


}
