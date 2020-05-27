package redis.base;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.Tuple;
import utils.ExceptionEx;
import utils.GsonUtils;

/**
 * Redis 操作类 简单封装
 */
public class ShardedRedisOper extends RedisOper{
    private static final Logger LOGGER = Logger.getLogger(ShardedRedisOper.class);
   
    private ShardedJedisPool shardedJedisPool;

    public ShardedRedisOper(ShardedJedisPool shardedJedisPool) {
        super(null);
        this.shardedJedisPool = shardedJedisPool;
    }

    public <T> T getObjectByHash(String hashName, Object key, Class<T> clzss) {
        try (ShardedJedis shardedjedis = shardedJedisPool.getResource()) {
            Jedis jedis = shardedjedis.getShard(hashName);
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
        try (ShardedJedis shardedjedis = shardedJedisPool.getResource()) {
            Jedis jedis = shardedjedis.getShard(hashName);
            long value = jedis.hdel(hashName, String.valueOf(key));
            return value;
        } catch (Exception e) {
            LOGGER.info(ExceptionEx.e2s(e));
        }
        return -1;
    }

    public <T> HashMap<Integer, T> getAllByHash(String hashName, Class<T> clzss) {
        try (ShardedJedis shardedjedis = shardedJedisPool.getResource()) {
            Jedis jedis = shardedjedis.getShard(hashName);
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
        try (ShardedJedis shardedjedis = shardedJedisPool.getResource()) {
            Jedis jedis = shardedjedis.getShard(hashName);
            String content = GsonUtils.toJson(val);
            // String content = JSONObject.toJSONString();
            return jedis.hset(hashName, String.valueOf(key), content);
        } catch (Exception e) {
            LOGGER.info(ExceptionEx.e2s(e));
        }

        return 0;
    }

    public Object execute(RedisCmd cmd, String key, Object... params) {
        try (ShardedJedis shardedjedis = shardedJedisPool.getResource()) {
            Jedis jedis = shardedjedis.getShard(key);
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
                    return jedis.zadd(key, (Double) params[0], (String) params[1]);
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

        Collection<Jedis> jedis = shardedJedisPool.getResource().getAllShards();
        for (Jedis _jedis : jedis) {
            if (keySet == null)
                keySet = _jedis.keys(cmd);
            else
                keySet.addAll(_jedis.keys(cmd));
        }
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

    public List<String> scan(Jedis jedis, String cursor, ScanParams params) {
        ScanResult<String> result = jedis.scan(cursor, params);
        return result.getResult();
    }
}
