/**
 * 
 */
package com.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.constant.LoginErrorCode;
import com.service.ServerListManager;
import com.utils.LogicScriptsUtils;

@Controller
@RequestMapping("server")
public class ServerManagerController extends AbstractHandler{
    @Autowired
    private ServerListManager serverListManager;
	
	public ServerListManager getServerListManager() {
		return serverListManager;
	}

	public void setServerListManager(ServerListManager serverListManager) {
		this.serverListManager = serverListManager;
	}

	public ServerManagerController() {
		super();
	}

	@ApiOperation(value="获取服务器全部信息", notes="JSON格式返回")
	@RequestMapping(value = {"/list_all"} , method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String getServerInfo(HttpServletRequest request,
			HttpServletResponse response) throws ClientProtocolException, IOException {
		return LogicScriptsUtils.getIServerManagerControllerScript().getAllServerInfo(this,request);
	}
	
    @ApiOperation(value = "获取服务器初始化信息", notes = "JSON格式返回")
    @RequestMapping(value = {"/server_init"}, method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String serverInit(HttpServletRequest request,
            HttpServletResponse response) throws ClientProtocolException, IOException {
    	return LogicScriptsUtils.getIServerManagerControllerScript().serverInit(this,request);
    }

	@ApiOperation(value="添加修改服务器", notes="添加修改服务器")
	@ApiImplicitParam(paramType = "body",name = "serverInfo", value = "服务器信息实体serverInfo", required = true, dataType = "ServerInfo")
	@RequestMapping(value = {"/save"} , method = RequestMethod.POST)
	public @ResponseBody String saveServerInfo(HttpServletRequest request,
			HttpServletResponse response) throws ClientProtocolException, IOException{
		LoginErrorCode errCode = LogicScriptsUtils.getIServerManagerControllerScript().operServer(this,request);
		JSONObject object = new JSONObject();
		object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
		String result = JSON.toJSONString(object,true);
		return result;
	}
	
	@ApiOperation(value="服务器在线人数", notes="接收游戏服务器人数通知")
	@ApiImplicitParams({
	    @ApiImplicitParam(paramType = "query",name = "serverId", value = "服务器Id", required = true, dataType = "Integer"),
	    @ApiImplicitParam(paramType = "query",name = "onlineNum", value = "在线人数", required = true, dataType = "Integer")
	})
	@RequestMapping(value = {"/onlineNum"} , method = RequestMethod.GET)
	public @ResponseBody void onlineNum(HttpServletRequest request,
			HttpServletResponse response) throws ClientProtocolException, IOException {
		LogicScriptsUtils.getIServerManagerControllerScript().onlineNum(this,request);
	}
	
	@ApiOperation(value="设置服务器维护", notes="设置服务器维护")
	@ApiImplicitParams({
	 	@ApiImplicitParam(paramType = "query",name = "serverId", value = "服务器id", required = true, dataType = "Integer"),
	 	@ApiImplicitParam(paramType = "query",name = "mark", value = "是否维护1是0否", required = true, dataType = "Integer"),
	})
	@RequestMapping(value = {"/server_maintenance"} , method = RequestMethod.POST)
	public @ResponseBody String serverMaintenance(HttpServletRequest request,
			HttpServletResponse response) throws ClientProtocolException, IOException {
		LoginErrorCode errCode = LogicScriptsUtils.getIServerManagerControllerScript().serverMaintenance(this,request);
		JSONObject object = new JSONObject();
		object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
		String result = JSON.toJSONString(object,true);
		return result;
	}
}
