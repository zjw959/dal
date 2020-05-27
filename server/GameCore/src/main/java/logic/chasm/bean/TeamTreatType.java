package logic.chasm.bean;

public enum TeamTreatType {
    PROMOTE(1), // 任命队长
    KICK(2); // 踢出队伍

    private int type;

    private TeamTreatType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static TeamTreatType getTeamType(int type) {
        for (TeamTreatType t : TeamTreatType.values()) {
            if (t.compare(type)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown TeamTreatType value:" + type);
    }

    public boolean compare(int type) {
        return this.type == type;
    }
}
