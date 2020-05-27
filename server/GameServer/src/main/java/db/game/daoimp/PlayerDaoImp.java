package db.game.daoimp;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.jarvis.cache.annotation.Cache;
import com.jarvis.cache.annotation.ExCache;
import com.jarvis.cache.annotation.LocalCache;
import com.jarvis.cache.type.CacheOpType;

import db.DBFactory;
import db.game.bean.PlayerDBBean;
import db.game.bean.PlayerIdBean;
import db.game.dao.PlayerDao;
import logic.character.PlayerViewService;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.friend.FriendService;
import redis.base.PlayerSnap;
import redis.clients.jedis.Jedis;
import redis.service.ERedisType;
import redis.service.RedisServices;
import utils.ExceptionEx;
import utils.TimeUtil;

@Repository("playerDao")
public class PlayerDaoImp implements PlayerDao {
    public static final String viewPrefix = "view::";

    public static final String snapPrefix = "snap::";
    
    /** 玩家等级rediskey，存储结构sortedset key:level member:charId score:登陆时间*/
    public transient static String PLAYER_LEVEL_JEDIS_KEY = "lvl::";

    /**
     * 正常过期时间 等于玩家Update更新时长x(2~3)
     */
    public static final int expireTimeNormal = 5 * 60;
    // (int) (DateEx.TIME_SECOND * 120 / DateEx.TIME_SECOND);
    
    public static final int expireTimeView = expireTimeNormal * 3;

    /**
     * 离线存储时间 3小时
     */
    public static final int expireOffLineView = 60 * 60 * 3;
    // (int) (DateEx.TIME_SECOND * 120 * 6 / DateEx.TIME_SECOND);

    /**
     * 插入帐号
     * 
     * @param playerBean
     * @return
     */
    @Override
    public PlayerDBBean insert(PlayerDBBean playerBean) {
        try (SqlSession session = DBFactory.GAME_DB.getSessionFactory().openSession()) {
            int rows = session.insert("t_player.insertOne", playerBean);
            if (rows <= 0) {
                throw new Exception("insert error. rows <= 0. name:" + playerBean.getPlayername());
            }
            session.commit();
            return playerBean;
        } catch (Exception e) {
            Logger log = DBFactory.GAME_DB.getLogger();
            log.error(ExceptionEx.e2s(e));
        }
        return null;
    }

    /**
     * 通过roleId 查询RoleBean信息
     * 
     * @param roleId
     * @return
     */
    @Override
    public PlayerDBBean selectByPlayerId(int playerId) {
        try (SqlSession session = DBFactory.GAME_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("playerId", playerId);
            PlayerDBBean playerBean = session.selectOne("t_player.selectAllByPlayerId", map);
            return playerBean;
        } catch (Exception e) {
            Logger log = DBFactory.GAME_DB.getLogger();
            log.error(ExceptionEx.e2s(e));
            throw e;
        }
    }

    /**
     * 通过userName 查询PalyerDBBean信息
     * 
     * @param userId
     * @param serverId
     * @return
     */
    @Override
    public PlayerDBBean selectByUserName(String userName) {
        try (SqlSession session = DBFactory.GAME_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("userName", userName);
            PlayerDBBean playerBean = session.selectOne("t_player.selectAllByUserName", map);
            return playerBean;
        } catch (Exception e) {
            Logger log = DBFactory.GAME_DB.getLogger();
            log.error(ExceptionEx.e2s(e));
            throw e;
        }
    }

    /**
     * 更改为可变信息才修改
     * 
     * @param playerBean
     * @return PlayerDBBean 依赖传入的值生成PlayerSnap缓存对象
     */
    @Override
    @Cache(expire = expireTimeView,
            expireExpression = "true == #args[0].isOnline ? #target.expireTimeNormal * 3 : #target.expireOffLineView",
            key = "'" + viewPrefix + "'+#args[0].playerId", condition = "null != #args[0]",
            opType = CacheOpType.WRITE,
            exCache = @ExCache(expire = 0, key = "'" + snapPrefix + "'+#args[0].playerId",
                    condition = " null != #retVal",
                    cacheObject = "new redis.base.PlayerSnap(#retVal)", hfield = true,
                    jedisIndex = 2))
    public PlayerDBBean updateRole(PlayerDBBean playerBean) {
        try (SqlSession session = DBFactory.GAME_DB.getSessionFactory().openSession()) {
            int ret = session.update("t_player.updateOne", playerBean);
            if (ret <= 0) {
                session.rollback();
                return null;
            } else {
                session.commit();
                return playerBean;
            }
        } catch (Exception e) {
            Logger log = DBFactory.GAME_DB.getLogger();
            log.error(ConstDefine.LOG_ERROR_DB_EXCEPTION_PREFIX + ExceptionEx.e2s(e));
        }
        return null;
    }

