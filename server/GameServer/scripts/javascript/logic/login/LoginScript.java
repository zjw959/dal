package javascript.logic.login;

import org.apache.log4j.Logger;
import org.game.protobuf.c2s.C2SLoginMsg.EnterGame;
import org.game.protobuf.c2s.C2SLoginMsg.ReqReconnect;
import org.game.protobuf.s2c.S2CLoginMsg;

import db.game.bean.PlayerDBBean;
import db.game.service.PlayerDaoService;
import io.netty.channel.ChannelHandlerContext;
import logic.basecore.IAcrossDay;
import logic.character.PlayerManager;
import logic.character.bean.Player;
import logic.character.bean.Player.PlayerState;
import logic.character.bean.Token;
import logic.chasm.TeamService;
import logic.chat.ChatService;
import logic.constant.ConstDefine;
import logic.constant.EScriptIdDefine;
import logic.constant.GameErrorCode;
import logic.constant.LoginErrorCode;
import logic.friend.FriendService;
import logic.friend.bean.FriendView;
import logic.gloabl.GlobalService;
import logic.info.InfoManager;
import logic.log.bean.CreateRoleLog;
import logic.log.bean.LogoutLog;
import logic.log.bean.SdkLoginLog;
import logic.login.bean.LoginCheckBean;
import logic.login.handler.DBCreatePlayerHandler;
import logic.login.handler.DBLoginDataVerifyHandler;
import logic.login.handler.LLoginCheckAddQueueHandler;
import logic.login.service.ILoginScript;
import logic.login.service.LoginCheckService;
import logic.login.service.LoginService;
import logic.login.service.LoginService.CTXState;
import logic.login.service.LoginService.PlayerFrom;
import logic.login.struct.ChannelInfo;
import logic.msgBuilder.PlayerMsgBuilder;
import logic.support.MessageUtils;
import message.SMessage;
import net.AttributeKeys;
import net.codec.util.IChannelConstants;
import redis.service.ESpringConextType;
import server.ServerConfig;
import thread.log.LogProcessor;
import thread.player.PlayerDBProcessor;
import thread.player.PlayerDBProcessorManager;
import thread.player.PlayerProcessorManager;
import utils.ChannelUtils;
import utils.ExceptionEx;
import utils.SpringContextUtils;
import utils.pool.LRUCache;

public class LoginScript implements ILoginScript {
    private static final Logger LOGGER = Logger.getLogger(LoginService.class);
    
    @Override
    public int getScriptId() {
        return EScriptIdDefine.LOGIN_SCRIPT.Value();
    }

    /**
     * 登陆
     * 
     * @param ctx
     * @param loginMsg 正确结构:
     *        需要包含之前上一步httpLogin里httpRequest传入的参数，目前代码调整阶段，所以在上一步做了Account缓存，将参数填充到Account）
     */
    @Override
    public void reqLogin(ChannelHandlerContext ctx, EnterGame loginMsg) {
        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "reqLogin." + ChannelUtils.logInfo(ctx));

        // 请求登录和重登录时需要重新初始化密钥,不然连异常提示客户端都不能正常解析
        ctx.channel().attr(IChannelConstants.DECRYPTION_KEYS)
                .set(new int[] {1, 2, 3, 4, 5, 6, 7, 8});
        ctx.channel().attr(IChannelConstants.ENCRYPTION_KEYS)
                .set(new int[] {1, 2, 3, 4, 5, 6, 7, 8});

        boolean isRepeat = _repeatLogin(ctx, false);
        if (isRepeat) {
            return;
        }

        LoginService.getInstance().ctxs.put(ctx.hashCode(), ctx);

