package javascript.db;

import java.util.List;

import org.apache.log4j.Logger;

import db.game.bean.PlayerDBBean;
import db.game.service.PlayerDaoService;
import io.netty.channel.ChannelHandlerContext;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.EScriptIdDefine;
import logic.constant.LoginErrorCode;
import logic.login.handler.LLoginCreatePlayerResHandler;
import logic.login.handler.LLoginDataVerifyResHandler;
import logic.login.service.LoginService;
import logic.login.struct.ChannelInfo;
import net.AttributeKeys;
import redis.service.ESpringConextType;
import thread.base.GameBaseProcessor;
import thread.base.GameInnerHandler;
import thread.db.IDBRoleScript;
import thread.player.PlayerDBProcessorManager;
import thread.player.PlayerProcessorManager;
import thread.player.hanlder.base.LRoleBaseOfflineHandler;
import utils.ExceptionEx;
import utils.SpringContextUtils;

public class DBRoleScript extends IDBRoleScript {
    private static final Logger LOGGER = Logger.getLogger(DBRoleScript.class);


    @Override
    public int getScriptId() {
        return EScriptIdDefine.DB_ROLE.Value();
    }

    @Override
    public void updateRole(PlayerDBBean playerDBBean, GameBaseProcessor cbProce,
            GameInnerHandler cbHandler) {
        PlayerDaoService playerDaoService = SpringContextUtils
                .getBean(ESpringConextType.PlAYER.getType(), PlayerDaoService.class);

        PlayerDBBean dbBean = null;

        long now = System.currentTimeMillis();
        dbBean = playerDaoService.updateRole(playerDBBean);

        PlayerDBProcessorManager.getInstance().addDoUpdateCount();
        PlayerDBProcessorManager.getInstance().decUpdateSize();

        if (dbBean == null) {
            LOGGER.error(ConstDefine.LOG_WAIT_OVER_TIME + "回存失败:" + "playerDBBean, id:"
                    + playerDBBean.getPlayerId() + ",name:"
                    + playerDBBean.getPlayername());
        }

        long _delta = System.currentTimeMillis() - now;
        if (_delta > PlayerProcessorManager.getInstance().getSingleDBTime()) {
            PlayerDBProcessorManager.getInstance().addDelayUpdateCount();
            LOGGER.warn(ConstDefine.LOG_WAIT_OVER_TIME + "回存超时, _delta:" + _delta + ",delayCount:"
                    + PlayerDBProcessorManager.getInstance().getDelayUpdateCount() + ",playerId:"
                    + dbBean.getPlayerId());
        }

        // 独立表数据回存
        // try {
        // Player.saveToDB(this.playerDBBean);
        // } catch (Exception e) {
        // LOGGER.error("player [roleBean] : " + this.playerDBBean.getPlayername() + " id:"
        // + this.playerDBBean.getPlayerId() + " save back failed." + ExceptionEx.e2s(e));
        // }

        if (cbProce != null && cbHandler != null && !cbProce.isStop(false)) {
            cbProce.executeInnerHandler(cbHandler);
        }
    }

    @Override
    public void updateRoleBatch(List<PlayerDBBean> roleBeans, boolean isOffLine) {
        PlayerDaoService playerDaoService = SpringContextUtils
                .getBean(ESpringConextType.PlAYER.getType(), PlayerDaoService.class);

        for (PlayerDBBean bean : roleBeans) {
            try {
                PlayerDBBean dbBean = null;
                if (isOffLine) {
                    bean.setIsOnline(false);
                } else {
                    bean.setIsOnline(true);
                }

                long now = System.currentTimeMillis();
                dbBean = playerDaoService.updateRole(bean);

                PlayerDBProcessorManager.getInstance().addDoUpdateCount();
                PlayerDBProcessorManager.getInstance().decUpdateSize();

                if (dbBean == null) {
                    LOGGER.error(ConstDefine.LOG_WAIT_OVER_TIME + "回存失败:" + "playerDBBean, id:"
                            + bean.getPlayerId() + ",name:" + bean.getPlayername());
                    continue;
                }

                long _delta = System.currentTimeMillis() - now;
                if (_delta > PlayerProcessorManager.getInstance().getSingleDBTime()) {
                    PlayerDBProcessorManager.getInstance().addDelayUpdateCount();
                    LOGGER.warn(ConstDefine.LOG_WAIT_OVER_TIME + "回存超时, _delta:" + _delta
                            + ",delayCount:"
                            + PlayerDBProcessorManager.getInstance().getDelayUpdateCount()
                            + ",playerId:" + dbBean.getPlayerId());
                }

                // 独立表数据回存
                // Player.saveToDB(bean);
            } catch (Exception e) {
                LOGGER.error("player [roleBean] : " + bean.getPlayername() + " id:"
                        + bean.getPlayerId() + "  save back failed." + ExceptionEx.e2s(e));
            }
        }
    }

