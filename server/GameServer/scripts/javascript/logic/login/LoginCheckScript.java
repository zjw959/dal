package javascript.logic.login;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;

import java.util.concurrent.ConcurrentLinkedQueue;

import logic.character.PlayerManager;
import logic.constant.ConstDefine;
import logic.constant.EScriptIdDefine;
import logic.constant.LoginErrorCode;
import logic.gloabl.GlobalService;
import logic.login.bean.LoginCheckBean;
import logic.login.handler.LResLoginCheckSuccHandler;
import logic.login.handler.LoginCheckHttpCallBack;
import logic.login.service.ILoginCheckScript;
import logic.login.service.ILoginScript;
import logic.login.service.LoginCheckService;
import logic.login.service.LoginService;
import logic.login.service.LoginService.CTXState;
import logic.login.struct.ChannelInfo;
import logic.login.struct.ParamKey;
import logic.support.LogicScriptsUtils;
import net.AttributeKeys;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import server.GameServer;
import server.ServerConfig;
import utils.ChannelUtils;
import utils.ExceptionEx;

import com.alibaba.fastjson.JSONObject;

public class LoginCheckScript implements ILoginCheckScript {
    private static final Logger LOGGER = Logger.getLogger(LoginCheckScript.class);

    @Override
    public int getScriptId() {
        return EScriptIdDefine.LOGIN_CHECK_SCRIPT.Value();
    }

    @Override
    public void addQueue(LoginCheckBean bean) {
        ConcurrentLinkedQueue<LoginCheckBean> queue = LoginCheckService.getInstance().getQueue();
        if (queue.size() >= GlobalService.LOGIN_QUEUE_MAX) {
            LOGGER.warn("登陆队列人数已满:[" + queue.size() + "," + GlobalService.LOGIN_QUEUE_MAX + "]");
            ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
            script.loginFailed(bean.getCtx(), LoginErrorCode.LOGIN_QUEUE_IS_MAX,
                    "login failed. token=" + bean.getToken() + " result=login queue is max!");
            return;
        }
        if (bean == null) {
            LOGGER.error("addQueue bean is null");
            return;
        }
        boolean b = queue.add(bean);
        if (b) {
            int que = queue.size();
            que = que > 0 ? que : 1;
            int time = this.getQueueTime(que);
            bean.getCtx().channel().attr(AttributeKeys.CHANNEL_STATUS)
                    .set(LoginService.CTXState.LOGIN_QUEUE);

            // 通知新加入队列的玩家当前排队号
            ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
            script.resLoginQueue(bean.getCtx(), que, time);
        }

        if (!b) {
            ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
            script.loginFailed(bean.getCtx(), LoginErrorCode.LOGIN_QUEUE_IS_MAX,
                    "login failed. token=" + bean.getToken() + " result=login queue is max!");
        }
    }

    @Override
    public void reqLoginCheck(LoginCheckBean bean) {
        CloseableHttpAsyncClient client = LoginCheckService.getInstance().getClient();
        RequestConfig requestConfig = LoginCheckService.getInstance().getRequestConfig();
        if (bean.getMethod().equalsIgnoreCase("GET")) {
            HttpGet get = new HttpGet(bean.getUrl());
            get.setConfig(requestConfig);
            client.execute(get, new LoginCheckHttpCallBack(bean));
        } else if (bean.getMethod().equalsIgnoreCase("POST")) {
            HttpPost post = new HttpPost(bean.getUrl());
            post.setConfig(requestConfig);
            post.setEntity((StringEntity) bean.getParameter());
            client.execute(post, new LoginCheckHttpCallBack(bean));
        } else {
            LOGGER.error(ConstDefine.LOG_LOGIN_PREFIX + " 不支持的http请求方式: " + bean.getMethod()
                    + ",beanInfo: remoteAddress:" + bean.getRemoteAddress() + ",token:"
                    + bean.getToken() + ",ctx:" + bean.getCtx());
            ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
            script.loginFailed(bean.getCtx(), LoginErrorCode.LOGIN_TIME_OUT, "login failed. token="
                    + bean.getToken() + " result=" + "invalid http request:" + bean.getMethod());
        }
    }