        /** 状态验证 验证是否重复消息等 */
        CTXState state = ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).get();
        if (state == null) {
            loginFailed(ctx, LoginErrorCode.STATE_CODE_IS_ERR, "loginstate is null");
            return;
        }

        if (state != CTXState.CONNECT) {
            loginFailed(ctx, LoginErrorCode.STATE_CODE_IS_ERR, "loginstate is:" + state);
            return;
        }

        ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).set(CTXState.VERIFY_ACCOUNT);

        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "reqlogin." + ChannelUtils.logInfo(ctx));

        // 交给登录服做验证(异步)
        ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).set(LoginService.CTXState.VERIFY_ACCOUNT);
        String url =
                ServerConfig.getInstance().getLoginVerifyUrl() + "?token=" + loginMsg.getToken();
        LoginCheckService.getInstance().reqLoginCheckAysnc(loginMsg.getToken(), ctx, url, null,
                "GET", loginMsg.getAnti(), false);
    }

    @Override
    public void reqReconnect(ChannelHandlerContext ctx, ReqReconnect msg) {
        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "reqReconnect." + ChannelUtils.logInfo(ctx));

        // 请求登录和重登录时需要重新初始化密钥,不然连异常提示客户端都不能正常解析
        ctx.channel().attr(IChannelConstants.DECRYPTION_KEYS)
                .set(new int[] {1, 2, 3, 4, 5, 6, 7, 8});
        ctx.channel().attr(IChannelConstants.ENCRYPTION_KEYS)
                .set(new int[] {1, 2, 3, 4, 5, 6, 7, 8});

        boolean isRepeat = _repeatLogin(ctx, false);
        if (isRepeat) {
            return;
        }

        LoginService.getInstance().ctxs.put(ctx.hashCode(), ctx);

        /** 状态验证 验证是否重复消息等 */
        CTXState state = ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).get();
        if (state == null) {
            loginFailed(ctx, LoginErrorCode.STATE_CODE_IS_ERR, "loginstate is null");
            return;
        }

        if (state != CTXState.CONNECT) {
            loginFailed(ctx, LoginErrorCode.STATE_CODE_IS_ERR, "loginstate is:" + state);
            return;
        }


        // 交给登录服做验证(异步)
        ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).set(LoginService.CTXState.VERIFY_ACCOUNT);
        String url = ServerConfig.getInstance().getLoginVerifyUrl() + "?token=" + msg.getToken();
        LoginCheckService.getInstance().reqLoginCheckAysnc(msg.getToken(), ctx, url, null, "GET",
                msg.getAnti(),
                true);
    }

    /**
     * 渠道校验成功后的处理，需要异步处理
     */
    @Override
    public void innerLoginCheckSuccCallBack(LoginCheckBean checkBean) {
        ChannelHandlerContext ctx = checkBean.getCtx();
        CTXState state = ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).get();

        if (state == null || (state != CTXState.VERIFY_ACCOUNT_FINISH
                && state != CTXState.LOGIN_QUEUE_DONE)) {
            loginFailed(ctx, LoginErrorCode.STATE_CODE_IS_ERR,
                    state != null ? "loginstate is:" + state : "");
            return;
        }

        if (state != CTXState.LOGIN_QUEUE_DONE) {
            int onlineNum = PlayerManager.getOnlineNum();
            // System.out.println("---当前在线人数=" + onlineNum + " ctx.size="
            // + LoginService.getInstance().ctxs.size() + " max=" + GlobalService.ONLINE_MAX);
            if (onlineNum + LoginService.getInstance().ctxs.size() > GlobalService.ONLINE_MAX) {
                // 进入登录排队
                LoginCheckService.getInstance().getProcess()
                        .executeInnerHandler(new LLoginCheckAddQueueHandler(checkBean));
                return;
            }
        }

        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + " 登陆验证成功结果返回, ctx:" + ChannelUtils.logInfo(ctx)
                + (checkBean.isReconnect() ? "重连登陆" : ""));

        if (ChannelUtils.isDisconnectChannel(ctx)) {
            LOGGER.warn(ConstDefine.LOG_LOGIN_PREFIX + "res sdk verify . channel was disconnected."
                    + ChannelUtils.logInfo(ctx));
            return;
        }

        ChannelInfo channelInfo = ctx.channel().attr(AttributeKeys.CHANNEL_INFO).get();
        int playerId = channelInfo.getPlayerId();
        if (channelInfo.isAdminLogin()) {
            LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + " 托管登录成功, playerId:" + playerId + " ctx:"
                    + ChannelUtils.logInfo(ctx));
        }
        Player player = PlayerManager.getPlayerByPlayerId(playerId);
        if (player == null) {
            // 缓存中没有玩家，则从DB拉取
            ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).set(CTXState.VERIFY_DB);
            PlayerDBProcessor dbProcessor = PlayerDBProcessorManager.getInstance()
                    .getProcessorByUserName(channelInfo.getFullUserName());
            PlayerDBProcessorManager.getInstance().addPlayerHandler(dbProcessor.getLineIndex(),
                    new DBLoginDataVerifyHandler(ctx, checkBean.isReconnect()));
        } else {
            ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).set(CTXState.SKIP_VERIFY_DB);
            initPlayer(null, ctx, player, PlayerFrom.INMEMPLAYER, checkBean.isReconnect());
        }
    }

    /**
     * 登陆查询用户角色数据回调
     * 
     * @param ctx
     * @param success
     * @param reason
     */
    @Override
    public void innnerLoginDataVerifyBack(ChannelHandlerContext ctx, boolean success, int reason,
            boolean isReconnect) {
        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "innnerLoginDataVerifyBack."
                + ChannelUtils.logInfo(ctx));

        if (!success) {
            loginFailed(ctx, reason, " innnerLoginDataVerifyBack code:" + reason);
            return;
        }
        if (ChannelUtils.isDisconnectChannel(ctx)) {
            LOGGER.info("loginDataVerifyRes. Channel is Disconnected." + ChannelUtils.logInfo(ctx));
            return;
        }

        CTXState state = ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).get();
        if (state == null || (state != CTXState.VERIFY_DB)) {
            loginFailed(ctx, LoginErrorCode.STATE_CODE_IS_ERR,
                    state != null ? "loginstate is:" + state : "");
            return;
        }

        ChannelInfo channelInfo = ctx.channel().attr(AttributeKeys.CHANNEL_INFO).get();
        // 账号下没有角色直接创建新角色
        PlayerDBBean playerBean = channelInfo.getRoleBean();
        Player player;
        if (playerBean == null) {
            if (isReconnect) {
                loginFailed(ctx, LoginErrorCode.NOT_FIND_ACCOUNT,
                        " innnerLoginDataVerifyBack, playerBean is null, by isReconnect. code:"
                                + reason);
                return;
            }
            // Player相关的初始化数据必须放在这里进行调用.否则流程中断则会写入坏的数据
            try {
                player = new Player(channelInfo);
                
            } catch (Exception e) {
                loginFailed(ctx, GameErrorCode.PLAYER_DATA_ERROR, ExceptionEx.e2s(e));
                return;
            }

            ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).set(CTXState.CREATE_ROLE_DB);
            PlayerDBProcessorManager.getInstance().addPlayerHandler(player,
                    new DBCreatePlayerHandler(ctx, player));
            return;
        } else {
            try {
                player = new Player(playerBean, false);
            } catch (Exception e) {
                loginFailed(ctx, GameErrorCode.PLAYER_DATA_ERROR, ExceptionEx.e2s(e));
                return;
            }
        }

        ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).set(CTXState.VERIFY_DB_FINISH);

        initPlayer(channelInfo.getRoleBean(), ctx, player, PlayerFrom.DBPLAYER, isReconnect);
    }

    /**
     * 创建角色数据库结果返回
     * 
     * @param ctxx
     * @param success
     */
    @Override
    public void innerCreatePlayerDBBack(ChannelHandlerContext ctx, boolean success,
            PlayerDBBean playerBean, Player player, boolean isReconnect) {
        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "_innerCreateRoleDBBack."
                + ChannelUtils.logInfo(ctx));

        if (!success) {
            loginFailed(ctx, LoginErrorCode.CREATOR_TYPE_ERR, "create role failed.");
            return;
        }
        ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).set(CTXState.CREATE_ROLE_DB_FINISH);
        initPlayer(playerBean, ctx, player, PlayerFrom.CREATEPLAYER, isReconnect);
    }

    @Override
    public void initPlayer(PlayerDBBean playerBean, ChannelHandlerContext ctx, Player player,
            PlayerFrom playerFrom, boolean isReconnect) {
        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + ",initPlayer." + ChannelUtils.logInfo(ctx));

        if (player == null) {
            loginFailed(ctx, GameErrorCode.PLAYER_DATA_ERROR,"login failed.init player,player is null." + ExceptionEx.currentThreadTraces());
            return;
        }

        if (ChannelUtils.isDisconnectChannel(ctx)) {
            LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX
                    + "login failed.init player,channel is disconnected."
                    + ChannelUtils.logInfo(ctx));
            return;
        }

        CTXState state = ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).get();
        if (playerFrom == PlayerFrom.CREATEPLAYER) {
            if (state != CTXState.CREATE_ROLE_DB_FINISH) {
                loginFailed(ctx, LoginErrorCode.STATE_CODE_IS_ERR,
                        LoginErrorCode.INIT_PLAYER + " state:" + state);
                return;
            }
        } else {
            if (state != CTXState.VERIFY_DB_FINISH && state != CTXState.SKIP_VERIFY_DB) {
                loginFailed(ctx, LoginErrorCode.STATE_CODE_IS_ERR,
                        LoginErrorCode.INIT_PLAYER + " state:" + state);
                return;
            }
        }
        int roleId = player.getPlayerId();
        if (playerBean != null) {
            roleId = playerBean.getPlayerId();
        }
        // 再次判断缓存中的Player对象是否一致
        Player _player = PlayerManager.getPlayerByPlayerId(roleId);
        if (_player != null) {
            ChannelHandlerContext oldCtx = _player.getCtx();
            if (oldCtx != null) {
                if (oldCtx.equals(ctx)) {
                    LOGGER.warn(ConstDefine.LOG_LOGIN_PREFIX
                            + (isReconnect ? "在线状态发送重连," : "" + "客户端多次登录消息.") + player.logInfo()
                            + ChannelUtils.logInfo(ctx));
                    return;
                } else {
                    LOGGER.warn(ConstDefine.LOG_LOGIN_PREFIX + "当前用户在线,开始顶号: " + player.logInfo()
                            + " ,oldCtx:" + ChannelUtils.logInfo(oldCtx) + ",newCtx:"
                            + ChannelUtils.logInfo(ctx));
                    // 清除CHANNEL_INFO是为了让老连接断开是不去影响新登录的连接,
                    oldCtx.channel().attr(AttributeKeys.CHANNEL_INFO).set(null);
                    // 设置为被顶号状态
                    oldCtx.channel().attr(AttributeKeys.CHANNEL_STATUS)
                            .set(LoginService.CTXState.BE_REPLACE_ACCOUNT);
                    // 告知双方账号在其他地方有登录，并等待客户端主动关闭连接
                    SMessage msg = new SMessage(S2CLoginMsg.LogoutSuc.MsgID.eMsgID_VALUE,
                            LoginErrorCode.OFF_SITE_LANDING);
                    MessageUtils.send(oldCtx, msg);
                }
            }
        }

        ChannelInfo channelInfo = ctx.channel().attr(AttributeKeys.CHANNEL_INFO).get();

        // 防沉迷
        int anti = channelInfo.getAntiAddiction();
        InfoManager infoManager = player.getInfoManager();
        infoManager.setAntiStatus(anti);
        // 防止防沉迷系统不跨天
        ((IAcrossDay) infoManager).tickAcrossDay(infoManager, System.currentTimeMillis(), false);
        if (infoManager.isAnti()) {
            loginFailed(ctx, LoginErrorCode.ANTI_ADDICTION,
                    "init player. anti-addiction, RoleId:" + roleId + ",from:" + playerFrom);
            ctx.channel().attr(AttributeKeys.CHANNEL_INFO).set(null);
            return;
        }

        ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).set(CTXState.REGISTER_PLAYER);

        channelInfo.setRoleId(player.getPlayerId());

        // 清空挂起的数据库原始数据
        channelInfo.setRoleBean(null);

        // player数据不是从现有内存中加载的
        if (playerFrom != PlayerFrom.INMEMPLAYER) {
            // 注册Player
            boolean isSucc = PlayerManager.register(player);
            if (!isSucc) {
                loginFailed(ctx, LoginErrorCode.UNKNOWN,
                        "init player. player choose logicProcess failed, RoleId:" + roleId
                                + ",from:" + playerFrom + ChannelUtils.logInfo(ctx));
                return;
            }
        }

        // 设置player的网络属性
        {
            player.setCtx(ctx);
            player.setIP(channelInfo.getIpAddress());
            player.setState(Player.PlayerState.ONLINE);
            player.setLoginTime(System.currentTimeMillis());
            ChannelHandlerContext context = player.getCtx();
            context.channel().attr(AttributeKeys.CHANNEL_STATUS).set(CTXState.NORMAL);
            // 为player 分配令牌
            player.setToken(new Token(channelInfo));
        }

        PlayerProcessorManager.getInstance().addPlayerHandler(player,
                LoginService.getInstance().new LLoginInit(player));

        loginSuccess(player, playerFrom == PlayerFrom.CREATEPLAYER);
        // 记日志
        try {
            if (PlayerFrom.CREATEPLAYER.equals(playerFrom)) {
                LogProcessor.getInstance().sendLog(new CreateRoleLog(player));
            } else {
                LogProcessor.getInstance().sendLog(new SdkLoginLog(player));
            }
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    @Override
    public void loginFailed(ChannelHandlerContext ctx, int errorCode, String note) {
        LoginService.getInstance().ctxs.remove(ctx.hashCode());
        LOGGER.warn(ConstDefine.LOG_LOGIN_PREFIX + "loginFaild.errorCode:" + errorCode
                + (note == null ? "" : " note:" + note) + " ctx:" + ChannelUtils.logInfo(ctx)
                                + ExceptionEx.currentThreadTraces());
        if (ChannelUtils.isDisconnectChannel(ctx)) {
            return;
        }
        SMessage msg = new SMessage(S2CLoginMsg.EnterSuc.MsgID.eMsgID_VALUE, errorCode);
        MessageUtils.send(ctx, msg);
    }

    @Override
    public void loginSuccess(Player player, boolean isFirstLogin) {
        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "login success." + player.logInfo());

        _loginSend(player, isFirstLogin);

        // 向redis推送View
        PlayerProcessorManager.getInstance().addPlayerHandler(player,
                LoginService.getInstance().new LPlayerViewUpdate(player));
        ChannelHandlerContext ctx = player.getCtx();
        if (ctx != null) {
            LoginService.getInstance().ctxs.remove(ctx.hashCode());
        }
    }

    private void _loginSend(Player player, boolean isFirstLogin) {
        S2CLoginMsg.EnterSuc.Builder builder = S2CLoginMsg.EnterSuc.newBuilder();
        builder.setQueue(0);
        builder.setQueueTime(0);
        builder.setServerTime((int) (System.currentTimeMillis() / 1000));
        // 玩家信息
        builder.setPlayerinfo(PlayerMsgBuilder.createPlayerInfo(player, isFirstLogin));
        MessageUtils.send(player, builder);
    }

    /**
     * 重复连接
     * 
     * @param ctx
     * @param isFirstLogin
     */
    public boolean _repeatLogin(ChannelHandlerContext ctx, boolean isFirstLogin) {
        // 特殊情况 避免客户端重复发送登陆,尽量让玩家进入游戏
        CTXState state = ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).get();
        if (state == CTXState.NORMAL) {
            Player player = PlayerManager.getPlayerByCtx(ctx);
            LOGGER.warn(ConstDefine.LOG_LOGIN_PREFIX + "特殊情况 避免客户端重复发送登陆,尽量让玩家进入游戏"
                    + ChannelUtils.logInfo(ctx) + (player == null ? "" : player.logInfo()));
            if (player != null) {
                _loginSend(player, false);
                return true;
            }
        }
        return false;
    }

    @Override
    public void resLoginQueue(ChannelHandlerContext ctx, int queue, int queueTime) {
        S2CLoginMsg.EnterSuc.Builder builder = S2CLoginMsg.EnterSuc.newBuilder();
        builder.setQueue(queue);
        builder.setServerTime((int) (System.currentTimeMillis() / 1000));
        builder.setQueueTime(queueTime);
        MessageUtils.send(ctx, builder, LoginService.getInstance().factory);
    }

    @Override
    public void tick() {
        Object[] channelHandlerContexts =
                LoginService.getInstance().ctxs.values().toArray();
        for (Object obj : channelHandlerContexts) {
            ChannelHandlerContext ctx = (ChannelHandlerContext) obj;
            if (ChannelUtils.isDisconnectChannel(ctx)) {
                LOGGER.warn(ConstDefine.LOG_LOGIN_PREFIX + "登陆线程记录的正在登陆的连接失效"
                        + ChannelUtils.logInfo(ctx));
                LoginService.getInstance().ctxs.remove(ctx.hashCode());
            }
        }
    }

    @Override
    public void logout(Player player) {
        long now = System.currentTimeMillis();
        try {
            LogProcessor.getInstance().sendLog(new LogoutLog(player));

        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "player in logout." + player.logInfo());

        AttributeKeys.clear(player.getCtx());
        player.setCtx(null);
        player.setState(PlayerState.OFFLINE);
        player.isClosingTime = 0l;

        long cur = System.currentTimeMillis();
        player.setOfflineTime(cur);
        // 本次在线时长
        int curOnlineTime = (int) (cur - player.getLoginTime());
        // 总在线时长
        long onlineTime = player.getOnlineTime() + curOnlineTime;
        player.setOnlineTime(onlineTime);
        player.setLastLoginTime(player.getLoginTime());

        long off = System.currentTimeMillis();
        player.managerOffline();
        long offtime = (System.currentTimeMillis() - off);
        if (offtime > 100) {
            LOGGER.error(ConstDefine.LOG_DO_OVER_TIME + " logout. offtime:" + offtime);
        }

        player.setDirtyKey(player.toPlayerBean());

        // 存放Player data 中相关数据必须放在save方法之前
        PlayerDBBean dbBean = player.save(true);

        long redis = System.currentTimeMillis();
        // io操作
        try {
            ChatService.getInstance().exitChatRoom(player.getPlayerId());
            TeamService.getDefault().exitChasmTeam(player.getPlayerId());
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
        long redistime = (System.currentTimeMillis() - redis);
        if (redistime > 100) {
            LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " logout redistime:" + redistime);
        }

        // 注意 必须保证该操作都在一个线程内执行
        PlayerDaoService playerDaoService = SpringContextUtils
                .getBean(ESpringConextType.PlAYER.getType(), PlayerDaoService.class);
        playerDaoService.updatePlayerLvlRedis(player.getPlayerId(), player.getLevel());

        // 持有player到player缓存中
        LRUCache<Integer, FriendView> lruCache =
                FriendService.PLAYER_LEVEL_MAP_FROM_DB.get(player.getLevel());
        if (lruCache != null) {
            FriendView _player = null;
            try {
                _player = new FriendView(dbBean, true);
            } catch (Exception e) {
                LOGGER.error(ExceptionEx.e2s(e));
            }
            if (_player != null) {
                lruCache.put(player.getPlayerId(), _player);
            }
        } else {
            LOGGER.error("lruCache is null, level id:" + player.getLevel() + player.logInfo());
        }

        long logoutTime = (System.currentTimeMillis() - now);
        if (logoutTime > 300) {
            LOGGER.warn(ConstDefine.LOG_DO_OVER_TIME + " logout. time:" + logoutTime);
        }
    }

    @Override
    public String ctxInfo(ChannelHandlerContext ctx) {
        CTXState _cStatus = ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).get();
        ChannelInfo _cInfo = ctx.channel().attr(AttributeKeys.CHANNEL_INFO).get();
        return "{\"ctxInfo\":" + ctx.channel().remoteAddress() + "@" + ctx.hashCode()
                + (_cInfo == null ? ""
                        : (",channelInfo:" + "accountId:" + _cInfo.getAccountId() + ",playerId:"
                                + _cInfo.getPlayerId() + ",token:" + _cInfo.getToken()))
                + "}" + (_cStatus != null ? ("_cStatus:" + _cStatus.name()) : "");
    }
}
