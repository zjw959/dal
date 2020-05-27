package logic.character;

import java.util.Map;

import org.apache.log4j.Logger;

import db.game.daoimp.PlayerDaoImp;
import kafka.team.param.g2g.NotifyFriendEx;
import kafka.team.param.g2g.NotifyObj;
import logic.constant.ConstDefine;
import redis.base.PlayerSnap;
import redis.base.RedisCmd;
import redis.base.RedisOper;
import redis.service.ERedisType;
import redis.service.RedisServices;
import utils.ExceptionEx;

/**
 * 角色快照管理器
 */
public class PlayerSnapService {
    private static final Logger LOGGER = Logger.getLogger(PlayerSnapService.class);

    public static PlayerSnap getPlayerShap(NotifyObj obj, NotifyFriendEx tar) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.SNAP.getType()).getRedisOper();
        boolean isExist =
                (boolean) oper.execute(RedisCmd.exists, PlayerDaoImp.snapPrefix + tar.tarPlayerId);
        if (isExist) {
            @SuppressWarnings("unchecked")
            Map<String, String> hash = (Map<String, String>) oper.execute(RedisCmd.hgetAll,
                    PlayerDaoImp.snapPrefix + tar.tarPlayerId);
            return new PlayerSnap(hash);
        } else {
            LOGGER.error(ConstDefine.LOG_ERROR_PROGRAMMER_PREFIX + 
                    "can not get playersnap, id:" + obj.playerId + "tarId:" + tar.tarPlayerId
                            + ", tar:" + tar.status
                            + ExceptionEx.currentThreadTraces());
            return null;
        }
    }


    @SuppressWarnings("unchecked")
    public static PlayerSnap getPlayerShap(int playerId) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.SNAP.getType()).getRedisOper();
        boolean isExist =
                (boolean) oper.execute(RedisCmd.exists, PlayerDaoImp.snapPrefix + playerId);
        if (isExist) {
            Map<String, String> hash = (Map<String, String>) oper.execute(RedisCmd.hgetAll,
                    PlayerDaoImp.snapPrefix + playerId);

            return new PlayerSnap(hash);
        } else {
            LOGGER.error("can not get playersnap, id:" + playerId + ", "
                    + ExceptionEx.currentThreadTraces());
            return null;
        }
    }

    public static boolean getPlayerIsOnline(int playerId) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.SNAP.getType()).getRedisOper();
        String isOnline = (String) oper.execute(RedisCmd.hget, PlayerDaoImp.snapPrefix + playerId,
                "isonline");

        return Boolean.parseBoolean(isOnline);
    }

    /**
     * 判断用户是否存在
     * 
     * 暂时 不保证数据的完整性,如果存在,则一定存在.如果不存在需要从数据库中再次判断.
     * 
     * @param playerId
     * @return
     */
    public static boolean getPlayerIsExist(int playerId) {
        RedisOper oper = RedisServices.getRedisService(ERedisType.SNAP.getType()).getRedisOper();
        Boolean isOnline =
                (Boolean) oper.execute(RedisCmd.exists, PlayerDaoImp.snapPrefix + playerId);
        return isOnline;
    }

}
