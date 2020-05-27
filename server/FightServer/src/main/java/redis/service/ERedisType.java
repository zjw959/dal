package redis.service;

public enum ERedisType {
    Fight(1);

    private int type;
    
    private ERedisType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
    
    public static ERedisType getTeamType(int type) {
        for (ERedisType t : ERedisType.values()) {
            if (t.compare(type)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown redis type value:" + type);
    }

    public boolean compare(int type) {
        return this.type == type;
    }
}
