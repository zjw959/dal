package javascript.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import utils.PlayerIdAdaptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.constant.ChannelType;
import com.constant.EScriptIdDefine;
import com.constant.LoginErrorCode;
import com.controller.AccountController;
import com.enity.AccountInfo;
import com.enity.AccountPermissionInfo;
import com.enity.ServerGroup;
import com.enity.ServerInfo;
import com.iscript.IAccountControllerScript;
import com.service.AccountPermissionService;
import com.service.AccountService;
import com.service.ServerListManager;
import com.utils.HttpBaseRequest;

/**
 * 
 * @Description 账号登录
 * @author LiuJiang
 * @date 2018年8月20日 下午9:50:21
 *
 */
public class AccountControllerScript extends IAccountControllerScript {
    private static final Logger log = Logger.getLogger(AccountControllerScript.class);
    private static String ht_url = "http://smi.heitao.com/yhdzz/auth";

    @Override
    public int getScriptId() {
        return EScriptIdDefine.ACCOUNT_CONTROLLER_SCRIPTID.Value();
    }
    
    @Override
    public String getServerInfo(AccountController accountContoller,
            HttpServletRequest request) throws ClientProtocolException, IOException {
        AccountService accountService = accountContoller.getAccountService();
        ServerListManager serverListManager = accountContoller.getServerListManager();
        AccountPermissionService accountPermissionService =
                accountContoller.getAccountPermissionService();
        boolean isLocalTest = accountContoller.isLocalTest();

        // 必要的登陆信息
        String accountId = request.getParameter("accountId");// 渠道账号id
        accountId = accountId.toLowerCase();
        String channelId = request.getParameter("channelId");// 渠道标示
        String token = request.getParameter("token");
        String serverName = request.getParameter("serverName");// --内网用于指定登陆服务器
        String serverGroupName = request.getParameter("serverGroup");

        // 本次登陆玩家设备、客户端信息 ----该信息只在redis做记录 木有哪里用到该些信息
        String channelAppId = request.getParameter("channelAppId");
        String deviceName = request.getParameter("deviceName");
        String deviceId = request.getParameter("deviceid");
        String sdk = request.getParameter("sdk");
        String sdkVersion = request.getParameter("sdkVersion");
        String osName = request.getParameter("osName");
        String osVersion = request.getParameter("osVersion");
        String clientVersion = request.getParameter("version");
        String networkType = request.getParameter("networkType");
        String networkCarrier = request.getParameter("networkCarrier");
        String screenWidth = request.getParameter("screenWidth");
        String screenHeight = request.getParameter("screenHeight");
        String appVersion = request.getParameter("appVersion");
        String devicebrand = request.getParameter("devicebrand");
        String idfa = request.getParameter("idfa");
        String imei = request.getParameter("imei");
        String androidid = request.getParameter("androidid");
        String ip = HttpBaseRequest.getDefault().getIpAddr(request);

//        log.info("player login start !param out:accountId={},channelId={},token={},serverName={},"
//                + "serverGroup={},channelAppId={},deviceName={},deviceId={},sdk={}"
//                + ",sdkVersion={},osName={},osVersion={},clientVersion={},ip={}", accountId,
//                channelId, token, serverName, serverGroupName, channelAppId, deviceName, deviceId,
//                sdk, sdkVersion, osName, osVersion, clientVersion, ip);
        log.info("player login start !param out:accountId=" + accountId + ",channelId=" + channelId
                + ",token=" + token + ",serverName=" + serverName + "," + "serverGroup="
                + serverGroupName + ",channelAppId=" + channelAppId + ",deviceName=" + deviceName
                + ",deviceId=" + deviceId + ",sdk=" + sdk + "" + ",sdkVersion=" + sdkVersion
                + ",osName=" + osName + ",osVersion=" + osVersion + ",clientVersion="
                + clientVersion + ",networkType=" + networkType + ",networkCarrier="
                + networkCarrier + ",screenWidth=" + screenWidth + ",screenHeight=" + screenHeight
                + ",appVersion=" + appVersion + ", devicebrand=" + devicebrand + ", idfa=" + idfa
                + ", imei=" + imei + ", androidid=" + androidid+ ", ip=" + ip);
        long now = System.currentTimeMillis();
        LoginErrorCode errCode = LoginErrorCode.SUCCESS;
        JSONObject object = new JSONObject();
        String mobile = "";// 手机号
        if (channelAppId == null || "".equals(channelAppId.trim())) {
            channelAppId = "-1";
        }
        // 测试环境下 token由用户中心生成并保存
        if (ChannelType.HEI_TAO.equals(channelId)) {
            errCode = htVerifyer(accountId, token, mobile);
        } else if (ChannelType.LOCAL_TEST.equals(channelId)) {
            if (!isLocalTest) {
                errCode = LoginErrorCode.NOT_FOND_CHANNEL;
            } else {
                // 本地测试登录，不做验证
                token = UUID.randomUUID().toString();
            }
        } else {
            errCode = LoginErrorCode.NOT_FOND_CHANNEL;
        }

        if (errCode == LoginErrorCode.SUCCESS) {
            AccountInfo accountInfo =
                    accountService.findByAccountIdAndChannel(accountId,
                            channelId);
            // 将一些不需要存数据库的数据和token都存在缓存中 方便取出来
            accountInfo.setToken(token);
            accountInfo.setChannelAppId(channelAppId);
            accountInfo.setDeviceName(deviceName);
            accountInfo.setDeviceId(deviceId);
            accountInfo.setSdk(sdk);
            accountInfo.setSdkVersion(sdkVersion);
            accountInfo.setOsName(osName);
            accountInfo.setOsVersion(osVersion);
            accountInfo.setClientVersion(clientVersion);
            accountInfo.setNetworkType(networkType);
            accountInfo.setNetworkCarrier(networkCarrier);
            accountInfo.setScreenWidth(screenWidth);
            accountInfo.setScreenHeight(screenHeight);
            accountInfo.setAppVersion(appVersion);
            accountInfo.setDevicebrand(devicebrand);
            accountInfo.setIdfa(idfa);
            accountInfo.setImei(imei);
            accountInfo.setAndroidid(androidid);
            accountInfo.setMobile(mobile);

            if (accountInfo.getPlayerId() == 0) {
                int playerIdAdaptor = PlayerIdAdaptor.change(accountInfo.getId());
                accountInfo.setPlayerId(playerIdAdaptor);
                accountService.putAccountIdAndChannel(accountInfo);
            }
            // 判断账号是否被封号
            AccountPermissionInfo perInfo =
                    accountPermissionService.findByPlayerIdAndType(
                            accountInfo.getPlayerId(),
                            AccountPermissionService.ban);
            if (perInfo != null && perInfo.isEnable()) {
                errCode = LoginErrorCode.ACCOUNT_INTERCEPT;
                object.put("status", errCode.getCode());
                object.put("msg", errCode.getMsg());
                String result = JSON.toJSONString(object, true);
                return result;
            }
            // 是否是白名单账号
            boolean isWhiteUser = false;
            AccountPermissionInfo perInfo_white =
                    accountPermissionService.findByPlayerIdAndType(accountInfo.getPlayerId(),
                            AccountPermissionService.white_user);
            if (perInfo_white != null && perInfo_white.isEnable()) {
                isWhiteUser = true;
            }
            accountInfo.setWhiteUser(isWhiteUser);
            ServerInfo serverInfo = null;

            if (isLocalTest) {// 内网
                // 1、首先看serverName有没有指定服务器 有就直接进入
                if (serverName != null && !serverName.isEmpty()) {
                    serverInfo = serverListManager.getServerByName(serverName);
                }
            }

            if (serverInfo == null) {
                serverInfo =
                        allocationServer(accountService, serverListManager,
                                accountPermissionService, isLocalTest, accountInfo,
                                serverGroupName, appVersion, osName);
            }

            if (serverInfo != null) {
                accountInfo.setServerId(serverInfo.getId());
                accountInfo.setAdminLogin(false);
                accountService.putAccountByToken(accountInfo);
                accountService.putAccountIdAndChannel(accountInfo);
                accountService.putServerIdByPlayerId(accountInfo.getPlayerId(), serverInfo.getId());
                JSONObject obj = new JSONObject();
                obj.put("token", token);
                obj.put("gameServerIp", serverInfo.getGameServerExternalIp().trim());
                obj.put("gameServerPort", serverInfo.getGameServerTcpPort());
                object.put("data", obj);
                log.info("player login end! used times=" + (System.currentTimeMillis() - now)
                        + ",playerId=" + accountInfo.getPlayerId() + ",accountId="
                        + accountInfo.getAccountId() + ",channelId=" + accountInfo.getChannelId()
                        + ",serverId=" + serverInfo.getId() + ",serverName=" + serverInfo.getName()
                        + ",serverIP=" + serverInfo.getGameServerExternalIp() + ":"
                        + serverInfo.getGameServerTcpPort());
            } else {
                errCode = LoginErrorCode.STATE_AEGIS;
                log.info(
                        "player login end! no have using server, used times="+(System.currentTimeMillis() - now)+",playerId="
                +accountInfo.getPlayerId()+",accountId="+accountInfo.getAccountId()+",channelId="+accountInfo.getChannelId());
            }
        }
        object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
        String result = JSON.toJSONString(object, true);
        return result;
    }

