package redis.base;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
import utils.ExceptionEx;
import utils.GsonUtils;

/**
 * Redis 操作类 简单封装
 */
public class RedisOper {
    private transient static final Logger LOGGER = Logger.getLogger(RedisOper.class);
    // 玩家的队伍id
    public static final String TEAM_ID_PREFIX = "teamid::";
    // 队伍操作时间
    public static final String TEAM_TIME_PREFIX = "teamtime::";
    // 队伍信息
    public static final String TEAM_PREFIX = "team::";
    // 单人组队申请人信息
    public static final String TEAM_MATCH_PREFIX = "match::";
    // 单人组队申请列表
    public static final String TEAM_DUNGEON_PREFIX = "dungeoncid::";
    // 创建队伍申请列表
    public static final String TEAM_CREATE_PREFIX = "createteam";
    // 创建队伍申请人信息
    public static final String TEAM_LEADER_PREFIX = "leader::";

    private JedisPool jedisPool;

    public RedisOper(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public <T> T getObjectByHash(String hashName, Object key, Class<T> clzss) {
        try (Jedis jedis = jedisPool.getResource()) {
            String value = jedis.hget(hashName, String.valueOf(key));
            if (value == null || value.isEmpty())
                return null;
            return GsonUtils.fromJson(value, clzss);

            // return (T) JSONObject.parseObject(value, clzss);
        } catch (Exception e) {
            LOGGER.info(ExceptionEx.e2s(e));
        }
        return null;
    }

    public long removeByHash(String hashName, Object key) {
        try (Jedis jedis = jedisPool.getResource()) {
            long value = jedis.hdel(hashName, String.valueOf(key));
            return value;
        } catch (Exception e) {
            LOGGER.info(ExceptionEx.e2s(e));
        }
        return -1;
    }

    public <T> HashMap<Integer, T> getAllByHash(String hashName, Class<T> clzss) {
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> map = jedis.hgetAll(hashName);
            HashMap<Integer, T> retMap = new HashMap<Integer, T>(map.size());
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String val = map.get(key);
                retMap.put(Integer.parseInt(key), (T) GsonUtils.fromJson(val, clzss));
            }
            return retMap;
        } catch (Exception e) {
            LOGGER.info(ExceptionEx.e2s(e));
        }
        return null;
    }


    public long setObjectByHash(String hashName, Object key, Object val) {
        try (Jedis jedis = jedisPool.getResource()) {
            String content = GsonUtils.toJson(val);
            // String content = JSONObject.toJSONString();
            return jedis.hset(hashName, String.valueOf(key), content);
        } catch (Exception e) {
            LOGGER.info(ExceptionEx.e2s(e));
        }

        return 0;
    }

    public Object execute(RedisCmd cmd, String key, Object... params) {
        try (Jedis jedis = jedisPool.getResource()) {
            switch (cmd) {
                case dbsize:
                    return jedis.dbSize();
                case del:
                    return jedis.del(key);
                case zrangeWithScores:
                    return zrangeWithScores(jedis, key, "asc", params);
                case zrevrangeWithScores:
                    return zrangeWithScores(jedis, key, "desc", params);
                case zremArr:
                    return jedis.zrem(key, (String[]) params);
                case zrem:
                    return jedis.zrem(key, (String) params[0]);
                case zremrangebyrank:
                    return jedis.zremrangeByRank(key, (long) params[0], (long) params[1]);
                case zrevrange:
                    return jedis.zrevrange(key, (long) params[0], (long) params[1]);
                case zsize:
                case zcard:
                    return jedis.zcard(key);
                case zcount:// 有序列表对应score区间内，有多少个元素
                    return jedis.zcount(key, (double) params[0], (double) params[1]);
                case zrevrank:
                    return jedis.zrevrank(key, (String) params[0]);
                case zrank:
                    return jedis.zrank(key, (String) params[0]);
                case zadd:
                    return jedis.zadd(key, (double) params[0], (String) params[1]);
                case zadds:
                    return jedis.zadd(key, (Map<String, Double>) params[0]);
                case hset:
                    return jedis.hset(key, (String) params[0], (String) params[1]);
                case hget:
                    return jedis.hget(key, (String) params[0]);
                case hmset:
                    return jedis.hmset(key, (Map<String, String>) params[0]);
                case hmsetJavaObj:
                    // return jedis.hmset(key, Util.toMapKeyValueString(
                    // (Map<String, String>) JSONObject.toJSON(params[0])));
                    return "";
                case hvals:
                    return jedis.hvals(key);
                case hkeys:
                    return jedis.hkeys(key);
                case hgetAll:
                    return jedis.hgetAll(key);
                case hgetAllJavaObj:
                    // return Util.parseMaptoJavaObject((Class) params[0], jedis.hgetAll(key));
                    return "";
                case hdel:
                    return jedis.hdel(key, (String) params[0]);
                case exists:
                    return jedis.exists(key);
                case lpush:
                    return jedis.lpush(key, (String) params[0]);
                case lsize:
                case lget:
                    return jedis.lrange(key, 0, jedis.llen(key));
                case lrem:
                    return jedis.lrem(key, 0L, (String) params[0]);
                case lset:
                    return jedis.lset(key, (long) params[0], (String) params[1]);
                case sadd:
                    return jedis.sadd(key, (String) params[0]);
                case smimber:
                    return jedis.sismember(key, (String) params[0]);
                case smembers:
                    return jedis.smembers(key);
                case srem:
                    return jedis.srem(key, (String) params[0]);
                case set:
                    return jedis.set(key, (String) params[0]);
                case get:
                    return jedis.get(key);
                case incr:
                    return jedis.incr(key);
                case keys:
                    return keys(key);
                case eval:
                    String _script = (String) params[0];
                    int _keyCount = Integer.valueOf((String) params[1]);
                    String[] _params = new String[params.length - 2];
                    System.arraycopy(params, 2, _params, 0, params.length - 2);
                    return jedis.eval(_script, _keyCount, _params);
                case evalsha:
                    String _sha1 = (String) params[0];
                    int _keyCount2 = Integer.valueOf((String) params[1]);
                    String[] _params2 = new String[params.length - 2];
                    System.arraycopy(params, 2, _params2, 0, params.length - 2);
                    return jedis.evalsha(_sha1, _keyCount2, _params2);
                case scriptload:
                    return jedis.scriptLoad((String) params[0]);
                case scriptflush:
                    return jedis.scriptFlush();
                case scriptexists:
                    String __sha1 = (String) params[0];
                    return jedis.scriptExists(__sha1);
                case expire:
                    int expireTime = (int) params[0];
                    return jedis.expire(key, expireTime);
                default:
                    LOGGER.error("unkown cmd:" + cmd);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        return null;
    }

    private Object keys(String key) {
        String cmd = "*" + key + "*";
        Set<String> keySet = null;

        Jedis jedis = jedisPool.getResource();
        keySet = jedis.keys(cmd);
        return keySet;
    }


    private Object zrangeWithScores(Jedis jedis, String key, String sort, Object... params) {
        ArrayList<Object[]> list = null;
        Set<Tuple> tuples = null;
        if (sort.equalsIgnoreCase("asc"))
            tuples = jedis.zrangeWithScores(key, (long) params[0], (long) params[1]);
        else if (sort.equalsIgnoreCase("desc"))
            tuples = jedis.zrevrangeWithScores(key, (long) params[0], (long) params[1]);
        if (tuples.size() > 0)
            list = new ArrayList<Object[]>();

        for (Tuple tuple : tuples)
            list.add(new Object[] {tuple.getElement(), tuple.getScore()});
        return list;
    }


    public long dbsize(Jedis jedis) {
        return jedis.dbSize();
    }
}
