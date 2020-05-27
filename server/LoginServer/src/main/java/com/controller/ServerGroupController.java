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
@RequestMapping("serverGroup")
public class ServerGroupController extends AbstractHandler{
    @Autowired
    private ServerListManager serverListManager;
	
	public ServerListManager getServerListManager() {
		return serverListManager;
	}

	public void setServerListManager(ServerListManager serverListManager) {
		this.serverListManager = serverListManager;
	}

	public ServerGroupController() {
		super();
	}

	@ApiOperation(value="获取服务器分组全部信息", notes="JSON格式返回")
	@RequestMapping(value = {"/server_group_all"} , method = {RequestMethod.GET,RequestMethod.POST})
	public @ResponseBody String getServerGroup(HttpServletRequest request,
			HttpServletResponse response) throws ClientProtocolException, IOException {
		return LogicScriptsUtils.getIServerGroupControllerScript().getAllServerGroup(this,request);
	}
	
	@ApiOperation(value="添加修改分组信息", notes="添加修改分组信息")
	@ApiImplicitParam(paramType = "body",name = "ServerGroup", value = "服务器组信息", required = true, dataType = "ServerGroup")
	@RequestMapping(value = {"/save_server_group"} , method = RequestMethod.POST)
	public @ResponseBody String saveServerGroup(HttpServletRequest request,
			HttpServletResponse response) throws ClientProtocolException, IOException {
		LoginErrorCode errCode = LogicScriptsUtils.getIServerGroupControllerScript().saveServerGroup(this,request);
		JSONObject object = new JSONObject();
		object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
		String result = JSON.toJSONString(object,true);
		return result;
	}
	
	
	@ApiOperation(value="设置服务器维护", notes="设置分组下所有服务器维护时间")
	@ApiImplicitParams({
	 	@ApiImplicitParam(paramType = "query",name = "groupId", value = "分组id", required = true, dataType = "Integer"),
	 	@ApiImplicitParam(paramType = "query",name = "mark", value = "是否维护1是0否", required = true, dataType = "Integer"),
	 	@ApiImplicitParam(paramType = "query",name = "notice", value = "通告", required = false, dataType = "String")
	})
	@RequestMapping(value = {"/group_maintenance"} , method = RequestMethod.POST)
	public @ResponseBody String serverMaintenance(HttpServletRequest request,
			HttpServletResponse response) throws ClientProtocolException, IOException {
		LoginErrorCode errCode = LogicScriptsUtils.getIServerGroupControllerScript().groupMaintenance(this,request);
		JSONObject object = new JSONObject();
		object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
		String result = JSON.toJSONString(object,true);
		return result;
	}
}
