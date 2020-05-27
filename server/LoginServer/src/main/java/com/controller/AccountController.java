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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.service.AccountPermissionService;
import com.service.AccountService;
import com.service.ServerListManager;
import com.utils.LogicScriptsUtils;


@Controller
@RequestMapping("account")
public class AccountController{
    @Autowired
    private AccountService accountService;
    @Autowired
    private ServerListManager serverListManager;
    @Autowired
    private AccountPermissionService accountPermissionService;
    
    @Value("${service.isLocalTest}")
    private boolean isLocalTest = false;
	
    public AccountController() {
        super();
    }

    //1、客户端发送玩家渠道账号到用户中心
  	//2、用户中心根据渠道账号找出对应的唯一身份账号
  	//3、判断所分配的服务器的状态
  	//4、返回客户端玩家信息和服务器状态
    @ApiOperation(value="请求登陆", notes="客户端向用户中心请求登陆账号；成功：返回登陆服务器有效信息")
    @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query",name = "accountId", value = "玩家渠道ID", required = true, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "channelId", value = "玩家渠道标识", required = true, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "token", value = "玩家身份认证", required = true, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "serverName", value = "指定要登陆的服务器", required = false, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "serverGroup", value = "指定要登陆的服务组", required = false, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "channelAppId", value = "渠道APPID", required = false, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "deviceName", value = "设备名称", required = false, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "deviceId", value = "设备唯一ID", required = false, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "sdk", value = "SDK类型", required = false, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "sdkVersion", value = "SDK版本", required = false, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "osName", value = "系统名称", required = false, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "osVersion", value = "系统版本", required = false, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "clientVersion", value = "客户端版本号", required = false, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "ip", value = "ip地址", required = false, dataType = "String")
    })
    @RequestMapping(value = {"/login"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String getServerInfo(HttpServletRequest request,
            HttpServletResponse response) throws ClientProtocolException, IOException {
        return LogicScriptsUtils.getIAccountControllerScript().getServerInfo(this, request);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "pid", value = "玩家角色ID",
            required = true, dataType = "String")})
    @RequestMapping(value = {"/adminLogin"}, method = {RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String adminLogin(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String result = LogicScriptsUtils.getIAccountControllerScript().adminLogin(this, request);
        return result;
    }
    
    @ApiOperation(value="登陆验证", notes="服务器向用户中心请求登陆账号的有效性")
    @ApiImplicitParam(paramType = "query",name = "token", value = "身份标识", required = true, dataType = "String")
    @RequestMapping(value = {"/loginVerify"}, method = {RequestMethod.POST,RequestMethod.GET})
    public @ResponseBody String loginVerify(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        String result = LogicScriptsUtils.getIAccountControllerScript().loginVerify(this, request);
        return result;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public ServerListManager getServerListManager() {
        return serverListManager;
    }

    public AccountPermissionService getAccountPermissionService() {
        return accountPermissionService;
    }

    public boolean isLocalTest() {
        return isLocalTest;
    }

    @ApiOperation(value="查询玩家信息", notes="通过渠道账号获得玩家信息")
    @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query",name = "accountId", value = "玩家渠道ID", required = true, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "channelId", value = "玩家渠道标识", required = true, dataType = "String"),
    })
    @RequestMapping(value = {"/queryPlayerInfoByAccount"}, method = {RequestMethod.GET})
    public @ResponseBody String queryPlayerInfoByAccount(HttpServletRequest request,
            HttpServletResponse response) throws ClientProtocolException, IOException {
        return LogicScriptsUtils.getIAccountControllerScript().queryPlayerInfoByAccount(this, request);
    }
    
    
    @ApiOperation(value="查询玩家ID", notes="通过渠道账号获得玩家ID")
    @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query",name = "accountId", value = "玩家渠道ID", required = true, dataType = "String"),
      @ApiImplicitParam(paramType = "query",name = "channelId", value = "玩家渠道标识", required = true, dataType = "String"),
    })
    @RequestMapping(value = {"/queryPlayerIdByAccount"}, method = {RequestMethod.GET})
    public @ResponseBody String queryPlayerIdByAccount(HttpServletRequest request,
            HttpServletResponse response) throws ClientProtocolException, IOException {
        return LogicScriptsUtils.getIAccountControllerScript().queryPlayerIdByAccount(this, request);
    }
    
    @ApiOperation(value="查询玩家信息", notes="通过玩家ID获得玩家信息")
    @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query",name = "playerId", value = "玩家ID", required = true, dataType = "String"),
    })
    @RequestMapping(value = {"/queryPlayerInfoByPlayerId"}, method = {RequestMethod.GET})
    public @ResponseBody String queryPlayerInfoByPlayerId(HttpServletRequest request,
            HttpServletResponse response) throws ClientProtocolException, IOException {
        return LogicScriptsUtils.getIAccountControllerScript().queryPlayerInfoByPlayerId(this, request);
    }
    
    @ApiOperation(value="查询玩家信息", notes="通过手机号获得玩家信息")
    @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query",name = "mobile", value = "手机号", required = true, dataType = "String"),
    })
    @RequestMapping(value = {"/queryPlayerInfoByMobile"}, method = {RequestMethod.GET})
    public @ResponseBody String queryPlayerInfoByMobile(HttpServletRequest request,
            HttpServletResponse response) throws ClientProtocolException, IOException {
        return LogicScriptsUtils.getIAccountControllerScript().queryPlayerInfoByMobile(this, request);
    }
}
