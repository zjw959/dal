package com.controller;

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
import com.service.AccountPermissionService;
import com.service.AccountService;
import com.service.ServerListManager;
import com.utils.LogicScriptsUtils;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 *	账号权限管理
 */
@Controller
@RequestMapping("permission")
public class AccountPermissionController extends AbstractHandler {
	@Autowired
    private AccountPermissionService acountPermissionService;
	@Autowired
	private ServerListManager serverListManager;
	@Autowired
    private AccountService accountService;
	
	public AccountPermissionService getAcountPermissionService() {
		return acountPermissionService;
	}

	public void setAcountPermissionService(AccountPermissionService acountPermissionService) {
		this.acountPermissionService = acountPermissionService;
	}

	public ServerListManager getServerListManager() {
		return serverListManager;
	}

	public void setServerListManager(ServerListManager serverListManager) {
		this.serverListManager = serverListManager;
	}

	public AccountService getAccountService() {
		return accountService;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	@ApiOperation(value="用户封号禁言加白名单", notes="禁止玩家登陆or游戏内聊天")
	@ApiImplicitParams({
	 	@ApiImplicitParam(paramType = "query",name = "playerId", value = "玩家id", required = true, dataType = "Integer"),
	 	@ApiImplicitParam(paramType = "query",name = "type", value = "类型：1是封号2是禁言3是白名单", required = true, dataType = "Integer"),
	 	@ApiImplicitParam(paramType = "query",name = "startTime", value = "开始时间", required = true, dataType = "Date"),
	 	@ApiImplicitParam(paramType = "query",name = "endTime", value = "结束时间", required = true, dataType = "Date"),
	 	@ApiImplicitParam(paramType = "query",name = "reason", value = "原因", required = false, dataType = "String"),
	 	@ApiImplicitParam(paramType = "query",name = "operator", value = "操作者", required = false, dataType = "String")
	})
    @RequestMapping(value = {"/operatorPermission"}, method = {RequestMethod.POST,RequestMethod.GET})
    public @ResponseBody String operatorPermission(HttpServletRequest request, HttpServletResponse response)
            throws ClientProtocolException, IOException {
		LoginErrorCode errCode = LogicScriptsUtils.getIAccountPermissionControllerScript().operatorPermission(this,request);
		JSONObject object = new JSONObject();
		object.put("status", errCode.getCode());
        object.put("msg", errCode.getMsg());
		String result = JSON.toJSONString(object,true);
        return result;
    }	
	
	@ApiOperation(value="解除封号或禁言", notes="解除封号或禁言")
	@ApiImplicitParams({
	 	@ApiImplicitParam(paramType = "query",name = "playerId", value = "玩家id", required = true, dataType = "int"),
	 	@ApiImplicitParam(paramType = "query",name = "type", value = "类型：1是封号2是禁言3是白名单", required = true, dataType = "int")
	})
    @RequestMapping(value = {"/reOperatorPermission"}, method = {RequestMethod.POST,RequestMethod.GET})
    public @ResponseBody String removeAccountPermission(HttpServletRequest request, HttpServletResponse response)
            throws ClientProtocolException, IOException {
		LoginErrorCode errCode = LogicScriptsUtils.getIAccountPermissionControllerScript().reOperatorPermission(this,request);
		JSONObject responseObject = new JSONObject();
		responseObject.put("status", errCode.getCode());
		responseObject.put("msg", errCode.getMsg());
		String result = JSON.toJSONString(responseObject,true);
        return result;
    }
	
	@ApiOperation(value="获取封号禁言用户列表", notes="查看封号禁言用户列表  返回格式JSON 例子：")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType = "query",name = "size", value = "查询的数量", required = true, dataType = "Integer" ,defaultValue="50"),
	 	@ApiImplicitParam(paramType = "query",name = "page", value = "分页", required = true, dataType = "Integer" ,defaultValue="1"),
		@ApiImplicitParam(paramType = "query",name = "playerId", value = "玩家id", required = false, dataType = "Integer"),
	 	@ApiImplicitParam(paramType = "query",name = "type", value = "类型：1是封号2是禁言3是白名单", required = false, dataType = "Integer"),
	 	@ApiImplicitParam(paramType = "query",name = "startTime", value = "开始时间", required = false, dataType = "Date"),
	 	@ApiImplicitParam(paramType = "query",name = "endTime", value = "结束时间", required = false, dataType = "Date"),
	 	@ApiImplicitParam(paramType = "query",name = "reason", value = "原因", required = false, dataType = "String"),
	 	@ApiImplicitParam(paramType = "query",name = "operator", value = "操作者", required = false, dataType = "String")
	})
    @RequestMapping(value = {"/queryOPList"}, method = {RequestMethod.POST,RequestMethod.GET})
    public @ResponseBody String queryOPList(HttpServletRequest request, HttpServletResponse response)
            throws ClientProtocolException, IOException {
		return LogicScriptsUtils.getIAccountPermissionControllerScript().queryOPList(this,request);
    }
}
