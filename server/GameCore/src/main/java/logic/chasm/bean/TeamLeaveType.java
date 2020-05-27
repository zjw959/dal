package logic.chasm.bean;

public enum TeamLeaveType {
    ACTIVE(1), // 主动退出
    KICK(2), // 队长踢出
    TIME_OUT(3); //队伍超时

    private int type;

    private TeamLeaveType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static TeamLeaveType getTeamLeaveType(int type) {
        for (TeamLeaveType t : TeamLeaveType.values()) {
            if (t.compare(type)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown TeamLeaveType value:" + type);
    }

    public boolean compare(int type) {
        return this.type == type;
    }
}
