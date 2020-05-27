package logic.constant;

public enum EScriptIdDefine {
    SHUTDOWN_SCRIPT(1),
    REDIS_OPERATE_SCRIPT(2),
    
    HTTPEXECUTE_SCRIPTID(10),

    /** 队伍脚本 */
    TEAM_SCRIPT(100711),
    /** 深渊副本 */
    CHASM_DUNGEON_SCRIPT(100712);
    
    private final int value;

    private static class Counter {
        private static int nextValue = 0;
    }

    EScriptIdDefine() {
        this(++Counter.nextValue);
    }

    EScriptIdDefine(int value) {
        this.value = value;
        Counter.nextValue = value;
    }

    public int Value() {
        return this.value;
    }
}