    @Override
    public String adminLogin(AccountController accountContoller, HttpServletRequest request)
            throws Exception {
        AccountService accountService = accountContoller.getAccountService();
        ServerListManager serverListManager = accountContoller.getServerListManager();
        AccountPermissionService accountPermissionService =
                accountContoller.getAccountPermissionService();
        String pid = request.getParameter("pid");// 玩家角色id
        log.info("adminLogin start, pid:" + pid);
        LoginErrorCode errCode = LoginErrorCode.SUCCESS;
        JSONObject object = new JSONObject();
        int playerId = Integer.parseInt(pid);
        AccountInfo accountInfo = accountService.findByPlayerId(playerId);
        if (accountInfo == null) {
            errCode = LoginErrorCode.PID_IS_WRONG;
        } else {
            AccountPermissionInfo perInfo =
                    accountPermissionService.findByPlayerIdAndType(accountInfo.getPlayerId(),
                            AccountPermissionService.ban);
            if (perInfo == null || !perInfo.isEnable()) {
                errCode = LoginErrorCode.NOT_FREEZE;
            } else {
                int serverId = accountInfo.getServerId();
                ServerInfo serverInfo = serverListManager.getServerById(serverId);
                log.info("adminLogin, serverId:" + serverId + "  serverInfo:" + serverInfo);
                if (serverInfo != null) {
                    String token = UUID.randomUUID().toString();
                    accountInfo.setServerId(serverInfo.getId());
                    accountInfo.setAdminLogin(true);
                    accountInfo.setToken(token);
                    accountService.putAccountByToken(accountInfo);
                    accountService.putAccountIdAndChannel(accountInfo);
                    accountService.putServerIdByPlayerId(accountInfo.getPlayerId(),
                            serverInfo.getId());
                    JSONObject obj = new JSONObject();
                    obj.put("token", token);
                    obj.put("gameServerIp", serverInfo.getGameServerExternalIp().trim());
                    obj.put("gameServerPort", serverInfo.getGameServerTcpPort());
                    object.put("data", obj);
                } else {
                    errCode = LoginErrorCode.STATE_AEGIS;
                }
            }
        }
        object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
        String result = JSON.toJSONString(object, true);
        log.info("adminLogin end, result=" + result);
        return result;
    }

