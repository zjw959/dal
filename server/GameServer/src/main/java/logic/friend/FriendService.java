package logic.friend;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.jarvis.cache.serializer.StringSerializer;

import db.game.daoimp.PlayerDaoImp;
import logic.friend.bean.FriendView;
import logic.support.LogicScriptsUtils;
import thread.sys.base.SysService;
import utils.pool.LRUCache;

/**
 * 
 * @Description 好友服务类
 * @author hongfu.wang
 * @date 2018-08-09
 *
 */
public class FriendService extends SysService {

    public static final StringSerializer KEY_SERIALIZER = new StringSerializer();

    /** 玩家等级组 key：等级 value<key:playerId,value:Player> */
    public static Map<Integer, Map<Integer, FriendView>> PLAYER_LEVEL_MAP = new ConcurrentHashMap<>();
    /** 玩家等级redis 上次过期是删除时间 */
    public static long LAST_DEL_EXPIRE_LEVEL_JEDIS_TIME = 0;
    /** 等级redis过期时间 view 的五分之四时长，保证比view时间少单位（min） */
    public static int LEVEL_JEDIS_EXPIRE_TIME = (int) (PlayerDaoImp.expireTimeView * 0.8) / 60;
    /** 玩家等级组 key：等级 value<key:playerId,value:Player> */
    public static Map<Integer, LRUCache<Integer, FriendView>> PLAYER_LEVEL_MAP_FROM_DB =
            new ConcurrentHashMap<>();

    /** 上次从数据库加载推荐数据时间 只会加载一次 不会再加载了 */
    // public static long LAST_LOAD_RECOMMEND_FROM_DB_TIME = 0;
    /** 从数据库加载玩家数据时间间隔(这里填个非常大的数，则在运行过程中不会重新加载，只会在启动时加载) 10年*/
    // public static long LOAD_RECOMMEND_FROM_DB_INTERVAL = TimeUtil.ONE_DAY * 3650;

    public int getMapSize() {
        int size = 0;
        Set<Entry<Integer, Map<Integer, FriendView>>> entries = PLAYER_LEVEL_MAP.entrySet();
        for (Entry<Integer, Map<Integer, FriendView>> entry : entries) {
            size += entry.getValue().size();
        }
        return size;
    }

    public static FriendService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        FriendService instance;

        private Singleton() {
            instance = new FriendService();
        }

        FriendService getInstance() {
            return instance;
        }
    }

    public void reloadPlayerLvlWhenExpired() throws Exception {
        LogicScriptsUtils.getIFriendScript().reloadPlayerLvlWhenExpired();
    }
}