    @Override
    public void loginCheckCallBack(LoginCheckBean bean, HttpResponse t) {
        try {
            ChannelHandlerContext ctx = bean.getCtx();

            /** 状态验证 验证是否重复消息等 */
            CTXState state = ctx.channel().attr(AttributeKeys.CHANNEL_STATUS).get();
            if (state == null || state != CTXState.VERIFY_ACCOUNT) {
                ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
                script.loginFailed(ctx, LoginErrorCode.STATE_CODE_IS_ERR,
                        state == null ? "loginstate is null" : "state is:" + state);
                return;
            }

            ctx.channel().attr(AttributeKeys.CHANNEL_STATUS)
                    .set(LoginService.CTXState.VERIFY_ACCOUNT_CALLBACK);

            String result = EntityUtils.toString(t.getEntity());
            JSONObject json = JSONObject.parseObject(result);
            int errno = json.getIntValue("errno");
            if (errno != 0) {
                ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
                script.loginFailed(ctx, errno,
                        "login failed. token is invalid. token=" + bean.getToken() + " errno="
                                + errno + " errmsg=" + json.getString("errmsg"));
                return;
            }
            if (ChannelUtils.isDisconnectChannel(ctx)) {
                LOGGER.info(ConstDefine.LOG_LOGIN_PREFIX + "登陆验证结果回来时, ctx已经失效了. ctx:"
                        + ChannelUtils.logInfo(ctx));
                return;
            }
            String ip = ctx.channel().remoteAddress().toString();
            JSONObject account = json.getJSONObject("data");
            String accountId = account.getString(ParamKey.ACCOUNT_ID);
            boolean isWhiteUser = account.getBooleanValue(ParamKey.IS_WHITE_USER);
            // 服务器状态验证
            if (GameServer.getInstance().isMaintaining() && !isWhiteUser) {
                ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
                script.loginFailed(ctx, LoginErrorCode.STATE_AEGIS,
                        "server is in maintain status. accountId:" + accountId + " ip:" + ip
                                + " serverStatus:" + GameServer.getInstance().getStatus());
                return;
            }
            int serverId = account.getIntValue(ParamKey.SERVER_ID);
            if (serverId != 0 && serverId != ServerConfig.getInstance().getServerId()) {
                ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
                script.loginFailed(ctx, LoginErrorCode.TOKEN_IS_OUT_OF_TIME,
                        "login failed. serverId is not same. oldServerId=" + serverId
                                + " nowServerId=" + ServerConfig.getInstance().getServerId());
                return;
            }

            boolean isBusy = false;

            // 服务器繁忙验证,判定条件走开关
            if (GlobalService.MEMORY_BUSY_SWITCH) {
                if (PlayerManager.getAllNum() >= GlobalService.ONLINE_MAX
                        * GlobalService.MEMORY_MAX) {
                    isBusy = true;
                }
            }

            // 重连用户不受内存限制(大概率在内存中)
            if (isBusy && !bean.isReconnect()) {
                ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
                script.loginFailed(ctx, LoginErrorCode.SERVER_BUSY, "login failed. server is busy.");
                return;
            }

            ctx.channel().attr(AttributeKeys.CHANNEL_STATUS)
                    .set(LoginService.CTXState.VERIFY_ACCOUNT_FINISH);

            ChannelInfo channelInfo = new ChannelInfo();
            channelInfo.setAccountId(account.getString(ParamKey.ACCOUNT_ID));
            channelInfo.setChannelId(account.getString(ParamKey.CHANNEL_ID));
            channelInfo.setChannelAppId(account.getString(ParamKey.CHANNEL_APP_ID));
            channelInfo.setClientVersion(account.getString(ParamKey.CLIENT_VERSION));
            channelInfo.setOsName(account.getString(ParamKey.OS_NAME));
            channelInfo.setOsVersion(account.getString(ParamKey.OS_VERSION));
            channelInfo.setDeviceId(account.getString(ParamKey.DEVICE_ID));
            channelInfo.setDeviceToken(account.getString(ParamKey.DEVICE_TOKEN));
            channelInfo.setDeviceName(account.getString(ParamKey.DEVICE_NAME));
            channelInfo.setSdk(account.getString(ParamKey.SDK));
            channelInfo.setSdkVersion(account.getString(ParamKey.SDK_VERSION));
            channelInfo.setIpAddress(ip);
            channelInfo.setPlayerId(account.getIntValue(ParamKey.PLAYER_ID));// 由登录服统一分配角色id
            channelInfo.setKeepSilent(account.getBooleanValue(ParamKey.SERVER_STATE));
            channelInfo.setAntiAddiction(bean.getAntiStatus());// 防沉迷
            channelInfo.setAdminLogin(account.getBooleanValue(ParamKey.IS_ADMIN_LOGIN));// 是否是托管登录
            channelInfo.setWhiteUser(account.getBooleanValue(ParamKey.IS_WHITE_USER));// 是否是白名单用户
            channelInfo.setNetworkType(account.getString(ParamKey.NET_WORK_TYPE));
            channelInfo.setNetworkCarrier(account.getString(ParamKey.NET_WORK_CARRIER));
            channelInfo.setScreenWidth(account.getString(ParamKey.SCREEN_WIDTH));
            channelInfo.setScreenHeight(account.getString(ParamKey.SCREEN_HEIGHT));
            channelInfo.setAppVersion(account.getString(ParamKey.APP_VERSION));
            channelInfo.setDevicebrand(account.getString(ParamKey.DEVICE_BRAND));
            channelInfo.setIdfa(account.getString(ParamKey.IDFA));
            channelInfo.setImei(account.getString(ParamKey.IMEI));
            channelInfo.setAndroidid(account.getString(ParamKey.ANDROID_ID));
            channelInfo.setMobile(account.getString(ParamKey.MOBILE));
            
            ctx.channel().attr(AttributeKeys.CHANNEL_INFO).set(channelInfo);
            // 抛回登陆线程
            LoginService.getInstance().getProcess()
                    .executeInnerHandler(new LResLoginCheckSuccHandler(bean));
        } catch (Exception e) {
            LOGGER.error(ExceptionEx.e2s(e));
        }
    }