    @Override
    public String loginVerify(AccountController accountContoller, HttpServletRequest request)
            throws Exception {
        AccountService accountService = accountContoller.getAccountService();
        AccountPermissionService accountPermissionService =
                accountContoller.getAccountPermissionService();
        String token = request.getParameter("token");
        LoginErrorCode errCode = LoginErrorCode.SUCCESS;
        JSONObject obj = new JSONObject();
        if (token == null) {
            errCode = LoginErrorCode.NO_TOKEN;
        } else {
            AccountInfo info = accountService.getAccountByToken(token);
            if (info == null) {
                errCode = LoginErrorCode.TOKEN_IS_OUT_OF_TIME;
            } else {
                JSONObject object = new JSONObject();
                object.put("accountId", info.getAccountId());
                // object.put("admin", 0);
                object.put("channelAppId", info.getChannelAppId());
                object.put("channelId", info.getChannelId());
                object.put("version", info.getClientVersion());
                object.put("createTime", info.getCreateTime());
                object.put("deviceid", info.getDeviceId());
                object.put("deviceName", info.getDeviceName());
                object.put("ip", info.getIp());
                object.put("osName", info.getOsName());
                object.put("osVersion", info.getOsVersion());
                object.put("playerId", info.getPlayerId());
                object.put("sdk", info.getSdk());
                object.put("sdkVersion", info.getSdkVersion());
                object.put("networkType", info.getNetworkType());
                object.put("networkCarrier", info.getNetworkCarrier());
                object.put("screenWidth", info.getScreenWidth());
                object.put("screenHeight", info.getScreenHeight());
                object.put("appVersion", info.getAppVersion());
                object.put("devicebrand", info.getDevicebrand());
                object.put("idfa", info.getIdfa());
                object.put("imei", info.getImei());
                object.put("androidid", info.getAndroidid());
                object.put("mobile", info.getMobile());

                AccountPermissionInfo perInfo =
                        accountPermissionService.findByPlayerIdAndType(info.getPlayerId(),
                                AccountPermissionService.gag);
                boolean state = false;
                if (perInfo != null && perInfo.isEnable()) {
                    state = true;
                }
                object.put("state", state);
                object.put("token", info.getToken());
                // 查询最新当前serverId
                Integer sid = accountService.getLocServerId(info.getPlayerId());
                object.put("serverId", sid);
                object.put("isAdminLogin", info.isAdminLogin());
                object.put("isWhiteUser", info.isWhiteUser());
                obj.put("data", object);
            }
        }
        obj.put("errno", errCode.getCode());
        obj.put("errmsg", errCode.getMsg());
        String result = JSON.toJSONString(obj, true);
        if (errCode != LoginErrorCode.SUCCESS) {
            log.info("---loginVerifyResult=" + result + "  token=" + token);
        }
        return result;
    }

