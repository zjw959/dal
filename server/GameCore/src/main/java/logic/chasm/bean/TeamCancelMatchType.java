package logic.chasm.bean;

public enum TeamCancelMatchType {
    // 1:主动取消 2:匹配超时 3:取消匹配失败

    CANCEL(1), TIMEOUT(2), CANCEL_FAIL(3);

    private int type;

    private TeamCancelMatchType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public static TeamCancelMatchType getTeamCancelMatchType(int type) {
        for (TeamCancelMatchType t : TeamCancelMatchType.values()) {
            if (t.compare(type)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown TeamCancelMatchType value:" + type);
    }

    public boolean compare(int type) {
        return this.type == type;
    }
}