    @Override
    public void tick() {
        ConcurrentLinkedQueue<LoginCheckBean> queue = LoginCheckService.getInstance().getQueue();
        if (queue.size() == 0) {
            return;
        }
        // 清理失效的连接
        Object[] array = queue.toArray();
        LoginCheckBean bean = null;
        for (int i = 0; i < array.length; i++) {
            bean = (LoginCheckBean) array[i];
            if (ChannelUtils.isDisconnectChannel(bean.getCtx())) {
                queue.remove(bean);
            }
        }
        int onlineNum = PlayerManager.getOnlineNum();
        if (onlineNum < GlobalService.ONLINE_MAX) {
            int logincount = LoginService.getInstance().ctxs.size();
            int dis = GlobalService.ONLINE_MAX - (logincount + onlineNum);
            // 如果在线没满 但登陆队列满员的情况
            if (dis <= 0) {
                if (logincount > 0 && onlineNum < GlobalService.ONLINE_MAX) {
                    dis = (GlobalService.ONLINE_MAX - onlineNum) / 10;
                    if (dis > 100) {
                        dis = 100;
                    }
                    if (dis < 10) {
                        dis = 10;
                    }
                }
            }

            for (int i = 0; i < dis; i++) {
                LoginCheckBean _bean = queue.poll();
                try {
                    if (_bean == null) {
                        continue;
                    }
                    ChannelHandlerContext context = _bean.getCtx();
                    Attribute<CTXState> attribute =
                            context.channel().attr(AttributeKeys.CHANNEL_STATUS);
                    attribute.set(CTXState.LOGIN_QUEUE_DONE);
                    LoginService.getInstance().getProcess()
                            .executeInnerHandler(new LResLoginCheckSuccHandler(_bean));
                } catch (Exception e) {
                    LOGGER.error(ExceptionEx.e2s(e));
                }
            }
            long disTime =
                    System.currentTimeMillis() - LoginCheckService.getInstance().getLastEnterTime();
            int queueUnitTime = (int) (disTime / 1000 / dis);
            if (queueUnitTime > 2 * 60) {
                queueUnitTime = 2 * 60;
            } else if (queueUnitTime == 0) {
                queueUnitTime = 1;
            }
            LoginCheckService.getInstance().setQueueUnitTime(queueUnitTime);
            LoginCheckService.getInstance().setLastEnterTime(System.currentTimeMillis());
        }
        // 通知排队的人刷新排队号
        sendLoginQueue();
    }

    private void sendLoginQueue() {
        ConcurrentLinkedQueue<LoginCheckBean> queue = LoginCheckService.getInstance().getQueue();
        if (queue.size() == 0) {
            return;
        }
        Object[] array = queue.toArray();
        LoginCheckBean bean = null;
        for (int i = 0; i < array.length; i++) {
            bean = (LoginCheckBean) array[i];
            int que = i + 1;
            int time = getQueueTime(que);
            ILoginScript script = LogicScriptsUtils.getLoginHttpScript();
            script.resLoginQueue(bean.getCtx(), i + 1, time);
        }
    }

    /** 发送给客户端当前排队时长 */
    private int getQueueTime(int queue) {
        int time = queue * LoginCheckService.getInstance().getQueueUnitTime() / 60;
        int max = 12 * 60;// 预计排队时间最多12小时
        int min = 1;// 预计排队时间最少1分钟
        if (time > max) {
            time = max;
        } else if (time < min) {
            time = min;
        }
        return time;// 返回分钟数
    }

}
