package db.game.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import db.game.bean.PlayerDBBean;
import db.game.bean.PlayerIdBean;
import db.game.dao.PlayerDao;
import redis.base.PlayerSnap;

@Service("playerDaoService")
public class PlayerDaoService {
    @Autowired
    PlayerDao playerDao;

    /**
     * 插入帐号
     * 
     * @param playerBean
     * @return
     */
    public PlayerDBBean insert(PlayerDBBean playerBean) {
        return playerDao.insert(playerBean);
    }

    /**
     * 通过roleId 查询RoleBean信息
     * 
     * @param roleId
     * @return
     */
    public PlayerDBBean selectByPlayerId(int playerId) {
        return playerDao.selectByPlayerId(playerId);
    }

    /**
     * 通过roleId 查询RoleBean(PlayerView)信息
     * 
     * @param roleId
     * @return
     */
    public PlayerDBBean selectPlayerView(int playerId) {
        return playerDao.selectPlayerView(playerId);
    }

    /**
     * 通过roleId 查询RoleBean(PlayerView)信息
     * 
     * @param roleId
     * @return
     */
    public List<PlayerDBBean> selectPlayerView(int being, int end) {
        return playerDao.selectPlayerView(being, end);
    }

    /**
     * 通过userName 查询PalyerDBBean信息
     * 
     * @param userId
     * @param serverId
     * @return
     */
    public PlayerDBBean selectByUserName(String userName) {
        return playerDao.selectByUserName(userName);
    }

    /**
     * 更新玩家数据
     * 
     * @param playerBean
     * @return
     */
    public PlayerDBBean updateRole(PlayerDBBean playerBean) {
        PlayerDBBean playerDBBean2 = playerDao.updateRole(playerBean);
        return playerDBBean2;
    }

    public int selectPlayerNum() throws Exception {
        return playerDao.selectPlayerNum();
    }

    public int selectPlayerNumByPlayerId(int playerId) throws Exception {
        return playerDao.selectPlayerNumByPlayerId(playerId);
    }

    public List<PlayerDBBean> selectRoleBeans(int start, int num) {
        return playerDao.selectRoleBeans(start, num);
    }

    public List<PlayerDBBean> selectRoleBeanExclusiveData(int start, int num) {
        return playerDao.selectRoleBeanExclusiveData(start, num);
    }

    public PlayerDBBean updateRedis(PlayerDBBean playerBean) throws Exception {
        return playerDao.updateRedis(playerBean);
    }

    public PlayerSnap selectShapByPlayerId(int playerId) {
        return playerDao.selectShapByPlayerId(playerId);
    }

    /**
     * 慎用 耗时操作
     * 
     * @param i
     * @param dbCount
     * @param level
     * @return
     */
    public List<PlayerDBBean> selectPlayerViewByNum(int i, int dbCount, int level) {
        return playerDao.selectPlayerViewByNum(i, dbCount, level);
    }
    
    /**
     * 更新玩家等级redis
     */
    public void updatePlayerLvlRedis(int playerId, int nowlvl) {
        updatePlayerLvlRedis(playerId, -1, nowlvl);
    }

    /**
     * 更新玩家等级redis
     * 
     * @param playerId
     * @param oldLevel 旧等级，如果是登陆或非更新操作传-1
     * @param nowlevel
     */
    public void updatePlayerLvlRedis(int playerId, int oldLevel, int nowlevel) {
        playerDao.updatePlayerLvlRedis(playerId, oldLevel, nowlevel);
    }

    public void removePlayerLvlRedisExpire(int maxLvl) {
        playerDao.removePlayerLvlRedisExpire(maxLvl);
    }

    public Set<String> selectTopLvlPlayerIds(int level, int delta) {
        return playerDao.selectTopLvlPlayerIds(level,delta);
    }

    public PlayerIdBean selectPlayerId(int level) {
        return playerDao.selectPlayerId(level);
    }
}