    /**
     * 给玩家分配服务器 规则：先获取上一次登陆的服务器id和分配状态 如果分配状态是不能更换 那么不管服务器人数如何 返回上一次登陆的服务器信息给客户端 如果分配状态是可以更换
     * 1、上一次服务人数不到临界值 -- 还是直接去上一次服务器 2、上一次服务器人数超过临界值 -- 从所有可运行的服务器中挑选人数最少的给玩家
     * 
     * @param accountInfo
     * @param serverGroupName
     * @return
     */
    private ServerInfo allocationServer(AccountService accountService,
            ServerListManager serverListManager, AccountPermissionService accountPermissionService,
            boolean isLocalTest, AccountInfo accountInfo, String serverGroupName,
            String appVersion, String osName) {
        if (serverGroupName == null || serverGroupName.isEmpty()) {
            log.error("serverGroupName is null. accountInfo:" + accountInfo);
            return null;
        }
        ServerGroup serverGroup = serverListManager.getServerGroupByName(serverGroupName);
        if (serverGroup == null) {
            log.error("serverGroup is null. serverGroupName:" + serverGroupName + " accountInfo:"
                    + accountInfo);
            return null;
        }
        // 根据客户端版本号验证是否走审核服
        if (appVersion != null) {
            if (osName != null && "ios".equals(osName.toLowerCase())) {
                if (appVersion.equals(serverGroup.getIosCheckVersion())) {
                    serverGroup =
                            serverListManager.getServerGroupById(serverGroup.getIosCheckGroup());
                }
            } else {
                if (appVersion.equals(serverGroup.getAndroidCheckVersion())) {
                    serverGroup =
                            serverListManager
                                    .getServerGroupById(serverGroup
                                    .getAndroidCheckGroup());
                }
            }
            if (serverGroup == null) {
                log.error("serverGroup is null. osName:" + osName + " appVersion:" + appVersion
                        + " accountInfo:" + accountInfo);
                return null;
            }
        }

        boolean isWhiteUser = false;
        AccountPermissionInfo perInfo =
                accountPermissionService.findByPlayerIdAndType(accountInfo.getPlayerId(),
                        AccountPermissionService.white_user);
        if (perInfo != null && perInfo.isEnable()) {
            isWhiteUser = true;
        }
        if (serverGroup.getMark() == ServerListManager.weihu_status && !isWhiteUser) {
            return null;
        }
        int groupId = serverGroup.getId();
        ServerInfo serverInfo = null;
        // 如果是玩家第一次登陆服务器 第一次分配的时候通过取模判断去哪个服务器
        if (accountInfo.getServerId() == 0) {
            Integer id = accountInfo.getId();
            List<ServerInfo> list = serverListManager.getServerListByGroupId(groupId, isWhiteUser);
            Integer size = list.size();
            if (size == 0) {
                log.info("serverGroup has no using server");
                return null;
            }
            int index = id % size;
            serverInfo = list.get(index);
            // 判断服务器是否人数到达临界值
            if (serverInfo.getOnlineNum() > serverInfo.getSplitFlowNum()) {
                ServerInfo sinfo = serverListManager.getMinOnlineNumServer(groupId, isWhiteUser);
                if (sinfo != null && sinfo.getOnlineNumDegree() != serverInfo.getOnlineNumDegree()) {
                    serverInfo = sinfo;// 在线人数不在同一档位，则替换
                }
                return serverInfo;
            }
        } else {// 如果不是第一次登陆 则获取玩家分配状态
            Integer sid = accountService.getLocServerId(accountInfo.getPlayerId());

            // 如果服务器这边已经处理完所有数据 则sid==-1表示该玩家可以重新分配
            if (sid == -1) {
                sid = accountInfo.getServerId();// 数据库记录的serverid
                serverInfo = serverListManager.getServerById(sid);
                // 判断该服务器是否维护--如果是维护状态则重新分配
                if (serverInfo.getMark() == ServerListManager.weihu_status) {
                    serverInfo = serverListManager.getMinOnlineNumServer(groupId, isWhiteUser);
                    return serverInfo;
                }
                if (serverInfo.getOnlineNum() > serverInfo.getSplitFlowNum()) {
                    ServerInfo sinfo =
                            serverListManager.getMinOnlineNumServer(groupId, isWhiteUser);
                    if (sinfo != null
                            && sinfo.getOnlineNumDegree() != serverInfo.getOnlineNumDegree()) {
                        serverInfo = sinfo;// 在线人数不在同一档位，则替换
                    }
                    return serverInfo;
                }
            } else {
                serverInfo = serverListManager.getServerById(sid);

                // 如果切换了分组 那么就不再重新登陆原服务器 按照分组选择该分组下面分数最少的服务器
                if (serverInfo.getServerGroup() != groupId) {
                    serverInfo = serverListManager.getMinOnlineNumServer(groupId, isWhiteUser);
                    return serverInfo;
                }

                // 判断该服务器是否维护--如果是维护状态则重新分配
                if (serverInfo.getMark() == 1 && !isWhiteUser) {
                    serverInfo = serverListManager.getMinOnlineNumServer(groupId, isWhiteUser);
                    return serverInfo;
                }
            }
        }
        return serverInfo;
    }


