package logic.character;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import db.game.bean.PlayerDBBean;
import db.game.service.PlayerDaoService;
import logic.basecore.IView;
import logic.character.bean.Player;
import logic.character.handler.LGetHelpFightPlayersHandler;
import logic.constant.ConstDefine;
import logic.friend.FriendManager;
import logic.friend.handler.LClearExpireFriendApplyHandler;
import logic.friend.handler.LGetFriendsInfoHandler;
import logic.friend.handler.LGetRecommendHandler;
import redis.service.ESpringConextType;
import thread.player.hanlder.LPlayerViewProcessHandler;
import thread.sys.base.SysService;
import utils.ExceptionEx;
import utils.GsonUtils;
import utils.SpringContextUtils;

/**
 * 角色信息查看管理器
 */
public class PlayerViewService extends SysService {

    private static final Logger LOGGER = Logger.getLogger(PlayerViewService.class);

    /**
     * 立即更新redis中的缓存数据
     *
     * 必须在玩家线程调用
     *
     * 并通知好友
     *
     * @param player
     * @throws Exception
     */
    public void updatePlayerView(PlayerDBBean playerDBBean,boolean needUpdateLvlRedis,int oldLvl) {
        PlayerDaoService playerDaoService = SpringContextUtils
                .getBean(ESpringConextType.PlAYER.getType(), PlayerDaoService.class);
        try {
            playerDaoService.updateRedis(playerDBBean);
            if (needUpdateLvlRedis) {
                playerDaoService.updatePlayerLvlRedis(playerDBBean.getPlayerId(), oldLvl,
                        playerDBBean.getLevel());
            }
        } catch (Exception e) {
            LOGGER.error(
                    ConstDefine.LOG_ERROR_EXCEPTION_PREFIX + ExceptionEx.e2s(e) + "id:"
                            + playerDBBean.getPlayerId() + "," + playerDBBean.getPlayername());
        }
        FriendManager.notifyFriendsSelfUpdate(playerDBBean.getPlayerId());
    }

    /**
     * 同步获取一个playerView
     * 
     * 注意,尽量使用异步.
     * 
     * @param player
     * @throws Exception
     */
    public Player getPlayerView(int playerId) throws Exception {
        Player player = null;

        // 首先从本服中获取
        // if (GameServer.getInstance().isIDEMode()) {
        player = _getPlayerViewInServer(playerId);
        if (player != null) {
            return player;
        }
        // }

        PlayerDBBean playerDBBean = _getPlayerViewRemote(playerId);
        if (playerDBBean == null) {
            return null;
        }
        try {
            player = new Player(playerDBBean, true);
        } catch (Exception ex) {
            LOGGER.error(ConstDefine.LOG_ERROR_EXCEPTION_PREFIX + "Playerview decode fail playerId:"
                    + playerId + ExceptionEx.e2s(ex));
        }
        return player;
    }


    /**
     * 异步获取一个playerView
     * 
     * @param cbProc 回调线程
     * @param hanlder 回调handler
     * @throws Exception
     */
    public void getPlayerViewAnsyc(LPlayerViewProcessHandler hanlder)
            throws Exception {
        _getPlayerViewRemoteAnsyc(hanlder);
    }

    /**
     * 从本地缓存中获取player
     * 
     * @param playerId
     * @return
     */
    private Player _getPlayerViewInServer(int playerId) {
        Player player = PlayerManager.getPlayerByPlayerId(playerId);
        return player;
    }


    /**
     * 从缓存(redis)中获取一个playerview
     * 
     * 有过期时间, 有自动加载, 有本地缓存
     * 
     * @param player
     */
    private static PlayerDBBean _getPlayerViewRemote(int playerId) throws Exception {
        PlayerDaoService playerDaoService =
                SpringContextUtils.getBean(ESpringConextType.PlAYER.getType(),
                        PlayerDaoService.class);
        PlayerDBBean playerDBBean = playerDaoService.selectPlayerView(playerId);
        return playerDBBean;
    }

    /**
     * 异步从数据库中获取用户信息
     * 
     * @param playerId
     * @return
     */
    private void _getPlayerViewRemoteAnsyc(LPlayerViewProcessHandler hanlder) {
        this.getProcess().executeInnerHandler(hanlder);
    }

    /** 转化为PlayerViewBean */
    public PlayerDBBean toViewBean(PlayerDBBean fullBean) throws Exception {
        // 克隆PlayerDBBean 因为PlayerDBBean内没有对象引用. 直接clone既是深度克隆
        PlayerDBBean bean = (PlayerDBBean) fullBean.clone();
        // 修改data数据
        JsonObject json = GsonUtils.toJsonObject(bean.getData());
        List<Class<?>> keepClass = Player.getIViewClass();
        Set<String> keys = json.keySet();
        JsonObject _json = new JsonObject();
        for (String key : keys) {
            for (Class<?> cls : keepClass) {
                if (cls.getSimpleName().equals(key)) {
                    JsonElement e =
                            ((IView) cls.newInstance()).toViewJson(json.get(key).toString());
                    _json.add(key, e);
                    break;
                }
            }
        }
        bean.setData(_json.toString());
        return bean;
    }

    /**
     * 获取好友View信息（异步）
     * 
     * @param token
     * @param ctx
     * @param url
     * @param parameter
     * @param method
     */
    public void getFriendsInfoAysnc(int playerId, int rmsgId) {
        this.getProcess().executeInnerHandler(new LGetFriendsInfoHandler(playerId, rmsgId));
    }

    /**
     * 清除过期好友申请数据（异步）
     * 
     * @param token
     * @param ctx
     * @param url
     * @param parameter
     * @param method
     */
    public void clearExpireFriendApplyAysnc(int playerId) {
        this.getProcess().executeInnerHandler(new LClearExpireFriendApplyHandler(playerId));
    }

    
    /**
     * 获取推荐好友（异步）
     * 
     * @param player
     * @param rmsgId
     * @param isCheckCD
     */
    public void getRecommands(Player player, int rmsgId, boolean isCheckCD) {
        this.getProcess().executeInnerHandler(
                new LGetRecommendHandler(player.getPlayerId(), rmsgId, isCheckCD));
    }

    /**
     * 获取助战玩家（异步）
     * 
     * @param player
     * @param rmsgId
     */
    public void getHelpFightPlayers(Player player, int rmsgId) {
        this.getProcess()
                .executeInnerHandler(new LGetHelpFightPlayersHandler(player.getPlayerId(), rmsgId));
    }

    public static PlayerViewService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        PlayerViewService instance;

        private Singleton() {
            instance = new PlayerViewService();
        }

        PlayerViewService getInstance() {
            return instance;
        }
    }
}