    /**
     * 更新redis
     * 
     * 注意: 不入库,仅转化
     * 
     * @throws Exception
     */
    @Override
    @Cache(expire = expireTimeView, key = "'" + viewPrefix + "'+#args[0].playerId",
            condition = "null != #args[0]", opType = CacheOpType.WRITE,
            exCache = @ExCache(expire = 0, key = "'" + snapPrefix + "'+#args[0].playerId",
                    cacheObject = "new redis.base.PlayerSnap(#retVal)", hfield = true,
                    jedisIndex = 2))
    public PlayerDBBean updateRedis(PlayerDBBean playerBean) throws Exception {
        PlayerDBBean viewBean = PlayerViewService.getInstance().toViewBean(playerBean);
        return viewBean;
    }
  

    @Override
    public int selectPlayerNum() throws Exception {
        try (SqlSession session = DBFactory.GAME_DB.getSessionFactory().openSession()) {
            int ret = session.selectOne("t_player.selectCount");
            return ret;
        } catch (Exception e) {
            Logger log = DBFactory.GAME_DB.getLogger();
            log.error(ExceptionEx.e2s(e));
            throw new Exception(e);
        }
    }

    public int selectPlayerNumByPlayerId(int playerId) throws Exception {
        try (SqlSession session = DBFactory.GAME_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("playerId", playerId);
            int ret = session.selectOne("t_player.selectCountByPlayerId", map);
            return ret;
        } catch (Exception e) {
            Logger log = DBFactory.GAME_DB.getLogger();
            log.error(ConstDefine.LOG_ERROR_DB_EXCEPTION_PREFIX + ExceptionEx.e2s(e));
            throw new Exception(e);
        }
    }

