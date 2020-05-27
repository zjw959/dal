package javascript.controller;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.Consts;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import utils.ExceptionEx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.constant.EScriptIdDefine;
import com.constant.LoginErrorCode;
import com.controller.ServerManagerController;
import com.enity.ServerGroup;
import com.enity.ServerInfo;
import com.iscript.IServerManagerControllerScript;
import com.service.ServerListManager;
import com.utils.HttpBaseRequest;

public class ServerManagerControllerScript extends IServerManagerControllerScript {
    private static final Logger log = Logger.getLogger(ServerManagerControllerScript.class);

    @Override
    public int getScriptId() {
        return EScriptIdDefine.Server_Manager_Controller_SCRIPTID.Value();
    }

    private String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."+
			"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
			"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."+
			"(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
    
	@Override
	public String getAllServerInfo(ServerManagerController serverManagerController, HttpServletRequest request) {
		ServerListManager serverListManager =serverManagerController.getServerListManager();
		LoginErrorCode errCode = LoginErrorCode.SUCCESS;
		String allServerJson = JSON.toJSONString(serverListManager.getAllServerInfo(), true);
        String usingServerJson =
                JSON.toJSONString(serverListManager.getUsingServerInfo(false), true);
		JSONObject object = new JSONObject();
		object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
        JSONObject dataObject = new JSONObject();
        dataObject.put("all_server", allServerJson);
        dataObject.put("using_server", usingServerJson);
        String dataResult = JSON.toJSONString(dataObject,true);
        object.put("data", dataResult);
		String result = JSON.toJSONString(object,true);
		return result;
	}

	@Override
	public String serverInit(ServerManagerController serverManagerController, HttpServletRequest request) {
		ServerListManager serverListManager =serverManagerController.getServerListManager();
		LoginErrorCode errCode = LoginErrorCode.SUCCESS;
        String internalIp = request.getParameter("internalIp");
        int tcpPort = Integer.parseInt(request.getParameter("tcpPort"));
        ServerInfo server = serverListManager.getServerByIpAndPort(internalIp, tcpPort);
        JSONObject object = new JSONObject();
        if (server != null) {
            ServerGroup group = serverListManager.getServerGroupById(server.getServerGroup());
            JSONObject json = server.toJson();
            json.put("groupName", group.getName());
            json.put("csvUrl", group.getCsvUrl());
            json.put("giftCodeVerifyUrl", group.getGiftCodeVerifyUrl());
            object.put("data", json);
            object.put("gameServerCount",
                    serverListManager.getGameServerCount(server.getServerGroup()));
        } else {
            errCode = LoginErrorCode.UNKNOWN_EXCEPTION;
        }
        object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
        String result = JSON.toJSONString(object, true);
        return result;
	}

	@Override
    public LoginErrorCode operServer(ServerManagerController serverManagerController,
            HttpServletRequest request) {
		ServerListManager serverListManager =serverManagerController.getServerListManager();
		LoginErrorCode errCode = LoginErrorCode.SUCCESS;
		String text = null;
		try {
			text = HttpBaseRequest.getDefault().getRequestJson(request);
		} catch (IOException e) {
            log.error(ExceptionEx.e2s(e));
		}
		JSONObject jsonObject = JSONObject.parseObject(text);
		ServerInfo serverInfo = JSON.toJavaObject(jsonObject.getJSONObject("ServerInfo"), ServerInfo.class);
		if(serverInfo.getName()==null||serverInfo.getName().isEmpty()){
			errCode = LoginErrorCode.SERVER_NAME_MISS;
			return errCode;
		}
		if(!serverInfo.getGameServerExternalIp().matches(regex)||!serverInfo.getGameServerInternalIp().matches(regex)){
			errCode = LoginErrorCode.SERVER_IP_MISS;
			return errCode;
		}
		serverInfo.setLastUpdateTime(new Date());
		boolean status = true;
        log.info("后台添加修改服务器  serverInfo=" + serverInfo);
		ServerInfo info = serverListManager.getServerById(serverInfo.getId());
        if (info != null) {
            serverInfo.setOnlineNum(info.getOnlineNum());
            serverInfo.setOnlineNumTime(info.getOnlineNumTime());
            if (info.getMaxOnlineNum() != serverInfo.getMaxOnlineNum()) {
                try {
                    // 通知该服务器
                    CloseableHttpClient httpClient = HttpClients.createDefault();
                    String url = serverInfo.getHttpUrl() + "/gm/maxonlinenum";
                    JSONObject object = new JSONObject();
                    object.put("maxonlinenum", serverInfo.getMaxOnlineNum());
                    String content = object.toString();
                    ContentType contentType = ContentType.create("application/json", Consts.UTF_8);
                    StringEntity entity = new StringEntity(content, contentType);
                    entity.setChunked(true);
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(entity);
                    httpClient.execute(httpPost);
                } catch (Exception e) {
                    log.error(ExceptionEx.e2s(e));
                }
            }
        }
		serverListManager.saveServer(serverInfo);
		if(!status){
			errCode = LoginErrorCode.UNKNOWN_EXCEPTION;
		}
		return errCode;
	}

	@Override
	public void onlineNum(ServerManagerController serverManagerController, HttpServletRequest request) {
		ServerListManager serverListManager =serverManagerController.getServerListManager();
		String serverIdStr = request.getParameter("serverId");
		String onlineNumStr = request.getParameter("onlineNum");
		Integer serverId = Integer.parseInt(serverIdStr);
		Integer onlineNum = Integer.parseInt(onlineNumStr);
		serverListManager.updateOnlineNum(serverId,onlineNum);
	}

	@Override
	public LoginErrorCode serverMaintenance(ServerManagerController serverManagerController,
			HttpServletRequest request) throws IOException {
		log.info("后台设置服务器维护状态 ");
		
		ServerListManager serverListManager =serverManagerController.getServerListManager();
		LoginErrorCode errCode = LoginErrorCode.SUCCESS;
		String text = HttpBaseRequest.getDefault().getRequestJson(request);
		Integer serverId = null;
		Integer mark = null;
		if(text.isEmpty()){
			String serverIdStr = request.getParameter("serverId");
			if(serverIdStr==null||serverIdStr.isEmpty()){
				errCode = LoginErrorCode.MISS_PARAM;
				return errCode;
			}
			serverId = Integer.parseInt(serverIdStr);
			String markStr = request.getParameter("mark");
			mark = Integer.parseInt(markStr);
		}else{
			JSONObject jsonObject = JSONObject.parseObject(text);
			serverId = jsonObject.getInteger("serverId");
			mark = jsonObject.getInteger("mark");
		}
		ServerInfo serverInfo = serverListManager.getServerById(serverId);
		serverInfo.setMark(mark);
		serverListManager.saveServer(serverInfo);
		return errCode;
	}
}
