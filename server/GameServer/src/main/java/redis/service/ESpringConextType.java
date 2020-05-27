package redis.service;

public enum ESpringConextType {
    PlAYER(1, "file:./config/redis_view_context.xml"), FRIEND(2,
            "file:./config/redis_friend_context.xml"), UC(3,
                    "file:./config/redis_location_context.xml"), FIGHT(4,
                            "file:./config/redis_fight_context.xml"), OTHER(5,
                                    "file:./config/redis_other_context.xml"),OTHER_DB(6,
                                            "file:./config/redis_other_db_context.xml");

    private int type;
    private String path;

    ESpringConextType(int value, String springPath) {
        _init(value, springPath);
    }

    private void _init(int value, String springPath) {
        this.type = value;
        this.path = springPath;
    }

    public int getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public static ESpringConextType getType(int type) {
        for (ESpringConextType t : ESpringConextType.values()) {
            if (t.compare(type)) {
                return t;
            }
        }
        throw new IllegalArgumentException("unknown spring type value:" + type);
    }

    public boolean compare(int type) {
        return this.type == type;
    }
}


// class RedisServiceConfig {
// private static final Logger LOGGER = Logger.getLogger(RedisServiceConfig.class);
//
// private static Map<Integer, String> CONFIG = new HashMap();
//
// public static RedisServiceConfig getInstance() {
// return Singleton.INSTANCE.getProcesser();
// }
//
// private RedisServiceConfig() {
// try {
// CONFIG.put(ERedisType.VIEW.getType(), "file:./config/redis_view_context.xml");
// CONFIG.put(ERedisType.FRIEND.getType(), "file:./config/redis_friend_context.xml");
// CONFIG.put(ERedisType.UC.getType(), "file:./config/redis_location_context.xml");
// CONFIG.put(ERedisType.CHAT.getType(), "file:./config/redis_chatroom_context.xml");
// CONFIG.put(ERedisType.FIGHT.getType(), "file:./config/redis_fight_context.xml");
//
//
// Set<Entry<Integer, String>> entries = CONFIG.entrySet();
// for (Entry<Integer, String> entry : entries) {
// new RedisService(entry.getKey(), entry.getValue());
// }
// } catch (Exception e) {
// LOGGER.error(ExceptionEx.e2s(e));
// System.exit(-1);
// }
// }
//
// private enum Singleton {
// INSTANCE;
//
// RedisServiceConfig processer;
//
// private Singleton() {
// processer = new RedisServiceConfig();
// }
//
// RedisServiceConfig getProcesser() {
// return processer;
// }
// }
// }

