package db.game.dao;

import java.util.List;
import java.util.Set;

import db.game.bean.PlayerDBBean;
import db.game.bean.PlayerIdBean;
import redis.base.PlayerSnap;

public interface PlayerDao {
    /**
     * 插入帐号
     * 
     * @param playerBean
     * @return
     */
    public PlayerDBBean insert(PlayerDBBean playerBean);

    /**
     * 通过roleId 查询RoleBean信息
     * 
     * @param roleId
     * @return
     */
    public PlayerDBBean selectByPlayerId(int playerId);

    /**
     * 通过userName 查询PalyerDBBean信息
     * 
     * @param userId
     * @param serverId
     * @return
     */
    public PlayerDBBean selectByUserName(String userName);
    
    /**
     * 通过userName 查询PalyerDBBean(PlayerView)信息
     * 
     * @param userId
     * @param serverId
     * @return
     */
    public PlayerDBBean selectPlayerView(int playerId);

    public int selectPlayerNum() throws Exception;

    public int selectPlayerNumByPlayerId(int playerId) throws Exception;

    public List<PlayerDBBean> selectRoleBeans(int start, int num);

    public List<PlayerDBBean> selectRoleBeanExclusiveData(int start, int num);

    public PlayerDBBean updateRedis(PlayerDBBean playerBean) throws Exception;

    public PlayerSnap selectShapByPlayerId(int playerId);

    public PlayerDBBean updateRole(PlayerDBBean playerBean);

    public List<PlayerDBBean> selectPlayerView(int being, int end);

    public List<PlayerDBBean> selectPlayerViewByNum(int i, int dbCount, int level);

    /**
     * 更新玩家等级redis
     * 
     * @param playerId
     * @param oldLevel 旧等级，如果是登陆或非更新操作传-1
     * @param nowlevel
     */
    public void updatePlayerLvlRedis(int playerId, int oldLevel, int nowlevel);

    public void removePlayerLvlRedisExpire(int maxLvl);

    public Set<String> selectTopLvlPlayerIds(int level, int delta);

    public PlayerIdBean selectPlayerId(int level);
}