    /**
     * 黑桃验证token
     * 
     * @param userId
     * @param token
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public LoginErrorCode htVerifyer(String userId, String token, String mobile)
            throws ClientProtocolException, IOException {
        if (token == null || token.isEmpty()) {
            return LoginErrorCode.NO_TOKEN;
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String url = ht_url + "?uid=" + userId + "&token=" + token;
        RequestConfig requestConfig = getRequestConfig();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(requestConfig);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        log.info("htVerifyer---> url=" + url);
        String status = "";
        LoginErrorCode code = LoginErrorCode.REMOTE_VERIFYER_ERROR;
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                return code;
            }
            HttpEntity responseEntity = response.getEntity();
            String text = EntityUtils.toString(responseEntity, "UTF-8");
            log.info("htVerifyer---> result=" + text);
            JSONObject resultJO = JSONObject.parseObject(text);
            status = resultJO.getString("errno");
            if (!"0".equals(status)) {
                if ("30007".equals(status)) {// 非白名单用户
                    code = LoginErrorCode.NOT_TEST_PLAYER;
                }
                return code;
            }
            JSONObject data = resultJO.getJSONObject("data");
            if (data != null) {
                // if (data.containsKey("dal_beta_type")) {
                // channelAppId = data.getString("dal_beta_type");
                // }
                if(data.containsKey("mobile")){
                    mobile = data.getString("mobile");
                }
            }
            code = LoginErrorCode.SUCCESS;
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return code;
    }

    protected RequestConfig getRequestConfig() {
        return RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(10000).build();
    }

	@Override
	public String queryPlayerInfoByAccount(AccountController accountController, 
			HttpServletRequest request) {
		AccountService accountService = accountController.getAccountService();
        
        LoginErrorCode errCode = LoginErrorCode.SUCCESS;
        
        String accountId = request.getParameter("accountId");// 渠道账号id
        accountId = accountId.toLowerCase();
        String channelId = request.getParameter("channelId");// 渠道标示
        AccountInfo info = accountService.findByAccountIdAndChannelNotCreate(accountId, channelId);
		
        JSONObject object = new JSONObject();
        if(info==null){
			errCode = LoginErrorCode.NOT_FIND_ACCOUNT;
		}else{
			object.put("data", JSON.toJSONString(info));
		}
		object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
		String result = JSON.toJSONString(object,true);
        return result;
	}

	@Override
	public String queryPlayerIdByAccount(AccountController accountController, 
			HttpServletRequest request) {
		AccountService accountService = accountController.getAccountService();
        
        LoginErrorCode errCode = LoginErrorCode.SUCCESS;
        
        String accountId = request.getParameter("accountId");// 渠道账号id
        accountId = accountId.toLowerCase();
        String channelId = request.getParameter("channelId");// 渠道标示
        AccountInfo info = accountService.findByAccountIdAndChannelNotCreate(accountId, channelId);
		
        JSONObject object = new JSONObject();
        if(info==null){
			errCode = LoginErrorCode.NOT_FIND_ACCOUNT;
		}else{
			object.put("data", info.getPlayerId());
		}
		object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
		String result = JSON.toJSONString(object,true);
        return result;
	}

	@Override
	public String queryPlayerInfoByPlayerId(AccountController accountController, HttpServletRequest request) {
		AccountService accountService = accountController.getAccountService();
        
        LoginErrorCode errCode = LoginErrorCode.SUCCESS;
        
        String playerId = request.getParameter("playerId");
        AccountInfo info = accountService.findByPlayerId(Integer.parseInt(playerId));
		
        JSONObject object = new JSONObject();
        if(info==null){
			errCode = LoginErrorCode.NOT_FIND_ACCOUNT;
		}else{
			object.put("data", JSON.toJSONString(info));
		}
		object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
		String result = JSON.toJSONString(object,true);
        return result;
	}

	@Override
	public String queryPlayerInfoByMobile(AccountController accountController, HttpServletRequest request) {
		AccountService accountService = accountController.getAccountService();
        
        LoginErrorCode errCode = LoginErrorCode.SUCCESS;
        
        String mobile = request.getParameter("mobile");
        List<AccountInfo> info = accountService.findByMobile(mobile);
		
        JSONObject object = new JSONObject();
        if(info==null){
			errCode = LoginErrorCode.NOT_FIND_ACCOUNT;
		}else{
			object.put("data", JSON.toJSONString(info));
		}
		object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
		String result = JSON.toJSONString(object,true);
        return result;
	}
}
