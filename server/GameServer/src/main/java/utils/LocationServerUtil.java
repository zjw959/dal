package utils;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.service.ERedisType;
import redis.service.RedisServices;

/**
 * 
 * @Description 用户中心 定位服务（定位玩家当前所在游戏服）
 * @author LiuJiang
 * @date 2018年6月26日 下午3:47:53
 *
 */
public class LocationServerUtil {
    private static String locationPrefix = "loc::";

    private transient static final Logger LOGGER = Logger.getLogger(LocationServerUtil.class);


    /**
     * 获取玩家当前所在游戏服
     * 
     * 注意: -1代表未知区域
     */
    public static int getServerId(int playerId) {
        // int serverId = 0;
        // try (ShardedJedis shardedJedis = RedisLocationService.getInstance().getShardedJedis()) {
        // // 从redis获取玩家当前所在游戏服
        // String value = shardedJedis.get(locationPrefix + playerId);
        // if (value != null && !value.isEmpty()) {
        // serverId = Integer.valueOf(value);
        // } else {
        // // redis不存在，则按统一规则获取
        // ServerListManager.getServerIdByPlayerId(playerId);
        // }
        // return serverId;
        // } catch (Exception e) {
        // throw e;
        // }
        int serverId = -1;
        try (Jedis jedis =
                RedisServices.getRedisService(ERedisType.UC.getType()).getJedis()) {
            // 从redis获取玩家当前所在游戏服
            try {
                String value = jedis.hget(locationPrefix + playerId, "serverId");
                if (value != null && !value.isEmpty()) {
                    value = value.replaceAll("\"", "");
                    serverId = Integer.valueOf(value);
                } else {
                    LOGGER.warn("get serverId failed. redis is empty. playerID:" + playerId);
                }
            } catch (Exception e) {
                LOGGER.error("get serverId failed. redis failed" + ExceptionEx.e2s(e));
            }
            return serverId;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 在用户中心设置玩家的在线状态
     */
    @Deprecated
    public static void setIsOnline(int playerId, boolean isOnline) {
        try (Jedis jedis =
                RedisServices.getRedisService(ERedisType.UC.getType()).getJedis()) {
            // 从redis获取玩家当前所在游戏服
            try {
                long succ = jedis.hset(locationPrefix + playerId, "online",
                        String.valueOf(isOnline));
                if (succ != 0) {
                    LOGGER.error("set location online failed. playerId:" + playerId);
                }
            } catch (Exception e) {
                LOGGER.error("get location online failed. redis failed." + ExceptionEx.e2s(e));
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取用户的在线状态
     */
    @Deprecated
    public static boolean getIsOnline(int playerId) {
        boolean isOnline = false;
        try (Jedis jedis =
                RedisServices.getRedisService(ERedisType.UC.getType()).getJedis()) {
            // 从redis获取玩家当前所在游戏服
            try {
                String value = jedis.hget(locationPrefix + playerId, "online");
                if (value == null || value.isEmpty()) {
                    LOGGER.error("get location online failed. playerId:" + playerId);
                } else {
                    value = value.replaceAll("\"", "");
                    isOnline = Boolean.valueOf(value);
                }
            } catch (Exception e) {
                LOGGER.error("get location online failed. redis failed." + ExceptionEx.e2s(e));
            }
        } catch (Exception e) {
            throw e;
        }
        return isOnline;
    }
}
