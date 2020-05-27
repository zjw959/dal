package logic.chasm;

import java.util.Date;
import org.apache.log4j.Logger;
import com.alibaba.fastjson.JSONObject;
import kafka.team.param.g2f.MatchTeamHandlerParam;
import redis.base.RedisCmd;
import redis.base.RedisOper;
import redis.clients.jedis.Jedis;
import redis.service.ERedisType;
import redis.service.RedisServices;
import utils.ExceptionEx;

public class TeamRedisService {
    private transient static final Logger LOGGER = Logger.getLogger(TeamRedisService.class);
    public static long getPlayerTeamId(int playerId) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getRedisOper();
        Object teamId = oper.execute(RedisCmd.get, RedisOper.TEAM_ID_PREFIX + playerId);
        return (teamId == null || "".equals(teamId)) ? 0L : Long.parseLong("" + teamId);
    }

    public static long getPlayerTeamTime(int playerId) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getRedisOper();
        Object teamTime = oper.execute(RedisCmd.get, RedisOper.TEAM_TIME_PREFIX + playerId);
        return (teamTime == null || "".equals(teamTime)) ? 0L : Long.parseLong("" + teamTime);
    }

    public static int getDungonId(int playerId) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getRedisOper();
        Object objStr = oper.execute(RedisCmd.get, RedisOper.TEAM_MATCH_PREFIX + playerId);
        if (objStr == null || "".equals(objStr))
            return 0;
        MatchTeamHandlerParam param =
                JSONObject.parseObject("" + objStr, MatchTeamHandlerParam.class);
        return param.getDungeonCid();
    }

    // 记录队伍操作时间 避免短期内重复请求
    public static void setPlayerTeamTime(int playerId) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getRedisOper();
        oper.execute(RedisCmd.set, RedisOper.TEAM_TIME_PREFIX + playerId,
                String.valueOf(new Date().getTime()));
    }
    
    public static void setExpireTime(String key, int time) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getRedisOper();
        oper.execute(RedisCmd.expire, key, time);
    }

    public static void delPlayerTeamTime(int playerId) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getRedisOper();
        oper.execute(RedisCmd.del, RedisOper.TEAM_TIME_PREFIX + playerId);
    }

    public static long delPlayerMatch(int playerId) {
        int dungeonCid = TeamRedisService.getDungonId(playerId);
        if (dungeonCid != 0) {
            RedisOper oper =
                    RedisServices.getRedisService(ERedisType.FIGHT.getType()).getRedisOper();
            Long num = (Long) oper.execute(RedisCmd.zrem,
                    RedisOper.TEAM_DUNGEON_PREFIX + dungeonCid, "" + playerId);
            if(num > 0) {
                oper.execute(RedisCmd.del, RedisOper.TEAM_MATCH_PREFIX + playerId);
            }
            return num;
        }
        return 0L;
    }

    public static void setPlayerTeamId(int playerId, long teamId) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getRedisOper();
        oper.execute(RedisCmd.set, RedisOper.TEAM_ID_PREFIX + playerId, "" + teamId);
        oper.execute(RedisCmd.expire, RedisOper.TEAM_ID_PREFIX + playerId, 1800);
    }

    public static void delPlayerTeamId(int playerId) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getRedisOper();
        oper.execute(RedisCmd.del, RedisOper.TEAM_ID_PREFIX + playerId);
    }

    public static boolean isTeamExist(long teamId) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.FIGHT.getType()).getRedisOper();
        return (Boolean) oper.execute(RedisCmd.exists, RedisOper.TEAM_PREFIX + teamId, "" + teamId);
    }

    public static int getTeamServerId(long teamId) {
        try (Jedis jedis =
                RedisServices.getRedisService(ERedisType.FIGHT.getType()).getJedis()) {
            String serverStr = jedis.hget(RedisOper.TEAM_PREFIX + teamId, "serverId");
            if (serverStr == null || "".equals(serverStr))
                return 0;
            return Integer.valueOf(serverStr);
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        return 0;
    }
}
