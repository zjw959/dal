package redis;

import java.util.List;

import script.IScript;

public abstract class IRedisOperScript implements IScript {
    public abstract List<String> match(String queueKey, long endTime, int size, String[] sha) throws Exception;
}
