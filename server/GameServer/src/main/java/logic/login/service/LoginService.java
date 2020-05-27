package logic.login.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.game.protobuf.s2c.S2CLoginMsg;

import io.netty.channel.ChannelHandlerContext;
import logic.character.PlayerViewService;
import logic.character.bean.Player;
import logic.constant.ConstDefine;
import logic.constant.LoginErrorCode;
import logic.support.MessageUtils;
import message.SMessage;
import message.SMessageFactory;
import thread.base.LBaseHandler;
import thread.sys.base.SysService;
import utils.ChannelUtils;

public class LoginService extends SysService {

    private static final Logger LOGGER = Logger.getLogger(LoginService.class);

    public SMessageFactory factory = new SMessageFactory(200);

    public volatile Map<Integer, ChannelHandlerContext> ctxs = new ConcurrentHashMap<>();

    // public volatile AtomicLong count = new AtomicLong();

    /**
     * 通知客户端被服务器强制下线
     * 
     * @param ctx
     * @param reason
     */
    public static void sendForceOffline(ChannelHandlerContext ctx) {
        LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + " sendForceOffline ctx:"
                + ChannelUtils.logInfo(ctx));
        if (ChannelUtils.isDisconnectChannel(ctx)) {
            return;
        }
        SMessage msg =
                new SMessage(S2CLoginMsg.EnterSuc.MsgID.eMsgID_VALUE, LoginErrorCode.FORCE_OFFLINE);
        MessageUtils.send(ctx, msg);
    }

    public class LPlayerViewUpdate extends LBaseHandler {

        public LPlayerViewUpdate(Player player) {
            super(player);
        }

        @Override
        public void action() throws Exception {
            PlayerViewService.getInstance().updatePlayerView(player.toPlayerBean(), true,
                    player.getLevel());
        }
    }
    
    public class LLoginInit extends LBaseHandler {

        public LLoginInit(Player player) {
            super(player);
        }

        @Override
        public void action() throws Exception {
            player.loginInit();
        }
    }
    
    public enum CTXState {
        /** 建立连接 */
        CONNECT,
        /** 验证账号 */
        VERIFY_ACCOUNT,
        /** 用户中心验证 **/
        VERIFY_ACCOUNT_CALLBACK, VERIFY_ACCOUNT_FINISH,
        /** 排队中 **/
        LOGIN_QUEUE,
        LOGIN_QUEUE_DONE,
        /** 内存中存在,跳过数据库查询角色 */
        SKIP_VERIFY_DB,
        /** 数据库查询角色 */
        VERIFY_DB,
        /** 查询完成 **/
        VERIFY_DB_FINISH,
        /** 等待创建角色 */
        WAITFOR_CLIENT_CREATE_ROLE,
        /** 创建角色 写数据库 */
        CREATE_ROLE_DB, CREATE_ROLE_DB_FINISH,
        /** 注册PLAYER **/
        REGISTER_PLAYER,
        /** 正常状态 */
        NORMAL,
        /** 被顶号状态 */
        BE_REPLACE_ACCOUNT,
    }

    /** 初始化PLAYER状态 **/
    public enum PlayerFrom {
        /** 注册账号 **/
        CREATEPLAYER,
        /** 从内存中读取 **/
        INMEMPLAYER,
        /** 从数据库中读取 **/
        DBPLAYER
    }

    public static LoginService getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        LoginService instance;

        private Singleton() {
            instance = new LoginService();
        }

        LoginService getInstance() {
            return instance;
        }
    }
}
