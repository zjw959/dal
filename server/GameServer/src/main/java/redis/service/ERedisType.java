package redis.service;

public enum ERedisType {
    VIEW(1, ESpringConextType.PlAYER, "jedisPoolView"), SNAP(2, ESpringConextType.PlAYER,
            "jedisPoolSnap"), FRIEND(3, ESpringConextType.FRIEND), UC(4,
                    ESpringConextType.UC), FIGHT(5, ESpringConextType.FIGHT), CHAT(6,
                            ESpringConextType.OTHER, "jedisPoolChat"), LEVEL(7,
                                    ESpringConextType.OTHER, "jedisPoolLevel"), COMMENT(8,
                                            ESpringConextType.OTHER_DB, "jedisPoolComment"), ACTIVITY(
                                                    9, ESpringConextType.OTHER_DB, "jedisPoolActivity");

    private int type;
    private String poolName;
    ESpringConextType eSpringConextType;

    ERedisType(int value, ESpringConextType eSpringConextType, String poolName) {
        _init(value, eSpringConextType, poolName);
    }

    ERedisType(int value, ESpringConextType eSpringConextType) {
        _init(value, eSpringConextType, "jedisPool");
    }

    private void _init(int value, ESpringConextType eSpringConextType, String poolName) {
        this.poolName = poolName;
        this.type = value;
        this.eSpringConextType = eSpringConextType;
    }

    public int getType() {
        return type;
    }

    public String getPoolName() {
        return poolName;
    }

    public ESpringConextType getSpringConextType() {
        return eSpringConextType;
    }

    public int getSpringType() {
        return eSpringConextType.getType();
    }

    public static ERedisType getType(int type) {
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
