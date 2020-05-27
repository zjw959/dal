package logic.constant;

/**
 * 服务器状态
 */
public enum EServerStatus {
    /**
     * 启动中
     */
    STARTING(1),
    /**
     * 运行中
     */
    RUNNING(2),
    /**
     * 维护中
     */
    MAINTAINING(3),
    /**
     * 停服中
     */
    STOPPING(4);

    private final int val;

    EServerStatus(int val) {
        this.val = val;
    }

    public int getValue() {
        return val;
    }

    public static EServerStatus getServerStatus(int status) {
        for (EServerStatus type : EServerStatus.values()) {
            if (type.val == status) {
                return type;
            }
        }
        return null;
    }
}