    @Override
    public List<PlayerDBBean> selectRoleBeans(int start, int num) {
        try (SqlSession session = DBFactory.GAME_DB.getSessionFactory().openSession()) {
            HashMap<String, Integer> map = new HashMap<>();
            map.put("m", start);
            map.put("n", num);
            return session.selectList("t_player.selectPlayer", map);
        } catch (Exception e) {
            Logger log = DBFactory.GAME_DB.getLogger();
            log.error(ConstDefine.LOG_ERROR_DB_EXCEPTION_PREFIX + ExceptionEx.e2s(e));
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<PlayerDBBean> selectRoleBeanExclusiveData(int start, int num) {
        try (SqlSession session = DBFactory.GAME_DB.getSessionFactory().openSession()) {
            HashMap<String, Integer> map = new HashMap<>();
            map.put("m", start);
            map.put("n", num);
            return session.selectList("t_player.selectPlayerExclusiveData", map);
        } catch (Exception e) {
            Logger log = DBFactory.GAME_DB.getLogger();
            log.error(ConstDefine.LOG_ERROR_DB_EXCEPTION_PREFIX + ExceptionEx.e2s(e));
        }
        return Collections.emptyList();
    }

    @Override
    @Cache(expire = expireTimeNormal * 6, key = "'" + viewPrefix + "'+#args[0]",
            condition = "#args[0] > 0", opType = CacheOpType.READ_WRITE, waitTimeOut = 500,
            lockExpire = 1, autoload = true, argumentsDeepcloneEnable = false,
            requestTimeout = 1800, alarmTime = (int) (expireTimeNormal / 1))
    // 本地保存1/2分钟
    @LocalCache(expire = (int) (expireTimeNormal / 2))
    public PlayerDBBean selectPlayerView(int playerId) {
        try (SqlSession session = DBFactory.GAME_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("playerId", playerId);
            map.put("keys", Player.getIViewImpls());
            return session.selectOne("t_player.selectPlayerView", map);
        } catch (Exception e) {
            Logger log = DBFactory.GAME_DB.getLogger();
            log.error(ConstDefine.LOG_ERROR_DB_EXCEPTION_PREFIX + "playerId:" + playerId
                    + ExceptionEx.e2s(e));
        }
        return null;
    }

    @Override
    public List<PlayerDBBean> selectPlayerView(int start, int num) {
        try (SqlSession session = DBFactory.GAME_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("keys", Player.getIViewImpls());
            map.put("m", start);
            map.put("n", num);
            return session.selectList("t_player.selectPlayerView", map);
        } catch (Exception e) {
            Logger log = DBFactory.GAME_DB.getLogger();
            log.error(ConstDefine.LOG_ERROR_DB_EXCEPTION_PREFIX + "playerId:" + start + "," + num
                    + ExceptionEx.e2s(e));
        }
        return null;
    }


    @Override
    public List<PlayerDBBean> selectPlayerViewByNum(int i, int dbCount, int level) {
        try (SqlSession session = DBFactory.GAME_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("keys", Player.getIViewImpls());
            map.put("m", i);
            map.put("n", dbCount);
            map.put("level", level);
            return session.selectList("t_player.selectPlayerViewByNum", map);
        } catch (Exception e) {
            Logger log = DBFactory.GAME_DB.getLogger();
            log.error(ConstDefine.LOG_ERROR_DB_EXCEPTION_PREFIX + "playerId begin:" + i + ","
                    + dbCount + ExceptionEx.e2s(e));
        }
        return null;
    }

    @Override
    @Cache(expire = 0, key = "'" + snapPrefix + "'+#args[0]",
            condition = "#args[0] > 0", opType = CacheOpType.READ_ONLY, hfield = true,
            waitTimeOut = 500, lockExpire = 1, autoload = false, argumentsDeepcloneEnable = false,
            requestTimeout = 1800, alarmTime = (int) (expireTimeNormal / 1))
    public PlayerSnap selectShapByPlayerId(int playerId) {
        return null;
    }

    /**
     * 更新玩家等级redis
     * 
     * @param playerId
     * @param oldLevel 旧等级，如果是登陆或非更新操作传-1
     * @param nowlevel
     */
    public void updatePlayerLvlRedis(int playerId, int oldLevel, int nowlevel) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.LEVEL.getType()).getJedis()) {
            // 旧等级和新等级不一致，需要删除旧等级
            if (oldLevel != -1 && oldLevel != nowlevel) {
                String _key = PLAYER_LEVEL_JEDIS_KEY + oldLevel;
                jedis.zrem(_key, String.valueOf(playerId));
            }
            String _key = PLAYER_LEVEL_JEDIS_KEY + nowlevel;
            jedis.zadd(_key, (int) TimeUtil.getMinute(), String.valueOf(playerId));
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * 移除指定时间之前的等级玩家数据
     */
    public void removePlayerLvlRedisExpire(int maxLevel) {
        // 未达到删除时间间隔不删除
        int _expireTime = (int) TimeUtil.getMinute() - FriendService.LEVEL_JEDIS_EXPIRE_TIME;
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.LEVEL.getType()).getJedis()) {
            for (int _level = 1; _level <= maxLevel; _level++) {
                String _key = PLAYER_LEVEL_JEDIS_KEY + _level;
                jedis.zremrangeByScore(_key, 0, _expireTime);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 查询前n条对应等级玩家id记录
     * 
     * @param level
     * @param count
     */
    public Set<String> selectTopLvlPlayerIds(int level, int count) {
        try (Jedis jedis = RedisServices.getRedisService(ERedisType.LEVEL.getType()).getJedis()) {
            String _key = PLAYER_LEVEL_JEDIS_KEY + level;
            Set<String> _sortedList = jedis.zrevrange(_key, 0, count);
            return _sortedList;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public PlayerIdBean selectPlayerId(int level) {
        try (SqlSession session = DBFactory.GAME_DB.getSessionFactory().openSession()) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("level", level);
            return session.selectOne("t_player.selectPlayerId", map);
        } catch (Exception e) {
            Logger log = DBFactory.GAME_DB.getLogger();
            log.error(ConstDefine.LOG_ERROR_DB_EXCEPTION_PREFIX + "select level:" + level
                    + ExceptionEx.e2s(e));
        }
        return null;
    }
}