    @Override
    public void roleInfo(LRoleBaseOfflineHandler handler, GameBaseProcessor cbProce,
            boolean isView) {
        PlayerDaoService playerDaoService = SpringContextUtils
                .getBean(ESpringConextType.PlAYER.getType(), PlayerDaoService.class);
        PlayerDBBean _bean = playerDaoService.selectByPlayerId(handler.getQueryRoleId());
        Player _player = null;
        try {
            // 从数据库取出的玩家都默认为离线
            // 项目中所有在线用户都会存在redis中,除非redis内存不足.
            _player = new Player(_bean, isView);
            // PlayerManager.putCachePlayer(_player);
        } catch (Exception e) {
            LOGGER.error("初始化玩家数据为Player异常 + id:" + _bean.getPlayerId() + "name:"
                    + _player.getPlayerName() + ExceptionEx.e2s(e));
            return;
        }
        // 回调hanlder设置
        handler.setPlayer(_player);
        cbProce.executeInnerHandler(handler);
    }

    @Override
    public void createPlayer(ChannelHandlerContext ctx, Player player) {
        boolean success = true;
        PlayerDBBean playerBean = null;

        try {
            PlayerDaoService playerDaoService = SpringContextUtils
                    .getBean(ESpringConextType.PlAYER.getType(), PlayerDaoService.class);
            int playerCount = playerDaoService.selectPlayerNumByPlayerId(player.getPlayerId());

            if (playerCount > 0) {
                LOGGER.error(
                        ConstDefine.LOG_LOGIN_PREFIX + "create role . has exist roleBean. count:"
                                + playerCount + ",id:" + player.getPlayerId());
                success = false;
            }

            if (success) {
                playerBean = player.toPlayerBean();
                PlayerDBBean playerDBBean = playerDaoService.insert(playerBean);

                if (playerDBBean == null) {
                    LOGGER.error(ConstDefine.LOG_LOGIN_PREFIX
                            + "create role . roleBean can not insert to database.");
                    success = false;
                }
            }

            PlayerDBProcessorManager.getInstance().addDoInsertCount();
            PlayerDBProcessorManager.getInstance().decInsertSize();

        } catch (Exception e) {
            success = false;
            LOGGER.error(ExceptionEx.e2s(e));
        }
        LoginService.getInstance().getProcess().executeInnerHandler(
                new LLoginCreatePlayerResHandler(ctx, success, playerBean, player, false));
    }

    @Override
    public void DBLoginDataVerify(ChannelHandlerContext ctx, boolean isReconnect) {
        ChannelInfo channelInfo = ctx.channel().attr(AttributeKeys.CHANNEL_INFO).get();
        int playerId = channelInfo.getPlayerId();
        PlayerDaoService playerDaoService = SpringContextUtils
                .getBean(ESpringConextType.PlAYER.getType(), PlayerDaoService.class);
        PlayerDBBean roleBean = playerDaoService.selectByPlayerId(playerId);

        if (roleBean != null) {
            // 绑定role
            channelInfo.setRoleBean(roleBean);

            try {
                Player.loadFromDB(roleBean);
            } catch (Exception e) {
                LOGGER.error("DBLoginDataVerify:" + ExceptionEx.e2s(e));
                resVerify(ctx, false, LoginErrorCode.UNKNOWN, isReconnect);
            }
        }
        resVerify(ctx, true, 0, isReconnect);

        PlayerDBProcessorManager.getInstance().addDoSelectCount();
        PlayerDBProcessorManager.getInstance().decSelectSize();
    }

    private void resVerify(ChannelHandlerContext ctx, boolean res, int reason,
            boolean isReconnect) {
        LoginService.getInstance().getProcess()
                .executeInnerHandler(new LLoginDataVerifyResHandler(ctx, res, reason, isReconnect));
    }
}
